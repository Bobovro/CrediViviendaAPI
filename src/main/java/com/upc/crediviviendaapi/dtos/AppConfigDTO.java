package com.upc.crediviviendaapi.dtos;

import com.upc.crediviviendaapi.enums.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppConfigDTO {
    private Moneda monedaDefault;
    private TipoTasa tipoTasaDefault;
    private Capitalizacion capitalizacion; // nullable si EFECTIVA
    private GraciaTipo graciaTipo;
    private Integer graciaPeriodos;
}
