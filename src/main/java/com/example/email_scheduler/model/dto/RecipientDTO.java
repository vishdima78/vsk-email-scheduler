package com.example.email_scheduler.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;

public record RecipientDTO(@JsonProperty(access = JsonProperty.Access.READ_ONLY) Long id,
                           @Email(message = "Некорректный формат, пример vishdima@gmail.com") String email) {}