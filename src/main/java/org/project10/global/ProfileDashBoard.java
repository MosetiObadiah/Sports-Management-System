package org.project10.global;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ProfileDashBoard extends JPanel {
    public ProfileDashBoard() { init(); }

    private void init() {
        setLayout(new MigLayout());
        setOpaque(false);
        try {
            BasicDetails basicDetails = new BasicDetails();
            basicDetails.setPreferredSize(new Dimension(400, 698));
            add(basicDetails, "alignx left, aligny top");

            AllDetails allDetails = new AllDetails();
            allDetails.setPreferredSize(new Dimension(658, 698));
            add(allDetails, "grow");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
