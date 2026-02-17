package com.upc.crediviviendaapi.repositories;

import com.upc.crediviviendaapi.entities.Cuota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CuotaRepository extends JpaRepository<Cuota, Long> {
    List<Cuota> findByPrestamoIdOrderByNumeroCuota(Long prestamoId);
}
