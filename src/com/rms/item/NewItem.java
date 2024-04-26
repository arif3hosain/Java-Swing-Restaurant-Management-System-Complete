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

public class NewItem {

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
    Font font = new Font("SansSerif", Font.BOLD, 15);

    NewItem(){
        prepareGUI();
    }

    private void prepareGUI(){
        mainFrame = new JFrame("Add New Item!");
        mainFrame.setSize(430,500);
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
        headerLabel = new JLabel("Add New Item!", JLabel.CENTER);
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

        getCategory();
        Object[] items = new Object[itemCount+1];
        for(int i =0; i<(itemCount); i++){
            items[i] = data[i];
        }

        headerLabel.setText("Add New Food Item");
        headerLabel.setFont(new Font(null, Font.BOLD, 20));

        JLabel category = new JLabel("Food Category");
        category.setBounds(10,50,120,30);
        JComboBox cats = new JComboBox(items);
        cats.setBounds(120,50,270,40);
        cats.setFont(font);


        name = new JLabel("Item Name");
        name.setBounds(10,90,100,30);
        JTextField tf1=new JTextField();
        tf1.setBounds(120,90,270,38);
        tf1.setFont(font);

        Object[] size = new Object[]{"-1","Full","Half","Small","Large"};
        quantity = new JLabel("Food Size Type");
        quantity.setBounds(10,130,100,30);
        JComboBox types = new JComboBox(size);
        types.setBounds(120,130,270,40);
        types.setFont(font);

        price = new JLabel("Price");
        price.setBounds(10,170,100,30);
        JTextField tf3=new JTextField();
        tf3.setBounds(120,170,270,38);
        tf3.setFont(font);

        JLabel description = new JLabel("Description");
        description.setBounds(10,210,100,30);

        JTextField tf4 = new JTextField();
        tf4.setBounds(120,210,270,40);

//        JLabel image = new JLabel("Image");
//        JButton choose = new JButton("Choose...");
//        JLabel path = new JLabel();
//

//        image.setBounds(10,210,100,30);
//        choose.setBounds(120,210,270,40);
//        path.setBounds(5,340,400,30);


     /*   choose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.addChoosableFileFilter(new ImageFilter());
                fileChooser.setAcceptAllFileFilterUsed(false);

                int option = fileChooser.showOpenDialog(mainFrame);
                if(option == JFileChooser.APPROVE_OPTION){
                    File file = fileChooser.getSelectedFile();
                    //path.setText("File - " + file.getName());
                }else{
                  //  path.setText("Open command canceled");
                }
            }
        });*/

        JButton okButton = new JButton("Save Item");
        okButton.setBounds(120,300,130,40);



        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(tf1.getText().trim().length() < 35) {
                    try {
                        pst = con.mkDataBase().prepareStatement("insert into  Item (item_name,quantity,price,cat_id,created_date,description)" +
                                " values (?,?,?,?,?,?)");
                        pst.setString(1, tf1.getText());
                        pst.setString(2, types.getSelectedItem().toString());
                        pst.setDouble(3, Double.parseDouble(tf3.getText()));
                        pst.setInt(4, getCategoryID(cats.getSelectedItem().toString()));
                        pst.setDate(5, new java.sql.Date(new java.util.Date().getTime()));
                        pst.setString(6, tf4.getText());
                        pst.execute();

                        if (tf1.getText().trim().length() > 0 && tf3.getText().trim().length() > 0) {
                            ItemView.showButtonDemo();
                            mainFrame.setVisible(false);
                            JOptionPane.showMessageDialog(null, "Item [" + tf1.getText() + "] has been saved! ");
                        } else {

                            JOptionPane.showMessageDialog(null, "Please input all fields! ");
                        }
                    } catch (NumberFormatException f) {
                        f.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Please input correct data format!");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "All fields required!");
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
            JOptionPane.showMessageDialog(null, "getting category 211!");
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
            JOptionPane.showMessageDialog(null, "getCategoryID");
        }
        return 0;
    }
}

