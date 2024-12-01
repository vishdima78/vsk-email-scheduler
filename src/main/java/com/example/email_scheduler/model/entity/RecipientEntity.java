package com.example.email_scheduler.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name = "recipients")
public class RecipientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Некорректный формат, пример vishdima@gmail.com")
    @Column(nullable = false, unique = true)
    private String email;

    private LocalDateTime lastSendTime;
}
