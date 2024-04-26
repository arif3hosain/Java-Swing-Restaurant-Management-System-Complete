package com.rms;
import com.rms.bill.BillHistory;
import com.rms.bill.GenerateBill;
import com.rms.category.CategoryView;
import com.rms.item.ItemView;
import com.rms.setting.Diff;
import com.rms.setting.Setting;
import db.DBConnection;

import java.awt.*;
import java.awt.event.*;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Color;
import java.io.File;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Frame2new {

    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JPanel controlPanel;
    JButton btnItem = new JButton("Item Info");
    JButton btnBill = new JButton("Generate Bill");
    JButton btnCategory = new JButton("Category");
    JButton billHistory = new JButton("Bill History");
    JButton setting = new JButton("Setting");
    public static String reportPath;
    public static String vat;
    public static String discount;
    public static String logo = "D:/pizzacaptain/logo.jpeg";
    public static Integer fromID;
    public static Integer storedDays;
    public static Integer days;
    public static boolean allow = true;
    static int dayLimit = 45;
    int exitLimit = dayLimit+3;


   public Frame2new(){
      prepareGUI();
   }

   private void prepareGUI(){
      mainFrame = new JFrame("Restaurant Management System ");
      mainFrame.setBounds(100,100,700,300);
      mainFrame.setLayout(new GridLayout(3,1));
      mainFrame.setResizable(false);
	  mainFrame.getContentPane().setBackground(Color.orange);
       getCategory();
       countDay();
      mainFrame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent windowEvent){
            System.exit(0);
         }
      });
      headerLabel = new JLabel("", JLabel.CENTER);
      statusLabel = new JLabel("",JLabel.CENTER);
      statusLabel.setSize(350,300);
      controlPanel = new JPanel();
      controlPanel.setLayout(new GridLayout(1,3));
      mainFrame.add(headerLabel);
      mainFrame.add(controlPanel);
      mainFrame.add(statusLabel);

       try{
           mainFrame.setIconImage(ImageIO.read(new File(Frame2new.logo)));
       }
       catch (Exception ex){
           JOptionPane.showMessageDialog(null, "Logo not found!");
       }

       if(days >= exitLimit){
           JOptionPane.showMessageDialog(null, "You billing period has been expired with additional 3 days. Call at 01754282387 for help. ");
           btnItem.setEnabled(false);
           btnCategory.setEnabled(false);
           billHistory.setEnabled(false);
           btnBill.setEnabled(false);
       }
       mainFrame.setVisible(true);
   }


   public void showButtonDemo(){
		headerLabel.setText("Palki Chinese Restaurant");
		this.headerLabel.setFont(new Font(null, Font.BOLD, 27));
		headerLabel.setForeground(Color.white);

       btnItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
           ItemView itemView=new ItemView();
             itemView.showButtonDemo();
         }
});

       btnCategory.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
           CategoryView cv=new CategoryView();
             cv.showButtonDemo();
         }
});

		
        btnBill.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            GenerateBill gb=new GenerateBill();
         }
});

        billHistory.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
             BillHistory  bh = new BillHistory();
         }
});

        setting.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Setting set = new Setting();
            set.showButtonDemo();
         }
});
      controlPanel.add(btnItem);
	  controlPanel.add(btnCategory);
	  controlPanel.add(btnBill);
	  controlPanel.add(billHistory);
	  controlPanel.add(setting);
      mainFrame.setVisible(true);
	  mainFrame.setLocationRelativeTo(null);


   }

    public static  void getCategory() {
        ResultSet rs;
        PreparedStatement pst;
        DBConnection con = new DBConnection();
        try{
            pst = con.mkDataBase().prepareStatement("select * from keyvalue");
            rs = pst.executeQuery();
            String stored_days = null;
            String from_id = null;
            while(rs.next()){
                reportPath = rs.getString("report_path");
                vat = rs.getString("vat");
                discount = rs.getString("discount");
                logo = rs.getString("logo");
                stored_days = rs.getString("duration_count");
                from_id = rs.getString("subscription_from");
            }
           days = Integer.parseInt(Diff.decrypt(stored_days));
           fromID = Integer.parseInt(Diff.decrypt(from_id));
        }catch(Exception e){
            //e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Data changes has been found!");
        }
    }


    public static  void countDay()  {
        ResultSet rs;
        PreparedStatement pst;
        DBConnection con = new DBConnection();
        try{
            pst = con.mkDataBase().prepareStatement("select count(*)d from\n" +
                    "(select distinct x.days from\n" +
                    "(select id,cast( created_date as date) days from bill\n" +
                    "where id > "+fromID+")x)data");
            rs = pst.executeQuery();
            while(rs.next()){
                days = rs.getInt("d");
            }
            rs.close();
        }catch(Exception e){
         //   //e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Changes found to the system!");
        }
        if(days > dayLimit){
            allow = false;
        }else if (days == dayLimit){
            JOptionPane.showMessageDialog(null, "Your monthly subscription will be ended today, Please update your limit! ");
        }else if ((days+1) == dayLimit){
            JOptionPane.showMessageDialog(null, "Your monthly subscription will be ended day after tomorrow, Please renew subscription! ");
        }

           if(!days.equals(storedDays)){
               try {
                   pst = con.mkDataBase().prepareStatement("update keyvalue set duration_count=? Where id = 1 ");
                   pst.setString(1, Diff.encrypt(days.toString()));
                   pst.execute();
               }catch (SQLException | NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e){
                  ////e.printStackTrace();
                   JOptionPane.showMessageDialog(null, "SQL Error found!");
               }
           }else{
               System.out.println(days);
               System.out.println(storedDays);
           }

    }




}
