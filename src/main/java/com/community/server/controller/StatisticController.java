package com.community.server.controller;

import com.community.server.dto.StatisticDTO;
import com.community.server.dto.TicketStatisticDTO;
import com.community.server.entity.TicketEntity;
import com.community.server.entity.UserEntity;
import com.community.server.enums.TicketResultStatus;
import com.community.server.enums.TicketStatus;
import com.community.server.repository.TicketRepository;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/statistic")
public class StatisticController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private TicketRepository ticketRepository;

    @GetMapping
    public Object getStatistic(HttpServletRequest httpServletRequest){

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(httpServletRequest);
        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));

        StatisticDTO statisticDTO = new StatisticDTO();
        statisticDTO.setName(userEntity.getName());
        statisticDTO.setEmail(userEntity.getEmail());
        statisticDTO.setUsername(userEntity.getUsername());
        statisticDTO.setPhoto(userEntity.getFileNameAvatar());

        if(userEntity.getSubscribeEnd() != null && !userEntity.getSubscribeEnd().before(new Date())) {

            statisticDTO.setSubscribeEnd(userEntity.getSubscribeEnd());
            statisticDTO.setSubscribe(true);

        }

        TicketStatisticDTO ticketStatisticDTO = new TicketStatisticDTO();

        List<TicketEntity> ticketEntity = ticketRepository.findByUserId(userId);
        for(TicketEntity ticket : ticketEntity){

            if(ticket.getTicketResultStatus() == TicketResultStatus.TICKET_PASSED)
                ticketStatisticDTO.setResolved(ticketStatisticDTO.getResolved() + 1);

            if(ticket.getTicketResultStatus() == TicketResultStatus.TICKET_NOT_PASS)
                ticketStatisticDTO.setUnresolved(ticketStatisticDTO.getUnresolved() + 1);

            if(ticket.getTicketResultStatus() == TicketResultStatus.TICKET_NOT_END)
                ticketStatisticDTO.setUndelivered(ticketStatisticDTO.getUndelivered() + 1);

            ticketStatisticDTO.setTotal(ticketStatisticDTO.getTotal() + 1);
        }


        if(ticketStatisticDTO.getResolved() + ticketStatisticDTO.getUnresolved() != 0)
            ticketStatisticDTO.setProbability((float) ticketStatisticDTO.getResolved() / (float) (ticketStatisticDTO.getTotal()) * 100);

        statisticDTO.setTicket(ticketStatisticDTO);
        return statisticDTO;
    }

}
