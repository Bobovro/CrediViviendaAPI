package com.upc.crediviviendaapi.controllers;

import com.upc.crediviviendaapi.dtos.AuditLogDTO;
import com.upc.crediviviendaapi.interfaces.IAuditService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/audit")
public class AuditAdminController {

    private final IAuditService auditService;

    public AuditAdminController(IAuditService auditService) {
        this.auditService = auditService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<AuditLogDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "fecha") String sort,
            @RequestParam(defaultValue = "desc") String dir,

            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) String username
    ) {
        return auditService.listar(page, size, sort, dir, entidad, accion, username);
    }
}
