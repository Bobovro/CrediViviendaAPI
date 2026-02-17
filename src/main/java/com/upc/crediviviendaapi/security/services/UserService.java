package com.upc.crediviviendaapi.security.services;


import com.upc.crediviviendaapi.repositories.UsuarioRepository;
import com.upc.crediviviendaapi.security.entities.Rol;
import com.upc.crediviviendaapi.security.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UsuarioRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public void grabar(Rol role) {
        role.setCodigo(null);
        roleRepository.save(role);
    }
    public Integer insertUserRol(Integer user_id, Integer rol_id) {
        Integer result = 0;
        userRepository.insertUserRol(user_id, rol_id);
        return 1;
    }
    public List<Rol> listarRol(){
        return roleRepository.findAll();
    }

}
