package com.upc.crediviviendaapi.interfaces;

import com.upc.crediviviendaapi.dtos.AuditLogDTO;
import org.springframework.data.domain.Page;

public interface IAuditService {

    // registrar evento
    void log(String accion, String entidad, Long entidadId, String detalle);

    // listar auditoría (admin)
    Page<AuditLogDTO> listar(int page, int size, String sort, String dir,
                             String entidad, String accion, String username);
}
