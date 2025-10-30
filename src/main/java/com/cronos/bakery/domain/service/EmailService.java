package com.cronos.bakery.domain.service;

import com.cronos.bakery.application.dto.notifications.LowStockItem;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.domain.entity.quote.Quote;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendPriceChangeNotification(User user, PriceChangeEmailData data) {
        try {
            Context context = new Context(getLocale(user.getDefaultLanguage()));
            context.setVariable("user", user);
            context.setVariable("data", data);

            String htmlContent = templateEngine.process("emails/price-change", context);

            sendEmail(
                    user.getEmail(),
                    getMessage("email.priceChange.subject", user.getDefaultLanguage()),
                    htmlContent
            );

        } catch (Exception e) {
            log.error("Error sending price change notification to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    @Async
    public void sendQuoteEmail(Quote quote, String recipientEmail, byte[] pdfAttachment) {
        try {
            User user = quote.getUser();
            Context context = new Context(getLocale(user.getDefaultLanguage()));
            context.setVariable("quote", quote);
            context.setVariable("user", user);

            String htmlContent = templateEngine.process("emails/quote", context);

            sendEmailWithAttachment(
                    recipientEmail,
                    getMessage("email.quote.subject", user.getDefaultLanguage()) + " - " + quote.getQuoteNumber(),
                    htmlContent,
                    "quote-" + quote.getQuoteNumber() + ".pdf",
                    pdfAttachment
            );

        } catch (Exception e) {
            log.error("Error sending quote email: {}", e.getMessage());
        }
    }

    @Async
    public void sendLowStockAlert(User user, List<LowStockItem> items) {
        try {
            Context context = new Context(getLocale(user.getDefaultLanguage()));
            context.setVariable("user", user);
            context.setVariable("items", items);

            String htmlContent = templateEngine.process("emails/low-stock-alert", context);

            sendEmail(
                    user.getEmail(),
                    getMessage("email.lowStock.subject", user.getDefaultLanguage()),
                    htmlContent
            );

        } catch (Exception e) {
            log.error("Error sending low stock alert: {}", e.getMessage());
        }
    }

    private void sendEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        helper.setFrom("noreply@bakery-cost.com");

        mailSender.send(message);
    }

    private void sendEmailWithAttachment(String to, String subject, String htmlContent, String attachmentName, byte[] attachment) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        helper.setFrom("noreply@bakery-cost.com");
        helper.addAttachment(attachmentName, () -> new ByteArrayInputStream(attachment), "application/pdf");

        mailSender.send(message);
    }

    private Locale getLocale(String language) {
        return "es".equals(language) ? Locale.of("es", "MX") : Locale.ENGLISH;
    }

    private String getMessage(String key, String language) {
        // Simple implementation - should use MessageSource
        if ("es".equals(language)) {
            return switch (key) {
                case "email.priceChange.subject" -> "Alerta: Cambio de precio en materia prima";
                case "email.quote.subject" -> "Cotización";
                case "email.lowStock.subject" -> "Alerta: Stock bajo";
                default -> key;
            };
        } else {
            return switch (key) {
                case "email.priceChange.subject" -> "Alert: Raw material price change";
                case "email.quote.subject" -> "Quote";
                case "email.lowStock.subject" -> "Alert: Low stock";
                default -> key;
            };
        }
    }
}
