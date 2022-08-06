package com.community.server.dto;

import com.community.server.enums.TicketResultStatus;
import com.community.server.gson.TicketGson;
import com.community.server.gson.TicketGsonWithAnswers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class TicketEndDTO {

    private int ticketResultStatus;
    private Long correct;

    private TicketGsonWithAnswers[] ticket;

}
