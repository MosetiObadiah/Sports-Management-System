package org.project10.admin;

import net.miginfocom.swing.MigLayout;
import org.project10.componentMaker.ButtonMaker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberInformation extends JPanel implements ActionListener {
    private JTable table;
    private DefaultTableModel tableModel;
    private List<String> userData;
    private String currentQuery;

    public MemberInformation() {
        init();
    }

    JButton viewAdmins;
    JButton viewPatrons;
    JButton viewCaptains;
    JButton viewNormalUsers;
    JButton viewAllMembers;

    private void init() {
        setLayout(new MigLayout("align center"));
        setOpaque(false);

        viewNormalUsers = ButtonMaker.createButton("Normal Users", null);
        viewAdmins = ButtonMaker.createButton("Admins", null);
        viewPatrons = ButtonMaker.createButton("Patrons", null);
        viewCaptains = ButtonMaker.createButton("Captains", null);
        viewAllMembers = ButtonMaker.createButton("All Members", null);

        JButton[] buttonGroup = {viewAllMembers, viewAdmins, viewPatrons, viewCaptains, viewNormalUsers};
        for (JButton button : buttonGroup) {
            button.addActionListener(this);
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        for (JButton button : buttonGroup) {
            buttonPanel.add(button);
        }
        add(buttonPanel, "wrap");

        tableModel = new DefaultTableModel();
        tableModel.addColumn("First Name");
        tableModel.addColumn("Last Name");
        tableModel.addColumn("Username");
        tableModel.addColumn("Password");
        tableModel.addColumn("Account");
        table = new JTable(tableModel);

        // TODO Make the table transparent

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, "wrap");

        JButton addUserButton = ButtonMaker.createButton("Add User", null);
        JButton deleteUserButton = ButtonMaker.createButton("Delete User", null);

        addUserButton.addActionListener(e -> {
            org.project10.global.DashBoard.switchCenterPanel("MemberAddition");
            org.project10.global.DashBoard.backBtn.setVisible(true);
        });

        deleteUserButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                deleteSelectedUser(selectedRow);
            }
        });

        // Adding the buttons for adding and deleting users
        add(addUserButton, "wrap");
        add(deleteUserButton);

        setQuery("SELECT fname, lname, username, password, accountType FROM project10.MembersTable;");
        loadUserDataFromDatabase();
    }

    void loadUserDataFromDatabase() {
        userData = new ArrayList<>();
        Connection connection = null;
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/project10", "moseti", "tajiri01");

            PreparedStatement statement = connection.prepareStatement(currentQuery);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String firstName = resultSet.getString("fname");
                String lastName = resultSet.getString("lname");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String accountType = resultSet.getString("accountType");
                userData.add(firstName + " " + lastName + " " + username + " " + password + " " + accountType);
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
        populateTable();
    }

    private void populateTable() {
        tableModel.setRowCount(0);
        for (String line : userData) {
            String[] data = line.split(" ");
            if (data.length == 5) {
                tableModel.addRow(data);
            }
        }
    }

    void refreshTheMembersList() {
        //get updated data
        loadUserDataFromDatabase();
        //remove current table
        removeAllTables();
        //add new updated table
        tableModel = new DefaultTableModel();
        tableModel.addColumn("First Name");
        tableModel.addColumn("Last Name");
        tableModel.addColumn("Username");
        tableModel.addColumn("Password");
        tableModel.addColumn("Account");
        table = new JTable(tableModel);

        // Repaint the panel to reflect the changes
        revalidate();
        repaint();
    }

    //Todo fix the getting and updating of this table
    private void removeAllTables() {
        // Get all components in the panel
        Component[] components = getComponents();

        // Iterate through the components
        for (Component component : components) {
            // Check if the component is a JLabel
            if (component instanceof JTable) {
                // Remove the JLabel from the panel
                remove(component);
            }
        }
    }

    private void deleteSelectedUser(int selectedRow) {
        if (selectedRow >= 0) {
            // Get the username from the selected row
            String username = tableModel.getValueAt(selectedRow, 2).toString();

            // Execute a DELETE query to remove the user from the database
            Connection connection = null;
            try {
                Class.forName("org.mariadb.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/project10", "moseti", "tajiri01");

                String deleteQuery = "DELETE FROM MembersTable WHERE username = ?";
                PreparedStatement statement = connection.prepareStatement(deleteQuery);
                statement.setString(1, username);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    // Removews the selected row from the JTable's DefaultTableModel
                    tableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(this, "User not found in the database", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            //TODO fix not working
            JOptionPane.showMessageDialog(this, "No row selected for deletion", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == viewAdmins) {
            setQuery("SELECT fname, lname, username, password, accountType FROM project10.MembersTable where accountType='Admin';");

        } else if (actionEvent.getSource() == viewPatrons) {
            setQuery("SELECT fname, lname, username, password, accountType FROM project10.MembersTable where accountType='Patron';");

        } else if (actionEvent.getSource() == viewCaptains) {
            setQuery("SELECT fname, lname, username, password, accountType FROM project10.MembersTable where accountType='Captain';");

        } else if (actionEvent.getSource() == viewNormalUsers) {
            setQuery("SELECT fname, lname, username, password, accountType FROM project10.MembersTable where accountType='User';");

        } else if (actionEvent.getSource() == viewAllMembers) {
            setQuery("SELECT fname, lname, username, password, accountType FROM project10.MembersTable;");
        }
        // Reloads data with the updated query
        loadUserDataFromDatabase();
    }

    private void setQuery(String query) {
        currentQuery = query;
    }
}
