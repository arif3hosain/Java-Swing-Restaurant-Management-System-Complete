package com.rms.bill;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class ItemCard extends JPanel {

    private static final Color BG_NORMAL = Color.WHITE;
    private static final Color BG_HOVER = new Color(235, 243, 255);
    private static final Color BORDER_NORMAL = new Color(220, 220, 220);
    private static final Color BORDER_HOVER = new Color(37, 99, 235);

    public ItemCard(String name, String size, double price, ImageIcon icon, Consumer<CartItem> onItemClick) {
        setLayout(new BorderLayout(0, 4));
        setBackground(BG_NORMAL);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_NORMAL, 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(145, 148));
        setMinimumSize(new Dimension(145, 148));
        setMaximumSize(new Dimension(145, 148));

        // Image
        JLabel imgLabel = new JLabel(icon, SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(120, 90));
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(imgLabel, BorderLayout.CENTER);

        // Bottom info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(BG_NORMAL);

        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLabel = new JLabel(String.format("৳ %.2f", price), SwingConstants.CENTER);
        priceLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        priceLabel.setForeground(new Color(37, 99, 235));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);
        add(infoPanel, BorderLayout.SOUTH);

        // Hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(BG_HOVER);
                infoPanel.setBackground(BG_HOVER);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_HOVER, 2, true),
                        BorderFactory.createEmptyBorder(4, 4, 4, 4)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(BG_NORMAL);
                infoPanel.setBackground(BG_NORMAL);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_NORMAL, 1, true),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                onItemClick.accept(new CartItem(name, size, price, 1));
            }
        });
    }
}
