package com.lcwd.electronic.store.services;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String text);
    void sendHtmlEmail(String to, String subject, String htmlContent);
    void sendEmailWithAttachment(String to, String subject, String text, String attachmentPath);
}