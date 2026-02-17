package com.upc.crediviviendaapi.controllers;

import com.upc.crediviviendaapi.dtos.SimulacionRequestDTO;
import com.upc.crediviviendaapi.dtos.SimulacionResponseDTO;
import com.upc.crediviviendaapi.interfaces.ISimuladorFinancieroService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/simulaciones")
public class SimulacionController {

    private final ISimuladorFinancieroService simulador;

    public SimulacionController(ISimuladorFinancieroService simulador) {
        this.simulador = simulador;
    }

    @PostMapping
    public SimulacionResponseDTO simular(@RequestBody SimulacionRequestDTO request) {
        return simulador.simular(request);
    }
}