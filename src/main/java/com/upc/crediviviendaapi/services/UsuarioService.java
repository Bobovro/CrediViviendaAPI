package com.upc.crediviviendaapi.services;

import com.upc.crediviviendaapi.dtos.UsuarioDTO;
import com.upc.crediviviendaapi.entities.Usuario;
import com.upc.crediviviendaapi.exceptions.BadRequestException;
import com.upc.crediviviendaapi.exceptions.ResourceNotFoundException;
import com.upc.crediviviendaapi.interfaces.IUsuarioService;
import com.upc.crediviviendaapi.repositories.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioService implements IUsuarioService {

    private final UsuarioRepository clienteRepository;
    private final ModelMapper mapper;

    public UsuarioService(UsuarioRepository clienteRepository, ModelMapper mapper) {
        this.clienteRepository = clienteRepository;
        this.mapper = mapper;
    }

    @Override
    public UsuarioDTO crear(UsuarioDTO dto) {
        if (dto.getDni() == null || dto.getDni().isBlank()) {
            throw new BadRequestException("El DNI es obligatorio.");
        }
        Usuario entity = mapper.map(dto, Usuario.class);
        entity.setId(null);

        Usuario saved = clienteRepository.save(entity);
        return mapper.map(saved, UsuarioDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorId(Long id) {
        Usuario entity = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: id=" + id));
        return mapper.map(entity, UsuarioDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listar() {
        return clienteRepository.findAll()
                .stream()
                .map(c -> mapper.map(c, UsuarioDTO.class))
                .toList();
    }

    @Override
    public UsuarioDTO actualizar(Long id, UsuarioDTO dto) {
        Usuario entity = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: id=" + id));

        Usuario saved = clienteRepository.save(entity);
        return mapper.map(saved, UsuarioDTO.class);
    }

    @Override
    public void eliminar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente no encontrado: id=" + id);
        }
        clienteRepository.deleteById(id);
    }
}
