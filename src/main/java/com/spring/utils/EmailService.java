package com.spring.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void envoyerMail(String destinataire, String sujet, String contenu) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinataire);
        message.setSubject(sujet);
        message.setText(contenu);
        message.setFrom("cygestionRH@gmail.com");

        mailSender.send(message);

        System.out.println("📩 Mail envoyé à " + destinataire);
    }
}
