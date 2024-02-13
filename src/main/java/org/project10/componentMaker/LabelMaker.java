package org.project10.componentMaker;

import javax.swing.*;
import java.awt.*;

public class LabelMaker extends JLabel {
    public static JLabel createLabel(String text, ImageIcon icon) {
        JLabel label = new JLabel(text, icon, JLabel.CENTER);
        label.setForeground(new Color(8, 38, 32));
        label.setFont(new Font("Arial", Font.BOLD, 18));

        return label;
    }
}
