package com.upc.crediviviendaapi.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "unidades_inmobiliarias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnidadInmobiliaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El proyecto es obligatorio")
    @Size(max = 80, message = "Proyecto: máximo 80 caracteres")
    @Column(nullable = false, length = 80)
    private String proyecto;

    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 120, message = "Ubicación: máximo 120 caracteres")
    @Column(nullable = false, length = 120)
    private String ubicacion;

    @NotNull(message = "El valor del inmueble es obligatorio")
    @DecimalMin(value = "0.01", inclusive = true, message = "El valor del inmueble debe ser mayor a 0")
    @DecimalMax(value = "1000000.00", message = "El valor del inmueble no puede superar 1,000,000")
    @Column(nullable = false)
    private Double valorInmueble;

    @NotNull(message = "Debe indicar si aplica Techo Propio")
    @Column(nullable = false)
    private Boolean aplicaTechoPropio;

    @NotNull(message = "El bono Techo Propio es obligatorio (0 si no aplica)")
    @DecimalMin(value = "0.00", message = "El bono no puede ser negativo")
    @DecimalMax(value = "60000.00", message = "El bono no puede superar 60,000")
    @Column(nullable = false)
    private Double bonoTechoPropio;
}