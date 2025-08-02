package com.rms.service;

import com.rms.DataSource;
import com.rms.setting.Utils;
import db.DBConnection;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ReportService {

    ResultSet rs;
    PreparedStatement pst;
    DBConnection con = new DBConnection();


    public String  exportPDF(String fromDate,String toDate)  {
        String sql = "";
        if(fromDate != null || toDate != null) {
            sql = "select * from bill where created_date between '"+(fromDate+" 00:00:01")+"' and '"+(toDate+" 23:59:59")+"' order by id desc";
        }else{
            sql = "select * from bill order by id desc ";
        }
        try {
            pst = con.mkDataBase().prepareStatement(sql);
            rs = pst.executeQuery();
            int i = 0;
            int row = 0;
            String[] fields = new String[]{"created_date", "description", "vat_amt", "discount_amt", "total","amount"};
            List inList = new ArrayList();
            Map map = new HashMap();
            map.put("logo","/home/ahosain/Documents/personal/RMS/logo.png");

            while (rs.next()) {
                row ++;
                Date billingTime = rs.getTimestamp("created_date");
                String description = "";
                Double vat = rs.getDouble("vat_amt");
                Double discount = rs.getDouble("discount_amt");
                Double totalBill = rs.getDouble("total");
                Double foodBill = rs.getDouble("amount");
                inList.add(new Object[]{billingTime,description, vat, discount, totalBill, foodBill});
            }//rs.next();
            if(row > 0) {
                JasperPrint jasperPrint = null;
                InputStream jasperStream = null;
                jasperStream = new FileInputStream(new File("/home/ahosain/Documents/personal/RMS/palki_billing.jasper"));
//            jasperStream = this.getClass().getResourceAsStream(GET(INBOUND_TOKEN));
                jasperPrint = JasperFillManager.fillReport(jasperStream, map, new DataSource(inList, fields));
                JasperExportManager.exportReportToPdfFile(jasperPrint, Utils.REPORT_PATH +"(" + fromDate + ") - (" + toDate + ").PDF");
                return " Transactions have been exported!";
            }else{
              return "No data found to export!";
            }
        } catch (SQLException e) {
           return "Please input valid date format";
        } catch ( JRException e) {
            JOptionPane.showMessageDialog(null, "Warning when exporting report");
            return e.getMessage();
        }catch ( IOException e) {
            JOptionPane.showMessageDialog(null, "Please input correct format (e.g.YYYY-MM-DD");
            return e.getMessage();
        }
    }
}
