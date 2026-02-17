package com.upc.crediviviendaapi.controllers;

import com.upc.crediviviendaapi.dtos.UnidadInmobiliariaDTO;
import com.upc.crediviviendaapi.interfaces.IUnidadInmobiliariaService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unidades")
public class UnidadInmobiliariaController {

    private final IUnidadInmobiliariaService unidadService;

    public UnidadInmobiliariaController(IUnidadInmobiliariaService unidadService) {
        this.unidadService = unidadService;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UnidadInmobiliariaDTO crear(@RequestBody UnidadInmobiliariaDTO dto) {
        return unidadService.crear(dto);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public UnidadInmobiliariaDTO obtenerPorId(@PathVariable Long id) {
        return unidadService.obtenerPorId(id);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public List<UnidadInmobiliariaDTO> listar() {
        return unidadService.listar();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/{id}")
    public UnidadInmobiliariaDTO actualizar(@PathVariable Long id, @RequestBody UnidadInmobiliariaDTO dto) {
        return unidadService.actualizar(id, dto);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        unidadService.eliminar(id);
    }
}