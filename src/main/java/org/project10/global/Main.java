package org.project10.global;

import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        SwingUtilities.invokeLater(() -> {
            new Frame();
            new Frame().setVisible(true);
        });
    }
}