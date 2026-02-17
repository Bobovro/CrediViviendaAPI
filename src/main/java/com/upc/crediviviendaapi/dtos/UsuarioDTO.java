package com.upc.crediviviendaapi.dtos;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioDTO {

    private Long id;

    private String nombres;
    private String apellidos;
    private String dni;

    private String email;
    private String telefono;

    private Double ingresoMensual;
    private Double gastoMensual;
}