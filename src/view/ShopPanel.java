package view;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import dao.ShopItemDAO;
import dao.PlayerDAO;
import dao.entities.ShopItem;

public class ShopPanel extends JPanel {

    private ShopItemDAO shopItemDAO;
    private PlayerDAO playerDAO;
    private int currentPlayerId;
    private JLabel balanceLabel;

    public ShopPanel(GameFrame frame, int playerId) {
        this.currentPlayerId = playerId;
        this.shopItemDAO = new ShopItemDAO();
        this.playerDAO = new PlayerDAO();
        
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(50, 50, 50));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("GAME SHOP");
        title.setFont(loadFont(24f));
        title.setForeground(Color.PINK);
        headerPanel.add(title, BorderLayout.WEST);

        balanceLabel = new JLabel("COINS: 0");
        balanceLabel.setFont(loadFont(14f));
        balanceLabel.setForeground(Color.YELLOW);
        headerPanel.add(balanceLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Item List
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(new Color(30, 30, 30));
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        List<ShopItem> items = shopItemDAO.getAllItems();
        for (ShopItem item : items) {
            itemsPanel.add(createItemRow(item));
            itemsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Back Button
        JButton backBtn = new JButton("BACK TO MENU");
        backBtn.setFont(loadFont(12f));
        backBtn.setBackground(Color.DARK_GRAY);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> frame.showMenu());
        add(backBtn, BorderLayout.SOUTH);

        refreshBalance();
    }

    private JPanel createItemRow(ShopItem item) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(45, 45, 45));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        row.setMaximumSize(new Dimension(1000, 100));

        // Left side: Name & Description
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBackground(new Color(45, 45, 45));
        
        JLabel nameLabel = new JLabel(item.getItemName().toUpperCase());
        nameLabel.setFont(loadFont(14f));
        nameLabel.setForeground(Color.WHITE);
        
        JLabel descLabel = new JLabel(item.getDescription());
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(Color.LIGHT_GRAY);
        
        infoPanel.add(nameLabel);
        infoPanel.add(descLabel);
        row.add(infoPanel, BorderLayout.CENTER);

        // Right side: Price & Buy Button
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(new Color(45, 45, 45));

        JLabel priceLabel = new JLabel(item.getPrice() + " " + item.getCurrencyType());
        priceLabel.setFont(loadFont(10f));
        priceLabel.setForeground(item.getCurrencyType().equals("Coin") ? Color.YELLOW : Color.CYAN);
        
        JButton buyBtn = new JButton("BUY");
        buyBtn.setFont(loadFont(10f));
        buyBtn.setBackground(new Color(0, 150, 0));
        buyBtn.setForeground(Color.WHITE);
        buyBtn.setFocusPainted(false);
        buyBtn.addActionListener(e -> {
            if (shopItemDAO.buyItem(currentPlayerId, item.getItemId(), 1)) {
                JOptionPane.showMessageDialog(this, "Mua thành công " + item.getItemName() + "!");
                refreshBalance();
            } else {
                JOptionPane.showMessageDialog(this, "Không đủ tiền để mua vật phẩm này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        actionPanel.add(priceLabel);
        actionPanel.add(buyBtn);
        row.add(actionPanel, BorderLayout.EAST);

        return row;
    }

    private void refreshBalance() {
        int[] balance = playerDAO.checkBalance(currentPlayerId);
        balanceLabel.setText("COINS: " + balance[0] + " | GEMS: " + balance[1]);
    }

    private Font loadFont(float size) {
        try {
            File fontFile = new File("assets/PressStart2P-Regular.ttf");
            if (fontFile.exists()) {
                return Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(size);
            }
        } catch (Exception e) {}
        return new Font("Arial", Font.BOLD, (int) size);
    }
}
