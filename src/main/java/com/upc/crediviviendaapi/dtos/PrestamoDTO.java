package com.upc.crediviviendaapi.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PrestamoDTO {

    private Long id;

    private Long clienteId;
    private Long unidadInmobiliariaId;

    private Double montoPrestamo;
    private Integer plazoMeses;

    private String moneda;        // PEN / USD
    private String tipoTasa;       // EFECTIVA / NOMINAL
    private String capitalizacion; // DIARIA / MENSUAL / ANUAL (solo si NOMINAL)
    private Double tasaInteres;

    private Integer graciaTotal;
    private Integer graciaParcial;

    private Double van;
    private Double tir;
    private Double cuotaFija;
    private Double interesesTotales;
    private Double montoTotalPagado;
    private Double tcea;

    private LocalDate fechaSimulacion;

    private List<CuotaDTO> cronograma;
}
