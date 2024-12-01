package com.example.email_scheduler.service;

import com.example.email_scheduler.dao.repository.RecipientRepository;
import com.example.email_scheduler.model.entity.RecipientEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final RecipientRepository recipientRepository;

    private static final int BATCH_SIZE = 20;

//    @Scheduled(cron = "*/15 * * * * *")
    @Scheduled(cron = "0 0 17 * * MON-FRI")
    @Transactional
    public void sendEmails() {
        LocalDateTime thresholdTime = LocalDateTime.now().minusDays(1);
        List<RecipientEntity> recipientEntities = recipientRepository.findAllByLastSendTimeBeforeOrLastSendTimeIsNull(thresholdTime);

        log.info("Найдено адресатов для отправки: {}", recipientEntities.stream()
                .map(RecipientEntity::getEmail)
                .collect(Collectors.toList()));

        int totalRecipients = recipientEntities.size();
        int sentCount = 0;

        for (int i = 0; i < totalRecipients; i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, totalRecipients);
            List<RecipientEntity> batch = recipientEntities.subList(i, end);

            log.info("Отправка писем для партии: {}", batch.stream()
                    .map(RecipientEntity::getEmail)
                    .collect(Collectors.toList()));

            for (RecipientEntity recipientEntity : batch) {
                sendEmail(recipientEntity);
                sentCount++;
            }

            recipientRepository.saveAll(batch);
        }

        log.info("Отправка по адресатам прошла успешно: {}", sentCount);
    }

    private void sendEmail(RecipientEntity recipient) {
        String email = recipient.getEmail().trim();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Напоминаю");
        message.setText("Пора пить чай!");
        mailSender.send(message);
        log.info("Письмо отправлено на адрес: {}", email);
        recipient.setLastSendTime(LocalDateTime.now());
    }
}