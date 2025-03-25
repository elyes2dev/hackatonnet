package com.esprit.pi.dtos;


public class RegisterUserDto {
    private String email;
    private String password;
    private String name;  // Fixed capitalization from "Name" to "name"

    public String getName() {
        return name;  // Fixed variable name
    }

    public String getEmail() {  // ✅ Change return type from Object to String
        return email;
    }

    public String getPassword() {  // ✅ Change return type from CharSequence to String
        return password;
    }
}

