package com.community.server.repository;

import com.community.server.entity.SubscribeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscribeRepository extends JpaRepository<SubscribeEntity, Long> {

    List<SubscribeEntity> findByUserId(Long id);
    Boolean existsByCodeAndUserId(String code, Long userId);

}
