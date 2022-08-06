package com.community.server.dto;

import com.community.server.gson.TicketAnswerGson;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class TicketDTO {

    private String text;
    private String photo;
    private TicketAnswerGson[] answers;

}
