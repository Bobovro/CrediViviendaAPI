package com.upc.crediviviendaapi.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "cuotas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Mucho mejor LAZY para evitar cargas innecesarias
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestamo_id", nullable = false)
    @JsonBackReference
    private Prestamo prestamo;

    @Column(nullable = false)
    private Integer numeroCuota;

    private Double saldoInicial;
    private Double amortizacion;
    private Double interes;
    private Double cuotaTotal;
    private Double saldoFinal;

    private Boolean esGraciaTotal;
    private Boolean esGraciaParcial;

    private LocalDate fechaVencimiento;
}
