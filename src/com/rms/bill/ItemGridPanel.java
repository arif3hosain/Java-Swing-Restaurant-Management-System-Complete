package com.rms.bill;

import com.rms.setting.Utils;
import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Consumer;

public class ItemGridPanel extends JPanel {

    private static final int MAX_COLS = 10;
    private final JPanel gridPanel;
    private final ItemImageLoader imageLoader = new ItemImageLoader();
    private final Consumer<CartItem> onItemClick;
    private final JLabel emptyLabel;

    public ItemGridPanel(Consumer<CartItem> onItemClick) {
        this.onItemClick = onItemClick;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        gridPanel = new JPanel(new GridLayout(0, MAX_COLS, 6, 6));
        gridPanel.setBackground(new Color(245, 247, 250));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        // Wrap in a top-anchored container so GridLayout doesn't stretch vertically
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(245, 247, 250));
        wrapper.add(gridPanel, BorderLayout.NORTH);

        emptyLabel = new JLabel("No items in this category", SwingConstants.CENTER);
        emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        emptyLabel.setForeground(Color.GRAY);
        emptyLabel.setVisible(false);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
        add(emptyLabel, BorderLayout.SOUTH);
    }

    public void loadItems(String categoryFilter) {
        gridPanel.removeAll();
        DBConnection con = new DBConnection();
        String sql = "SELECT a.item_name, a.price, a.quantity, b.name as cat_name " +
                "FROM item a JOIN category b ON b.id = a.cat_id " +
                "WHERE a.deleted = false AND b.deleted = false ";
        if (categoryFilter != null && !categoryFilter.isEmpty()) {
            sql += "AND b.name = ? ";
        }
        sql += "ORDER BY a.item_name";

        try (PreparedStatement pst = con.mkDataBase().prepareStatement(sql)) {
            if (categoryFilter != null && !categoryFilter.isEmpty()) {
                pst.setString(1, categoryFilter);
            }
            ResultSet rs = pst.executeQuery();

            java.util.List<ItemCard> cards = new java.util.ArrayList<>();
            while (rs.next()) {
                String name = rs.getString("item_name");
                double price = rs.getDouble("price");
                String size = rs.getString("quantity");
                ImageIcon icon = imageLoader.loadImage(name);
                cards.add(new ItemCard(name, size, price, icon, onItemClick));
            }

            emptyLabel.setVisible(cards.isEmpty());
            gridPanel.setVisible(!cards.isEmpty());

            for (ItemCard card : cards) {
                gridPanel.add(card);
            }

            // GridLayout always fills rows left-to-right. If last row is partial,
            // pad with empty panels so existing cards don't stretch.
            int remainder = cards.size() % MAX_COLS;
            if (remainder != 0 && !cards.isEmpty()) {
                for (int i = remainder; i < MAX_COLS; i++) {
                    JPanel filler = new JPanel();
                    filler.setBackground(new Color(245, 247, 250));
                    filler.setPreferredSize(new Dimension(145, 148));
                    gridPanel.add(filler);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading items: " + e.getMessage());
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }
}
