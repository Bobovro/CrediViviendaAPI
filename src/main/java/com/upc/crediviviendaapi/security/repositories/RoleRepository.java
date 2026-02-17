package com.upc.crediviviendaapi.security.repositories;
import com.upc.crediviviendaapi.security.entities.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByNombre(String nombre);
    boolean existsByNombre(String nombre);

}
