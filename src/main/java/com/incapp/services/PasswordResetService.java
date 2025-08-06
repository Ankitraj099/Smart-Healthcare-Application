package com.incapp.services;

import com.incapp.models.PasswordResetToken;
import com.incapp.repo.PasswordResetTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Transactional
@Service
public class PasswordResetService {
    @Autowired
    private PasswordResetTokenRepo tokenRepo;
    @Autowired
    private JavaMailSender mailSender;

    public String createPasswordResetToken(String email) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setToken(token);
        resetToken.setExpiryDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60)); // 1 hour
        tokenRepo.save(resetToken);
        return token;
    }

    public void sendResetEmail(String email, String token, String userType) {
        String link = "http://localhost:7070/" + userType + "/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ankitraj09012004@gmail.com");
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("Click the following link to reset your password: " + link + "\nThis link will expire in 1 hour.");
        mailSender.send(message);
    }

    public PasswordResetToken getByToken(String token) {
        return tokenRepo.findByToken(token);
    }
    
    @Transactional
    public void deleteByToken(String token) {
        tokenRepo.deleteByToken(token);
    }
} 