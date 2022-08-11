package com.community.server.repository;

import com.community.server.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
    Optional<TicketEntity> findFirstByUserIdOrderByIdDesc(Long id);
    Optional<TicketEntity> findByUuid(String uuid);

    List<TicketEntity> findByUserId(Long id);
}
