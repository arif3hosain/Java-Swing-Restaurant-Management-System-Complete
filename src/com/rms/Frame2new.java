package com.rms;

import com.rms.bill.BillHistory;
import com.rms.bill.GenerateBill;
import com.rms.bill.ReportBuilder;
import com.rms.category.CategoryView;
import com.rms.item.ItemView;
import com.rms.setting.Diff;
import com.rms.setting.Setting;
import com.rms.setting.Utils;
import db.DBConnection;
import dto.Role;

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
    JButton reports = new JButton("Reports");
    public static boolean allow = true;
    private static String from_id = null;

   public Frame2new(){
      prepareGUI();
   }

   private void prepareGUI(){
      mainFrame = new JFrame("Restaurant Management System ");
      mainFrame.setBounds(100,100,700,300);
      mainFrame.setLayout(new GridLayout(3,1));
      mainFrame.setResizable(false);
	  mainFrame.getContentPane().setBackground(Color.orange);

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
      countDay();
       try{
           mainFrame.setIconImage(ImageIO.read(new File(Utils.LOGO_PATH)));
       }
       catch (Exception ex){
           JOptionPane.showMessageDialog(null, Utils.LOGO_NOT_FOUND);
       }
       System.out.println("days limit "+Utils.daysLimit);
       System.out.println("day used  "+Utils.daysUsed);
       System.out.println();

       if(Utils.daysUsed > Utils.daysLimit ){
           JOptionPane.showMessageDialog(null, "Your billing period expired. Call at 01754282387 for renewal. ");
           btnItem.setEnabled(false);
           btnCategory.setEnabled(false);
           billHistory.setEnabled(false);
           btnBill.setEnabled(false);
           reports.setEnabled(false);
       }
       else if(Utils.daysUsed +3 >= Utils.daysLimit ){
           JOptionPane.showMessageDialog(null, "Your billing period will be expired soon. Call at 01754282387 for renewal. ");
       }
       mainFrame.setLocationRelativeTo(null);
   }


   public void showButtonDemo(){
		headerLabel.setText(Utils.TITLE);
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
            // new AutoSearch();
         new BillHistory();
         }
});

        setting.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Setting set = new Setting();
            set.showButtonDemo();
         }
});

       reports.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
           new ReportBuilder();
         }
});
      if(Utils.authority.role.equals(Role.ADMIN)) {
          controlPanel.add(btnItem);
          controlPanel.add(btnCategory);
          controlPanel.add(btnBill);
          controlPanel.add(billHistory);
          controlPanel.add(setting);
          controlPanel.add(reports);
      } if(Utils.authority.role.equals(Role.USER)) {
           controlPanel.add(btnBill);
           controlPanel.add(billHistory);
           controlPanel.add(reports);
       }
       mainFrame.setLocationRelativeTo(null);
       mainFrame.setVisible(true);



   }

    public static  void getDefaultValues() {
        ResultSet rs;
        PreparedStatement pst;
        DBConnection con = new DBConnection();
        try{
            pst = con.mkDataBase().prepareStatement("select * from keyvalue");
            rs = pst.executeQuery();
            String stored_days = null;

            while(rs.next()){
                Utils.REPORT_EXPORT_PATH = rs.getString("report_path");
                Utils.VAT = rs.getString("vat");
                Utils.DISCOUNT = rs.getString("discount");
                Utils.LOGO_PATH = rs.getString("logo");
                stored_days = rs.getString("duration_count");
                from_id = rs.getString("subscription_from");
            }
           Utils.daysLimit = Integer.parseInt(Diff.decrypt(stored_days));
            System.out.println("days limit "+Utils.daysLimit);
           Utils.billingId = Integer.parseInt(Diff.decrypt(from_id));
//           System.out.println(days +"::: "+fromID);
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
                    "where id > "+Utils.billingId+")x)data");
            rs = pst.executeQuery();
            while(rs.next()){
                Utils.daysUsed = rs.getInt("d");
            }
            rs.close();
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Changes found to the system!");
        }
       /* System.out.println("days: "+days);
        System.out.println("limit days: "+dayLimit);

        if(days > dayLimit){
            allow = false;
        }else if (days == dayLimit){
            JOptionPane.showMessageDialog(null, "Please unlock your subscription today to continue system use. ");
        }else if ((days+1) == dayLimit){
            JOptionPane.showMessageDialog(null, "System will be locked day after tomorrow. Please unlock your subscription.");
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
           }*/

    }




}
