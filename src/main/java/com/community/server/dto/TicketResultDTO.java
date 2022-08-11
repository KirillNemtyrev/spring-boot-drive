package com.community.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Data
@Getter
public class TicketResultDTO {

    private String uuid;
    private Date dateStart;
    private Date dateEnd;
    private Long attempts;

    private TicketDTO[] ticket;
}
