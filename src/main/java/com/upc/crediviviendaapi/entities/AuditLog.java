package com.upc.crediviviendaapi.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;

    // actor (desde JWT)
    private String username;  // auth.getName() (dni/email/username)
    private String roles;     // "ROLE_ADMIN,ROLE_USER"

    // evento
    private String accion;    // CREATE, UPDATE, DELETE
    private String entidad;   // CLIENTE, UNIDAD, PRESTAMO, CONFIG
    private Long entidadId;   // id afectado (si aplica)

    @Column(length = 2000)
    private String detalle;   // texto/json: antes/después
}
