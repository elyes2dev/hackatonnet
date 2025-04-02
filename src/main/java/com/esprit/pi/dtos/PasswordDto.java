package com.esprit.pi.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordDto {

    private String oldPassword;

    private  String token;

    private String newPassword;
}

