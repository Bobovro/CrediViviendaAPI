package com.upc.crediviviendaapi.entities;

import com.upc.crediviviendaapi.enums.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppConfigEntity {

    @Id
    private Long id; // siempre 1

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Moneda monedaDefault;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTasa tipoTasaDefault;

    @Enumerated(EnumType.STRING)
    private Capitalizacion capitalizacion; // solo si NOMINAL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GraciaTipo graciaTipo;

    @Column(nullable = false)
    private Integer graciaPeriodos;
}
