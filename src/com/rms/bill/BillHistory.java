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

    String columns[] = new String[] {"SERIAL","INVOICE NO","BILL TIME","CUSTOMER BILL","DISCOUNT","VAT","PAID BILL", "METHOD"};
    private JFrame mainFrame;
    JTable tbl = null;
    DefaultTableModel dtm = null;
    PreparedStatement pst;
    ResultSet rs;
    DBConnection con = new DBConnection();

    JButton btnSearch = new JButton("Search");
    JButton btnExportPDF = new JButton("Export PDF");
    JButton btnAdvanceReport = new JButton("Advance Search");
    JTextField fromDate = new JTextField("");
    JTextField toDate = new JTextField("");
    JLabel message = new JLabel("5 transactions have been found!");


    public BillHistory(){
        mainFrame = new JFrame("Bill History");
        mainFrame.setSize(1300,900);
        mainFrame.setResizable(false);
        mainFrame.setLayout(null);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        try{
            mainFrame.setIconImage(ImageIO.read(new File(Utils.LOGO_PATH)));
        }
        catch (Exception ex){
            JOptionPane.showMessageDialog(null, Utils.LOGO_NOT_FOUND);
        }

        JPanel top = new JPanel(null);
        top.setBackground(Color.orange);
        top.setBounds(0,0,1300,100);
        JPanel bottom = new JPanel(null);
       // bottom.setBackground(Color.orange);
        bottom.setBounds(0,100,1300,800);
        fromDate.setBounds(170,50,200,35);
        fromDate.setText(Utils.getTokenDate(new Date()));
        top.add(fromDate);
        toDate.setBounds(380,50,200,35);
        toDate.setText(Utils.getTokenDate(new Date()));
        top.add(toDate);
        btnSearch.setBounds(590,50,80,35);
        top.add(btnSearch);
        btnExportPDF.setBounds(680,50,100,35);
      //  top.add(btnExportPDF);
        btnAdvanceReport.setBounds(790,50,120,35);
        top.add(btnAdvanceReport);
        message.setBounds(950,50,300,25);
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

        btnAdvanceReport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              new ReportBuilder();
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
        tbl.setFont(Utils.FONT_16);
        // tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Allow manual sizing
        tbl.getColumnModel().getColumn(0).setPreferredWidth(30);   // SERIAL - small
        tbl.getColumnModel().getColumn(1).setPreferredWidth(100);   // SERIAL - small
        tbl.getColumnModel().getColumn(2).setPreferredWidth(200);  // CREATED TIME - large
        tbl.getColumnModel().getColumn(3).setPreferredWidth(80);  // CREATED TIME - large
        tbl.getColumnModel().getColumn(4).setPreferredWidth(50);  // CREATED TIME - large
        tbl.getColumnModel().getColumn(5).setPreferredWidth(50);  // CREATED TIME - large
        tbl.getColumnModel().getColumn(6).setPreferredWidth(80);  // CREATED TIME - large
        tbl.getColumnModel().getColumn(7).setPreferredWidth(70);  // CREATED TIME - large

        initialFillUp("","");
        JScrollPane pane = new JScrollPane(tbl, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setBounds(0,0,1300,700);
        bottom.add(pane);


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

            sql = "select * from bill order by id desc ";
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
                        rs.getString("invoice_no"),
                        Utils.dateToStr(rs.getTimestamp("created_date")),
                        rs.getString("total"),
                        rs.getString("discount_amt"),
                        rs.getString("vat_amt"),
                        rs.getString("amount"),
                        rs.getString("payment_method")
                       // , rs.getString("description")
                };
                dtm.addRow(data);
                i++;
            }
            message.setText(row +" Transactions have been found!");
        } catch (Exception e) {
            message.setText("Input correct date format - YYYY-MM-DD");
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
      //  exportPDF(inputFrom,inputTo);
    }



}