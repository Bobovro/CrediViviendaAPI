package com.upc.crediviviendaapi.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CuotaDTO {

    private Long id;
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
