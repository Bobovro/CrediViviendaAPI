package com.upc.crediviviendaapi.security.controllers;

import com.upc.crediviviendaapi.security.entities.Rol;
import com.upc.crediviviendaapi.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin(origins = "${ip.frontend}")
@CrossOrigin(origins = "${ip.frontend}", allowCredentials = "true", exposedHeaders = "Authorization") //para cloud
@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/rol/registrar")
    @PreAuthorize("hasRole('ADMIN')")
    public void createRol(@RequestBody Rol rol) {
           userService.grabar(rol);
    }
    @GetMapping("/rol/listar")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Rol> listarRol(){
        return userService.listarRol();
    }

    @PostMapping("/usuariosRoles/registrar/{user_id}/{rol_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Integer> saveUseRol(@PathVariable("user_id") Integer user_id, @PathVariable("rol_id") Integer rol_id){
        return new ResponseEntity<Integer>(userService.insertUserRol(user_id, rol_id), HttpStatus.OK);
        //return new ResponseEntity<Integer>(uService.insertUserRol2(user_id, rol_id),HttpStatus.OK);
    }
}
