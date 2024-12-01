package com.example.email_scheduler.controller;

import com.example.email_scheduler.model.dto.RecipientDTO;
import com.example.email_scheduler.service.RecipientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)

class RecipientControllerTest {

    @InjectMocks
    private RecipientController recipientController;

    @Mock
    private RecipientService recipientService;

    private MockMvc mockMvc;

    private final Long RECIPIENT_ID_1 = 1L;
    private final Long RECIPIENT_ID_2 = 2L;
    private final String RECIPIENT_EMAIL_1 = "recipient1@test.com";
    private final String RECIPIENT_EMAIL_2 = "recipient2@test.com";
    private final RecipientDTO recipientDTO1 = new RecipientDTO(RECIPIENT_ID_1, RECIPIENT_EMAIL_1);
    private final RecipientDTO recipientDTO2 = new RecipientDTO(RECIPIENT_ID_2, RECIPIENT_EMAIL_2);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(recipientController).build();
    }

    @DisplayName("Получение всех адресатов с пагинацией")
    @Test
    void testGetAllRecipients() throws Exception {
        List<RecipientDTO> recipients = Arrays.asList(recipientDTO1, recipientDTO2);
        int page = 0;
        int size = 100;

        when(recipientService.getAllRecipients(page, size)).thenReturn(recipients);

        mockMvc.perform(get("/api/v1/recipients?page=" + page + "&size=" + size))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email", is(RECIPIENT_EMAIL_1)))
                .andExpect(jsonPath("$[1].email", is(RECIPIENT_EMAIL_2)));

        verify(recipientService, times(1)).getAllRecipients(page, size);
    }

    @DisplayName("Добавление нового адресата")
    @Test
    void testAddRecipient() throws Exception {
        when(recipientService.addRecipient(any(RecipientDTO.class))).thenReturn(recipientDTO1);

        mockMvc.perform(post("/api/v1/recipients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"email\": \"" + RECIPIENT_EMAIL_1 + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is(RECIPIENT_EMAIL_1)));

        verify(recipientService, times(1)).addRecipient(any(RecipientDTO.class));
    }

    @DisplayName("Ошибка при добавлении адресата с дублирующимся email")
    @Test
    void testAddRecipientWithDuplicateEmail() throws Exception {
        when(recipientService.addRecipient(any(RecipientDTO.class))).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(post("/api/v1/recipients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"email\": \"" + RECIPIENT_EMAIL_1 + "\"}"))
                .andExpect(status().isBadRequest());

        verify(recipientService, times(1)).addRecipient(any(RecipientDTO.class));
    }

    @DisplayName("Добавление нескольких адресатов")
    @Test
    void testAddRecipients() throws Exception {
        List<RecipientDTO> recipientDTOs = Arrays.asList(recipientDTO1, recipientDTO2);

        when(recipientService.addRecipients(anyList())).thenReturn(recipientDTOs);

        String jsonPayload = "[{\"id\": " + RECIPIENT_ID_1 + ", \"email\": \"" + RECIPIENT_EMAIL_1 + "\"}, " +
                "{\"id\": " + RECIPIENT_ID_2 + ", \"email\": \"" + RECIPIENT_EMAIL_2 + "\"}]";

        mockMvc.perform(post("/api/v1/recipients/several")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].email", is(RECIPIENT_EMAIL_1)))
                .andExpect(jsonPath("$[1].email", is(RECIPIENT_EMAIL_2)));

        verify(recipientService, times(1)).addRecipients(anyList());
    }

    @DisplayName("Удаление адресата по ID")
    @Test
    void testDeleteRecipient() throws Exception {
        mockMvc.perform(delete("/api/v1/recipients/{id}", RECIPIENT_ID_1))
                .andExpect(status().isOk());

        verify(recipientService, times(1)).deleteRecipient(RECIPIENT_ID_1);
    }

    @DisplayName("Удаление нескольких адресатов по ID")
    @Test
    void testDeleteRecipients() throws Exception {
        List<Long> recipientIds = Arrays.asList(RECIPIENT_ID_1, RECIPIENT_ID_2);

        String jsonPayload = "[1, 2]";

        mockMvc.perform(delete("/api/v1/recipients/several")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk());

        verify(recipientService, times(1)).deleteRecipients(argThat(ids -> ids.containsAll(recipientIds) && ids.size() == recipientIds.size()));
    }

    @DisplayName("Обновление адресата")
    @Test
    void testUpdateRecipient() throws Exception {
        when(recipientService.updateRecipientOrThrow(eq(RECIPIENT_ID_1), any(RecipientDTO.class))).thenReturn(recipientDTO1);

        mockMvc.perform(put("/api/v1/recipients/{id}", RECIPIENT_ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"email\": \"" + RECIPIENT_EMAIL_1 + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(RECIPIENT_EMAIL_1)));

        verify(recipientService, times(1)).updateRecipientOrThrow(eq(RECIPIENT_ID_1), any(RecipientDTO.class));
    }
}