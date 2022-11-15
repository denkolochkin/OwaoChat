package com.clone.instagram.authservice.payload;

import lombok.Data;


@Data
public class LoginRequest {

    private String username;

    private String password;
}
