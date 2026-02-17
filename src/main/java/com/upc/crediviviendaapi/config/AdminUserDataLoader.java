package com.upc.crediviviendaapi.config;

import com.upc.crediviviendaapi.entities.Usuario;
import com.upc.crediviviendaapi.repositories.UsuarioRepository;
import com.upc.crediviviendaapi.security.entities.Rol;
import com.upc.crediviviendaapi.security.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserDataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserDataLoader(
            UsuarioRepository usuarioRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (usuarioRepository.existsByUsername("admin")) return;

        Rol adminRole = roleRepository.findByNombre("ADMIN")
                .orElseGet(() -> roleRepository.save(new Rol(null, "ADMIN")));

        Usuario admin = new Usuario();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.getRoles().add(adminRole);

        usuarioRepository.save(admin);
    }
}