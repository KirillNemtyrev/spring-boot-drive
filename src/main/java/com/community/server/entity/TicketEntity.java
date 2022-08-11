package com.community.server.entity;

import com.community.server.enums.TicketResultStatus;
import com.community.server.enums.TicketStatus;
import lombok.Getter;
import lombok.Setter;
import net.bytebuddy.asm.Advice;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "tickets")
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid = UUID.randomUUID().toString();
    private Long userId;
    private Long correct = 0L;
    private Long attempts = 1L;
    @NotNull
    private String ticket;

    private ArrayList<Long> answers;

    private TicketStatus ticketStatus = TicketStatus.TICKET_BEGIN;
    private TicketResultStatus ticketResultStatus = TicketResultStatus.TICKET_NOT_END;

    private Date ticketDateStart = new Date();
    private Date ticketDateEnd = new Date(new Date().getTime() + 40 * 60 * 1000);

    public TicketEntity(){}
    public TicketEntity(Long userId, String ticket){
        this.userId = userId;
        this.ticket = ticket;
    }

}
