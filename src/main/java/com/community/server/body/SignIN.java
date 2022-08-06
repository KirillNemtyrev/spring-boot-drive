package com.community.server.body;

import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
public class SignIN {

    private String usernameOrEmail;
    private String password;
}
