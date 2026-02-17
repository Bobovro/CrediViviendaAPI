package com.upc.crediviviendaapi.interfaces;

import com.upc.crediviviendaapi.dtos.UnidadInmobiliariaDTO;

import java.util.List;

public interface IUnidadInmobiliariaService {

    UnidadInmobiliariaDTO crear(UnidadInmobiliariaDTO dto);

    UnidadInmobiliariaDTO obtenerPorId(Long id);

    List<UnidadInmobiliariaDTO> listar();

    UnidadInmobiliariaDTO actualizar(Long id, UnidadInmobiliariaDTO dto);

    void eliminar(Long id);
}