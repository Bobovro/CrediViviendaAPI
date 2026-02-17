package com.upc.crediviviendaapi.interfaces;

import com.upc.crediviviendaapi.dtos.ClienteDTO;

import java.util.List;

public interface IClienteService {

    ClienteDTO crear(ClienteDTO dto);

    ClienteDTO obtenerPorId(Long id);

    List<ClienteDTO> listar();

    ClienteDTO actualizar(Long id, ClienteDTO dto);

    void eliminar(Long id);
}
