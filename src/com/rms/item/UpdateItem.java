package com.rms.item;

import com.rms.Frame2new;
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

public class UpdateItem {

    private JFrame mainFrame;
    private JLabel headerLabel;
    Object[] data = new Object[500];
    JLabel id,name,description,price,vat,quantity;
    private static int count = 0;
    Integer itemCount = 0;
    GridLayout experimentLayout = new GridLayout(0,2);
    ResultSet rs;
    PreparedStatement pst;
    DBConnection con = new DBConnection();
    JPanel bottomPanel = new JPanel(null);
    Font font = new Font("SansSerif", Font.BOLD, 15);

    UpdateItem(){
        prepareGUI();
    }

    private void prepareGUI(){
        mainFrame = new JFrame("Update Food Item");
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
        headerLabel = new JLabel("Update Food Item!", JLabel.CENTER);
        headerLabel.setBounds(30,10,300,50);
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


    public void showButtonDemo(Long id,String cat,String item,String des,String qty,String sellRate){

        getCategory();
        Object[] items = new Object[itemCount+1];
        for(int i =0; i<(itemCount); i++){
            items[i] = data[i];
        }

        headerLabel.setText("Update Food Item");
        headerLabel.setFont(new Font(null, Font.BOLD, 20));

        JLabel category = new JLabel("Food Category");
        category.setBounds(10,50,120,30);
        JComboBox cats = new JComboBox(items);
        cats.setBounds(120,50,270,40);
        cats.setFont(font);
        cats.setSelectedItem(cat);

        name = new JLabel("Item Name");
        name.setBounds(10,90,100,30);
        JTextField tf1=new JTextField();
        tf1.setText(item);
        tf1.setBounds(120,90,270,38);
        tf1.setFont(font);

        Object[] size = new Object[]{"-1","Full","Half","Small","Large"};
        quantity = new JLabel("Food Size Type");
        quantity.setBounds(10,130,100,30);
        JComboBox types = new JComboBox(size);
        types.setBounds(120,130,270,40);
        types.setFont(font);
        types.setSelectedItem(qty);

        price = new JLabel("Price");
        price.setBounds(10,170,100,30);
        JTextField tf3=new JTextField();
        tf3.setBounds(120,170,270,38);
        tf3.setText(sellRate);
        tf3.setFont(font);

        description = new JLabel("Description");
        description.setBounds(10,210,100,30);
        JTextField tf4 = new JTextField();
        tf4.setBounds(120,210,270,40);
        tf4.setText(des);

//        JLabel image = new JLabel("Image");
//        JButton choose = new JButton("Choose...");
//        JLabel path = new JLabel();

//        image.setBounds(10,210,100,30);
//        choose.setBounds(120,210,270,40);
//        path.setBounds(5,340,400,30);

//
//        choose.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JFileChooser fileChooser = new JFileChooser();
//                fileChooser.addChoosableFileFilter(new ImageFilter());
//                fileChooser.setAcceptAllFileFilterUsed(false);
//
//                int option = fileChooser.showOpenDialog(mainFrame);
//                if(option == JFileChooser.APPROVE_OPTION){
//                    File file = fileChooser.getSelectedFile();
//                    //path.setText("File - " + file.getName());
//                }else{
//                  //  path.setText("Open command canceled");
//                }
//            }
//        });

        JButton okButton = new JButton("Update Item");
        okButton.setBounds(120,300,130,40);



        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(tf1.getText().trim().length() < 35) {
                    PreparedStatement pst;
                    DBConnection con = new DBConnection();
                    try {
                        pst = con.mkDataBase().prepareStatement("update Item set item_name = ?, cat_id = ?, quantity = ?, price = ?,description = ?  Where id = ? ");
                        pst.setString(1, tf1.getText());
                        pst.setInt(2, getCategoryID(cats.getSelectedItem().toString()));
                        pst.setString(3, types.getSelectedItem().toString());
                        pst.setDouble(4, Double.parseDouble(tf3.getText()));
                        pst.setString(5, tf4.getText());
                        pst.setDouble(6, id);
                        if (tf1.getText().trim().length() > 0 && tf3.getText().length() > 0) {
                            pst.execute();
                            ItemView.showButtonDemo();
                            mainFrame.setVisible(false);
                            JOptionPane.showMessageDialog(null, "Update Successful! " + tf1.getText());
                        } else {
                            JOptionPane.showMessageDialog(null, "Please input all fields!");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println(ex);
                        JOptionPane.showMessageDialog(null, "Please input correct data format!");
                    } finally {
                    }
                }else {
                    JOptionPane.showMessageDialog(null, "Food item name is too large, set max length 32!");
                }
            }
        });
        bottomPanel.add(name);
        bottomPanel.add(tf1);
        bottomPanel.add(quantity);
        bottomPanel.add(tf3);
        bottomPanel.add(price);
        bottomPanel.add(types);
        bottomPanel.add(okButton);
        bottomPanel.add(category);
        bottomPanel.add(cats);
        bottomPanel.add(description);
        bottomPanel.add(tf4);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    public void getCategory() {
        try{
            pst = con.mkDataBase().prepareStatement("select * from category");
            rs = pst.executeQuery();
            while(rs.next()){
                data[itemCount] = rs.getString("name");
                itemCount++;
            }
        }catch(Exception e){
            //e.printStackTrace();
            JOptionPane.showMessageDialog(null, "getCategory error 204");
        }
    }


    public int getCategoryID(String name) {
        DBConnection con1 = new DBConnection();
        try{
            PreparedStatement pst1 = con1.mkDataBase().prepareStatement("select * from category where name = '"+name+"'");
            ResultSet rs1 = pst1.executeQuery();
            while(rs1.next()){
                return rs1.getInt("id");
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "getCategoryID 218");
        }
        return 0;
    }
}

