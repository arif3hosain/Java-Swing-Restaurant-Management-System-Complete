package com.rms.category;

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

public class UpdateCategory {

    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel description;
    private static int count = 0;
    JPanel bottomPanel = new JPanel(null);
    Font font = new Font("SansSerif", Font.BOLD, 15);

    public UpdateCategory(){
        prepareGUI();
    }

    private void prepareGUI(){
        mainFrame = new JFrame("Update Category");
        mainFrame.setSize(400,400);
        mainFrame.setResizable(false);
        mainFrame.setLayout(null);
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
        headerLabel = new JLabel("Add New Category!", JLabel.CENTER);
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


    public void showButtonDemo(Long id,String catName,String des){
        headerLabel.setText("Add New Food Item");
        headerLabel.setFont(new Font(null, Font.BOLD, 20));

        JLabel category = new JLabel("Category Name");
        category.setBounds(10,50,120,30);
        JTextField tf1=new JTextField();
        tf1.setBounds(120,50,270,40);
        tf1.setFont(font);
        tf1.setText(catName);
        description = new JLabel("Description");
        description.setBounds(10,90,100,30);
        JTextField tf2=new JTextField();
        tf2.setBounds(120,90,270,38);
        tf2.setFont(font);
        tf2.setText(des);

        JButton okButton = new JButton("Update Category");
        okButton.setBounds(120,170,130,40);



        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PreparedStatement pst;
                DBConnection con = new DBConnection();
                try{
                    pst = con.mkDataBase().prepareStatement("update category set name =?, description = ? where id = ?");
                    pst.setString(1, tf1.getText());
                    pst.setString(2, tf2.getText());
                    pst.setLong(3, id);
                    if(tf1.getText().trim().length() > 0){
                        pst.execute();
                        mainFrame.setVisible(false);
                        CategoryView.showButtonDemo();
                        JOptionPane.showMessageDialog(null, "Category ["+tf1.getText()+"] has been updated! ");
                    }else{
                        JOptionPane.showMessageDialog(null, "Please Enter Category Name ");
                    }
                }catch(Exception ex){
                    System.out.println(ex);
                    JOptionPane.showMessageDialog(null, "Inserting Error 102: " + tf2.getText());
                }finally{
                }
            }
        });
        bottomPanel.add(category);
        bottomPanel.add(description);
        bottomPanel.add(tf1);
        bottomPanel.add(tf2);
        bottomPanel.add(okButton);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
}