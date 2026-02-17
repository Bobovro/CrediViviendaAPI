package com.upc.crediviviendaapi.config;

import com.upc.crediviviendaapi.security.entities.Rol;
import com.upc.crediviviendaapi.security.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        if (!roleRepository.existsByNombre("ADMIN")) roleRepository.save(new Rol(null, "ADMIN"));
        if (!roleRepository.existsByNombre("USER")) roleRepository.save(new Rol(null, "USER"));
    }
}
