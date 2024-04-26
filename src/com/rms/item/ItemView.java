package com.rms.item;

import com.rms.Frame2new;
import db.DBConnection;
import static com.rms.setting.Utils.*;

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

public class ItemView {

   private JFrame mainFrame;
   static JTable table = null;
    JPanel top = new JPanel(null);
    JPanel bottom = new JPanel(null);
   Font font = new Font("SansSerif", Font.BOLD, 15);
    static String[] columnNames = {"ID","SL","Category Name","Item Name","Description",
            "Size","Price","Discount(%)","VAT(%)","Available"
    };
    static DefaultTableModel dtm = null;
    public ItemView(){
      prepareGUI();
   }

   private void prepareGUI(){
      mainFrame = new JFrame("Showing All Food Items");
      mainFrame.setSize(1200,700);
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
       top.setBounds(0,0,1200,100);
    //   bottom.setBackground(Color.white);
       bottom.setBounds(0,100,1200,600);
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
               NewItem newItem = new NewItem();
               newItem.showButtonDemo();

           }
       });

       update.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               if(!table.getSelectionModel().isSelectionEmpty()){
                   int row = table.getSelectedRow();
                   Long id = Long.parseLong(table.getModel().getValueAt(row, 0).toString());
                   String category = getString(table.getModel().getValueAt(row, 2));
                   String name = getString(table.getModel().getValueAt(row, 3));
                   String description = getString(table.getModel().getValueAt(row, 4));
                   String quantity = getString(table.getModel().getValueAt(row, 5));
                   String price = getString(table.getModel().getValueAt(row, 6));
                   UpdateItem item = new UpdateItem();
                   item.showButtonDemo(id,category,name,description,quantity,price);
               }else{
                   JOptionPane.showMessageDialog(null, "Please select an item !");
               }
           }
       });

       delete.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               if(!table.getSelectionModel().isSelectionEmpty()){
                   int column = 0;
                   int row = table.getSelectedRow();
                   Long id = Long.parseLong(table.getModel().getValueAt(row, 0).toString());
                   String name = String.valueOf(table.getModel().getValueAt(row, 1).toString());
                   if (JOptionPane.showConfirmDialog(null, "Confirm delete item ["+name+"]?", "WARNING",
                           JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                       deleteItem(id);
                   } else {
                       // no option
                   }
               }else{
                   JOptionPane.showMessageDialog(null, "Please select an item !");
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


       JScrollPane pane = new JScrollPane(table);
       pane.setBounds(0,0,1190,500);
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
                pst = con.mkDataBase().prepareStatement("select b.name,a.id,a.item_name,a.description,a.quantity,a.price,a.discount,a.vat,a.available from item a " +
                        "inner join category b on b.id = a.cat_id order by b.name,a.item_name, a.quantity");
                rs = pst.executeQuery();
                int i=1;
                while(rs.next()){
                    String available = rs.getBoolean("available") ? "Available":"Unavailable";
                    Object row[] = new String[] {
                            rs.getString("id"),
                            String.valueOf(i),
                            rs.getString("name"),
                            rs.getString("item_name"),
                            rs.getString("description"),
                            rs.getString("quantity"),
                            rs.getString("price"),
                            rs.getString("discount"),
                            rs.getString("vat"),
                            available
                    };
                    dtm.addRow(row);
                    i++;
                }
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "showButtonDemo");
            }

       TableColumnModel columnModel = table.getColumnModel();
       columnModel.getColumn(0).setMaxWidth(50);
       columnModel.getColumn(1).setMaxWidth(50);
       columnModel.getColumn(2).setMinWidth(160);
       columnModel.getColumn(3).setMinWidth(150);
       columnModel.getColumn(4).setMinWidth(250);
       columnModel.getColumn(5).setMinWidth(100);
       columnModel.getColumn(6).setMaxWidth(50);
       columnModel.getColumn(7).setMaxWidth(80);
       columnModel.getColumn(8).setMaxWidth(80);
       columnModel.getColumn(9).setMaxWidth(100);
   }

   public void deleteItem(Long id){
       System.out.println("---------------"+id);
       PreparedStatement pst;
       DBConnection con = new DBConnection();
       try{
           pst = con.mkDataBase().prepareStatement("delete from item where id = ?");
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


