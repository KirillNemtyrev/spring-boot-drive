package com.community.server.controller;

import com.community.server.body.PromocodeBody;
import com.community.server.entity.PromocodeEntity;
import com.community.server.entity.SubscribeEntity;
import com.community.server.entity.UserEntity;
import com.community.server.repository.PromocodeRepository;
import com.community.server.repository.SubscribeRepository;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/subscribe")
public class SubscribeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscribeRepository subscribeRepository;

    @Autowired
    private PromocodeRepository promocodeRepository;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public List<SubscribeEntity> getSubscribes(HttpServletRequest httpServletRequest){

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(httpServletRequest);
        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);

        if(!userRepository.existsById(userId)){
            return null;
        }

        return subscribeRepository.findByUserId(userId);
    }

    @GetMapping("/date")
    public Long getSubscribeDate(HttpServletRequest httpServletRequest) {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(httpServletRequest);
        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        return userEntity.getSubscribeEnd().getTime();
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateCode(HttpServletRequest httpServletRequest, @Valid @RequestBody PromocodeBody promocodeBody){

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(httpServletRequest);
        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        PromocodeEntity promocodeEntity = promocodeRepository.findByNameIgnoreCase(promocodeBody.getCode()).orElse(null);
        if(promocodeEntity == null){
            return new ResponseEntity("This code is not found!", HttpStatus.BAD_REQUEST);
        }

        if(subscribeRepository.existsByCodeAndUserId(promocodeBody.getCode(), userId)){
            return new ResponseEntity("You have already used this promo code!", HttpStatus.BAD_REQUEST);
        }

        if(promocodeEntity.getCount() <= 0){
            return new ResponseEntity("The number of uses of this promotional code has been used up!", HttpStatus.BAD_REQUEST);
        }

        promocodeEntity.setCount(promocodeEntity.getCount() - 1);
        userEntity.setSubscribeEnd(
                (userEntity.getSubscribeEnd() == null || !new Date().before(userEntity.getSubscribeEnd())) ?
                        new Date(new Date().getTime() + promocodeEntity.getValue()*86400000) :
                            new Date(userEntity.getSubscribeEnd().getTime() + promocodeEntity.getValue()*86400000));


        SubscribeEntity subscribeEntity = new SubscribeEntity();
        subscribeEntity.setCode(promocodeEntity.getName());
        subscribeEntity.setDate(new Date());
        subscribeEntity.setValue(promocodeEntity.getValue());
        subscribeEntity.setUserId(userId);

        promocodeRepository.save(promocodeEntity);
        subscribeRepository.save(subscribeEntity);
        userRepository.save(userEntity);

        return new ResponseEntity("This code is activated!", HttpStatus.OK);
    }

}
