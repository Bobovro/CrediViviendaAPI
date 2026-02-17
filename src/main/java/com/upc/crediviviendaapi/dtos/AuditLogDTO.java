package com.upc.crediviviendaapi.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditLogDTO {
    private Long id;
    private LocalDateTime fecha;

    private String username;
    private String roles;

    private String accion;
    private String entidad;
    private Long entidadId;

    private String detalle;
}
