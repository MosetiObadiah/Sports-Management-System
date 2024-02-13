package org.project10.global;

import net.miginfocom.swing.MigLayout;
import org.project10.componentMaker.LabelMaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class BasicDetails extends TransparentSubPanel implements ActionListener  {
    JButton changeProfilePic;
    JButton changeUsernameBtn;
    JButton changePasswordBtn;
    public BasicDetails() throws SQLException {
        super(new Color(207, 216, 211, 80)); // somehow transparent? :)
        init();
    }
    public static String currentUserName = org.project10.global.WelcomePage.username4romdb;
    JLabel adminProfilePic;
    JLabel adminNameLabel;
    JLabel adminUsernameLabel;
    JLabel adminPassword;
    JLabel accountType4rombd;
    Statement statement;
    Connection connection;
    ResultSet rs;
    private void init() {
        //TODO hiding the back button since there is no back action possible
        setLayout(new MigLayout("align center, wrap"));
        //TODO find better transparency effect
        //debug
        System.out.println("profile admin accessed");

        loadUserDataFromTheDatabase();

        //TODO not yet implemented profile pic changer
        changeProfilePic = new JButton("Alter profPic", null);
        changeUsernameBtn = new JButton("Alter Username", null);
        changePasswordBtn = new JButton("Alter Password", null);

        JButton[] buttonGroup = {changeProfilePic, changeUsernameBtn, changePasswordBtn};
        for (JButton button : buttonGroup) {
            button.setPreferredSize(new Dimension(150,25));
            button.setBackground(Color.CYAN);
            button.setForeground(Color.BLACK);
            button.addActionListener(this);
        }

        //TODO put spaces between the rows
        add(adminProfilePic, "gap left 80");
        add(adminNameLabel);
        add(adminUsernameLabel);
        add(adminPassword);
        add(accountType4rombd);
        add(changeUsernameBtn);
        add(changePasswordBtn);
        add(changeProfilePic);

    }

    private void loadUserDataFromTheDatabase() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/project10", "moseti", "tajiri01");

            String query = "SELECT fname, lname, username, password, accountType FROM MembersTable WHERE username='"+ currentUserName +"';";
            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            String fname4romdb = null;
            String username4romdb = null;
            String password4romdb = null;
            String lname4romdb = null;
            String accountType4romdb = null;

            while (rs.next()) {
                fname4romdb = rs.getString("fname");
                lname4romdb = rs.getString("lname");
                username4romdb = rs.getString("username");
                password4romdb = rs.getString("password");
                accountType4romdb = rs.getString("accountType");
            }

            //TODO add profile pic
            ImageIcon profilePicture = new ImageIcon("src/main/java/org/project10/images/adminProfilePic.png");
            profilePicture.setImage(profilePicture.getImage().getScaledInstance(100, 80, Image.SCALE_SMOOTH));
            adminProfilePic = LabelMaker.createLabel(null, profilePicture);

            //spacing is for things to look nice :)
            adminNameLabel = LabelMaker.createLabel("Full Name: " + "       "  + fname4romdb + " " + lname4romdb, null);
            adminUsernameLabel = LabelMaker.createLabel("Username: "  + "       " + username4romdb, null);
            adminPassword = LabelMaker.createLabel("Password: "  + "       " + password4romdb, null);
            accountType4rombd = LabelMaker.createLabel("account Type: "  + " " + accountType4romdb, null);

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //making it global so that I can fix the change password issue
    String newUsername;
    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        if (actionEvent.getSource() == changeUsernameBtn) {
            newUsername = JOptionPane.showInputDialog(null, "Enter desired username");
            if(newUsername.isEmpty()) {
                org.project10.global.WelcomePage.showJOptionPaneWithTimer("Username is empty!! Try again", "ERROR!", JOptionPane.INFORMATION_MESSAGE, 1);
                changeUsernameBtn.doClick();
            }

            String usernameQuery = "update MembersTable set username='"+newUsername + "' where username='"+ currentUserName + "';";
            //update the admin username
            currentUserName = newUsername;
            try {
                statement.executeQuery(usernameQuery);
                org.project10.global.WelcomePage.showJOptionPaneWithTimer("Info updated successfully", "success", JOptionPane.INFORMATION_MESSAGE, 3);
                refreshLabels();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                throw new RuntimeException(e);
            }

        //TODO fix the error where the program is unable to change the password because the username was changed hence the program cannot see the "WHERE"...
        } else if (actionEvent.getSource() == changePasswordBtn) {

            String newUserPassword = JOptionPane.showInputDialog(null, "Enter desired password");
            if(newUserPassword.isEmpty()) {
                org.project10.global.WelcomePage.showJOptionPaneWithTimer("Password is empty!! Try again", "ERROR!", JOptionPane.INFORMATION_MESSAGE, 1);
                changePasswordBtn.doClick();
            }

            String passwordQuery = "update MembersTable set password='"+newUserPassword + "' where username='"+ currentUserName + "';";
            try {
                //TODO find a way to real time update the password
                statement.executeQuery(passwordQuery);
                org.project10.global.WelcomePage.showJOptionPaneWithTimer("Info updated succesfully", "success", JOptionPane.INFORMATION_MESSAGE, 3);
                refreshLabels();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                throw new RuntimeException(e);
            }

        } else if (actionEvent.getSource() == changeProfilePic) {
            //TODO implement a way to change the profile picture
        }
    }

    private void refreshLabels() {
        // Fetching updated data from the database and set values to the labels
        loadUserDataFromTheDatabase();
        // removing  existing labels
        removeAllLabels();

        add(adminProfilePic, "gap left 80");
        add(adminNameLabel);
        add(adminUsernameLabel);
        add(adminPassword);
        add(accountType4rombd);
        add(changeUsernameBtn);
        add(changePasswordBtn);
        add(changeProfilePic);

        // Repaint the panel to reflect the changes
        revalidate();
        repaint();
    }

    private void removeAllLabels() {
        // Get all components in the panel
        Component[] components = getComponents();

        // Iterate through the components
        for (Component component : components) {
            // Check if the component is a JLabel
            if (component instanceof JLabel) {
                // Remove the JLabel from the panel
                remove(component);
            }
        }
    }
}


