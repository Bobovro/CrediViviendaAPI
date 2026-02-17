package com.upc.crediviviendaapi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String proyecto;
    private String ubicacion;
    private Double valorInmueble;

    private Boolean aplicaTechoPropio;
    private Double bonoTechoPropio;

}
