package org.project10.admin;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MemberAddition extends JPanel implements ActionListener {
    public MemberAddition() {
        init();
    }
    JLabel firstNameLabel = new JLabel("First Name");
    JTextField firstNameTextField = new JTextField(20);
    JLabel lastNameLabel = new JLabel("Last Name");
    JTextField lastNameTextField = new JTextField(20);
    JLabel dateOfBirthLabel = new JLabel("Date of Birth");
    JTextField dateOfBirthTextField = new JTextField("yyyy-MM-dd",20);
    JLabel genderLabel = new JLabel("Gender");
    JRadioButton maleRadioButton = new JRadioButton("Male");
    JRadioButton femaleRadioButton = new JRadioButton("Female");
    JLabel phoneLabel = new JLabel("Phone");
    JTextField phoneTextField = new JTextField(20);
    JLabel nextOfKinNameLabel = new JLabel("Next of Kin Name");
    JTextField nextOfKinNameTextField = new JTextField(20);
    JLabel weightLabel = new JLabel("Weight");
    JTextField weightTextField = new JTextField(20);
    JLabel heightLabel = new JLabel("Height");
    JTextField heightTextField = new JTextField(20);
    JLabel usernameLabel = new JLabel("Username");
    JTextField usernameTextField = new JTextField(20);
    JLabel passwordLabel = new JLabel("Password");
    JTextField passwordTextField = new JTextField(20);
    JLabel subcountyLabel = new JLabel("Subcounty");
    String[] subcounties = {"Kiminini", "Starehe", "Manjaro", "Keroka"};
    JComboBox<String> subcountiesCombobox = new JComboBox<>(subcounties);
    JLabel groupBackingLabel = new JLabel("Group Backing");
    String[] groupBackings = {"School", "College", "Religious Institution", "Individual"};
    JComboBox<String> groupBackingsCombobox = new JComboBox<>(groupBackings);
    JLabel specialNeedsLabel = new JLabel("Special Needs");
    String[] specialNeeds = {"None", "food allergy","water allergy","fabric allergy"};
    JComboBox<String> specialNeedsCombobox = new JComboBox<>(specialNeeds);
    JButton saveBtn = new JButton("Save");
    JButton resetBtn = new JButton("Reset");
    String userType;
    String selectedGame;


    private void init() {
        setBackground(new Color(84, 100, 125));
        setLayout(new MigLayout("align center"));
        System.out.println("MemberAddition accessed");
        setOpaque(false);
        usernameTextField.requestFocusInWindow();

        add(firstNameLabel);
        add(firstNameTextField, "wrap");
        add(lastNameLabel);
        add(lastNameTextField, "wrap");
        add(dateOfBirthLabel);
        //TODO use JDatePicker instead of strings
        dateOfBirthTextField.setToolTipText("yyyy-MM-dd");
        add(dateOfBirthTextField, "wrap");
        add(genderLabel);

        ButtonGroup genderButtonGroup = new ButtonGroup();
        genderButtonGroup.add(maleRadioButton);
        genderButtonGroup.add(femaleRadioButton);
        JPanel kaPanel = new JPanel(new MigLayout());
        kaPanel.setOpaque(false);
        kaPanel.setPreferredSize(new Dimension(lastNameTextField.getPreferredSize().width, lastNameTextField.getPreferredSize().height));
        kaPanel.add(maleRadioButton);
        kaPanel.add(femaleRadioButton);
        add(kaPanel, "wrap");

        add(phoneLabel);
        add(phoneTextField, "wrap");
        add(nextOfKinNameLabel);
        add(nextOfKinNameTextField, "wrap");
        add(weightLabel);
        add(weightTextField, "wrap");
        add(heightLabel);
        add(heightTextField, "wrap");
        add(usernameLabel);
        add(usernameTextField, "wrap");
        add(passwordLabel);
        add(passwordTextField, "wrap");
        add(subcountyLabel);
        add(subcountiesCombobox, "wrap");
        add(groupBackingLabel);
        add(groupBackingsCombobox, "wrap");
        add(specialNeedsLabel);
        add(specialNeedsCombobox, "wrap");

        saveBtn.addActionListener(this);
        resetBtn.addActionListener(this);
        add(saveBtn, "newline,gap left 100");
        add(resetBtn);

        navigateTextFields();
    }

    Connection connection;
    public void saveToDatabase() throws ClassNotFoundException, SQLException {
        Class.forName("org.mariadb.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/project10", "moseti", "tajiri01");

        // Use prepared statement to prevent SQL injection
        String query = "INSERT INTO MembersTable (username, password, FName, LName, gender, dob, nokName, phoneNo, weight, height, subcounty, individualgroup, accounttype, specialneeds) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, usernameTextField.getText());
            preparedStatement.setString(2, passwordTextField.getText());
            preparedStatement.setString(3, firstNameTextField.getText());
            preparedStatement.setString(4, lastNameTextField.getText());

            // Set gender based on radio button selection
            preparedStatement.setString(5, maleRadioButton.isSelected() ? "Male" : "Female");

            // Parse dateOfBirth using SimpleDateFormat
            String dateOfBirthText = dateOfBirthTextField.getText();
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date utilDate = dateFormat.parse(dateOfBirthText);
                preparedStatement.setDate(6, new Date(utilDate.getTime()));
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid Date of Birth", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            preparedStatement.setString(7, nextOfKinNameTextField.getText());
            preparedStatement.setString(8, phoneTextField.getText());
            preparedStatement.setDouble(9, Double.parseDouble(weightTextField.getText()));
            preparedStatement.setDouble(10, Double.parseDouble(heightTextField.getText()));
            preparedStatement.setString(11, subcountiesCombobox.getSelectedItem().toString());
            preparedStatement.setString(12, groupBackingsCombobox.getSelectedItem().toString());
            preparedStatement.setString(13, userType);
            preparedStatement.setString(14, specialNeedsCombobox.getSelectedItem().toString());

            String childTableQuery = "INSERT INTO SportsOfIntrestTable (username, game1) VALUES (?, ?)";
            try (PreparedStatement childTableStatement = connection.prepareStatement(childTableQuery)) {
                childTableStatement.setString(1,usernameTextField.getText());
                childTableStatement.setString(2, selectedGame);
                childTableStatement.executeUpdate();
            }

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }
        // Debug
        System.out.println("Saved to database");
        resetTextFields();
        MemberInformation viewMembers = new MemberInformation();
        viewMembers.refreshTheMembersList();
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == saveBtn) {
            try {
                // Step 1: Get User Type
                userType = showUserTypeDialog();

                if (userType != null) {
                    // Step 2: Handle User Type Specifics
                    if (userType.equals("User")) {
                        handleUserSpecifics();
                    }

                    // Step 3: Save to Database
                    saveToDatabase();
                }
            } catch (ClassNotFoundException | SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException(e);
            }
        }else if (actionEvent.getSource() == resetBtn) {
            resetTextFields();
        }

    }
    private String showUserTypeDialog() {
        Object[] options = {"Admin", "Patron", "User"};
        int userTypeChoice = JOptionPane.showOptionDialog(
                this,
                "Select User Type:",
                "User Type Selection",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]
        );

        if (userTypeChoice == JOptionPane.CLOSED_OPTION) {
            // User closed the dialog
            return null;
        }

        return options[userTypeChoice].toString();
    }
    private void handleUserSpecifics() {
        // Show JOptionPane for game selection
        Object[] gameOptions = {"Football", "Swimming", "Hockey", "Handball"};
        selectedGame = (String) JOptionPane.showInputDialog(
                this,
                "Choose a Game of Interest:",
                "Game Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                gameOptions,
                gameOptions[0]
        );

        if (selectedGame != null) {
            //debug
            System.out.println("Selected Game: " + selectedGame);
        }
    }

    private void resetTextFields() {
        JTextField[] textFields = {firstNameTextField, lastNameTextField, dateOfBirthTextField, phoneTextField, nextOfKinNameTextField, weightTextField, heightTextField, usernameTextField, passwordTextField};
        for (JTextField textField : textFields) {
            textField.setText("");
            firstNameTextField.requestFocusInWindow();
        }
    }

    private void navigateTextFields() {
        JTextField[] textFields = {firstNameTextField, lastNameTextField, dateOfBirthTextField, phoneTextField, nextOfKinNameTextField, weightTextField, heightTextField, usernameTextField, passwordTextField};
        for (int i = 0; i < textFields.length - 1; i++) {
            JTextField currentField = textFields[i];
            JTextField nextField = textFields[i + 1];

            currentField.addActionListener(e -> {
                nextField.requestFocusInWindow();
            });
        }
    }
}
