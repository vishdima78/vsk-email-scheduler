package com.example.email_scheduler.controller;

import com.example.email_scheduler.model.dto.RecipientDTO;
import com.example.email_scheduler.service.EmailService;
import com.example.email_scheduler.service.RecipientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class RecipientViewControllerTest {

    private final RecipientService recipientService = mock(RecipientService.class);
    private final EmailService emailService = mock(EmailService.class);
    private final RecipientViewController recipientViewController = new RecipientViewController(recipientService);
    private final Model model = mock(Model.class);

    private static final Long RECIPIENT_ID_1 = 1L;
    private static final String RECIPIENT_EMAIL_1 = "recipient1@test.com";
    private static final RecipientDTO recipientDTO1 = new RecipientDTO(RECIPIENT_ID_1, RECIPIENT_EMAIL_1);

    @DisplayName("Получение списка адресатов с пагинацией")
    @Test
    void testListRecipients() {
        int page = 0;
        int size = 100;

        when(recipientService.getAllRecipients(page, size)).thenReturn(List.of(recipientDTO1));

        String viewName = recipientViewController.listRecipients(model, page, size);

        verify(model).addAttribute("recipients", List.of(recipientDTO1));
        verify(model).addAttribute("currentPage", page);
        verify(model).addAttribute("pageSize", size);

        assertEquals("recipients", viewName);
    }

    @DisplayName("Удаление адресата по ID")
    @Test
    void testDeleteRecipient() {
        String viewName = recipientViewController.deleteRecipient(RECIPIENT_ID_1);

        verify(recipientService, times(1)).deleteRecipient(RECIPIENT_ID_1);
        assertEquals("redirect:/recipients", viewName);
    }

    @DisplayName("Обновление адресата по ID")
    @Test
    void testUpdateRecipient() {
        String updatedEmail = "updated@test.com";
        RecipientDTO updatedRecipient = new RecipientDTO(RECIPIENT_ID_1, updatedEmail);

        when(recipientService.updateRecipientOrThrow(anyLong(), any(RecipientDTO.class))).thenReturn(updatedRecipient);
        String viewName = recipientViewController.updateRecipient(RECIPIENT_ID_1, updatedEmail, model);

        verify(recipientService).updateRecipientOrThrow(RECIPIENT_ID_1, updatedRecipient);
        assertEquals("redirect:/recipients", viewName);
    }

    @DisplayName("Обновление адресата с ошибкой")
    @Test
    void testUpdateRecipientWithError() {
        String invalidEmail = "invalid-email";
        RecipientDTO invalidRecipientDTO = new RecipientDTO(RECIPIENT_ID_1, invalidEmail);

        when(recipientService.updateRecipientOrThrow(anyLong(), any(RecipientDTO.class))).thenThrow(new IllegalArgumentException("Ошибка обновления"));

        String viewName = recipientViewController.updateRecipient(RECIPIENT_ID_1, invalidEmail, model);

        verify(model).addAttribute("error", "Ошибка обновления");
        assertEquals("recipients", viewName);
    }

    @DisplayName("Добавление нового адресата")
    @Test
    void testAddRecipient() {
        when(recipientService.addRecipient(any(RecipientDTO.class))).thenReturn(recipientDTO1);

        String viewName = recipientViewController.addRecipient(recipientDTO1, model);

        verify(recipientService).addRecipient(recipientDTO1);
        assertEquals("redirect:/recipients", viewName);
    }

    @DisplayName("Добавление нового адресата с ошибкой")
    @Test
    void testAddRecipientWithError() {
        when(recipientService.addRecipient(any(RecipientDTO.class))).thenThrow(new DataIntegrityViolationException("Ошибка"));

        String viewName = recipientViewController.addRecipient(recipientDTO1, model);

        verify(model).addAttribute("errorMessage", "Этот адрес электронной почты уже существует.");
        assertEquals("recipients", viewName);
    }
}