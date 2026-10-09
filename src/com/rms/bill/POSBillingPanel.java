package com.rms.bill;

import com.rms.service.AppService;
import com.rms.setting.Utils;
import db.DBConnection;
import dto.Category;
import print.PrinterService;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static com.rms.setting.Utils.getString;
import static com.rms.setting.Utils.todayDateTime;

public class POSBillingPanel extends JPanel {

    private final CartManager cartManager = new CartManager();
    private CartPanel cartPanel = null;
    private final ItemGridPanel itemGridPanel;
    private final AppService appService = new AppService();
    private final DBConnection con = new DBConnection();
    private PreparedStatement pst;
    private ResultSet rs;

    public POSBillingPanel(JFrame parentFrame) {
        setLayout(new BorderLayout());

        // ---- Left panel ----
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(245, 247, 250));

        List<Category> categories = appService.getCategory();
        itemGridPanel = new ItemGridPanel(cartItem -> {
            cartManager.addToCart(cartItem.name, cartItem.size, cartItem.unitPrice);
            cartPanel.refresh();
        });

        CategoryTabBar tabBar = new CategoryTabBar(categories, categoryName -> {
            itemGridPanel.loadItems(categoryName);
        });

        leftPanel.add(tabBar, BorderLayout.NORTH);
        leftPanel.add(itemGridPanel, BorderLayout.CENTER);

        // ---- Right panel ----
        cartPanel = new CartPanel(cartManager);
        cartPanel.setOnPayClicked(payCtx -> {
            PaymentDialog dialog = new PaymentDialog(parentFrame, payCtx.totals.total,
                    (method, reference) -> {
                        Integer invoiceNo = saveTransaction(payCtx, method);
                        if (invoiceNo == null) {
                            JOptionPane.showMessageDialog(parentFrame, "Failed to save transaction. Please try again.");
                            return;
                        }
                        printReceipt(invoiceNo, payCtx, method, reference);
                        cartManager.clearCart();
                        cartPanel.refresh();
                        JOptionPane.showMessageDialog(parentFrame, "Transaction saved & printed! Invoice #" + invoiceNo);
                    });
            dialog.setVisible(true);
        });

        add(leftPanel, BorderLayout.CENTER);
        add(cartPanel, BorderLayout.EAST);

        // Initial load
        itemGridPanel.loadItems("");
    }

    private Integer saveTransaction(CartPanel.PaymentContext payCtx, String paymentMethod) {
        TotalsResult t = payCtx.totals;
        Integer invoiceNo = null;
        try {
            pst = con.mkDataBase().prepareStatement(
                    "INSERT INTO bill (created_date, description, vat_amt, discount_amt, total, amount, payment_method, created_by) " +
                            "VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            pst.setString(1, "");
            pst.setDouble(2, t.vatAmt);
            pst.setDouble(3, t.discountAmt);
            pst.setDouble(4, t.subtotal);
            pst.setDouble(5, t.total);
            pst.setString(6, paymentMethod);
            pst.setString(7, Utils.authority.username);
            pst.executeUpdate();

            int billId = -1;
            ResultSet genKeys = pst.getGeneratedKeys();
            if (genKeys.next()) billId = genKeys.getInt(1);

            PreparedStatement pst2 = con.mkDataBase().prepareStatement(
                    "SELECT invoice_no FROM bill WHERE id = ? AND delete = false");
            pst2.setInt(1, billId);
            ResultSet rs2 = pst2.executeQuery();
            if (rs2.next()) invoiceNo = rs2.getInt("invoice_no");

            saveBillDetails(billId, payCtx.items);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Save transaction error: " + ex.getMessage());
        }
        return invoiceNo;
    }

    private void saveBillDetails(int billId, List<CartItem> items) {
        for (CartItem f : items) {
            try {
                pst = con.mkDataBase().prepareStatement(
                        "INSERT INTO bill_details(food, size, quantity, per_unit_price, total_price, bill_id, item_id)" +
                                " VALUES (?,?,?,?,?,?,?)");
                pst.setString(1, f.name);
                pst.setString(2, f.size);
                pst.setInt(3, f.quantity);
                pst.setDouble(4, f.unitPrice);
                pst.setDouble(5, f.lineTotal());
                pst.setInt(6, billId);
                pst.setInt(7, getItemId(f.name, f.size));
                pst.execute();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private int getItemId(String itemName, String unit) {
        try {
            PreparedStatement ps = con.mkDataBase().prepareStatement(
                    "SELECT id FROM item WHERE item_name=? AND lower(quantity) LIKE lower(?)");
            ps.setString(1, itemName);
            ps.setString(2, "%" + unit + "%");
            ResultSet r = ps.executeQuery();
            if (r.next()) return r.getInt("id");
        } catch (Exception ignored) {}
        return 0;
    }

    private void printReceipt(int invoiceNo, CartPanel.PaymentContext payCtx, String paymentMethod, String reference) {
        StringBuilder fullText = new StringBuilder();
        String text = "";
        TotalsResult t = payCtx.totals;

        String line = Utils.TITLE;
        String fmt = String.format("%-" + (42 - line.length()) / 2 + "s", text);
        fullText.append(fmt).append(line).append(fmt).append("\n");

        line = "Signboard, Siddhirganj, Narayanganj";
        fmt = String.format("%-" + (42 - line.length()) / 2 + "s", text);
        fullText.append(fmt).append(line).append(fmt);

        line = "Cell: 01600101001";
        fmt = "\n" + String.format("%-" + (42 - line.length()) / 2 + "s", text);
        fullText.append(fmt).append(line).append(fmt).append("\n");

        line = todayDateTime();
        fmt = String.format("%-" + (42 - line.length()) / 2 + "s", text);
        fullText.append(fmt).append(line).append(fmt).append("\n");

        line = "Invoice: " + invoiceNo;
        fmt = String.format("%-" + (42 - line.length()) / 2 + "s", text);
        fullText.append(fmt).append(line).append(fmt).append("\n");

        line = "User : " + Utils.authority.username;
        fmt = String.format("%-" + (42 - line.length()) / 2 + "s", text);
        fullText.append(fmt).append(line).append(fmt).append("\n\n");

        fullText.append("Item")
                .append(String.format("%-" + (42 - 22) + "s", text)).append("Qty X Rate")
                .append(String.format("%-" + (42 - 39) + "s", text)).append("Price").append("\n");
        fullText.append("==========================================\n");

        int products = 0;
        for (CartItem fc : payCtx.items) {
            products++;
            long rate = Math.round(fc.lineTotal());
            line = fc.name + " (" + fc.quantity + "x" + Math.round(fc.unitPrice) + ")" + rate;
            line = fc.name + " (" + fc.quantity + "x" + Math.round(fc.unitPrice) + ")" +
                    String.format("%-" + (42 - line.length()) + "s", text) + Math.round(rate) + "\n";
            fullText.append(line);
        }

        fullText.append("\n------------------------------------------\n");
        line = "Discount(" + payCtx.discountPctStr + "%)" + "(-) " + t.discountAmt;
        fullText.append("Discount(").append(payCtx.discountPctStr).append("%)").append(
                String.format("%-" + (44 - line.length()) + "s", text)).append("(-) ").append(Math.round(t.discountAmt)).append("\n");

        line = "VAT(" + Utils.VAT + "%)" + "(+) " + t.vatAmt;
        fullText.append("VAT(").append(Utils.VAT).append("%)").append(
                String.format("%-" + (44 - line.length()) + "s", text)).append("(+) ").append(Math.round(t.vatAmt));

        fullText.append("\n------------------------------------------\n");
        line = "Grand Total (Payment Method: " + paymentMethod + ")" + t.total;
        line = "Grand Total (Payment Method: " + paymentMethod + ")" +
                String.format("%-" + (44 - line.length()) + "s", text) + Math.round(t.total);
        fullText.append(line).append("\n");
        fullText.append("==========================================\n\n");

        line = "Number of Item purchased: " + products + "\n";
        fmt = String.format("%-" + (42 - line.length()) / 2 + "s", text);
        fullText.append(fmt).append(line).append(fmt).append("\n");

        // Note / reference
        if (reference != null && !reference.trim().isEmpty()) {
            int maxLineLength = 42;
            String[] words = reference.split("\\s+");
            StringBuilder wrappedNote = new StringBuilder("Note : ");
            int currentLength = 7;
            for (String word : words) {
                if (currentLength + word.length() + 1 > maxLineLength) {
                    wrappedNote.append("\n       ");
                    currentLength = 7 + word.length() + 1;
                } else {
                    currentLength += word.length() + 1;
                }
                wrappedNote.append(word).append(" ");
            }
            fullText.append(wrappedNote).append("\n\n");
        }

        line = "Powered By: www.facebook.com/easybilling";
        fmt = String.format("%-" + (42 - line.length()) / 2 + "s", text);
        fullText.append(fmt).append(line).append(fmt).append("\n");

        line = "System generated, no sign required.";
        fmt = String.format("%-" + (42 - line.length()) / 2 + "s", text);
        fullText.append(fmt).append(line).append(fmt).append("\n\n");

        PrinterService printerService = new PrinterService();
        printerService.printString("SEWOO SLK-TS100", fullText.toString());
        byte[] cutP = new byte[]{0x1d, 'V', 1};
        printerService.printBytes("SEWOO SLK-TS100", cutP);
    }
}
