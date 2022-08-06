package com.community.server.body;

import lombok.Getter;

import java.util.List;

@Getter
public class TicketEndBody {

    private String uuid;
    private List<Long> answers;

}
