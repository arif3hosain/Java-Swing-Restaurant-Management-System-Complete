package com.rms.bill;

import com.rms.Frame2new;
import com.rms.service.AppService;
import com.rms.setting.Utils;
import db.DBConnection;
import dto.Category;
import dto.Item;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public class ReportBuilder extends JFrame{

    String columns[] = new String[] {"SERIAL","INVOICE_NO","CATEGORY","ITEM NAME","SIZE","RATE", "BILLED AT","METHOD"};
    AppService appService = new AppService();
    private JFrame mainFrame;
    JTable tbl = null;
    DefaultTableModel dtm = null;
    PreparedStatement pst;
    ResultSet rs;
    DBConnection con = new DBConnection();

    JCheckBox chkDiscount = new JCheckBox();
    JCheckBox chkVAT = new JCheckBox();
    JButton btnSearch = new JButton("Search");
    JComboBox<String> categoryCombo = new JComboBox<>();
    JComboBox<String> itemCombo = new JComboBox<>();
    JComboBox<String> paymentCombo = new JComboBox<>(new String[]{"All", "Cash", "MFS", "Credit"});

    JLabel message = new JLabel("...");


    public ReportBuilder(){
        mainFrame = new JFrame("Advance Reporting");
        mainFrame.setSize(1500,900);
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
        top.setBounds(0,0,1500,100);

        categoryCombo.setBounds(50, 50, 300, 35);
        categoryCombo.setFont(Utils.FONT_16);
        top.add(categoryCombo);

        itemCombo.setBounds(360, 50, 300, 35);
        itemCombo.setFont(Utils.FONT_16);
        top.add(itemCombo);

        paymentCombo.setBounds(670, 50, 100, 35);
        paymentCombo.setFont(Utils.FONT_16);
        top.add(paymentCombo);


        chkDiscount.setText("Include Discount");
        chkDiscount.setBounds(780, 50, 150, 35);
        chkDiscount.setFont(Utils.FONT_16);
        chkDiscount.setSelected(true);
        top.add(chkDiscount);

        chkVAT.setText("Include VAT");
        chkVAT.setBounds(940, 50, 120, 35);
        chkVAT.setFont(Utils.FONT_16);
        chkVAT.setSelected(true);
        top.add(chkVAT);


        btnSearch.setBounds(1070, 50, 80, 35);
        top.add(btnSearch);
        JPanel bottom = new JPanel(null);
        bottom.setBounds(0,100,1500,800);




        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchHistory();
            }
        });

        tbl = new JTable();
        dtm = new DefaultTableModel(0, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        dtm.setColumnIdentifiers(columns);
        tbl.setModel(dtm);
        tbl.setFont(Utils.FONT_16);
        tbl.getColumnModel().getColumn(0).setPreferredWidth(20);   // SERIAL - small
        tbl.getColumnModel().getColumn(1).setPreferredWidth(60);   // SERIAL - small
        tbl.getColumnModel().getColumn(2).setPreferredWidth(250);  // CREATED TIME - large
        tbl.getColumnModel().getColumn(3).setPreferredWidth(250);  // CREATED TIME - large
        tbl.getColumnModel().getColumn(4).setPreferredWidth(50);  // CREATED TIME - large
        tbl.getColumnModel().getColumn(5).setPreferredWidth(50);  // CREATED TIME - large
        tbl.getColumnModel().getColumn(6).setPreferredWidth(170);  // CREATED TIME - large
        tbl.getColumnModel().getColumn(7).setPreferredWidth(50);  // CREATED TIME - large

     //   initialFillUp("","");
        JScrollPane pane = new JScrollPane(tbl, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setBounds(0,0,1500,700);
        bottom.add(pane);


        mainFrame.add(top);
        mainFrame.add(bottom);
        if(!Frame2new.allow){
            btnSearch.setVisible(false);
            message.setBounds(590,55,400,25);
            message.setText("Billing period expired, contact Administrator at 01754282387");
        }
        fetchCategoryItem();
    }


    public void searchHistory(){
        dtm.setRowCount(0);
        String category = categoryCombo.getSelectedItem().toString();
        String item =  itemCombo.getSelectedItem().toString();
        String paymentMethod =  paymentCombo.getSelectedItem().toString();
        boolean includeDiscount = chkDiscount.isSelected();
        boolean includeVat = chkVAT.isSelected();

        List<Map<String,Object>> objectList =  appService.getBillDetails(category, item, paymentMethod,includeDiscount,includeVat );

        int i =1;
        for (Map<String,Object> obj : objectList) {
            String data[]  = new String[]{
                    String.valueOf(i),
                    Utils.getString(obj.get("invoiceNo")),
                    Utils.getString(obj.get("category")),
                    Utils.getString(obj.get("item")),
                    Utils.getString(obj.get("size")),
                    Utils.getString(obj.get("price")),
                    Utils.convertToTableDate(obj.get("createdDate")),
                    Utils.getString(obj.get("payment"))
            };
            dtm.addRow(data);
            i++;
        }
    }



    public void fetchCategoryItem(){
        AppService service  = new AppService();
        List<Category> categories = service.getCategory();
        categoryCombo.addItem("All");
        for (int i = 0; i < categories.size(); i++){
            categoryCombo.addItem(categories.get(i).name);
        }

        List<Item> items = service.getItems();
        itemCombo.addItem("All");
        for (int i = 0; i < items.size(); i++){
            itemCombo.addItem(items.get(i).name);
        }
    }


}