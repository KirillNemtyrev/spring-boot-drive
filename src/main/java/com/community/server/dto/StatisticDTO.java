package com.community.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticDTO {

    private String name;
    private String username;
    private String email;
    private String photo;

    private TicketStatisticDTO ticket;

}
