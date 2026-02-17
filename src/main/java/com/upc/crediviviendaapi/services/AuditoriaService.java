package com.upc.crediviviendaapi.services;

import com.upc.crediviviendaapi.entities.AuditoriaOperacion;
import com.upc.crediviviendaapi.interfaces.IAuditoriaService;
import com.upc.crediviviendaapi.repositories.AuditoriaOperacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuditoriaService implements IAuditoriaService {

    private final AuditoriaOperacionRepository repo;

    public AuditoriaService(AuditoriaOperacionRepository repo) {
        this.repo = repo;
    }

    @Override
    public void registrar(String accion, String entidad, Long entidadId, String detalle) {
        AuditoriaOperacion log = new AuditoriaOperacion();
        log.setAccion(accion);
        log.setEntidad(entidad);
        log.setFecha(LocalDateTime.now());
        log.setDetalle(detalle);
        // usuario lo llenamos cuando metamos security
        log.setUsuario("SYSTEM");

        repo.save(log);
    }
}