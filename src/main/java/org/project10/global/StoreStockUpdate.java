package org.project10.global;

import net.miginfocom.swing.MigLayout;
import org.project10.componentMaker.ButtonMaker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import static org.project10.global.DashBoard.switchCenterPanel;

public class StoreStockUpdate extends TransparentSubPanel implements ActionListener {
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private JTable storeTable;
    private static DefaultTableModel storeTableModel;
    private JButton updateQuantityBtn;
    private static boolean lowStockChecked = false;

    public interface StockUpdateObserver {
        void notifyPurchase(String itemName, int purchasedQuantity);
    }
    String userType;

    public StoreStockUpdate(String userType) {
        super(new Color(23, 77, 89, 0));
        init();
        this.userType = userType;
        fetchAndCheckStock();
    }
    private void clearAndFetchStoreData() {
        // Clear the existing data in the table model
        storeTableModel.setRowCount(0);

        try {
            // Fetch and display the updated store data
            fetchStoreData();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() {
        setBackground(new Color(84, 100, 125));
        setLayout(new MigLayout("align center, wrap"));
        System.out.println("Store Stock Update accessed");

        // Create a table for the store
        String[] columnNames = {"Item Name", "Price per Item", "Quantity"};
        Object[][] data = {};
        storeTableModel = new DefaultTableModel(data, columnNames);
        storeTable = new JTable(storeTableModel);
        JScrollPane scrollPane = new JScrollPane(storeTable);
        add(scrollPane, "span, grow");

        // Button to update quantity
        updateQuantityBtn = ButtonMaker.createButton("Update Quantity", null);
        updateQuantityBtn.addActionListener(this);
        add(updateQuantityBtn);
    }

    private void fetchAndCheckStock() {
        try {
            // Fetch store data
            fetchStoreData();

            // Check low stock items if not checked yet
            if (!lowStockChecked) {
                checkLowStockItems(userType);
                lowStockChecked = true;  // Set the flag to true after checking
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void fetchStoreData() throws ClassNotFoundException, SQLException {
        Class.forName("org.mariadb.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/project10", "moseti", "tajiri01");
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT ItemName, priceperItem, quantity FROM StoreTable");

        while (resultSet.next()) {
            String itemName = resultSet.getString("ItemName");
            double pricePerItem = resultSet.getDouble("priceperItem");
            int quantity = resultSet.getInt("quantity");

            Object[] rowData = {itemName, pricePerItem, quantity};
            storeTableModel.addRow(rowData);
        }
    }

    void checkLowStockItems(String userType) {
        lowStockChecked = true;  // Set the flag to true after checking
        StringBuilder lowStockItems = new StringBuilder();

        // Check if quantity is below 10 only for admin
        if (userType.equals("Admin")) {
            for (int i = 0; i < storeTableModel.getRowCount(); i++) {
                String itemName = (String) storeTableModel.getValueAt(i, 0);
                int quantity = (int) storeTableModel.getValueAt(i, 2);

                if (quantity < 10) {
                    lowStockItems.append(itemName).append(" (").append(quantity).append(" left)\n");
                }
            }

            // Check if there are low stock items
            if (lowStockItems.length() > 0 && org.project10.global.DashBoard.adminHasReachedDashboard) {
                int option = JOptionPane.showConfirmDialog(this,
                        "The following items are running low in stock:\n" + lowStockItems.toString() +
                                "Do you want to update the stock?", "Low Stock Warning", JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    // Admin chose to update stock
                    org.project10.global.DashBoard.switchCenterPanel("storeStockUpdate");
                } else {
                    // Admin chose not to update stock
                    JOptionPane.showMessageDialog(this, "Please be aware that you will soon run out of stock for the listed items.",
                            "Stock Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == updateQuantityBtn) {
            handleUpdateQuantity();
        }
    }

    private void handleUpdateQuantity() {
        int selectedRow = storeTable.getSelectedRow();
        if (selectedRow != -1) {
            String itemName = (String) storeTableModel.getValueAt(selectedRow, 0);
            String quantityStr = JOptionPane.showInputDialog(this, "Enter new quantity for " + itemName + ":");

            try {
                int newQuantity = Integer.parseInt(quantityStr);
                updateQuantityInDatabase(itemName, newQuantity);

                // Update the table with the new quantity
                storeTableModel.setValueAt(newQuantity, selectedRow, 2);
                JOptionPane.showMessageDialog(this, "Quantity updated successfully.");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item from the table.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateQuantityInDatabase(String itemName, int newQuantity) {
        try {
            String updateQuery = "UPDATE StoreTable SET quantity = ? WHERE ItemName = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setInt(1, newQuantity);
            preparedStatement.setString(2, itemName);
            preparedStatement.executeUpdate();

            // After updating, clear and fetch the updated data
            clearAndFetchStoreData();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating quantity in the database: " + e.getMessage());
        }
    }

}
