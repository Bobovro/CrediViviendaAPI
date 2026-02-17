package com.upc.crediviviendaapi.controllers;

import com.upc.crediviviendaapi.dtos.UsuarioDTO;
import com.upc.crediviviendaapi.interfaces.IUsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final IUsuarioService clienteService;

    public UsuarioController(IUsuarioService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTO crear(@RequestBody UsuarioDTO dto) {
        return clienteService.crear(dto);
    }

    @GetMapping("/{id}")
    public UsuarioDTO obtenerPorId(@PathVariable Long id) {
        return clienteService.obtenerPorId(id);
    }

    @GetMapping
    public List<UsuarioDTO> listar() {
        return clienteService.listar();
    }

    @PutMapping("/{id}")
    public UsuarioDTO actualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        return clienteService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
    }
}