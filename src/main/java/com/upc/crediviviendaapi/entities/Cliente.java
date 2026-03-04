package com.upc.crediviviendaapi.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 60, message = "Máximo 60 caracteres")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]+$",
            message = "Los nombres solo pueden contener letras"
    )
    @Column(nullable = false)
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 60, message = "Máximo 60 caracteres")
    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]+$",
            message = "Los apellidos solo pueden contener letras"
    )
    @Column(nullable = false)
    private String apellidos;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(
            regexp = "^\\d{8}$",
            message = "El DNI debe tener exactamente 8 dígitos"
    )
    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    @Email(message = "El email no es válido")
    @Size(max = 80)
    private String email;

    @Pattern(
            regexp = "^\\d{0,15}$",
            message = "El teléfono solo puede contener números"
    )
    @Size(max = 15)
    private String telefono;

    @NotNull(message = "El ingreso mensual es obligatorio")
    @DecimalMin(value = "1000", message = "El ingreso mensual no puede ser negativo")
    @DecimalMax(value = "50000.0", message = "El ingreso mensual no puede superar 50,000")
    @Column(nullable = false)
    private Double ingresoMensual;

    @NotNull(message = "El gasto mensual es obligatorio")
    @DecimalMin(value = "0.0", message = "El gasto mensual no puede ser negativo")
    @DecimalMax(value = "50000.0", message = "El gasto mensual no puede superar 50,000")
    @Column(nullable = false)
    private Double gastoMensual;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}