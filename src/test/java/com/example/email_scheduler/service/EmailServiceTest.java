package com.example.email_scheduler.service;

import com.example.email_scheduler.model.entity.RecipientEntity;
import com.example.email_scheduler.dao.repository.RecipientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private RecipientRepository recipientRepository;

    private RecipientEntity recipientEntity1;
    private RecipientEntity recipientEntity2;
    private final String TEST_EMAIL_1 = "recipientEntity1@test.com";
    private final String TEST_EMAIL_2 = "recipientEntity2@test.com";

    @BeforeEach
    void setUp() {
        recipientEntity1 = new RecipientEntity();
        recipientEntity1.setEmail(TEST_EMAIL_1);
        recipientEntity1.setLastSendTime(null);

        recipientEntity2 = new RecipientEntity();
        recipientEntity2.setEmail(TEST_EMAIL_2);
        recipientEntity2.setLastSendTime(null);
    }

    @DisplayName("Адресаты отсутствуют, письма не должны быть отправлены")
    @Test
    void testSendEmailsWithNoRecipients() {
        when(recipientRepository.findAllByLastSendTimeBeforeOrLastSendTimeIsNull(any())).thenReturn(Collections.emptyList());
        emailService.sendEmails();
        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }

    @DisplayName("Адресаты присутствуют, письма должны быть отправлены")
    @Test
    void testSendEmailsWithRecipients() {
        List<RecipientEntity> recipientEntities = Arrays.asList(recipientEntity1, recipientEntity2);

        when(recipientRepository.findAllByLastSendTimeBeforeOrLastSendTimeIsNull(any())).thenReturn(recipientEntities);

        emailService.sendEmails();

        verify(recipientRepository, times(1)).findAllByLastSendTimeBeforeOrLastSendTimeIsNull(any());
        verify(javaMailSender, times(2)).send(any(SimpleMailMessage.class));
        verify(recipientRepository, times(1)).saveAll(recipientEntities);
    }


}