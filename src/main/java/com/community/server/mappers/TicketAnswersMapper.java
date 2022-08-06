package com.community.server.mappers;

import com.community.server.gson.TicketGson;
import com.community.server.gson.TicketGsonWithAnswers;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketAnswersMapper {

    TicketGson[] toDTO(TicketGsonWithAnswers[] ticketGsonWithAnswers);
    TicketGsonWithAnswers[] toModel(TicketGson[] ticketGsons);

}
