package com.example.email_scheduler.service;

import com.example.email_scheduler.dao.RecipientDAO;
import com.example.email_scheduler.model.dto.RecipientDTO;
import com.example.email_scheduler.mapper.RecipientMapper;
import com.example.email_scheduler.model.entity.RecipientEntity;
import com.example.email_scheduler.dao.repository.RecipientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RecipientService {

    private final RecipientRepository recipientRepository;
    private final RecipientMapper recipientMapper;

    public List<RecipientDTO> getAllRecipients(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RecipientEntity> recipientPage = recipientRepository.findAll(pageable);
        return recipientPage.stream()
                .map(recipientMapper::toDto)
                .collect(Collectors.toList());
    }

    public RecipientDTO addRecipient(RecipientDTO recipientDTO) throws DataIntegrityViolationException {
        try {
            RecipientEntity recipientEntity = recipientMapper.toEntity(recipientDTO);
            RecipientEntity savedRecipientEntity = recipientRepository.save(recipientEntity);
            log.info("Добавлен новый адресат: {}", savedRecipientEntity.getEmail());
            return recipientMapper.toDto(savedRecipientEntity);
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка при добавлении получателя: {}", e.getMessage());
            throw e;
        }
    }

    public List<RecipientDTO> addRecipients(List<RecipientDTO> recipientDTOs) {
        List<RecipientEntity> recipientEntities = recipientDTOs.stream()
                .map(recipientMapper::toEntity)
                .collect(Collectors.toList());
        List<RecipientEntity> savedRecipientEntities = recipientRepository.saveAll(recipientEntities);
        log.info("Добавлены новые адресаты: {}", String.join(", ", savedRecipientEntities.stream()
                .map(RecipientEntity::getEmail).toList()));
        return savedRecipientEntities.stream()
                .map(recipientMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteRecipient(Long id) {
        recipientRepository.deleteById(id);
        log.info("Удален адресат с ID: {}", id);
    }

    public void deleteRecipients(List<Long> ids) {
        recipientRepository.deleteAllById(ids);
        log.info("Удалены адресаты с ID: {}", String.join(", ", ids.stream()
                .map(String::valueOf).toList()));
    }

    public RecipientDTO updateRecipientOrThrow(Long id, RecipientDTO recipientDTO) {
        if (!recipientRepository.existsById(id)) {
            log.error("Адресат не найден с ID: {}", id);
            throw new IllegalArgumentException("Адресат с ID: " + id + " не найден");
        }
        RecipientEntity recipientEntity = recipientMapper.toEntity(recipientDTO);
        recipientEntity.setId(id);
        RecipientEntity updatedRecipientEntity = recipientRepository.save(recipientEntity);
        log.info("Обновлен адресат с ID: {}", updatedRecipientEntity.getId());
        return recipientMapper.toDto(updatedRecipientEntity);
    }
}