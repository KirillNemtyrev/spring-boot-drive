package com.community.server.body;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class SettingsBody {

    private String username;
    private String name;
    private MultipartFile file;

}
