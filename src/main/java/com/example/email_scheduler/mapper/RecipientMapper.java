package com.example.email_scheduler.mapper;

import com.example.email_scheduler.model.dto.RecipientDTO;
import com.example.email_scheduler.model.entity.RecipientEntity;
import org.springframework.stereotype.Component;

@Component
public class RecipientMapper {

    public RecipientDTO toDto(RecipientEntity recipientEntity) {
        return new RecipientDTO(recipientEntity.getId(), recipientEntity.getEmail());
    }

    public RecipientEntity toEntity(RecipientDTO recipientDTO) {
        RecipientEntity recipientEntity = new RecipientEntity();
        recipientEntity.setId(recipientDTO.id());
        recipientEntity.setEmail(recipientDTO.email());
        return recipientEntity;
    }
}