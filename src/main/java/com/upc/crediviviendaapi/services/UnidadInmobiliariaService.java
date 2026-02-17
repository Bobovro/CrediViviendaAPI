package com.upc.crediviviendaapi.services;

import com.upc.crediviviendaapi.dtos.UnidadInmobiliariaDTO;
import com.upc.crediviviendaapi.entities.UnidadInmobiliaria;
import com.upc.crediviviendaapi.exceptions.ResourceNotFoundException;
import com.upc.crediviviendaapi.interfaces.IAuditService;
import com.upc.crediviviendaapi.interfaces.IUnidadInmobiliariaService;
import com.upc.crediviviendaapi.repositories.UnidadInmobiliariaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UnidadInmobiliariaService implements IUnidadInmobiliariaService {

    private final UnidadInmobiliariaRepository repo;
    private final ModelMapper mapper;
    private final IAuditService audit; // ✅

    public UnidadInmobiliariaService(UnidadInmobiliariaRepository repo, ModelMapper mapper, IAuditService audit) {
        this.repo = repo;
        this.mapper = mapper;
        this.audit = audit;
    }

    @Override
    public UnidadInmobiliariaDTO crear(UnidadInmobiliariaDTO dto) {
        UnidadInmobiliaria entity = mapper.map(dto, UnidadInmobiliaria.class);
        entity.setId(null);
        UnidadInmobiliaria saved = repo.save(entity);

        audit.log(
                "CREATE",
                "UNIDAD_INMOBILIARIA",
                saved.getId(),
                "Se creó unidad: proyecto=" + saved.getProyecto()
                        + ", ubicacion=" + saved.getUbicacion()
                        + ", valorInmueble=" + saved.getValorInmueble()
                        + ", aplicaTechoPropio=" + saved.getAplicaTechoPropio()
                        + ", bonoTechoPropio=" + saved.getBonoTechoPropio()
        );

        return mapper.map(saved, UnidadInmobiliariaDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadInmobiliariaDTO obtenerPorId(Long id) {
        UnidadInmobiliaria entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad inmobiliaria no encontrada: id=" + id));
        return mapper.map(entity, UnidadInmobiliariaDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnidadInmobiliariaDTO> listar() {
        return repo.findAll()
                .stream()
                .map(u -> mapper.map(u, UnidadInmobiliariaDTO.class))
                .toList();
    }

    @Override
    public UnidadInmobiliariaDTO actualizar(Long id, UnidadInmobiliariaDTO dto) {
        UnidadInmobiliaria entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad inmobiliaria no encontrada: id=" + id));

        // snapshot antes (para auditoría)
        String before = "ANTES: proyecto=" + entity.getProyecto()
                + ", ubicacion=" + entity.getUbicacion()
                + ", valorInmueble=" + entity.getValorInmueble()
                + ", aplicaTechoPropio=" + entity.getAplicaTechoPropio()
                + ", bonoTechoPropio=" + entity.getBonoTechoPropio();

        entity.setProyecto(dto.getProyecto());
        entity.setUbicacion(dto.getUbicacion());
        entity.setValorInmueble(dto.getValorInmueble());
        entity.setAplicaTechoPropio(dto.getAplicaTechoPropio());
        entity.setBonoTechoPropio(dto.getBonoTechoPropio());

        UnidadInmobiliaria saved = repo.save(entity);

        String after = "DESPUÉS: proyecto=" + saved.getProyecto()
                + ", ubicacion=" + saved.getUbicacion()
                + ", valorInmueble=" + saved.getValorInmueble()
                + ", aplicaTechoPropio=" + saved.getAplicaTechoPropio()
                + ", bonoTechoPropio=" + saved.getBonoTechoPropio();

        audit.log("UPDATE", "UNIDAD_INMOBILIARIA", saved.getId(), before + " | " + after);

        return mapper.map(saved, UnidadInmobiliariaDTO.class);
    }

    @Override
    public void eliminar(Long id) {
        UnidadInmobiliaria entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad inmobiliaria no encontrada: id=" + id));

        audit.log(
                "DELETE",
                "UNIDAD_INMOBILIARIA",
                id,
                "Se eliminó unidad: proyecto=" + entity.getProyecto()
                        + ", ubicacion=" + entity.getUbicacion()
                        + ", valorInmueble=" + entity.getValorInmueble()
        );

        repo.delete(entity);
    }
}
