package com.upc.crediviviendaapi.controllers;

import com.upc.crediviviendaapi.dtos.AppConfigDTO;
import com.upc.crediviviendaapi.interfaces.IAppConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/config")
public class AppConfigController {

    private final IAppConfigService service;

    public AppConfigController(IAppConfigService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public AppConfigDTO get() {
        return service.getConfig();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public AppConfigDTO update(@RequestBody AppConfigDTO dto) {
        return service.updateConfig(dto);
    }
}
