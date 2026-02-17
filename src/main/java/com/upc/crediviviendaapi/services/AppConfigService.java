package com.upc.crediviviendaapi.services;

import com.upc.crediviviendaapi.dtos.AppConfigDTO;
import com.upc.crediviviendaapi.entities.AppConfigEntity;
import com.upc.crediviviendaapi.enums.*;
import com.upc.crediviviendaapi.interfaces.IAppConfigService;
import com.upc.crediviviendaapi.repositories.AppConfigRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppConfigService implements IAppConfigService {

    private static final long CONFIG_ID = 1L;

    private final AppConfigRepository repo;
    private final ModelMapper mapper;

    public AppConfigService(AppConfigRepository repo, ModelMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    private AppConfigEntity ensureExists() {
        return repo.findById(CONFIG_ID).orElseGet(() -> {
            AppConfigEntity def = AppConfigEntity.builder()
                    .id(CONFIG_ID)
                    .monedaDefault(Moneda.PEN)
                    .tipoTasaDefault(TipoTasa.EFECTIVA)
                    .capitalizacion(null) // solo si NOMINAL
                    .graciaTipo(GraciaTipo.NINGUNA)
                    .graciaPeriodos(0)
                    .build();
            return repo.save(def);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public AppConfigDTO getConfig() {
        AppConfigEntity entity = ensureExists();
        return mapper.map(entity, AppConfigDTO.class);
    }

    @Override
    public AppConfigDTO updateConfig(AppConfigDTO dto) {
        AppConfigEntity entity = ensureExists();

        entity.setMonedaDefault(dto.getMonedaDefault());
        entity.setTipoTasaDefault(dto.getTipoTasaDefault());
        entity.setGraciaTipo(dto.getGraciaTipo());
        entity.setGraciaPeriodos(dto.getGraciaPeriodos() == null ? 0 : dto.getGraciaPeriodos());

        // regla: capitalizacion solo si tipoTasaDefault = NOMINAL
        if (dto.getTipoTasaDefault() == TipoTasa.NOMINAL) {
            entity.setCapitalizacion(dto.getCapitalizacion());
        } else {
            entity.setCapitalizacion(null);
        }

        AppConfigEntity saved = repo.save(entity);
        return mapper.map(saved, AppConfigDTO.class);
    }
}
