package com.rms.bill;

import com.rms.PaymentType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.BiConsumer;

public class PaymentDialog extends JDialog {

    private final JComboBox<String> methodCombo;
    private final JTextField amountField;
    private final JTextField referenceField;
    private boolean confirmed = false;

    public PaymentDialog(JFrame parent, double totalDue, BiConsumer<String, String> onConfirm) {
        super(parent, "Complete Payment", true);
        setSize(460, 480);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(Color.WHITE);
        main.setBorder(new EmptyBorder(24, 32, 24, 32));

        // Title
        JLabel title = new JLabel("Complete Payment");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        main.add(title);
        main.add(Box.createVerticalStrut(8));

        // Total due
        JLabel totalLabel = new JLabel(String.format("Total due: ৳ %.2f", totalDue));
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        totalLabel.setForeground(new Color(37, 99, 235));
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        main.add(totalLabel);
        main.add(Box.createVerticalStrut(16));

        // Payment method
        JLabel methodLabel = new JLabel("Payment method*");
        methodLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        methodLabel.setForeground(Color.GRAY);
        methodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        main.add(methodLabel);
        main.add(Box.createVerticalStrut(4));

        methodCombo = new JComboBox<>(new String[]{
                PaymentType.Cash.name(), PaymentType.MFS.name(), PaymentType.Credit.name()
        });
        methodCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        methodCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        methodCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        main.add(methodCombo);
        main.add(Box.createVerticalStrut(10));

        // Amount received
        JLabel amtLabel = new JLabel("Amount received*");
        amtLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        amtLabel.setForeground(Color.GRAY);
        amtLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        main.add(amtLabel);
        main.add(Box.createVerticalStrut(4));

        amountField = new JTextField(String.format("%.0f", totalDue));
        amountField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        amountField.setAlignmentX(Component.LEFT_ALIGNMENT);
        main.add(amountField);
        main.add(Box.createVerticalStrut(10));

        // Reference
        referenceField = new JTextField();
        referenceField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        referenceField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        referenceField.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel refLabel = new JLabel("Reference (optional)");
        refLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        refLabel.setForeground(Color.GRAY);
        refLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        main.add(refLabel);
        main.add(Box.createVerticalStrut(4));
        main.add(referenceField);
        main.add(Box.createVerticalStrut(16));

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setBackground(Color.WHITE);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> dispose());

        JButton confirmBtn = new JButton("Confirm Payment");
        confirmBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        confirmBtn.setBackground(new Color(37, 99, 235));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setOpaque(true);
        confirmBtn.setBorderPainted(false);
        confirmBtn.setFocusPainted(false);
        confirmBtn.addActionListener(e -> {
            String method = (String) methodCombo.getSelectedItem();
            String reference = referenceField.getText();
            // Pass a Runnable to close this dialog on success
            onConfirm.accept(method, reference);
            // Dialog will be disposed by the caller via Window.getWindows() scan or direct dispose
            dispose();
        });

        btnRow.add(cancelBtn);
        btnRow.add(confirmBtn);
        main.add(btnRow);

        setContentPane(main);
    }
}
