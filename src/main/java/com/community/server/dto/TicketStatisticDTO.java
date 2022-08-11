package com.community.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketStatisticDTO {

    private Long total = 0L;
    private Long resolved = 0L;
    private Long undelivered = 0L;
    private Long unresolved = 0L;
    private Float probability = 0F;
}
