package com.upc.crediviviendaapi.dtos;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimulacionRequestDTO {

    private Double montoPrestamo;
    private Integer plazoMeses;

    private String tipoTasa;        // NOMINAL / EFECTIVA
    private String capitalizacion;  // MENSUAL / DIARIA
    private Double tasaInteres;

    private Integer graciaTotal;
    private Integer graciaParcial;
}
