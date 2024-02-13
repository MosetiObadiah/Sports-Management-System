package org.project10.global;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class TransparentSubPanel extends JPanel {
    private Color backgroundColor;

    public TransparentSubPanel(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        setOpaque(false); // Make the panel itself transparent
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Enable antialiasing for smoother edges
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set transparency using AlphaComposite
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
        g2d.setComposite(alphaComposite);

        // Paint the background
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Optional: Draw a border (rounded rectangle in this case)
        int cornerRadius = 15;
        g2d.setColor(getForeground());
        g2d.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));

        // Call super.paintComponent(g) at the end
        super.paintComponent(g);
    }
}
