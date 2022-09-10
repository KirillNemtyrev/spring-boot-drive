package com.community.server.controller;

import com.community.server.body.SettingsBody;
import com.community.server.entity.UserEntity;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import com.community.server.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.Random;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${app.resetExpirationInMs}")
    private int resetExpirationInMs;

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    public MailService mailService;


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

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(HttpServletRequest request, @Valid @RequestBody SettingsBody settingsBody){

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        if(!passwordEncoder.matches(settingsBody.getOldPassword(), userEntity.getPassword())){
            return new ResponseEntity("Invalid password!", HttpStatus.BAD_REQUEST);
        }

        if(!settingsBody.getNewPassword().matches("(?=^.{6,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$"))
            return new ResponseEntity("Wrong password format!", HttpStatus.BAD_REQUEST);

        userEntity.setPassword(passwordEncoder.encode(settingsBody.getNewPassword()));
        userRepository.save(userEntity);
        return new ResponseEntity("Password changed!", HttpStatus.OK);
    }

    @PostMapping("/email")
    public ResponseEntity<?> sendCode(HttpServletRequest request){

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        userEntity.setEmailChangeCode(new Random().ints(48, 122)
                .filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
                .mapToObj(i -> (char) i)
                .limit(6)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString().toUpperCase());

        userEntity.setEmailChangeDate(new Date(new Date().getTime() + resetExpirationInMs));

        try {
            mailService.sendEmail(userEntity.getEmail(), "Смена почтового адреса", "Ваш код - " + userEntity.getEmailChangeCode());
        } catch (MessagingException e) {
            return new ResponseEntity("Unable to send message", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        userRepository.save(userEntity);
        return new ResponseEntity("A message with a code has been sent!", HttpStatus.OK);
    }

    @PatchMapping("/email")
    public ResponseEntity<?> changeEmail(HttpServletRequest request, @Valid @RequestBody SettingsBody settingsBody){

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        if(userEntity.getEmailChangeCode() == null || !userEntity.getEmailChangeCode().equalsIgnoreCase(settingsBody.getCode()))
            return new ResponseEntity("Invalid code entered!", HttpStatus.BAD_REQUEST);

        if(userEntity.getEmailChangeDate() == null || userEntity.getEmailChangeDate().before(new Date()))
            return new ResponseEntity("Code time is up!", HttpStatus.BAD_REQUEST);

        if(userRepository.existsByEmail(settingsBody.getNewEmail())){
            return new ResponseEntity("This email already!", HttpStatus.BAD_REQUEST);
        }

        userEntity.setEmail(settingsBody.getNewEmail());
        userRepository.save(userEntity);
        return new ResponseEntity("Your email changed!", HttpStatus.OK);
    }

}
