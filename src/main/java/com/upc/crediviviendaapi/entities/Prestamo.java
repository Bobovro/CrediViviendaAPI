package com.upc.crediviviendaapi.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prestamos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cliente que solicita
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Unidad inmobiliaria asociada
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_id", nullable = false)
    private UnidadInmobiliaria unidad;

    @Column(nullable = false)
    private Double montoPrestamo;

    @Column(nullable = false)
    private Integer plazoMeses;

    // PEN / USD
    @Column(nullable = false, length = 3)
    private String moneda;

    // EFECTIVA / NOMINAL
    @Column(nullable = false, length = 10)
    private String tipoTasa;

    // DIARIA / MENSUAL / ANUAL (solo si NOMINAL)
    @Column(length = 10)
    private String capitalizacion;

    // tasaInteres: puede venir 0.12 o 12 (en el service normalizamos)
    @Column(nullable = false)
    private Double tasaInteres;

    // Gracia (meses)
    private Integer graciaTotal;   // 0..n
    private Integer graciaParcial; // 0..n

    // Indicadores
    private Double van;
    private Double tir;
    private Double cuotaFija;
    private Double interesesTotales;
    private Double montoTotalPagado;
    private Double tcea;

    private LocalDate fechaSimulacion;

    // ✅ Cronograma (French schedule) - se guarda en BD
    @OneToMany(mappedBy = "prestamo", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("numeroCuota ASC")
    @JsonManagedReference
    @Builder.Default
    private List<Cuota> cuotas = new ArrayList<>();
}
