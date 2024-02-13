package org.project10.global;

import net.miginfocom.swing.MigLayout;
import org.project10.admin.MemberAddition;
import org.project10.admin.MemberInformation;
import org.project10.componentMaker.ButtonMaker;
import org.project10.componentMaker.LabelMaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class DashBoard extends JPanel implements ActionListener {
    JPanel topPanel;
    JLabel adminLabel;
    JButton profileBtn;
    JButton logoutBtn;
    JPanel sidePanel;
    JButton viewMembersBtn;
    JButton signUpBtn;
    JButton viewStoreBtn;
    static JPanel centerPanel;
    JPanel bottomPanel;
    public static JButton backBtn;
    JButton exitBtn;
    static String userType;
    private StoreStockUpdate storeStockUpdate;
    public DashBoard(String theUserType) {
        init(theUserType);
    }
    static boolean adminHasReachedDashboard = true;
    static boolean patronHasReachedDashboard;
    static boolean captainHasReachedDashboard;
    static boolean userHasReachedDashboard;

    private void init(String userType) {
        DashBoard.userType = userType;
        storeStockUpdate = new StoreStockUpdate(userType);
        System.out.println(userType);
        setLayout(new BorderLayout());
        setBackground(new Color(23, 211, 130));

        showTopPanel();
        showSidePanel();
        try {
            showCenterPanel();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        showBottomPanel();

        add(topPanel, BorderLayout.NORTH);
        add(sidePanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void showTopPanel() {
        topPanel = new JPanel(new MigLayout("align center"));
        topPanel.setPreferredSize(new Dimension(1208, 30));
        topPanel.setOpaque(false);
        String userTypeText = "";
        if(userType.equals("Admin")) {
            adminHasReachedDashboard = true;
            userTypeText = "Admin";
        } else if(userType.equals("Captain")) {
            captainHasReachedDashboard = true;
            userTypeText = "Captain";
        } else if(userType.equals("Patron")) {
            patronHasReachedDashboard = true;
            userTypeText = "Patron";
        } else if(userType.equals("User")) {
            userHasReachedDashboard = true;
            userTypeText = "User";
        }
        adminLabel = LabelMaker.createLabel(userTypeText, null);
        adminLabel.setForeground(Color.WHITE);
        adminLabel.setFont(new Font("FreeMono", Font.BOLD, 26));

        logoutBtn = ButtonMaker.createButton("Logout", null);
        logoutBtn.addActionListener(this);

        topPanel.add(adminLabel);
        topPanel.add(logoutBtn, "gap left 950");
    }

    private void showSidePanel() {
        sidePanel = new JPanel(new MigLayout("align center, wrap"));
        sidePanel.setOpaque(false);
        sidePanel.setPreferredSize(new Dimension(160, 728));

        profileBtn = ButtonMaker.createButton("Profile", null);
        profileBtn.setToolTipText("You will see your own profile");
        profileBtn.addActionListener(this);

        viewMembersBtn = ButtonMaker.createButton("View Members", null);
        viewMembersBtn.addActionListener(this);

        signUpBtn = ButtonMaker.createButton("Add Member", null);
        signUpBtn.addActionListener(this);

        viewStoreBtn = ButtonMaker.createButton("Store", null);
        viewStoreBtn.addActionListener(this);

        if (!adminHasReachedDashboard) {
            signUpBtn.setVisible(false);

            // Check user type and modify buttons accordingly
            if (userType.equals("Patron")) {
                signUpBtn.setVisible(false);
            } else if (userType.equals("Captain") || userType.equals("User")) {
                // Rename the button for captains and users
                viewMembersBtn.setText("View Team");
            }
        }

        JButton[] buttonGroup = {profileBtn, viewStoreBtn, viewMembersBtn, signUpBtn};
        for (JButton button : buttonGroup) {
            button.setPreferredSize(new Dimension(160, 80));
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBounds(button.getX(), button.getY(), 160, 110);
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBounds(button.getX(), button.getY(), 160, 80);
                }
            });

            sidePanel.add(button);
        }
    }


    private void showCenterPanel() throws SQLException {
        centerPanel = new JPanel(new CardLayout());
        centerPanel.setOpaque(false);

        centerPanel.add(new ProfileDashBoard(), "adminProfile");
        centerPanel.add(new MemberAddition(), "MemberAddition");
        centerPanel.add(new Store(), "store");
        centerPanel.add(new MemberInformation(), "viewMembers");
        centerPanel.add(new StoreStockUpdate(userType), "storeStockUpdate");


    }

    private void showBottomPanel() {
        bottomPanel = new JPanel(new MigLayout("align right"));
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(1208, 40));

        backBtn = new JButton("Back", null);
        backBtn.addActionListener(this);
        backBtn.setBackground(Color.CYAN);
        backBtn.setForeground(Color.BLACK);
        backBtn.setVisible(false);

        exitBtn = new JButton("exit", null);
        exitBtn.addActionListener(this);
        exitBtn.setBackground(Color.CYAN);
        exitBtn.setForeground(Color.BLACK);

        bottomPanel.add(backBtn);
        bottomPanel.add(exitBtn);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == exitBtn) {
            System.exit(0);
        } else if (actionEvent.getSource() == viewMembersBtn) {
            backBtn.setVisible(true);
            new MemberInformation();
            switchCenterPanel("viewMembers");

        } else if (actionEvent.getSource() == signUpBtn) {
            backBtn.setVisible(true);
            switchCenterPanel("MemberAddition");

        } else if (actionEvent.getSource() == profileBtn) {
            switchCenterPanel("adminProfile");

        } else if (actionEvent.getSource() == logoutBtn) {
            org.project10.global.Frame mainframe = (org.project10.global.Frame) SwingUtilities.getWindowAncestor(DashBoard.this);
            mainframe.switchPanel(new WelcomePage());

        }  else if (actionEvent.getSource() == viewStoreBtn) {
            backBtn.setVisible(true);
            switchCenterPanel("store");

        } else if (actionEvent.getSource() == backBtn) {
            switchCenterPanel("adminProfile");
            backBtn.setVisible(false);
        }
    }

    public static void switchCenterPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) centerPanel.getLayout();
        cardLayout.show(centerPanel, panelName);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    Image backgroundImage = new ImageIcon("src/main/java/org/projectimages/green-picture.jpg").getImage();
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}

