package com.rms.category;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.PreparedStatement;

public class NewCategory {

    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel description;
    private static int count = 0;
    JPanel bottomPanel = new JPanel(null);
    Font font = new Font("SansSerif", Font.BOLD, 15);

    public NewCategory(){
        prepareGUI();
    }

    private void prepareGUI(){
        mainFrame = new JFrame("Add New Category");
        mainFrame.setSize(400,400);
        mainFrame.setResizable(false);
        mainFrame.setLayout(null);
        mainFrame.getContentPane().setBackground(Color.gray);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                mainFrame.setVisible(false);
            }
        });

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


    public void showButtonDemo(){
        headerLabel.setText("Add New Food Item");
        headerLabel.setFont(new Font(null, Font.BOLD, 20));

        JLabel category = new JLabel("Category Name");
        category.setBounds(10,50,120,30);
        JTextField tf1=new JTextField();
        tf1.setBounds(120,50,270,40);
        tf1.setFont(font);

        description = new JLabel("Description");
        description.setBounds(10,90,100,30);
        JTextField tf2=new JTextField();
        tf2.setBounds(120,90,270,38);
        tf2.setFont(font);

        JButton okButton = new JButton("Save Category");
        okButton.setBounds(120,170,130,40);



        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PreparedStatement pst;
                DBConnection con = new DBConnection();
                try{
                    pst = con.mkDataBase().prepareStatement("insert into  category(name,description) values (?,?)");
                    pst.setString(1, tf1.getText());
                    pst.setString(2, tf2.getText());
                    if(tf1.getText().trim().length() >0){
                        pst.execute();
                        mainFrame.setVisible(false);
                        CategoryView.showButtonDemo();
                        JOptionPane.showMessageDialog(null, "Category ["+tf1.getText()+"] has been saved! ");
                    }else{
                        JOptionPane.showMessageDialog(null, "Please Enter Category Name ");
                    }
                }catch(Exception ex){
                    System.out.println(ex);
                    JOptionPane.showMessageDialog(null, "Inserting Error 93 " );
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