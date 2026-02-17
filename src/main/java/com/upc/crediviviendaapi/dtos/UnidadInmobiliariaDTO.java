package com.upc.crediviviendaapi.dtos;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnidadInmobiliariaDTO {

    private Long id;

    private String proyecto;
    private String ubicacion;

    private Double valorInmueble;

    private Boolean aplicaTechoPropio;
    private Double bonoTechoPropio;
}