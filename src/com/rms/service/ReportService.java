package com.rms.service;

import com.rms.DataSource;
import com.rms.setting.Utils;
import db.DBConnection;
import dto.JasperFileName;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService {

    ResultSet rs;
    PreparedStatement pst;
    DBConnection con = new DBConnection();

    public void exportByItem(List<Map<String, Object>> data){
        try {
            String[] fields = new String[]{"serial","itemName","quantity", "amount", "unitPrice", "size"};
            List inList = new ArrayList();
            Map map = new HashMap();
            map.put("logo",Utils.LOGO_PATH);
            map.put("printedBy",Utils.authority.username);
             int i =0;
             for(Map<String, Object> obj : data){
                 i++;
                 String name = Utils.getString(obj.get("name"));
                 Integer unitPrice = Utils.getNumberValue(obj.get("unitPrice"));
                 String size = Utils.getString(obj.get("size"));
                 Integer quantity = Utils.getNumberValue(obj.get("quantity"));
                 Integer totalPrice = Utils.getNumberValue(obj.get("amount"));

                 inList.add(new Object[]{i,name, quantity, totalPrice, unitPrice, size});
            }
                JasperPrint jasperPrint = null;
                InputStream jasperStream = null;
                jasperStream = new FileInputStream(new File(Utils.JASPER_PATH + JasperFileName.ITEM_WISE_SALES_REPORT));
//            jasperStream = this.getClass().getResourceAsStream(GET(INBOUND_TOKEN));
                jasperPrint = JasperFillManager.fillReport(jasperStream, map, new DataSource(inList, fields));
                JasperExportManager.exportReportToPdfFile(jasperPrint, Utils.REPORT_EXPORT_PATH+"Item Wise Sales Report "+Math.random()+".PDF");

        } catch (JRException e) {
           // e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Warning when exporting report");

        }catch ( IOException e) {
           // e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Please input correct format (e.g.YYYY-MM-DD");

        }
    }
    public void exportByPaymentMethod(List<Map<String, Object>> data){
        try {
            String[] fields = new String[]{"name", "amount"};
            List inList = new ArrayList();
            Map map = new HashMap();
            map.put("logo",Utils.LOGO_PATH);
            map.put("printedBy",Utils.authority.username);
             for(Map<String, Object> obj : data){
                 String name = Utils.getString(obj.get("name"));
                 Integer amount = Utils.getNumberValue(obj.get("amount"));
                 inList.add(new Object[]{name, amount});
            }
                JasperPrint jasperPrint = null;
                InputStream jasperStream = null;
                jasperStream = new FileInputStream(new File(Utils.JASPER_PATH + JasperFileName.PAYMENT_METHOD_WISE_SALES_REPORT));
//            jasperStream = this.getClass().getResourceAsStream(GET(INBOUND_TOKEN));
                jasperPrint = JasperFillManager.fillReport(jasperStream, map, new DataSource(inList, fields));
                JasperExportManager.exportReportToPdfFile(jasperPrint, Utils.REPORT_EXPORT_PATH +"Sales Report by Payment Method "+Math.random()+".PDF");

        } catch (JRException e) {
          //  e.printStackTrace();
           JOptionPane.showMessageDialog(null, "Warning when exporting report");

        }catch ( IOException e) {
           // e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Please input correct format (e.g.YYYY-MM-DD");

        }
    }

    public void exportByCategory(List<Map<String, Object>> data){
        try {
            String[] fields = new String[]{"serial", "id","categoryName", "quantity","amount"};
            List inList = new ArrayList();
            Map map = new HashMap();
            map.put("logo",Utils.LOGO_PATH);
            map.put("printedBy",Utils.authority.username);
             int i =0;
             for(Map<String, Object> obj : data){
                 i++;
                 Integer id = Utils.getNumberValue(obj.get("catId"));
                 String categoryName = Utils.getString(obj.get("name"));
                 String quantity = Utils.getString(obj.get("quantity"));
                 Integer amount = Utils.getNumberValue(obj.get("amount"));

                 inList.add(new Object[]{i,id, categoryName, quantity, amount});
            }
                JasperPrint jasperPrint = null;
                InputStream jasperStream = null;
                jasperStream = new FileInputStream(new File(Utils.JASPER_PATH + JasperFileName.CATEGORY_WISE_SALES_REPORT));
                jasperPrint = JasperFillManager.fillReport(jasperStream, map, new DataSource(inList, fields));
                JasperExportManager.exportReportToPdfFile(jasperPrint, Utils.REPORT_EXPORT_PATH + "Category Wise Sales Report "+Math.random()+".PDF");

        } catch (JRException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Warning when exporting report");

        }catch ( IOException e) {
//            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Please check correct format (e.g.YYYY-MM-DD), "+e.getMessage());

        }
    }


    public void exportBillHistoryPDF(String fromDate, String toDate) {
        System.out.println("run this");
        StringBuilder sql = new StringBuilder("SELECT * FROM bill b WHERE b.delete = false");
        List<Object> params = new ArrayList<>();

        boolean hasFrom = fromDate != null && fromDate.trim().length() > 0;
        boolean hasTo = toDate != null && toDate.trim().length() > 0;

        if (hasFrom && hasTo) {
            sql.append(" AND b.created_date BETWEEN CAST(? AS TIMESTAMP) AND CAST(? AS TIMESTAMP)");
            params.add(fromDate.trim() + " 00:00:01");
            params.add(toDate.trim() + " 23:59:59");
        } else if (hasFrom) {
            sql.append(" AND b.created_date >= CAST(? AS TIMESTAMP)");
            params.add(fromDate.trim() + " 00:00:01");
        } else if (hasTo) {
            sql.append(" AND b.created_date <= CAST(? AS TIMESTAMP)");
            params.add(toDate.trim() + " 23:59:59");
        }

        sql.append(" ORDER BY b.id DESC");

        try {
            pst = con.mkDataBase().prepareStatement(sql.toString());

            // Bind parameters
            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i)); // JDBC params start at 1
            }

            rs = pst.executeQuery();
            int row = 0;
            String[] fields = {"creation", "amount", "discount", "vat", "billedAmount", "quantity", "serial", "invoiceNo"};
            List<Object[]> inList = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("from", fromDate);
            map.put("to", toDate);
            map.put("logo", Utils.LOGO_PATH);
            map.put("printedBy", Utils.authority.username);

            while (rs.next()) {
                row++;
            //    String billingTime = "abc";
                String billingTime = Utils.convertToTableDate(rs.getTimestamp("created_date"));
                String description = "";
                String vat = rs.getString("vat_amt");
                String discount = rs.getString("discount_amt");
                String amount = rs.getString("total");
                String billedAmount = rs.getString("amount");
                String invoiceNo = rs.getString("invoice_no");
                Date createdDate = rs.getDate("created_date");
                inList.add(new Object[]{billingTime,
                        Utils.getDoubleStringtoInteger(amount),
                        Utils.getDoubleStringtoInteger(discount),
                        Utils.getDoubleStringtoInteger(vat),
                        Utils.getDoubleStringtoInteger(billedAmount),
                         "", row,invoiceNo });
            }

            if (row > 0) {
                InputStream jasperStream = new FileInputStream(new File(Utils.JASPER_PATH + JasperFileName.HISTORY_REPORT));
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperStream, map, new DataSource(inList, fields));
                JasperExportManager.exportReportToPdfFile(jasperPrint, Utils.REPORT_EXPORT_PATH +
                        "(" + fromDate + ") - (" + toDate + ").PDF");
                JOptionPane.showMessageDialog(null, "Transactions have been exported!");
            } else {
                JOptionPane.showMessageDialog(null, "No data found to export");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Please input valid data format.");
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, "Warning when exporting report");
            e.printStackTrace();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Please input correct format (e.g. YYYY-MM-DD)");
            e.printStackTrace();
        }
    }

}
