package com.example.email_scheduler.service;

import com.example.email_scheduler.model.dto.RecipientDTO;
import com.example.email_scheduler.mapper.RecipientMapper;
import com.example.email_scheduler.model.entity.RecipientEntity;
import com.example.email_scheduler.dao.repository.RecipientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipientServiceTest {

    @InjectMocks
    private RecipientService recipientService;

    @Mock
    private RecipientRepository recipientRepository;

    @Mock
    private RecipientMapper recipientMapper;

    private RecipientEntity recipientEntity;
    private RecipientDTO recipientDTO;

    private final Long RECIPIENT_ID = 1L;
    private final String RECIPIENT_EMAIL = "recipientEntity@test.com";

    @BeforeEach
    void setUp() {
        recipientEntity = new RecipientEntity();
        recipientEntity.setId(RECIPIENT_ID);
        recipientEntity.setEmail(RECIPIENT_EMAIL);

        recipientDTO = new RecipientDTO(RECIPIENT_ID, RECIPIENT_EMAIL);
    }

    @DisplayName("Получение списка всех получателей с пагинацией")
    @Test
    void testGetAllRecipients() {
        int page = 0;
        int size = 100;

        Page<RecipientEntity> pageResponse = new PageImpl<>(Collections.singletonList(recipientEntity));
        when(recipientRepository.findAll(PageRequest.of(page, size))).thenReturn(pageResponse);
        when(recipientMapper.toDto(any(RecipientEntity.class))).thenReturn(recipientDTO);

        List<RecipientDTO> result = recipientService.getAllRecipients(page, size);

        assertEquals(1, result.size());
        assertEquals(RECIPIENT_EMAIL, result.get(0).email());
        verify(recipientRepository, times(1)).findAll(PageRequest.of(page, size));
        verify(recipientMapper, times(1)).toDto(any(RecipientEntity.class));
    }

    @DisplayName("Добавление нового получателя")
    @Test
    void testAddRecipient() {
        when(recipientMapper.toEntity(any(RecipientDTO.class))).thenReturn(recipientEntity);
        when(recipientRepository.save(any(RecipientEntity.class))).thenReturn(recipientEntity);
        when(recipientMapper.toDto(any(RecipientEntity.class))).thenReturn(recipientDTO);

        RecipientDTO result = recipientService.addRecipient(recipientDTO);

        assertNotNull(result);
        assertEquals(RECIPIENT_EMAIL, result.email());
        verify(recipientRepository, times(1)).save(any(RecipientEntity.class));
    }

    @DisplayName("Добавление нескольких получателей")
    @Test
    void testAddRecipients() {
        List<RecipientDTO> recipientDTOs = Arrays.asList(recipientDTO);
        List<RecipientEntity> recipientEntities = Arrays.asList(recipientEntity);

        when(recipientMapper.toEntity(any(RecipientDTO.class))).thenReturn(recipientEntity);
        when(recipientRepository.saveAll(anyList())).thenReturn(recipientEntities);
        when(recipientMapper.toDto(any(RecipientEntity.class))).thenReturn(recipientDTO);

        List<RecipientDTO> result = recipientService.addRecipients(recipientDTOs);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(RECIPIENT_EMAIL, result.get(0).email());
        verify(recipientMapper, times(1)).toEntity(any(RecipientDTO.class));
        verify(recipientRepository, times(1)).saveAll(anyList());
        verify(recipientMapper, times(1)).toDto(any(RecipientEntity.class));
    }

    @DisplayName("Ошибка при добавлении дублирующегося получателя")
    @Test
    void testAddRecipientWithDuplicateEmail() {
        when(recipientMapper.toEntity(any(RecipientDTO.class))).thenReturn(recipientEntity);
        when(recipientRepository.save(any(RecipientEntity.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DataIntegrityViolationException.class, () -> recipientService.addRecipient(recipientDTO));

        verify(recipientRepository, times(1)).save(any(RecipientEntity.class));
    }

    @DisplayName("Обновление существующего получателя")
    @Test
    void testUpdateRecipientOrThrow() {
        when(recipientRepository.existsById(RECIPIENT_ID)).thenReturn(true);
        when(recipientMapper.toEntity(any(RecipientDTO.class))).thenReturn(recipientEntity);
        when(recipientRepository.save(any(RecipientEntity.class))).thenReturn(recipientEntity);
        when(recipientMapper.toDto(any(RecipientEntity.class))).thenReturn(recipientDTO);

        RecipientDTO result = recipientService.updateRecipientOrThrow(RECIPIENT_ID, recipientDTO);

        assertNotNull(result);
        assertEquals(RECIPIENT_EMAIL, result.email());
        verify(recipientRepository, times(1)).save(any(RecipientEntity.class));
    }

    @DisplayName("Ошибка при обновлении несуществующего получателя")
    @Test
    void testUpdateRecipientOrThrowWithNonexistentID() {
        when(recipientRepository.existsById(RECIPIENT_ID)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            recipientService.updateRecipientOrThrow(RECIPIENT_ID, recipientDTO);
        });

        assertEquals("Адресат с ID: " + RECIPIENT_ID + " не найден", exception.getMessage());
        verify(recipientRepository, never()).save(any(RecipientEntity.class));
    }

    @DisplayName("Удаление получателя по ID")
    @Test
    void testDeleteRecipient() {
        doNothing().when(recipientRepository).deleteById(RECIPIENT_ID);

        recipientService.deleteRecipient(RECIPIENT_ID);

        verify(recipientRepository, times(1)).deleteById(RECIPIENT_ID);
    }

    @DisplayName("Удаление нескольких получателей по ID")
    @Test
    void testDeleteRecipients() {
        List<Long> ids = Arrays.asList(RECIPIENT_ID);

        doNothing().when(recipientRepository).deleteAllById(ids);

        recipientService.deleteRecipients(ids);

        verify(recipientRepository, times(1)).deleteAllById(ids);
    }
}
