package com.spring;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.awt.Desktop;
import java.net.URI;

@Component
public class BrowserLauncher {

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowserWhenReady() {
        String url = "http://localhost:8085/";

        try {
            if (!Desktop.isDesktopSupported()) {
                System.out.println("Desktop API non supportée sur cette machine.");
                return;
            }

            Desktop desktop = Desktop.getDesktop();
            if (!desktop.isSupported(Desktop.Action.BROWSE)) {
                System.out.println("L'action BROWSE n'est pas supportée.");
                return;
            }

            desktop.browse(new URI(url));
            System.out.println(" Navigateur ouvert sur : " + url);
        } catch (Exception e) {
            System.out.println(" Impossible d'ouvrir le navigateur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
