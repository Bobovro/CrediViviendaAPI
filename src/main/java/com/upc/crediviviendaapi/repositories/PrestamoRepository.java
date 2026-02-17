package com.upc.crediviviendaapi.repositories;

import com.upc.crediviviendaapi.entities.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    List<Prestamo> findByClienteId(Long clienteId);
}
