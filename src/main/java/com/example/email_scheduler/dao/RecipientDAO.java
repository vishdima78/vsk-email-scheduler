package com.example.email_scheduler.dao;

import com.example.email_scheduler.model.entity.RecipientEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecipientDAO {

    private final JdbcTemplate jdbcTemplate;

    // Пример кастомного запроса
    public List<RecipientEntity> getAllRecipients() {
        String sql = "SELECT id, email FROM recipients";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(RecipientEntity.class));
    }

}
