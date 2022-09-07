package com.community.server.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class StatisticDTO {

    private String name;
    private String username;
    private String email;
    private String photo;
    private boolean subscribe;
    private Date subscribeEnd;

    private TicketStatisticDTO ticket;

}
