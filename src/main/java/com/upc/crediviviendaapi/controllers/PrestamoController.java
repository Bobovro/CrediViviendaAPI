package com.upc.crediviviendaapi.controllers;

import com.upc.crediviviendaapi.dtos.PrestamoDTO;
import com.upc.crediviviendaapi.interfaces.IPrestamoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final IPrestamoService prestamoService;

    public PrestamoController(IPrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    // ✅ SIMULAR (NO guarda)
    @PostMapping("/simular")
    public PrestamoDTO simular(@RequestBody PrestamoDTO dto) {
        return prestamoService.simular(dto);
    }

    // ✅ CREAR (guarda préstamo + cronograma)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrestamoDTO crear(@RequestBody PrestamoDTO dto) {
        return prestamoService.crear(dto);
    }

    @GetMapping("/{id}")
    public PrestamoDTO obtener(@PathVariable Long id) {
        return prestamoService.obtenerPorId(id);
    }

    @GetMapping
    public List<PrestamoDTO> listar() {
        return prestamoService.listar();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        prestamoService.eliminar(id);
    }
}
