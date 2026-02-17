package com.upc.crediviviendaapi.interfaces;

import com.upc.crediviviendaapi.dtos.PrestamoDTO;
import com.upc.crediviviendaapi.dtos.SimulacionRequestDTO;
import com.upc.crediviviendaapi.dtos.SimulacionResponseDTO;

public interface ISimuladorFinancieroService {

    // Simula sin guardar en BD (para pantalla de simulación rápida)
    SimulacionResponseDTO simular(SimulacionRequestDTO request);

    // Genera cronograma + VAN/TIR a partir de un PrestamoDTO (para guardar/editar)
    PrestamoDTO calcularPrestamo(PrestamoDTO prestamo);
}
