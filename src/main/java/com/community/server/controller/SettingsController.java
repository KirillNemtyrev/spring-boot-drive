package com.community.server.controller;

import com.community.server.body.SettingsBody;
import com.community.server.entity.UserEntity;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @PatchMapping("/username")
    public ResponseEntity<?> changeUsername(HttpServletRequest request, @Valid @RequestBody SettingsBody settingsBody){

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        if(!settingsBody.getUsername().matches("^[a-zA-Z0-9]+$")) {
            return new ResponseEntity("Invalid username!", HttpStatus.BAD_REQUEST);
        }

        userEntity.setUsername(settingsBody.getUsername());
        userRepository.save(userEntity);
        return new ResponseEntity("Username changed!", HttpStatus.OK);
    }

    @PatchMapping("/name")
    public ResponseEntity<?> changeName(HttpServletRequest request, @Valid @RequestBody SettingsBody settingsBody){

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        if(settingsBody.getName().length() < 6 && settingsBody.getName().length() > 40) {
            return new ResponseEntity("Invalid username!", HttpStatus.BAD_REQUEST);
        }

        userEntity.setName(settingsBody.getName());
        userRepository.save(userEntity);
        return new ResponseEntity("Name changed!", HttpStatus.OK);
    }

}
