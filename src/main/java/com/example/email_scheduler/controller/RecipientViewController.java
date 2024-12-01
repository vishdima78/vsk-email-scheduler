package com.example.email_scheduler.controller;

import com.example.email_scheduler.model.dto.RecipientDTO;
import com.example.email_scheduler.service.EmailService;
import com.example.email_scheduler.service.RecipientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@RequiredArgsConstructor
@Controller
@Slf4j
public class RecipientViewController {

    private final RecipientService recipientService;

    @GetMapping("/recipients")
    public String listRecipients(Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "100") int size) {
        model.addAttribute("recipients", recipientService.getAllRecipients(page, size));
        model.addAttribute("newRecipient", new RecipientDTO(null, ""));
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        return "recipients";
    }

    @PostMapping("/recipients/delete/{id}")
    public String deleteRecipient(@PathVariable Long id) {
        recipientService.deleteRecipient(id);
        return "redirect:/recipients";
    }

    @PostMapping("/recipients/update/{id}")
    public String updateRecipient(@PathVariable Long id, @RequestParam("email") String email, Model model) {
        RecipientDTO recipientDTO = new RecipientDTO(id, email);

        try {
            recipientService.updateRecipientOrThrow(id, recipientDTO);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "recipients";
        }

        return "redirect:/recipients";
    }

    @PostMapping("/recipients")
    public String addRecipient(@ModelAttribute RecipientDTO newRecipient, Model model) {
        try {
            recipientService.addRecipient(newRecipient);
            return "redirect:/recipients";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("errorMessage", "Этот адрес электронной почты уже существует.");
            model.addAttribute("recipients", recipientService.getAllRecipients(0, 100));
            return "recipients";
        }
    }
}