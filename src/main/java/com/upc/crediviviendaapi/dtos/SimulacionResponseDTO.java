package com.upc.crediviviendaapi.dtos;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SimulacionResponseDTO {

    private Double cuota;
    private Double van;
    private Double tir;

    private List<CuotaDTO> cronograma;
}