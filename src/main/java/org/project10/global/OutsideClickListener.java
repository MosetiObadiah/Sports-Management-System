package org.project10.global;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//hides the login button if user clicks outside space of the login panel
class OutsideClickListener extends MouseAdapter {
    @Override
    public void mouseClicked(MouseEvent e) {
        if (WelcomePage.loginPanel.isVisible() && !WelcomePage.loginPanel.getBounds().contains(e.getPoint())) {
            WelcomePage.loginPanel.setVisible(false);
            WelcomePage.loginBtn.setVisible(true);
        }
    }
}
