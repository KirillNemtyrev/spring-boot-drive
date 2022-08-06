package com.community.server.gson;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketGsonWithAnswers {

    private String text;
    private String photo;
    private String explanation;
    private Long answer;
    private Long correct;
    private TicketAnswerGson[] answers;

}
