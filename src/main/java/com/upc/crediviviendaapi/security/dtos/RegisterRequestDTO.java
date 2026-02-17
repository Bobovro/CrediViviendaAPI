package com.upc.crediviviendaapi.security.dtos;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    private String dni;
    private String password;
}