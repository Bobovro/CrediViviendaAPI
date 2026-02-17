package com.upc.crediviviendaapi.repositories;

import com.upc.crediviviendaapi.entities.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByEntidadIgnoreCase(String entidad, Pageable pageable);
    Page<AuditLog> findByAccionIgnoreCase(String accion, Pageable pageable);
    Page<AuditLog> findByUsernameIgnoreCase(String username, Pageable pageable);
}
