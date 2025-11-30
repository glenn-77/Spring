package com.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        // Autoriser l'API Desktop (désactiver le mode headless)
        System.setProperty("java.awt.headless", "false");

        SpringApplication.run(Application.class, args);
    }
}
