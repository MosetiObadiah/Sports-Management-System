package org.project10.global;

import net.miginfocom.swing.MigLayout;
import org.project10.componentMaker.ButtonMaker;
import org.project10.componentMaker.LabelMaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AllDetails extends TransparentSubPanel implements ActionListener {
    JLabel fnameLabel;
    JLabel lnameLabel;
    JLabel dobLabel;
    JLabel genderLabel;
    JLabel ageLabel;
    JLabel phoneLabel;
    JLabel nokNameLabel;
    JLabel weightLabel;
    JLabel heightLabel;
    JLabel subCountyLabel;
    JLabel groupTypeLabel;
    JLabel specialneedsLabel;
    JLabel sportsofIntrestLabel;

    public AllDetails() {
        super(new Color(56, 196, 214, 80)); // somehow transparent? :)
        init();
    }

    JButton alterPersonlDetail;

    private void init() {
        setLayout(new MigLayout("align center, wrap"));

        loadUserDataFromDatabase();

        add(fnameLabel);
        add(lnameLabel);
        add(dobLabel);
        add(genderLabel);
        add(ageLabel);
        add(phoneLabel);
        add(nokNameLabel);
        add(weightLabel);
        add(heightLabel);
        add(subCountyLabel);
        add(groupTypeLabel);
        add(specialneedsLabel);
        add(sportsofIntrestLabel);

        alterPersonlDetail = ButtonMaker.createButton("Alter Information", null);
        alterPersonlDetail.addActionListener(this);
        alterPersonlDetail.setPreferredSize(new Dimension(100, 70));
        add(alterPersonlDetail, "alignx right, aligny bottom");
    }

    private void loadUserDataFromDatabase() {
        Connection connection = null;
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/project10", "moseti", "tajiri01");

            String query = "SELECT username, password, Fname, Lname, gender, dob, nokName, phoneNo, weight, height, subcounty, individualgroup, accounttype ,specialneeds , age FROM MembersTable WHERE username='" + org.project10.global.BasicDetails.currentUserName + "';";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Initialize labels outside the loop
            fnameLabel = LabelMaker.createLabel("", null);
            lnameLabel = LabelMaker.createLabel("", null);
            dobLabel = LabelMaker.createLabel("", null);
            genderLabel = LabelMaker.createLabel("", null);
            ageLabel = LabelMaker.createLabel("", null);
            phoneLabel = LabelMaker.createLabel("", null);
            nokNameLabel = LabelMaker.createLabel("", null);
            weightLabel = LabelMaker.createLabel("", null);
            heightLabel = LabelMaker.createLabel("", null);
            subCountyLabel = LabelMaker.createLabel("", null);
            groupTypeLabel = LabelMaker.createLabel("", null);
            specialneedsLabel = LabelMaker.createLabel("", null);
            sportsofIntrestLabel = LabelMaker.createLabel("", null);

            // Check if the result set is not empty
            if (resultSet.next()) {
                // Update label values inside the loop
                fnameLabel.setText("First name:         " + resultSet.getString("Fname"));
                lnameLabel.setText("Last name:         " + resultSet.getString("Lname"));
                dobLabel.setText("Date of birth:     " + resultSet.getString("dob"));
                genderLabel.setText("Gender:              " + resultSet.getString("gender"));
                ageLabel.setText("Age:                    " + resultSet.getString("age"));
                phoneLabel.setText("Phone:                " + resultSet.getString("phoneNo"));
                nokNameLabel.setText("Next of kin:        " + resultSet.getString("nokName"));
                weightLabel.setText("Weight:              " + resultSet.getString("weight"));
                heightLabel.setText("Height:               " + resultSet.getString("height"));
                subCountyLabel.setText("Sub-county:       " + resultSet.getString("subcounty"));
                groupTypeLabel.setText("Group type:       " + resultSet.getString("accounttype"));
                specialneedsLabel.setText("Special needs:   " + resultSet.getString("specialneeds"));

                // Retrieve and set value for the 'game1' column from SportsOfInterestTable
                String query2 = "SELECT game1 FROM SportsOfIntrestTable WHERE username=?";
                try (PreparedStatement statement2 = connection.prepareStatement(query2)) {
                    statement2.setString(1, org.project10.global.BasicDetails.currentUserName);
                    try (ResultSet resultSet2 = statement2.executeQuery()) {
                        if (resultSet2.next()) {
                            sportsofIntrestLabel.setText("Sports of interest: " + resultSet2.getString("game1"));
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == alterPersonlDetail) {
            // Show a dialog for user input
            String detailToEdit = JOptionPane.showInputDialog(this, "Enter the detail you want to edit (e.g., 'phone', 'username', 'password'):");

            if (detailToEdit != null && !detailToEdit.isEmpty()) {
                // Prompt the user for the new value
                String newValue = JOptionPane.showInputDialog(this, "Enter the new value for " + detailToEdit + ":");

                if (newValue != null) {
                    // Update the database with the new value
                    updateDetailInDatabase(detailToEdit, newValue);

                    // Update the displayed labels in real-time
                    loadUserDataFromDatabase();
                }
            }
        }
    }

    private void updateDetailInDatabase(String detailToEdit, String newValue) {
        Connection connection = null;
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/project10", "moseti", "tajiri01");

            String updateQuery = "";
            if (detailToEdit.equals("username")) {
                updateQuery = "UPDATE MembersTable SET username=? WHERE username=?";
            } else if (detailToEdit.equals("password")) {
                updateQuery = "UPDATE MembersTable SET password=? WHERE username=?";
            } else {
                updateQuery = "UPDATE MembersTable SET " + detailToEdit + "=? WHERE username=?";
            }

            PreparedStatement statement = connection.prepareStatement(updateQuery);
            statement.setString(1, newValue);
            statement.setString(2, org.project10.global.BasicDetails.currentUserName);
            statement.executeUpdate();

            // Refresh labels to reflect the changes
            refreshLabels();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void refreshLabels() {
        // Fetch updated data from the database and set values to the labels
        loadUserDataFromDatabase();

        // Clear existing labels
        removeAllLabels();

        // Add the updated labels to the panel
        add(fnameLabel);
        add(lnameLabel);
        add(dobLabel);
        add(genderLabel);
        add(ageLabel);
        add(phoneLabel);
        add(nokNameLabel);
        add(weightLabel);
        add(heightLabel);
        add(subCountyLabel);
        add(groupTypeLabel);
        add(specialneedsLabel);
        add(sportsofIntrestLabel);
        add(alterPersonlDetail);

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
                remove(alterPersonlDetail);
            }
        }
    }
}