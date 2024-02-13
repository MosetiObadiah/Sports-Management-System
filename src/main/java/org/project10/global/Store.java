package org.project10.global;

import net.miginfocom.swing.MigLayout;
import org.project10.componentMaker.ButtonMaker;
import org.project10.componentMaker.LabelMaker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.*;

import static org.project10.global.DashBoard.switchCenterPanel;

public class Store extends JPanel implements ActionListener, MouseListener {
    private Connection connection;
    JLabel itemLabel;
    int labelCount;
    String itemName;
    double price;
    private DefaultListModel<String> cartModel;
    private JList<String> cartList;
    private JButton buyButton;
    private JButton removeItemBtn;
    private JButton updateStock;
    JButton borrowBtn;
    private JLabel totalPriceLabel;
    private final ImageIcon[] scaledIcons = new ImageIcon[8];
    private final ImageIcon[] originalIcons = new ImageIcon[8];

    public Store() {
        init();
    }

    JPanel switchPanel12 = new JPanel(new CardLayout());

    public void switchPanels1and2(String panelName) {
        CardLayout cardLayout = (CardLayout) switchPanel12.getLayout();
        cardLayout.show(switchPanel12, panelName);
        switchPanel12.revalidate();
        switchPanel12.repaint();
    }

    private void init() {
        setLayout(new MigLayout());
        setOpaque(false);


        JPanel itemsPanel1 = new JPanel(new GridLayout(2, 2));
        itemsPanel1.setBorder(BorderFactory.createTitledBorder("Shop Section"));
        itemsPanel1.setPreferredSize(new Dimension(648, 800));
        itemsPanel1.setOpaque(false);

        JPanel itemsPanel2 = new JPanel(new GridLayout(2, 2));
        itemsPanel2.setBorder(BorderFactory.createTitledBorder("Shop Section"));
        itemsPanel2.setPreferredSize(new Dimension(648, 800));
        itemsPanel2.setOpaque(false);

        JPanel rightPanel = new JPanel(new MigLayout("align center"));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Cart Section"));
        rightPanel.setPreferredSize(new Dimension(400, 800));
        rightPanel.setOpaque(false);

        JButton nextButton = ButtonMaker.createButton("next -> page 2", null);
        nextButton.addActionListener(e -> {
            if (!itemsPanel2.isVisible()) {
                switchPanels1and2("panel2");
                nextButton.setText("back <- page 1");
            } else {
                switchPanels1and2("panel1");
                nextButton.setText("next -> page 2");
            }
        });

        switchPanel12.setOpaque(false);
        switchPanel12.add(itemsPanel1, "panel1");
        switchPanel12.add(itemsPanel2, "panel2");
        add(switchPanel12, "alignx left, aligny center");
        add(rightPanel, "alignx right, aligny center");

        // Create a panel for the left side (items) and right side (cart buttons)
        JPanel leftPanel = new JPanel(new MigLayout("wrap 3, gap 10px 10px"));
        leftPanel.setOpaque(false);

        cartModel = new DefaultListModel<>();
        cartList = new JList<>(cartModel);

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/project10", "moseti", "tajiri01");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT ItemName, priceperItem, Quantity FROM StoreTable");

            String[] iconPaths = {
                    "/home/user85/IdeaProjects/MaringoSports/src/main/java/org/maringo/icons4Store/Bloomer.png",
                    "/home/user85/IdeaProjects/MaringoSports/src/main/java/org/maringo/icons4Store/game shorts.png",
                    "/home/user85/IdeaProjects/MaringoSports/src/main/java/org/maringo/icons4Store/hockeyStick.png",
                    "/home/user85/IdeaProjects/MaringoSports/src/main/java/org/maringo/icons4Store/socks.png",
                    "/home/user85/IdeaProjects/MaringoSports/src/main/java/org/maringo/icons4Store/sportsShoes.png",
                    "/home/user85/IdeaProjects/MaringoSports/src/main/java/org/maringo/icons4Store/trackSuit.png",
                    "/home/user85/IdeaProjects/MaringoSports/src/main/java/org/maringo/icons4Store/Tshirt.png",
                    "/home/user85/IdeaProjects/MaringoSports/src/main/java/org/maringo/icons4Store/Wrapper.png",
            };

            for (int i = 0; i < iconPaths.length; i++) {
                originalIcons[i] = new ImageIcon(iconPaths[i]);
                Image originalImage = originalIcons[i].getImage();
                Image scaledImage = originalImage.getScaledInstance(150, 200, Image.SCALE_SMOOTH);
                scaledIcons[i] = new ImageIcon(scaledImage);
            }

            labelCount = 0;
            while (resultSet.next() && labelCount < 8) {
                itemName = resultSet.getString("ItemName");
                price = resultSet.getDouble("priceperItem");

                itemLabel = new JLabel(itemName + "  Ksh " + price, scaledIcons[labelCount], JLabel.CENTER);
                itemLabel.addMouseListener(this);
                if (labelCount < 4) {
                    itemsPanel1.add(itemLabel, "grow");
                } else {
                    itemsPanel2.add(itemLabel, "grow");
                }

                labelCount++;
            }

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

        String[] columnNames = {"Item Name", "Price", "Quantity"};
        Object[][] data = {};
        DefaultTableModel cartTableModel = new DefaultTableModel(data, columnNames);
        JTable cartTable = new JTable(cartTableModel);
        cartTable.setPreferredScrollableViewportSize(new Dimension(400, 600));

        // button to buy item + remove items from cart
        buyButton = ButtonMaker.createButton("Buy", null);
        removeItemBtn = new JButton("Remove Item");
        removeItemBtn.addActionListener(this);
        buyButton.addActionListener(this);
        borrowBtn = new JButton("Borrow selected Items");
        borrowBtn.addActionListener(this);

        JPanel totalPricePanel = new JPanel(new MigLayout("align center"));
        totalPricePanel.setBorder(BorderFactory.createTitledBorder("Total Price"));
        totalPriceLabel = new JLabel("Total: Ksh 0.0");
        totalPricePanel.setBackground(new Color(255, 255, 255, 200));
        totalPricePanel.add(totalPriceLabel);

        updateStock = new JButton("Update Stock");
        updateStock.addActionListener(this);
        updateStock.setVisible(true);

        JLabel cartLabel = LabelMaker.createLabel("Cart", null);
        rightPanel.add(cartLabel, "wrap");
        rightPanel.add(new JScrollPane(cartList), "wrap, span, grow");
        rightPanel.add(totalPricePanel, "wrap, span, grow");
        rightPanel.add(buyButton, "span, split 2");

        rightPanel.add(removeItemBtn, "wrap");
        rightPanel.add(updateStock, "wrap, span, grow");
        rightPanel.add(nextButton, "span, grow");

    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == buyButton) {
            double totalPrice = calculateTotalPrice();
            int result = JOptionPane.showConfirmDialog(this, "Total Price: Ksh " + totalPrice + "\nProceed with the purchase?", "Confirm Purchase", JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                // TODO: Update database items quantity
                updateDatabaseQuantity();
                JOptionPane.showMessageDialog(this, "Purchase successful! Thank you for shopping.");
                cartModel.removeAllElements();
                updateTotalPrice();
            }
        } else if (actionEvent.getSource() == removeItemBtn) {
            int selectedIndex = cartList.getSelectedIndex();
            if (selectedIndex != -1) {
                cartModel.remove(selectedIndex);
                updateTotalPrice();
            }
        } else if (actionEvent.getSource() == borrowBtn) {
            //TODO put logic for borrowing if user is captain
        } else if (actionEvent.getSource() == updateStock) {
            switchCenterPanel("storeStockUpdate");
        }
    }

    private void updateDatabaseQuantity() {
        for (int i = 0; i < cartModel.getSize(); i++) {
            String itemDetails = cartModel.getElementAt(i);
            String[] parts = itemDetails.split(" ");
            if (parts.length >= 2) {
                String itemName = parts[0];
                int purchasedQuantity = 1; // Assuming each click adds one quantity
                updateQuantityInDatabase(itemName, purchasedQuantity);
            }
        }
    }

    private void updateQuantityInDatabase(String itemName, int purchasedQuantity) {
        try {
            String updateQuery = "UPDATE StoreTable SET quantity = quantity - ? WHERE ItemName = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setInt(1, purchasedQuantity);
            preparedStatement.setString(2, itemName);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating quantity in the database: " + e.getMessage());
        }
    }

    private double calculateTotalPrice() {
        double totalPrice = 0.0;
        for (int i = 0; i < cartModel.getSize(); i++) {
            String itemDetails = cartModel.getElementAt(i);
            String[] parts = itemDetails.split(" ");
            if (parts.length >= 2) {
                double price = Double.parseDouble(parts[parts.length - 1]);
                System.out.println(price);
                totalPrice += price;
            }
        }
        return totalPrice;
    }

    private void updateTotalPrice() {
        double totalPrice = calculateTotalPrice();
        totalPriceLabel.setText("Total: Ksh " + totalPrice);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() instanceof JLabel clickedLabel) {
            String itemDetails = clickedLabel.getText();
            cartModel.addElement(itemDetails);
            updateTotalPrice();
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {
    }
    @Override
    public void mouseReleased(MouseEvent e) {
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getSource() instanceof JLabel label) {
            //TODO slightly scale the image on hover
        }
    }
    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource() instanceof JLabel label) {
            //TODO return the image to normal
        }
    }
}