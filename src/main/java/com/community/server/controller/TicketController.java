package com.community.server.controller;

import com.community.server.body.TicketEndBody;
import com.community.server.body.TicketRetryBody;
import com.community.server.dto.TicketDTO;
import com.community.server.dto.TicketEndDTO;
import com.community.server.dto.TicketResultDTO;
import com.community.server.entity.TicketEntity;
import com.community.server.enums.TicketResultStatus;
import com.community.server.enums.TicketStatus;
import com.community.server.exception.BadRequestException;
import com.community.server.gson.Ticket;
import com.community.server.gson.TicketGson;
import com.community.server.gson.TicketGsonWithAnswers;
import com.community.server.mappers.TicketAnswersMapper;
import com.community.server.mappers.TicketMapper;
import com.community.server.repository.TicketRepository;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import com.community.server.utils.TicketUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private TicketAnswersMapper ticketAnswersMapper;

    @PostMapping("/start")
    public Object startTicket(HttpServletRequest httpServletRequest) throws IOException {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(httpServletRequest);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        if(!userRepository.existsById(userId)) {
            return new UsernameNotFoundException("User is not found!");
        }

        TicketEntity ticketEntity = ticketRepository.findFirstByUserIdOrderByIdDesc(userId).orElse(null);

        /*if(ticketEntity != null && ticketEntity.getTicketStatus() == TicketStatus.TICKET_BEGIN) {
            if(!ticketEntity.getTicketDateEnd().before(new Date()))
                return new ResponseEntity("You have an unfinished test!", HttpStatus.BAD_REQUEST);

            ticketEntity.setTicketStatus(TicketStatus.TICKET_END);
            ticketRepository.save(ticketEntity);
        }*/

        File[] tickets = new File("tickets").listFiles();
        File fileTicket = tickets[new Random().nextInt(tickets.length)];
        System.out.println(fileTicket);
        Ticket ticket = new TicketUtil().getTicket(fileTicket);

        ticketEntity = new TicketEntity(userId, fileTicket.getName());

        ticketRepository.save(ticketEntity);

        return new TicketResultDTO(
                ticketEntity.getUuid(),
                ticketEntity.getTicketDateStart(),
                ticketEntity.getTicketDateEnd(),
                ticketEntity.getAttempts(),
                ticketMapper.toDTO(ticket.getTicket())
        );
    }

    @PostMapping("/end")
    public Object endTicket(HttpServletRequest httpServletRequest, @Valid @RequestBody TicketEndBody ticketEndBody) throws IOException {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(httpServletRequest);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        if(!userRepository.existsById(userId)) {
            return new UsernameNotFoundException("User is not found!");
        }

        TicketEntity ticketEntity = ticketRepository.findByUuid(ticketEndBody.getUuid()).orElse(null);
        if(ticketEntity == null){
            return new ResponseEntity("Ticket is not found!", HttpStatus.BAD_REQUEST);
        }

        Ticket ticket = new TicketUtil().getTicket(new File("tickets/" + ticketEntity.getTicket()));
        TicketGsonWithAnswers[] ticketGsonWithAnswers = ticketAnswersMapper.toModel(ticket.getTicket());

        Long correctAnswers = 0L;
        for(int count = 0; count < 40; count++){

            ticketGsonWithAnswers[count].setAnswer(ticketEndBody.getAnswers().get(count));

            if(ticket.getTicket()[count].getCorrect() != ticketEndBody.getAnswers().get(count)){
                continue;
            }
            correctAnswers++;
        }

        ticketEntity.setCorrect(correctAnswers);
        ticketEntity.setTicketResultStatus(correctAnswers >= 32 ? TicketResultStatus.TICKET_PASSED : TicketResultStatus.TICKET_NOT_PASS);
        ticketEntity.setTicketStatus(TicketStatus.TICKET_END);

        ticketRepository.save(ticketEntity);
        return new TicketEndDTO(
                correctAnswers >= 32 ? TicketResultStatus.TICKET_PASSED.ordinal() : TicketResultStatus.TICKET_NOT_PASS.ordinal(),
                correctAnswers,
                ticketGsonWithAnswers);
    }

    @PutMapping("/retry")
    public Object retryTicket(HttpServletRequest httpServletRequest, @Valid @RequestBody TicketRetryBody ticketRetryBody) throws IOException {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(httpServletRequest);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        if(!userRepository.existsById(userId)) {
            return new UsernameNotFoundException("User is not found!");
        }

        TicketEntity ticketEntity = ticketRepository.findByUuid(ticketRetryBody.getUuid()).orElse(null);
        if(ticketEntity == null){
            return new ResponseEntity("Ticket is not found!", HttpStatus.BAD_REQUEST);
        }

        ticketEntity.setAttempts(ticketEntity.getAttempts() + 1);
        ticketEntity.setTicketDateStart(new Date());
        ticketEntity.setTicketDateEnd(new Date(new Date().getTime() + 40 * 60 * 1000));
        ticketEntity.setAnswers(null);

        Ticket ticket = new TicketUtil().getTicket(new File("tickets/" + ticketEntity.getTicket()));

        ticketRepository.save(ticketEntity);
        return new TicketResultDTO(
                ticketEntity.getUuid(),
                ticketEntity.getTicketDateStart(),
                ticketEntity.getTicketDateEnd(),
                ticketEntity.getAttempts(),
                ticketMapper.toDTO(ticket.getTicket())
        );
    }

    @GetMapping("/history")
    public Object getHistory(HttpServletRequest httpServletRequest){

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(httpServletRequest);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        if(!userRepository.existsById(userId)) {
            return new UsernameNotFoundException("User is not found!");
        }

        return ticketRepository.findByUserId(userId);
    }
}
