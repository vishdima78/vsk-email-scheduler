package com.example.email_scheduler.controller;

import com.example.email_scheduler.model.dto.RecipientDTO;
import com.example.email_scheduler.service.RecipientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recipients")
@RequiredArgsConstructor
public class RecipientController {

    private static final Logger log = LoggerFactory.getLogger(RecipientController.class);
    private final RecipientService recipientService;

    @GetMapping
    @Operation(summary = "Получить список всех получателей с пагинацией")
    public List<RecipientDTO> getAllRecipients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return recipientService.getAllRecipients(page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить нового получателя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Получатель успешно добавлен"),
            @ApiResponse(responseCode = "400", description = "Ошибка при добавлении получателя")
    })
    public RecipientDTO addRecipient(@RequestBody RecipientDTO recipientDTO) {
        try {
            return recipientService.addRecipient(recipientDTO);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка при добавлении получателя");
        }
    }

    @PostMapping("/several")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить несколько получателей")
    public List<RecipientDTO> addRecipients(@RequestBody List<RecipientDTO> recipients) {
        return recipientService.addRecipients(recipients);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить получателя по ID")
    public void deleteRecipient(@PathVariable Long id) {
        recipientService.deleteRecipient(id);
    }

    @DeleteMapping("/several")
    @Operation(summary = "Удалить несколько получателей")
    public void deleteRecipients(@RequestBody List<Long> ids) {
        recipientService.deleteRecipients(ids);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить получателя по ID")
    public RecipientDTO updateRecipient(@PathVariable Long id, @RequestBody RecipientDTO recipientDTO) {
        return recipientService.updateRecipientOrThrow(id, recipientDTO);
    }
}