package com.example.demo.model.dto;

import lombok.Data;

@Data
public class UsuarioCreateDto {
    private String username;
    private String password;
    private String email;
}
