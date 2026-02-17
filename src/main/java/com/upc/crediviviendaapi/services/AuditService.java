package com.upc.crediviviendaapi.services;

import com.upc.crediviviendaapi.dtos.AuditLogDTO;
import com.upc.crediviviendaapi.entities.AuditLog;
import com.upc.crediviviendaapi.interfaces.IAuditService;
import com.upc.crediviviendaapi.repositories.AuditLogRepository;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuditService implements IAuditService {

    private final AuditLogRepository repo;

    public AuditService(AuditLogRepository repo) {
        this.repo = repo;
    }

    @Override
    public void log(String accion, String entidad, Long entidadId, String detalle) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = (auth != null ? auth.getName() : "ANON");
        String roles = (auth != null)
                ? auth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","))
                : "";

        AuditLog a = AuditLog.builder()
                .fecha(LocalDateTime.now())
                .username(username)
                .roles(roles)
                .accion(accion)
                .entidad(entidad)
                .entidadId(entidadId)
                .detalle(detalle)
                .build();

        repo.save(a);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogDTO> listar(int page, int size, String sort, String dir,
                                    String entidad, String accion, String username) {

        Sort s = "asc".equalsIgnoreCase(dir)
                ? Sort.by(sort).ascending()
                : Sort.by(sort).descending();

        Pageable pageable = PageRequest.of(page, size, s);

        Page<AuditLog> data;

        if (entidad != null && !entidad.isBlank()) {
            data = repo.findByEntidadIgnoreCase(entidad, pageable);
        } else if (accion != null && !accion.isBlank()) {
            data = repo.findByAccionIgnoreCase(accion, pageable);
        } else if (username != null && !username.isBlank()) {
            data = repo.findByUsernameIgnoreCase(username, pageable);
        } else {
            data = repo.findAll(pageable);
        }

        return data.map(this::toDto);
    }

    private AuditLogDTO toDto(AuditLog a) {
        AuditLogDTO dto = new AuditLogDTO();
        dto.setId(a.getId());
        dto.setFecha(a.getFecha());
        dto.setUsername(a.getUsername());
        dto.setRoles(a.getRoles());
        dto.setAccion(a.getAccion());
        dto.setEntidad(a.getEntidad());
        dto.setEntidadId(a.getEntidadId());
        dto.setDetalle(a.getDetalle());
        return dto;
    }
}
