package com.upc.crediviviendaapi.interfaces;

import com.upc.crediviviendaapi.dtos.PrestamoDTO;

import java.util.List;

public interface IPrestamoService {
    PrestamoDTO simular(PrestamoDTO dto);     // NO guarda (preview)
    PrestamoDTO crear(PrestamoDTO dto);       // guarda préstamo + cuotas
    PrestamoDTO obtenerPorId(Long id);        // devuelve préstamo + cronograma
    List<PrestamoDTO> listar();
    void eliminar(Long id);
}
