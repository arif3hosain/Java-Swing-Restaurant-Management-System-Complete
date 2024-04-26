package com.rms.setting;

import com.rms.Frame2new;
import com.rms.setting.Utils;
import db.DBConnection;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Setting {

    private JFrame mainFrame;
    private JLabel headerLabel;
    Object[] data = new Object[500];
    private JLabel id,name,price,quantity;
    private static int count = 0;
    Integer itemCount = 0;
    GridLayout experimentLayout = new GridLayout(0,2);
    ResultSet rs;
    PreparedStatement pst;
    DBConnection con = new DBConnection();
    JPanel bottomPanel = new JPanel(null);
    JTextField txtReportPath = null;
    JTextField txtLogoPath = null;
    JTextField txtVAT = null;
    JTextField txtDiscount = null;
    JTextField key = null;

    public Setting(){
        prepareGUI();
    }

    private void prepareGUI(){
        mainFrame = new JFrame("Store Setting");
        mainFrame.setSize(430,500);
        mainFrame.setLayout(null);
        mainFrame.setResizable(false);
        mainFrame.getContentPane().setBackground(Color.gray);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                mainFrame.setVisible(false);
            }
        });
        try{
            mainFrame.setIconImage(ImageIO.read(new File(Frame2new.logo)));
        }
        catch (Exception ex){
            JOptionPane.showMessageDialog(null, "Logo not found!");
        }
        headerLabel = new JLabel("Store system data!", JLabel.CENTER);
        headerLabel.setBounds(50,10,300,50);
        JPanel topPanel = new JPanel(null);
        topPanel.setBackground(Color.orange);
        topPanel.setBounds(0,0,500,70);
        topPanel.add(headerLabel);

        
        bottomPanel.setBounds(0,70,500,400);
        mainFrame.add(bottomPanel);

        mainFrame.add(headerLabel);
        mainFrame.add(topPanel);
        mainFrame.setVisible(true);
    }


    public void showButtonDemo(){

        Object[] items = new Object[itemCount+1];
        for(int i =0; i<(itemCount); i++){
            items[i] = data[i];
        }

        headerLabel.setText("Store System Data");
        headerLabel.setFont(new Font(null, Font.BOLD, 16));


        JLabel reportPath = new JLabel("Report Export Path");
        reportPath.setBounds(10,50,150,30);
        JLabel logoPath = new JLabel("Logo Path");
        logoPath.setBounds(10,90,150,30);
        JLabel vat = new JLabel("VAT");
        vat.setBounds(10,130,150,30);
        JLabel discount = new JLabel("Discount");
        discount.setBounds(10,170,150,30);

        JLabel subscriptionKey = new JLabel("Subscription Key");
        subscriptionKey.setBounds(10,210,150,30);




        txtReportPath = new JTextField();
        txtReportPath.setBounds(140,50,150,30);
        txtLogoPath = new JTextField();
        txtLogoPath.setBounds(140,90,150,30);
        txtVAT = new JTextField();
        txtVAT.setBounds(140,130,150,30);
        txtDiscount = new JTextField();
        txtDiscount.setBounds(140,170,150,30);
        key = new JTextField();
        key.setBounds(140,210,280,30);
        JButton btnSave = new JButton("Save");
        btnSave.setBounds(140,280,70,30);


        bottomPanel.add(reportPath);
        bottomPanel.add(logoPath);
        bottomPanel.add(vat);
        bottomPanel.add(discount);
        bottomPanel.add(subscriptionKey);
        bottomPanel.add(txtReportPath);
        bottomPanel.add(txtLogoPath);
        bottomPanel.add(txtVAT);
        bottomPanel.add(txtDiscount);
        bottomPanel.add(key);
        bottomPanel.add(btnSave);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);


        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveAll();
            }
        });
        setData();
    }

    public void saveAll(){
        String reportPath = Utils.getString(txtReportPath.getText());
        String logoPath = Utils.getString(txtLogoPath.getText());
        String vat = Utils.getString(txtVAT.getText());
        String discount = Utils.getString(txtDiscount.getText());
            try {
                System.out.println("---------------1");
                pst = con.mkDataBase().prepareStatement("update keyvalue set report_path = ?, logo = ?, vat = ?, discount,subscription_from = ?  Where id = 1 ");
                pst.setString(1, reportPath);
                pst.setString(2, logoPath);
                pst.setString(3, vat);
                pst.setString(4, discount);
                pst.setString(5, key.getText());
                pst.execute();
                JOptionPane.showMessageDialog(null, "Data has been successfully updated.");
                mainFrame.setVisible(false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Please input correct data format!");
            }
    }

    public void setData(){
        try{
            pst = con.mkDataBase().prepareStatement("select * from keyvalue");
            rs = pst.executeQuery();
            int i=1;
            while(rs.next()){
                txtReportPath.setText(rs.getString("report_path"));
                txtLogoPath.setText(rs.getString("logo"));
                txtVAT.setText(rs.getString("vat"));
                txtDiscount.setText(rs.getString("discount"));
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "set data 163");
        }

    }
}

