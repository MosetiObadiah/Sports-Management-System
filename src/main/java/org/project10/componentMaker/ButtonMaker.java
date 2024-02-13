package org.project10.componentMaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ButtonMaker extends JButton {
    public static JButton createButton(String text, ImageIcon icon) {
        JButton button = new JButton(text);
        button.setIcon(icon);
        button.setPreferredSize(new Dimension(150, 30));
        button.setBackground(org.project10.global.ControlColors.getLightButtonForeground());
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setForeground(Color.WHITE);
        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                //System.out.println("mouse clicked");
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                //System.out.println("mouse pressed");
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                //System.out.println("mouse released");
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                button.setBackground(Color.CYAN);
                button.setForeground(Color.BLACK);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                button.setBackground(new Color(34, 0, 0, 90));
                button.setForeground(Color.white);
            }
        });

        return button;
    }
}
