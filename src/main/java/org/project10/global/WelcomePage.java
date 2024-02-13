package org.project10.global;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import net.miginfocom.swing.MigLayout;
import org.project10.componentMaker.ButtonMaker;
import org.project10.componentMaker.LabelMaker;
import org.project10.componentMaker.TextFieldMaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class WelcomePage extends JPanel implements ActionListener {
    JPanel topPanel;
    JToggleButton changeThemeBtn;
    JButton aboutBtn;
    JButton exploreBtn;
    static JButton loginBtn;
    JPanel leftPanel;
    JButton readmoreBtn;
    JPanel bottomPanel;
    JButton exitButton;
    static JPanel loginPanel;
    JTextField usernameTextField;
    JPasswordField passwordField;
    JComboBox<String> userTypesCombobox;
    JButton loginPanelLoginBtn;
    JButton backBtn;
    static String userTypeonLogin;
    public static String username4romdb;
    public WelcomePage() {
        init();
    }

    private void init() {
        addMouseListener(new OutsideClickListener());
        setLayout(new BorderLayout());
        setBackground(new Color(84, 100, 125));

        showTopPanel();
        showLeftPanel();
        showBottomPanel();
        showLoginPanel();

        add(leftPanel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);
        add(loginPanel, BorderLayout.EAST);
    }

    private void showTopPanel() {
        topPanel = new JPanel(new MigLayout());
        topPanel.setOpaque(false);
        topPanel.setPreferredSize(new Dimension(1208, 38));

        JLabel maringoLabel = LabelMaker.createLabel("Maringo", null);
        changeThemeBtn = new JToggleButton("theme");
        changeThemeBtn.addActionListener(this);
        exploreBtn = ButtonMaker.createButton("explore", null);
        aboutBtn = ButtonMaker.createButton("about", null);
        loginBtn = ButtonMaker.createButton("login", null);

        JButton[] buttonGroup = {exploreBtn, aboutBtn, loginBtn};
        for (JButton button : buttonGroup) {
            button.addActionListener(this);
        }

        topPanel.add(maringoLabel);
        topPanel.add(changeThemeBtn);
        topPanel.add(exploreBtn, "gap left " + topPanel.getPreferredSize().getWidth()*0.5);
        topPanel.add(aboutBtn, "gap left 20");
        topPanel.add(loginBtn, "gap left 20");
    }

    private void showLeftPanel() {
        leftPanel = new JPanel(new MigLayout("align center, wrap"));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(558, 438));

        JLabel label = new JLabel("SPORTS HUB");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        JLabel label2 = new JLabel("Unleash the beast within reach max potential");
        JLabel label3 = new JLabel("If you can play, then you can win");
        JLabel label4 = new JLabel("Beware the dogs, they will lick your sweat");

        readmoreBtn = new JButton("Read More");
        readmoreBtn.setBackground(Color.CYAN);
        readmoreBtn.setForeground(Color.BLACK);
        readmoreBtn.addActionListener(this);

        leftPanel.add(label);
        leftPanel.add(label2);
        leftPanel.add(label3);
        leftPanel.add(label4);
        leftPanel.add(readmoreBtn);
    }

    private void showBottomPanel() {
        bottomPanel = new JPanel(new MigLayout("align right"));
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(1208, 35));

        exitButton = new JButton("exit");
        exitButton.setForeground(Color.BLACK);
        exitButton.setBackground(Color.CYAN);
        exitButton.addActionListener(this);

        bottomPanel.add(exitButton);
    }

    private void showLoginPanel() {
        loginPanel = new JPanel(new MigLayout("align center "));
        loginPanel.setBackground(new Color(23, 157, 213, 100));
        loginPanel.setPreferredSize(new Dimension(420, 400));

        JLabel usernameLabel = LabelMaker.createLabel("username", null);
        JLabel passwordLabel = LabelMaker.createLabel("password", null);
        String[] userTypes = {"Admin", "Patron", "Captain", "User"};
        usernameTextField = TextFieldMaker.createTextField(16);
        usernameTextField.addActionListener(e -> {
            passwordField.requestFocusInWindow();
        });
        passwordField = new JPasswordField(16);
        passwordField.addActionListener(e -> {
            loginPanelLoginBtn.doClick();
        });
        userTypesCombobox = new JComboBox<>(userTypes);

        loginPanelLoginBtn = ButtonMaker.createButton("login", null);
        loginPanelLoginBtn.addActionListener(this);
        backBtn = ButtonMaker.createButton("back", null);
        backBtn.addActionListener(this);

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameTextField, "wrap");
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField, "wrap");
        loginPanel.add(userTypesCombobox, "wrap");
        loginPanel.add(loginPanelLoginBtn);
        loginPanel.add(backBtn);
        loginPanel.setVisible(false);

        userTypeonLogin = (String) userTypesCombobox.getSelectedItem();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == changeThemeBtn) {
            //debug
            System.out.println("change theme");

            UIManager.removeAuxiliaryLookAndFeel(new FlatLightLaf());
            try {
                UIManager.setLookAndFeel(new FlatMacDarkLaf());
            } catch (UnsupportedLookAndFeelException e) {
                throw new RuntimeException(e);
            }
            SwingUtilities.updateComponentTreeUI(this);
        }
        if (actionEvent.getSource() == aboutBtn) {
            showJOptionPaneWithTimer("Maringo Sports is a system that helps you manage your sports team", "About", JOptionPane.INFORMATION_MESSAGE, 3);
        } else if (actionEvent.getSource() == exploreBtn) {
            showJOptionPaneWithTimer("To whom much has been given much is required", "Explore", JOptionPane.INFORMATION_MESSAGE, 3);
        } else if (actionEvent.getSource() == loginBtn) {
            loginBtn.setVisible(false);
            loginPanel.setVisible(true);
            usernameTextField.requestFocusInWindow();

        } else if (actionEvent.getSource() == readmoreBtn) {
            showJOptionPaneWithTimer("You Know there is more to this app other than the welcome page :(", "Read More", JOptionPane.INFORMATION_MESSAGE, 3);
        } else if (actionEvent.getSource() == exitButton) {
            System.exit(0);
        } else if (actionEvent.getSource() == loginPanelLoginBtn) {
            userTypeonLogin = (String) userTypesCombobox.getSelectedItem();
            try {
                gotoWhichUser();
            } catch (ClassNotFoundException | SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException(e);
            }
        }else if (actionEvent.getSource() == backBtn) {
            loginPanel.setVisible(false);
            loginBtn.setVisible(true);
        }
    }
    private void gotoWhichUser() throws ClassNotFoundException, SQLException {
        Statement statement;
        Connection connection = null;
        ResultSet rs = null;
        boolean userFound = false;

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/maringodb", "moseti", "tajiri01");

            String query = "SELECT username, password, accountType FROM MembersTable";
            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            while (rs.next()) {
                username4romdb = rs.getString("username");
                String password4romdb = rs.getString("password");
                String accountType4romdb = rs.getString("accountType");

                String enterpassword = new String(passwordField.getPassword());

                if (usernameTextField.getText().equals(username4romdb) && enterpassword.equals(password4romdb)) {
                    userFound = true;
                    //debug
                    System.out.println("Entered Username: " + usernameTextField.getText() + " from db: " + username4romdb);
                    System.out.println("Entered Password: " + new String(passwordField.getPassword()) + " from db: " + password4romdb);
                    System.out.println("Entered User Type: " + userTypeonLogin + " from db: " + accountType4romdb );

                    if (userTypeonLogin.equals(accountType4romdb)) {
                        Frame mainframe = (Frame) SwingUtilities.getWindowAncestor(WelcomePage.this);
                        switch (userTypeonLogin) {
                            case "Admin" -> {
                                mainframe.switchPanel(new DashBoard());
                            }
                            case "Captain", "User" -> {
                                //mainframe.switchPanel(new DashBoardUser(userTypeonLogin));
                            }
                            case "Patron" -> {
                                //mainframe.switchPanel(new DashBoardPatron());
                            }
                        }
                        break;
                    }
                }
            }
            if (!userFound) {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public static String getUserNameonLogin() {
        return username4romdb;
    }

    public static void showJOptionPaneWithTimer(String message, String title, int messageType, int delayInSeconds) {
        final JOptionPane optionPane = new JOptionPane(message, messageType);
        final JDialog dialog = optionPane.createDialog(title);

        Timer timer = new Timer(delayInSeconds * 1000, e -> {
            dialog.setVisible(false);
            dialog.dispose();
        });
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }

    Image backgroundImage = new ImageIcon(("src/main/java/org/maringo/images/welcomeBackground.png")).getImage();
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
