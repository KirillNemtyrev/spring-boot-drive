package com.community.server.gson;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class TicketGson {

    private String text;
    private String photo;
    private String explanation;
    private Long correct;
    private TicketAnswerGson[] answers;

}
