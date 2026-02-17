package com.upc.crediviviendaapi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_operaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaOperacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accion;
    private String entidad;

    private String usuario;
    private LocalDateTime fecha;

    @Column(length = 500)
    private String detalle;
}

