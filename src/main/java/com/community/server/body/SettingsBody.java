package com.community.server.body;

import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
public class SettingsBody {

    private String username;
    private String name;

    private String code;
    @Email
    private String newEmail;

    private String oldPassword;
    private String newPassword;

}
