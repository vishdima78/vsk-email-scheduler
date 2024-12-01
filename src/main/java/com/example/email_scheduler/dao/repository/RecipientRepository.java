package com.example.email_scheduler.dao.repository;

import com.example.email_scheduler.model.entity.RecipientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecipientRepository extends JpaRepository<RecipientEntity, Long> {
    List<RecipientEntity> findAllByLastSendTimeBeforeOrLastSendTimeIsNull(LocalDateTime thresholdTime);

    Page<RecipientEntity> findAll(Pageable pageable);
}