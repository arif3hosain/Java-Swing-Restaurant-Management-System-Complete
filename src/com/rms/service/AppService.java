package com.rms.service;

import com.rms.setting.Utils;
import db.DBConnection;
import dto.Authority;
import dto.Category;
import dto.Item;
import dto.Role;

import javax.swing.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppService {

    ResultSet rs;
    PreparedStatement pst;
    DBConnection con = new DBConnection();


    public boolean login(String username, String password) {
        boolean authenticated = false;

        try {
            String sql = "SELECT u.id, u.username, u.full_name, r.name AS role_name " +
                    "FROM app_user u " +
                    "LEFT JOIN role r ON u.role_id = r.id " +
                    "WHERE u.username = ? AND u.password = ? AND u.is_active = TRUE";

            pst = con.mkDataBase().prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            rs = pst.executeQuery();

            if (rs.next()) {
                String fullName = rs.getString("full_name");
                String roleName = rs.getString("role_name");
                Role role = Role.valueOf(roleName.toUpperCase());
                Utils.authority =  new Authority(username, fullName, role);
                authenticated = true;
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, "Unknown role in DB.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Login error: " + e.getMessage());
        }

        return authenticated;
    }




    public String[] getFoodTypes() {
        List<String> sizes = new ArrayList();
        try {
            pst = con.mkDataBase().prepareStatement("SELECT name FROM food_size");
            rs = pst.executeQuery();
            while (rs.next()) {
                sizes.add(rs.getString("name"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return sizes.toArray(new String[0]);
    }


    public List<Category> getCategory() {
        List<Category> categories = new ArrayList();
        Category category = null;
        try {
            pst = con.mkDataBase().prepareStatement("select * from category where deleted = false order by name");
            rs = pst.executeQuery();
            int i = 1;
            while (rs.next()) {
                category = new Category();
                category.id =  rs.getString("id");
                category.name = rs.getString("name");
                category.description = rs.getString("description");
                category.status = rs.getString("status");
                categories.add(category);
                i++;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return categories;
    }

    public List<Item> getItems() {
        List<Item> items = new ArrayList();
        Item item = null;
        try {
            pst = con.mkDataBase().prepareStatement("select * from item where deleted = false order by item_name");
            rs = pst.executeQuery();
            int i = 1;
            while (rs.next()) {
                item = new Item();
                item.name = rs.getString("item_name");
                items.add(item);
                i++;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return items;
    }

    public List<Item> getUniqueItems() {
        List<Item> items = new ArrayList();
        Item item = null;
        try {
            pst = con.mkDataBase().prepareStatement("select distinct item_name from item where deleted = false order by item_name");
            rs = pst.executeQuery();
            int i = 1;
            while (rs.next()) {
                item = new Item();
                item.name = rs.getString("item_name");
                items.add(item);
                i++;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return items;
    }


    public List<Map<String, Object>> getSummaryByPaymentMethod(
            String cat,           // e.g., "Beef" or "All"
            String itemName,      // e.g., "Beef Kolija" or "All"/""
            String paymentMethod, // e.g., "CASH" or "All"/""
            boolean includeDiscount,
            boolean includeVAT
    ) {
        List<Map<String, Object>> rows = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT\n" +
                        "    b.payment_method,\n" +
                        "    ROUND(SUM(b.amount), 2) AS customerBill\n" +
                        "FROM category c\n" +
                        "         JOIN item i\n" +
                        "              ON c.id = i.cat_id\n" +
                        "         JOIN bill_details dtl\n" +
                        "              ON i.id = dtl.item_id\n" +
                        "         JOIN bill b\n" +
                        "              ON b.id = dtl.bill_id\n" +
                        " where 1=1 "
        );

        List<Object> params = new ArrayList<>();

        // Filters (treat null/empty/"All" as no filter)
        if (cat != null && !cat.trim().isEmpty() && !"All".equalsIgnoreCase(cat)) {
            sql.append("AND c.name = ? ");
            params.add(cat.trim());
        }
        if (itemName != null && !itemName.trim().isEmpty() && !"All".equalsIgnoreCase(itemName)) {
            sql.append("AND i.item_name = ? ");
            params.add(itemName.trim());
        }
        if (paymentMethod != null && !paymentMethod.trim().isEmpty() && !"All".equalsIgnoreCase(paymentMethod)) {
            sql.append("AND b.payment_method = ? ");
            params.add(paymentMethod.trim());
        }

        // Discount/VAT include flags
        if (!includeDiscount) {
            sql.append("AND b.discount_amt = 0.00 ");
        }
        if (!includeVAT) {
            sql.append("AND b.vat_amt = 0.00 ");
        }

        sql.append("GROUP BY  b.payment_method ORDER BY b.payment_method ");

        try (PreparedStatement pst = con.mkDataBase().prepareStatement(sql.toString())) {
            // bind parameters
            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("name", rs.getString("payment_method"));
                    row.put("amount", rs.getInt("customerBill")); // rounded in SQL
                    rows.add(row);
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading summary by payment method: " + e.getMessage());
        }

        return rows;
    }



    public List<Map<String, Object>> getSummaryByItem(
            String cat,           // e.g., "Beef" or "All"
            String itemName,      // e.g., "Beef Kolija" or "All"/""
            String paymentMethod, // e.g., "CASH" or "All"/""
            boolean includeDiscount,
            boolean includeVAT
    ) {
        List<Map<String, Object>> rows = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT\n" +
                        "    i.item_name,\n" +
                        "    i.quantity,\n" +
                        "    ROUND(SUM(b.amount), 2) AS customerBill,\n" +
                        "    ROUND(SUM(discount_amt), 2) AS discount,\n" +
                        "    ROUND(SUM(vat_amt), 2) AS vat,\n" +
                        "    ROUND(SUM(total), 2) AS paid\n" +
                        "FROM category c\n" +
                        "         JOIN item i\n" +
                        "              ON c.id = i.cat_id\n" +
                        "         JOIN bill_details dtl\n" +
                        "              ON i.id = dtl.item_id\n" +
                        "         JOIN bill b\n" +
                        "              ON b.id = dtl.bill_id\n" +
                        " where 1=1 "
        );

        List<Object> params = new ArrayList<>();

        // Filters (treat null/empty/"All" as no filter)
        if (cat != null && !cat.trim().isEmpty() && !"All".equalsIgnoreCase(cat)) {
            sql.append("AND c.name = ? ");
            params.add(cat.trim());
        }
        if (itemName != null && !itemName.trim().isEmpty() && !"All".equalsIgnoreCase(itemName)) {
            sql.append("AND i.item_name = ? ");
            params.add(itemName.trim());
        }
        if (paymentMethod != null && !paymentMethod.trim().isEmpty() && !"All".equalsIgnoreCase(paymentMethod)) {
            sql.append("AND b.payment_method = ? ");
            params.add(paymentMethod.trim());
        }

        // Discount/VAT include flags
        if (!includeDiscount) {
            sql.append("AND b.discount_amt = 0.00 ");
        }
        if (!includeVAT) {
            sql.append("AND b.vat_amt = 0.00 ");
        }

        sql.append("GROUP BY  i.item_name, i.quantity ");
        sql.append("ORDER BY i.item_name");

        try (PreparedStatement pst = con.mkDataBase().prepareStatement(sql.toString())) {
            // bind parameters
            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("name", rs.getString("item_name"));
                    row.put("quantity", rs.getString("quantity"));
                    row.put("customerBill", rs.getInt("customerBill")); // rounded in SQL
                    row.put("discount", rs.getInt("discount"));
                    row.put("vat", rs.getInt("vat"));
                    row.put("paid", rs.getInt("paid"));
                    rows.add(row);
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading summary by item: " + e.getMessage());
        }

        return rows;
    }

    public List<Map<String, Object>> getSummaryByCategory(

            String cat,           // e.g., "Beef" or "All"
            String itemName,      // e.g., "Beef Kolija" or "All"/""
            String paymentMethod, // e.g., "CASH" or "All"/""
            boolean includeDiscount,
            boolean includeVAT
    ) {
        List<Map<String, Object>> rows = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT c.id, c.name, " +
                        "       ROUND(SUM(b.amount), 2)       AS customerBill, " +
                        "       ROUND(SUM(b.discount_amt), 2) AS discount, " +
                        "       ROUND(SUM(b.vat_amt), 2)      AS vat, " +
                        "       ROUND(SUM(b.total), 2)        AS paid " +
                        "FROM category c " +
                        "JOIN item i         ON c.id = i.cat_id " +
                        "JOIN bill_details d ON i.id = d.item_id " +
                        "JOIN bill b         ON b.id = d.bill_id " +
                        "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        // Filters (treat null/empty/"All" as no filter)
        if (cat != null && !cat.trim().isEmpty() && !"All".equalsIgnoreCase(cat)) {
            sql.append("AND c.name = ? ");
            params.add(cat.trim());
        }
        if (itemName != null && !itemName.trim().isEmpty() && !"All".equalsIgnoreCase(itemName)) {
            sql.append("AND i.item_name = ? ");
            params.add(itemName.trim());
        }
        if (paymentMethod != null && !paymentMethod.trim().isEmpty() && !"All".equalsIgnoreCase(paymentMethod)) {
            sql.append("AND b.payment_method = ? ");
            params.add(paymentMethod.trim());
        }

        // Discount/VAT include flags
        if (!includeDiscount) {
            sql.append("AND b.discount_amt = 0.00 ");
        }
        if (!includeVAT) {
            sql.append("AND b.vat_amt = 0.00 ");
        }

        sql.append("GROUP BY c.id, c.name ");
        sql.append("ORDER BY c.name");

        try (PreparedStatement pst = con.mkDataBase().prepareStatement(sql.toString())) {
            // bind parameters
            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getLong("id"));
                    row.put("name", rs.getString("name"));
                    row.put("customerBill", rs.getInt("customerBill")); // rounded in SQL
                    row.put("discount", rs.getInt("discount"));
                    row.put("vat", rs.getInt("vat"));
                    row.put("paid", rs.getInt("paid"));
                    rows.add(row);
                }
            }
        } catch (Exception e) {
           // e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading summary by category: " + e.getMessage());
        }

        return rows;
    }



    public List<Map<String, Object>> getBillDetails(String cat, String item, String method, boolean includeDiscount, boolean includeVAT) {
        List<Map<String, Object>> billDetails = new ArrayList<>();
        Map<String, Object> dtl;

        try {
            StringBuilder sql = new StringBuilder(
                    "SELECT DISTINCT cat.name AS category, detail.food,item.price, detail.size, b.created_date, " +
                            "b.invoice_no, b.amount, b.discount_amt, b.vat_amt,b.payment_method " +
                            "FROM bill_details detail " +
                            "JOIN bill b ON b.id = detail.bill_id " +
                            "JOIN item ON item.item_name = detail.food " +
                            "JOIN category cat ON cat.id = item.cat_id " +
                            "WHERE 1=1 "
            );

            List<String> params = new ArrayList<>();

            if (!"All".equalsIgnoreCase(cat)) {
                sql.append("AND cat.name = ? ");
                params.add(cat);
            }
            if (!"All".equalsIgnoreCase(item)) {
                sql.append("AND detail.food = ? ");
                params.add(item);
            }
            if (!"All".equalsIgnoreCase(method)) {
                sql.append("AND b.payment_method = ? ");
                params.add(method);
            }
            if (!includeDiscount) {
                sql.append("AND b.discount_amt = 0.00 ");
            }
            if (!includeVAT) {
                sql.append("AND b.vat_amt = 0.00 ");
            }

            sql.append("ORDER BY b.created_date DESC");

            pst = con.mkDataBase().prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                pst.setString(i + 1, params.get(i));
            }

            rs = pst.executeQuery();
            while (rs.next()) {
                dtl = new HashMap<>();
                dtl.put("category", rs.getString("category"));
                dtl.put("item", rs.getString("food"));
                dtl.put("size", rs.getString("size"));
                dtl.put("createdDate", rs.getString("created_date"));
                dtl.put("invoiceNo", rs.getString("invoice_no"));
                dtl.put("amount", rs.getString("amount"));
                dtl.put("price", rs.getString("price"));
                dtl.put("discount", rs.getString("discount_amt"));
                dtl.put("vat", rs.getString("vat_amt"));
                dtl.put("payment", rs.getString("payment_method"));
                billDetails.add(dtl);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading bill details: " + e.getMessage());
        }

        return billDetails;
    }


}
