package com.community.server.repository;

import com.community.server.entity.PromocodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromocodeRepository extends JpaRepository<PromocodeEntity, Long> {

    Optional<PromocodeEntity> findByNameIgnoreCase(String name);

}
