package com.upc.crediviviendaapi.interfaces;

import com.upc.crediviviendaapi.dtos.UsuarioDTO;

import java.util.List;

public interface IUsuarioService {
    UsuarioDTO crear(UsuarioDTO dto);

    UsuarioDTO obtenerPorId(Long id);

    List<UsuarioDTO> listar();

    UsuarioDTO actualizar(Long id, UsuarioDTO dto);

    void eliminar(Long id);
}
