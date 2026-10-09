package com.rms.bill;

import com.rms.PaymentType;
import com.rms.setting.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Insets;
import java.util.List;
import java.util.function.Consumer;

public class CartPanel extends JPanel {

    private static final Color PANEL_BG = new Color(250, 250, 252);
    private static final Color HEADER_BG = new Color(37, 99, 235);
    private static final Color BTN_PAY = new Color(37, 99, 235);
    private static final Color BTN_CANCEL = new Color(220, 53, 69);
    private static final int PANEL_WIDTH = 416; // 320 * 1.3 ≈ 416

    private final CartManager cartManager;
    private final JPanel cartListPanel;
    private final JLabel lblSubtotal, lblDiscount, lblVAT, lblTotal;
    private final JTextField discountField;
    private final JButton payButton;
    private Consumer<PaymentContext> onPayClicked;

    public CartPanel(CartManager cartManager) {
        this.cartManager = cartManager;
        setBackground(PANEL_BG);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(PANEL_WIDTH, 0));
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(220, 220, 220)));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel headerTitle = new JLabel("Current Order");
        headerTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        headerTitle.setForeground(Color.WHITE);
        header.add(headerTitle, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Cart list
        cartListPanel = new JPanel();
        cartListPanel.setLayout(new BoxLayout(cartListPanel, BoxLayout.Y_AXIS));
        cartListPanel.setBackground(PANEL_BG);

        JScrollPane scroll = new JScrollPane(cartListPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        add(scroll, BorderLayout.CENTER);

        // Bottom totals + buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(PANEL_BG);
        bottomPanel.setBorder(new EmptyBorder(10, 16, 10, 16));

        lblSubtotal = new JLabel("৳ 0.00");
        lblDiscount = new JLabel("৳ 0.00");
        lblVAT = new JLabel("৳ 0.00");
        lblTotal = new JLabel("৳ 0.00");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTotal.setForeground(HEADER_BG);

        Font amtFont = new Font("SansSerif", Font.BOLD, 15);
        lblSubtotal.setFont(amtFont);
        lblDiscount.setFont(amtFont);
        lblVAT.setFont(amtFont);

        discountField = new JTextField(Utils.DISCOUNT != null ? Utils.DISCOUNT : "0", 5);
        discountField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        discountField.setMaximumSize(new Dimension(60, 28));
        discountField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
        });

        double vatPct = Utils.getDoubleVal(Utils.VAT);
        double discPct = Utils.getDoubleVal(Utils.DISCOUNT);

        bottomPanel.add(makeTotalRow("Subtotal", lblSubtotal, null));
        bottomPanel.add(Box.createVerticalStrut(4));
        bottomPanel.add(makeTotalRow("Discount (" + discPct + "%)", lblDiscount, discountField));
        bottomPanel.add(Box.createVerticalStrut(4));
        bottomPanel.add(makeTotalRow("VAT (" + vatPct + "%)", lblVAT, null));
        bottomPanel.add(Box.createVerticalStrut(6));
        bottomPanel.add(makeSeparator());
        bottomPanel.add(Box.createVerticalStrut(6));
        bottomPanel.add(makeTotalRow("Total", lblTotal, null));
        bottomPanel.add(Box.createVerticalStrut(12));

        payButton = makeButton("Pay ৳ 0.00", BTN_PAY, Color.WHITE);
        payButton.setEnabled(false);
        payButton.addActionListener(e -> {
            if (onPayClicked != null && !cartManager.isEmpty()) {
                double discPctVal = Utils.getDoubleVal(discountField.getText());
                TotalsResult t = cartManager.recalculateTotals(discPctVal);
                onPayClicked.accept(new PaymentContext(t, discountField.getText(), cartManager.getItems()));
            }
        });
        bottomPanel.add(payButton);
        bottomPanel.add(Box.createVerticalStrut(6));

        JButton cancelBtn = makeButton("Cancel", BTN_CANCEL, Color.WHITE);
        cancelBtn.addActionListener(e -> {
            if (!cartManager.isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Cancel this transaction?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    cartManager.clearCart();
                    discountField.setText(Utils.DISCOUNT != null ? Utils.DISCOUNT : "0");
                    refresh();
                }
            }
        });
        bottomPanel.add(cancelBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void setOnPayClicked(Consumer<PaymentContext> callback) {
        this.onPayClicked = callback;
    }

    public void refresh() {
        cartListPanel.removeAll();
        List<CartItem> items = cartManager.getItems();

        if (items.isEmpty()) {
            JLabel empty = new JLabel("Cart is empty", SwingConstants.CENTER);
            empty.setFont(new Font("SansSerif", Font.PLAIN, 13));
            empty.setForeground(Color.GRAY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            empty.setBorder(new EmptyBorder(20, 0, 20, 0));
            cartListPanel.add(empty);
        } else {
            for (CartItem item : items) {
                cartListPanel.add(buildCartRow(item));
                cartListPanel.add(makeSeparator());
            }
        }

        double discPct = Utils.getDoubleVal(discountField.getText());
        TotalsResult t = cartManager.recalculateTotals(discPct);
        lblSubtotal.setText(String.format("৳ %.2f", t.subtotal));
        lblDiscount.setText(String.format("৳ %.2f", t.discountAmt));
        lblVAT.setText(String.format("৳ %.2f", t.vatAmt));
        lblTotal.setText(String.format("৳ %.2f", t.total));
        payButton.setText(String.format("Pay ৳ %.2f", t.total));
        payButton.setEnabled(!cartManager.isEmpty());

        cartListPanel.revalidate();
        cartListPanel.repaint();
    }

    private JPanel buildCartRow(CartItem item) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(8, 12, 8, 12));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        row.setMinimumSize(new Dimension(0, 62));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 2, 0, 2);
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        // Col 0: name + size (fills remaining space)
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setBackground(Color.WHITE);
        JLabel nameLabel = new JLabel(item.name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        JLabel sizeLabel = new JLabel(item.size);
        sizeLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        sizeLabel.setForeground(Color.GRAY);
        namePanel.add(nameLabel);
        namePanel.add(sizeLabel);

        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        row.add(namePanel, gbc);

        // Col 1: minus button
        JButton minus = makeSmallBtn("-");
        minus.addActionListener(e -> { cartManager.decrementCart(item.name, item.size); refresh(); });
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        row.add(minus, gbc);

        // Col 2: quantity label (fixed width)
        JLabel qtyLabel = new JLabel(String.valueOf(item.quantity), SwingConstants.CENTER);
        qtyLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        qtyLabel.setPreferredSize(new Dimension(28, 28));
        gbc.gridx = 2;
        row.add(qtyLabel, gbc);

        // Col 3: plus button
        JButton plus = makeSmallBtn("+");
        plus.addActionListener(e -> { cartManager.incrementCart(item.name, item.size); refresh(); });
        gbc.gridx = 3;
        row.add(plus, gbc);

        // Col 4: price label (fixed width, right-aligned)
        JLabel priceLabel = new JLabel(String.format("৳ %.0f", item.lineTotal()), SwingConstants.RIGHT);
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        priceLabel.setForeground(new Color(37, 99, 235));
        priceLabel.setPreferredSize(new Dimension(60, 28));
        gbc.gridx = 4;
        row.add(priceLabel, gbc);

        // Col 5: remove button
        JButton removeBtn = new JButton("x");
        removeBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        removeBtn.setForeground(Color.RED);
        removeBtn.setBorderPainted(false);
        removeBtn.setContentAreaFilled(false);
        removeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        removeBtn.setPreferredSize(new Dimension(24, 24));
        removeBtn.addActionListener(e -> { cartManager.removeFromCart(item.name, item.size); refresh(); });
        gbc.gridx = 5;
        row.add(removeBtn, gbc);

        return row;
    }

    private JPanel makeTotalRow(String label, JLabel valueLabel, JTextField editField) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(PANEL_BG);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        row.add(lbl, BorderLayout.WEST);
        if (editField != null) {
            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
            right.setBackground(PANEL_BG);
            right.add(editField);
            right.add(valueLabel);
            row.add(right, BorderLayout.EAST);
        } else {
            row.add(valueLabel, BorderLayout.EAST);
        }
        return row;
    }

    private JSeparator makeSeparator() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(235, 235, 235));
        return sep;
    }

    private JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private JButton makeSmallBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Dialog", Font.BOLD, 16));
        btn.setPreferredSize(new Dimension(36, 30));
        btn.setMinimumSize(new Dimension(36, 30));
        btn.setMaximumSize(new Dimension(36, 30));
        btn.setBackground(new Color(235, 243, 255));
        btn.setForeground(new Color(37, 99, 235));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Context passed to the pay callback
    public static class PaymentContext {
        public final TotalsResult totals;
        public final String discountPctStr;
        public final List<CartItem> items;

        public PaymentContext(TotalsResult totals, String discountPctStr, List<CartItem> items) {
            this.totals = totals;
            this.discountPctStr = discountPctStr;
            this.items = items;
        }
    }
}
