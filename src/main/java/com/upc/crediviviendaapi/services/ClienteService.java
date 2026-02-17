package com.upc.crediviviendaapi.services;

import com.upc.crediviviendaapi.dtos.ClienteDTO;
import com.upc.crediviviendaapi.entities.Cliente;
import com.upc.crediviviendaapi.entities.Usuario;
import com.upc.crediviviendaapi.exceptions.BadRequestException;
import com.upc.crediviviendaapi.exceptions.ResourceNotFoundException;
import com.upc.crediviviendaapi.interfaces.IAuditService;
import com.upc.crediviviendaapi.interfaces.IClienteService;
import com.upc.crediviviendaapi.repositories.ClienteRepository;
import com.upc.crediviviendaapi.repositories.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClienteService implements IClienteService {

    private final UsuarioRepository usuarioRepo;
    private final ClienteRepository repo;
    private final ModelMapper mapper;
    private final IAuditService audit; // ✅

    public ClienteService(
            ClienteRepository repo,
            ModelMapper mapper,
            UsuarioRepository usuarioRepo,
            IAuditService audit
    ) {
        this.repo = repo;
        this.mapper = mapper;
        this.usuarioRepo = usuarioRepo;
        this.audit = audit;
    }

    @Override
    public ClienteDTO crear(ClienteDTO dto) {
        if (dto.getDni() == null || dto.getDni().isBlank()) {
            throw new BadRequestException("El DNI es obligatorio.");
        }
        if (repo.existsByDni(dto.getDni())) {
            throw new BadRequestException("Ya existe un cliente con DNI " + dto.getDni());
        }

        Usuario usuario = requireAuthUser();

        Cliente entity = mapper.map(dto, Cliente.class);
        entity.setId(null);
        entity.setUsuario(usuario); // ✅ CLAVE: llena usuario_id

        // Defaults por si vienen null
        if (entity.getIngresoMensual() == null) entity.setIngresoMensual(0.0);
        if (entity.getGastoMensual() == null) entity.setGastoMensual(0.0);

        Cliente saved = repo.save(entity);

        // ✅ AUDITORÍA CREATE
        audit.log(
                "CREATE",
                "CLIENTE",
                saved.getId(),
                "Se creó cliente: dni=" + saved.getDni()
                        + ", nombres=" + saved.getNombres()
                        + ", apellidos=" + saved.getApellidos()
                        + ", email=" + saved.getEmail()
                        + ", telefono=" + saved.getTelefono()
                        + ", usuario=" + usuario.getUsername()
        );

        return mapper.map(saved, ClienteDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO obtenerPorId(Long id) {
        Cliente entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: id=" + id));
        return mapper.map(entity, ClienteDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> listar() {
        return repo.findAll().stream()
                .map(c -> mapper.map(c, ClienteDTO.class))
                .toList();
    }

    @Override
    public ClienteDTO actualizar(Long id, ClienteDTO dto) {
        Usuario usuario = requireAuthUser();

        Cliente entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: id=" + id));

        // snapshot antes (para auditoría)
        String before = "ANTES: dni=" + entity.getDni()
                + ", nombres=" + entity.getNombres()
                + ", apellidos=" + entity.getApellidos()
                + ", email=" + entity.getEmail()
                + ", telefono=" + entity.getTelefono()
                + ", ingresoMensual=" + entity.getIngresoMensual()
                + ", gastoMensual=" + entity.getGastoMensual();

        // Si cambia el DNI, validar duplicados
        if (dto.getDni() != null && !dto.getDni().equals(entity.getDni())) {
            if (repo.existsByDni(dto.getDni())) {
                throw new BadRequestException("Ya existe un cliente con DNI " + dto.getDni());
            }
            entity.setDni(dto.getDni());
        }

        entity.setNombres(dto.getNombres());
        entity.setApellidos(dto.getApellidos());
        entity.setEmail(dto.getEmail());
        entity.setTelefono(dto.getTelefono());
        entity.setIngresoMensual(dto.getIngresoMensual() == null ? 0.0 : dto.getIngresoMensual());
        entity.setGastoMensual(dto.getGastoMensual() == null ? 0.0 : dto.getGastoMensual());

        Cliente saved = repo.save(entity);

        String after = "DESPUÉS: dni=" + saved.getDni()
                + ", nombres=" + saved.getNombres()
                + ", apellidos=" + saved.getApellidos()
                + ", email=" + saved.getEmail()
                + ", telefono=" + saved.getTelefono()
                + ", ingresoMensual=" + saved.getIngresoMensual()
                + ", gastoMensual=" + saved.getGastoMensual();

        // ✅ AUDITORÍA UPDATE
        audit.log(
                "UPDATE",
                "CLIENTE",
                saved.getId(),
                "Usuario=" + usuario.getUsername() + " | " + before + " | " + after
        );

        return mapper.map(saved, ClienteDTO.class);
    }

    @Override
    public void eliminar(Long id) {
        Usuario usuario = requireAuthUser();

        Cliente entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: id=" + id));

        // ✅ AUDITORÍA DELETE (antes de borrar)
        audit.log(
                "DELETE",
                "CLIENTE",
                id,
                "Se eliminó cliente: dni=" + entity.getDni()
                        + ", nombres=" + entity.getNombres()
                        + ", apellidos=" + entity.getApellidos()
                        + ", usuario=" + usuario.getUsername()
        );

        repo.delete(entity);
    }

    // ================= Helpers =================

    private Usuario requireAuthUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new BadRequestException("No hay usuario autenticado. Envía el JWT en Authorization: Bearer <token>.");
        }

        String username = auth.getName();

        return usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
    }
}
