package com.community.server.mappers;

import com.community.server.dto.TicketDTO;
import com.community.server.gson.TicketGson;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    TicketDTO[] toDTO(TicketGson[] ticketGson);
    TicketGson[] toModel(TicketDTO[] ticketDTO);

}
