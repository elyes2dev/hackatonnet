package com.esprit.pi.dtos;

import com.esprit.pi.repositories.UserRepository;

public class LoginUserDto {
    UserRepository repo;

    private String email;

    private String password;


    public String getEmail() {
        return email;
    }

    public Object getPassword() {
        return password;
    }
}
