package com.rms.category;

import com.rms.Frame2new;
import db.DBConnection;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CategoryView {

    private JFrame mainFrame;
    static JTable table = null;
    JPanel top = new JPanel(null);
    JPanel bottom = new JPanel(null);
    Font font = new Font("SansSerif", Font.BOLD, 15);
    static String[] columnNames = {"ID","SL","Category Name", "Description", "Status"};
    static DefaultTableModel dtm = null;
    public CategoryView(){
        prepareGUI();
    }

    private void prepareGUI(){
        mainFrame = new JFrame("Showing All Categories");
        mainFrame.setSize(1100,700);
        mainFrame.setResizable(false);
        mainFrame.getContentPane().setBackground(Color.lightGray);
        mainFrame.setLayout(null);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                //System.exit(0);
            }
        });

        try{
            mainFrame.setIconImage(ImageIO.read(new File(Frame2new.logo)));
        }
        catch (Exception ex){
            JOptionPane.showMessageDialog(null, "Logo not found!");
        }
        top.setBackground(Color.orange);
        top.setBounds(0,0,1100,100);
        //   bottom.setBackground(Color.white);
        bottom.setBounds(0,100,1100,600);
        JButton addNew = new JButton("Add New");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        addNew.setBounds(400,10,100,35);
        addNew.setFont(font);
        top.add(addNew);
        update.setBounds(520,10,100,35);
        update.setFont(font);
        top.add(update);
        delete.setBounds(640,10,100,35);
        delete.setFont(font);
        top.setFont(font);
        top.add(addNew);
        top.add(update);
        top.add(delete);
        mainFrame.add(top);
        mainFrame.add(bottom);
        mainFrame.setVisible(true);

        addNew.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                NewCategory view = new NewCategory();
                view.showButtonDemo();
            }
        });

        update.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!table.getSelectionModel().isSelectionEmpty()){
                    int column = 0;
                    int row = table.getSelectedRow();
                    Long id = Long.parseLong(table.getModel().getValueAt(row, 0).toString());
                    String name = table.getModel().getValueAt(row, 2).toString();
                    String desc = table.getModel().getValueAt(row, 3).toString();
                    UpdateCategory updateCategory = new UpdateCategory();
                    updateCategory.showButtonDemo(id,name,desc);
                }else{
                    JOptionPane.showMessageDialog(null, "Please select a category !");
                }
            }
        });

        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!table.getSelectionModel().isSelectionEmpty()){
                    int column = 0;
                    int row = table.getSelectedRow();
                    Long id = Long.parseLong(table.getModel().getValueAt(row, 0).toString());
                    String name = String.valueOf(table.getModel().getValueAt(row, 2).toString());
                    if (JOptionPane.showConfirmDialog(null, "Confirm delete category ["+name+"]?", "WARNING",
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        deleteCategory(id);
                    } else {
                        // no option
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Please select a category !");
                }

            }
        });

        table = new JTable();
        dtm = new DefaultTableModel(0, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };

        table.setModel(dtm);
        table.setFont(font);
        JScrollPane pane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setBounds(0,0,1100,600);
        bottom.add(pane);
        mainFrame.setVisible(true);
    }


    public static void showButtonDemo() {
        dtm.setColumnIdentifiers(columnNames);
        dtm.setRowCount(0);
        PreparedStatement pst;
        ResultSet rs;
        DBConnection con = new DBConnection();
        try{
            pst = con.mkDataBase().prepareStatement("select * from category order by name");
            rs = pst.executeQuery();
            int i=1;
            while(rs.next()){
                String available = rs.getBoolean("status") ? "Active":"Inactive";
                Object row[] = new String[] {
                        rs.getString("id"),
                        String.valueOf(i),
                        rs.getString("name"),
                        rs.getString("description"),
                        available};
                dtm.addRow(row);
                i++;
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "showButtonDemo error 156");
        }

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setMinWidth(20);
        columnModel.getColumn(1).setMinWidth(20);
        columnModel.getColumn(2).setMinWidth(300);
        columnModel.getColumn(3).setMinWidth(440);
        columnModel.getColumn(4).setMinWidth(50);
    }

    public void deleteCategory(Long id){
        PreparedStatement pst;
        DBConnection con = new DBConnection();
        try{
            pst = con.mkDataBase().prepareStatement("delete from category where id = ?");
            pst.setLong(1, id);
            pst.execute();
            showButtonDemo();
        }catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unable to delete!");
        }finally{

        }
    }
}