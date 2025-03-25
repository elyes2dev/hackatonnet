package com.esprit.pi.models;

import lombok.Setter;

public class LoginResponse {
    private String token;


    @Setter
    private long expiresIn;

    public LoginResponse setToken(String jwtToken) {
        this.token = jwtToken;
        return this;
    }

}
