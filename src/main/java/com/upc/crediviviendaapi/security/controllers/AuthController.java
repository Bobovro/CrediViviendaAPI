package com.upc.crediviviendaapi.security.controllers;

import com.upc.crediviviendaapi.entities.Usuario;
import com.upc.crediviviendaapi.repositories.UsuarioRepository;
import com.upc.crediviviendaapi.security.dtos.AuthRequestDTO;
import com.upc.crediviviendaapi.security.dtos.AuthResponseDTO;
import com.upc.crediviviendaapi.security.dtos.RegisterRequestDTO;
import com.upc.crediviviendaapi.security.entities.Rol;
import com.upc.crediviviendaapi.security.repositories.RoleRepository;
import com.upc.crediviviendaapi.security.services.CustomUserDetailsService;
import com.upc.crediviviendaapi.security.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "${ip.frontend}", allowCredentials = "true", exposedHeaders = "Authorization")
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    // ✅ nuevos
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          CustomUserDetailsService userDetailsService,
                          UsuarioRepository usuarioRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ REGISTER (público)
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO dto) {

        // 1) Validaciones mínimas
        if (dto.getDni() == null || dto.getDni().isBlank()) {
            return ResponseEntity.badRequest().body("DNI es obligatorio");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Password es obligatorio");
        }

        // 2) Crear usuario (login por DNI)
        Usuario u = new Usuario();
        u.setUsername(dto.getDni()); // ✅ se loguea con DNI
        u.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Campos de cliente (si son NOT NULL en tu tabla
        // Si tu entidad tiene ingreso/gasto NOT NULL, pon defaults
        // u.setIngresoMensual(0.0);
        // u.setGastoMensual(0.0);

        Usuario saved = usuarioRepository.save(u);

        // 3) Asignar rol USER por defecto
        Rol userRole = roleRepository.findByNombre("USER")
                .orElseThrow(() -> new RuntimeException("Rol USER no existe. Revisa DataLoader"));

        // ✅ usando tu tabla puente (usuarios_roles)
        // Ajusta getCodigo()/getId según tu entidad
        saved.getRoles().add(userRole);
        usuarioRepository.save(saved);



        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado correctamente");
    }

    // ✅ LOGIN (ya lo tenías)
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDTO> createAuthenticationToken(@RequestBody AuthRequestDTO authRequest) throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String token = jwtUtil.generateToken(userDetails);

        Set<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Authorization", token);

        AuthResponseDTO authResponseDTO = new AuthResponseDTO();
        authResponseDTO.setRoles(roles);
        authResponseDTO.setJwt(token);

        return ResponseEntity.ok().headers(responseHeaders).body(authResponseDTO);
    }
}
