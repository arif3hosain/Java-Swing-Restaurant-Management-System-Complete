package com.rms.bill;

import com.rms.Frame2new;
import db.DBConnection;
import com.rms.DataSource;
import com.rms.setting.Utils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class BillHistory extends JFrame{

    String columns[] = new String[] {"Serial","Bill Time","Customer Bill","VAT","Discount","Total Bill"};
    private JFrame mainFrame;
    JTable tbl = null;
    DefaultTableModel dtm = null;
    PreparedStatement pst;
    ResultSet rs;
    DBConnection con = new DBConnection();
    Font font = new Font("SansSerif", Font.BOLD, 15);
    JButton btnSearch = new JButton("Search");
    JButton btnExportPDF = new JButton("Export PDF");
    JTextField fromDate = new JTextField("DD/MM/YYYY");
    JTextField toDate = new JTextField("DD/MM/YYYY");
    JLabel message = new JLabel("5 transactions have been found!");


    public BillHistory(){
        mainFrame = new JFrame("Bill Generator");
        mainFrame.setSize(1100,900);
        mainFrame.setResizable(false);
        mainFrame.setLayout(null);
        mainFrame.setVisible(true);
        try{
            mainFrame.setIconImage(ImageIO.read(new File(Frame2new.logo)));
        }
        catch (Exception ex){
            JOptionPane.showMessageDialog(null, "Logo not found!");
        }

        JPanel top = new JPanel(null);
        top.setBackground(Color.lightGray);
        top.setBounds(0,0,1100,100);
        JPanel bottom = new JPanel(null);
        bottom.setBackground(Color.orange);
        bottom.setBounds(0,100,1100,800);
        fromDate.setBounds(170,50,200,35);
        fromDate.setText(Utils.getTokenDate(new Date()));
        top.add(fromDate);
        toDate.setBounds(380,50,200,35);
        toDate.setText(Utils.getTokenDate(new Date()));
        top.add(toDate);
        btnSearch.setBounds(590,50,80,35);
        top.add(btnSearch);
        btnExportPDF.setBounds(680,50,100,35);
        top.add(btnExportPDF);
        message.setBounds(800,50,300,25);
        top.add(message);




        btnSearch.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
               searchHistory();
           }
       });
        btnExportPDF.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              dataPass();
           }
       });

        tbl = new JTable();
        dtm = new DefaultTableModel(0, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        dtm.setColumnIdentifiers(columns);
        tbl.setModel(dtm);
        tbl.setFont(font);
        initialFillUp("","");
        JScrollPane pane = new JScrollPane(tbl, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setBounds(0,0,1100,700);
        bottom.add(pane);
        tbl.setFont(font);


        mainFrame.add(top);
        mainFrame.add(bottom);
        if(!Frame2new.allow){
            btnExportPDF.setVisible(false);
            btnSearch.setVisible(false);
            message.setBounds(590,55,400,25);
            message.setText("Billing period expired, contact Administrator at 01754282387");
        }
    }


    public void initialFillUp(String fromDate,String toDate) {
        message.setText(0 +" Transactions have been found!");
        String sql = "";
        if(fromDate.trim().length() >0 & toDate.trim().length() >0) {
            sql = "select * from bill where created_date between '"+(fromDate+" 00:00:01")+"' and '"+(toDate+" 23:59:59")+"' order by id desc";
        }else{
          
            sql = "select * from bill order by id desc limit 300";
        }
            try {
            pst = con.mkDataBase().prepareStatement(sql);
            rs = pst.executeQuery();
            int i = 0;
            int row = 0;
            while (rs.next()) {
                row++;
                String data[] = new String[]{
                        String.valueOf(row),
                        Utils.dateToStr(rs.getTimestamp("created_date")),
                        rs.getString("amount"),
                        rs.getString("vat_amt"),
                        rs.getString("discount_amt"),
                        rs.getString("total")};
                dtm.addRow(data);
                i++;
            }
            message.setText(row +" Transactions have been found!");
        } catch (Exception e) {
                message.setText("Input correct date format - DD/MM/YYYY");
        }
    }


    public void exportPDF(String fromDate,String toDate)  {
        message.setText(0 +" Transactions have been found!");
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
                map.put("param1","");

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
                    jasperStream = new FileInputStream(new File("D:/app/palki_billing.jasper"));
//            jasperStream = this.getClass().getResourceAsStream(GET(INBOUND_TOKEN));
                    jasperPrint = JasperFillManager.fillReport(jasperStream, map, new DataSource(inList, fields));
                    JasperExportManager.exportReportToPdfFile(jasperPrint, Frame2new.reportPath +"(" + fromDate + ") - (" + toDate + ").PDF");
                    message.setText(row + " Transactions have been exported!");
                }else{
                    message.setText("No data found to export!");
                }
            } catch (SQLException e) {
                //e.printStackTrace();
                message.setText("Please input valid date format !");
               // //e.printStackTrace();
                //message.setText("Input correct date format - DD/MM/YYYY");
            } catch ( JRException e) {
                //e.printStackTrace();
                message.setText("Warning when exporting report");
                JOptionPane.showMessageDialog(null, "Warning when exporting report");
            }catch ( IOException e) {
                //e.printStackTrace();
                message.setText("Please input date with correct format (e.g. 20-06-2021");
                JOptionPane.showMessageDialog(null, "Please input correct format (e.g.yyyy/mm/dd");
            }
    }


    public void searchHistory(){
        dtm.setRowCount(0);
        String inputFrom = fromDate.getText();
        String inputTo = toDate.getText();
        initialFillUp(inputFrom,inputTo);
    }

    public void dataPass(){
        dtm.setRowCount(0);
        String inputFrom = fromDate.getText();
        String inputTo = toDate.getText();
        exportPDF(inputFrom,inputTo);
    }



}