package com.rms.bill;

import com.rms.Frame2new;
import com.rms.setting.Utils;
import db.DBConnection;
import print.PrinterService;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rms.setting.Utils.getString;
import static com.rms.setting.Utils.todayDateTime;

public class GenerateBill extends JFrame {

    private JFrame mainFrame;
    Object[] data = new Object[5000];
    Object[] data2 = new Object[5000];
    JTable tbl = null;
    JTable tbl2 = null;
    DefaultTableModel dtm = null;
    DefaultTableModel dtm2 = null;
    int index = 0;
    Integer itemCount = 0;
    Integer itemCount2 = 0;
    String columns[] = new String[] {"Serial","Item","Size","Qty","Unit Price","Total Bill"};
    String column2[] = new String[] {"Food Name","Size","Price"};
    JLabel lblItem,lblPrice,sizeType,lblQty,lblTotalPrice,lblTotal,lblDiscount,lblVAT,finalAmount ;
    JTextField txtTotalAmt,txtDiscountAmt,txtVATAmt,txtAmt;
    JSpinner quantity = null;
    JTextField txtTotal = null;
    JButton addFood = null;
    JButton btnDelete = new JButton("Delete Item");
    JButton btnClear = new JButton("Cancel");
    JTextField txtPrice = null;
    JComboBox comboItem=null;
    JComboBox comboSize=null;
    JComboBox catCombo=null;
    JTextField search = new JTextField();
    List<Map<String,foodCart>> cart = new ArrayList<>();
    List<foodCart> foodCartList = new ArrayList<>();
    Map<String,Object> list = new HashMap<>();
    PreparedStatement pst;
    ResultSet rs;
    DBConnection con = new DBConnection();
    List<foodCart> orderedFoodList = new ArrayList<>();
    JTextField vatPercentage = null;
    JTextField discountPercentage = null;
    int primaryKey ;


    int sl = 0;

         Font font = new Font("SansSerif", Font.BOLD, 15);

    public GenerateBill(){
        mainFrame = new JFrame("Bill Generator");
        mainFrame.setSize(1200,700);
        mainFrame.setLayout(null);
        mainFrame.setVisible(true);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
      //  mainFrame.setUndecorated(true);
        mainFrame.setBackground(Color.lightGray);
        mainFrame.setVisible(true);
        try{
            mainFrame.setIconImage(ImageIO.read(new File(Frame2new.logo)));
        }
        catch (Exception ex){
            JOptionPane.showMessageDialog(null, "Logo not found!");
        }
        showButtonDemo();
        Object[] items = new Object[itemCount+1];
        items[0] = "--";
        for(int i =0; i<(itemCount); i++){
            items[i+1] = data[i];
        }

        JPanel left = new JPanel(null);
        left.setBackground(Color.gray);
        left.setBounds(0,0,600,700);


        JPanel center = new JPanel(null);
        center.setBackground(Color.orange);
        center.setBounds(600,0,400,700);

        JPanel right = new JPanel(null);
        right.setBounds(1020,0,350,700);
        right.setBackground(Color.gray);

        SpinnerModel value = new SpinnerNumberModel(1, //initial value
                1, //minimum value
                500, //maximum value
                1); //step
        quantity = new JSpinner(value);
        txtPrice = new JTextField();
        addFood = new JButton("Add to Cart");
        txtTotal = new JTextField();
        comboItem=new JComboBox(items);

        lblItem = new JLabel("Food Name");
        lblItem.setBounds(40,60,100,30);
        lblItem.setFont(font);
        center.add(lblItem);

        sizeType = new JLabel("Size");
        sizeType.setBounds(40,100,100,30);
        sizeType.setFont(font);
        center.add(sizeType);


        lblPrice = new JLabel("Unit Price");
        lblPrice.setBounds(40,140,100,30);
        lblPrice.setFont(font);
        center.add(lblPrice);

        lblQty = new JLabel("Quantity");
        lblQty.setBounds(40,180,100,30);
        lblQty.setFont(font);
        center.add(lblQty);

        lblTotalPrice = new JLabel("Price");
        lblTotalPrice.setBounds(40,220,100,30);
        lblTotalPrice.setFont(font);
        center.add(lblTotalPrice);



        comboItem.setBounds(160,60,200,30);
        comboItem.setFont(font);
        center.add(comboItem);
        Object[] size = new Object[]{"-1","Full","Half","Small","Large"};
        comboSize = new JComboBox(size);
        comboSize.setBounds(160,100,200,30);
        comboSize.setFont(font);
        center.add(comboSize);

        txtPrice.setBounds(160,140,200,30);
        txtPrice.setEditable(false);
        txtPrice.setFont(font);
        center.add(txtPrice);
        quantity.setBounds(160, 180, 50, 30);
        quantity.setFont(font);
        center.add(quantity);
        txtTotal.setBounds(160,220,200,30);
        txtTotal.setFont(font);
        center.add(txtTotal);
        addFood.setBounds(160,270,200,30);
        addFood.setFont(font);
        center.add(addFood);


        lblTotal = new JLabel("Food Amount");
        lblTotal.setBounds(40,380,200,30);
        center.add(lblTotal);

        lblDiscount = new JLabel("Discount Amount (%)");
        lblDiscount.setBounds(40,420,200,30);
        center.add(lblDiscount);

        lblVAT = new JLabel("VAT Amount (%)");
        lblVAT.setBounds(40,460,200,30);
        center.add(lblVAT);

        finalAmount = new JLabel("Amount");
        finalAmount.setBounds(40,500,200,30);
        finalAmount.setFont(font);
        center.add(finalAmount);


        txtTotalAmt = new JTextField();
        txtTotalAmt.setBounds(240,380,120,30);
        txtTotalAmt.setFont(font);
        txtTotalAmt.setHorizontalAlignment(JTextField.CENTER);
        txtTotalAmt.setEditable(false);
        center.add(txtTotalAmt);

        discountPercentage = new JTextField(0);
        discountPercentage.setBounds(160,420,70,30);
        discountPercentage.setFont(font);
        center.add(discountPercentage);

        txtDiscountAmt = new JTextField();
        txtDiscountAmt.setBounds(240,420,120,30);
        txtDiscountAmt.setFont(font);
        txtDiscountAmt.setHorizontalAlignment(JTextField.CENTER);
        center.add(txtDiscountAmt);

        vatPercentage = new JTextField(0);
        vatPercentage.setBounds(160,460,70,30);
        vatPercentage.setFont(font);
        center.add(vatPercentage);
        discountPercentage.addKeyListener(
                new KeyListener(){

                    @Override
                    public void keyTyped(KeyEvent e) {
                      //  System.out.println("keyTyped");
                    }

                    public void keyPressed(KeyEvent e){
//                        System.out.println("keyPressed");
//                        if(e.getKeyChar() == KeyEvent.VK_ENTER){
//
//                            System.out.println("VK_ENTER");
//                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                       double discountAmt = Utils.getDoubleVal(discountPercentage.getText());
                        amtCalculator();
                        System.out.println("------------------");
                    }
                }
        );

        vatPercentage.setText(String.valueOf(Frame2new.vat));
        vatPercentage.setEditable(false);
        discountPercentage.setText(String.valueOf(Frame2new.discount));

        txtVATAmt = new JTextField();
        txtVATAmt.setBounds(240,460,120,30);
        txtVATAmt.setFont(font);
        txtVATAmt.setHorizontalAlignment(JTextField.CENTER);
        txtVATAmt.setEditable(false);
        center.add(txtVATAmt);

        txtAmt = new JTextField();
        txtAmt.setBounds(240,500,120,30);
        txtAmt.setFont(font);
        txtAmt.setHorizontalAlignment(JTextField.CENTER);
        txtAmt.setEditable(false);
        center.add(txtAmt);

        mainFrame.add(left);
        mainFrame.add(center);
        mainFrame.add(right);

        tbl = new JTable();
        dtm = new DefaultTableModel(0, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        dtm.setColumnIdentifiers(columns);
        tbl.setModel(dtm);
        tbl.setFont(font);

        JScrollPane pane = new JScrollPane(tbl, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setBounds(0,120,600,700);
        left.add(pane);



        getCategory();
        Object[] items2 = new Object[itemCount2+1];
        items2[0] = "";
        for(int i =0; i<(itemCount2); i++){
            items2[i+1] = data2[i];
        }

        JButton add = new JButton("<< Add");
        add.setBounds(50,20,250,30);
        right.add(add);

        catCombo = new JComboBox(items2);
        catCombo.setBounds(50,55,250,30);
        right.add(catCombo);
        search.setBounds(50,88,250,30);
        right.add(search);
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String comboItem = Utils.getString(catCombo.getSelectedItem());
                String searchText = Utils.getString(search.getText());
                loadItems(comboItem,searchText);
            }
        });

        //===============================right table
        tbl2 = new JTable();
        dtm2 = new DefaultTableModel(0, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        dtm2.setColumnIdentifiers(column2);
        tbl2.setModel(dtm2);
        tbl2.setFont(font);

        JScrollPane pane2 = new JScrollPane(tbl2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane2.setBounds(0,120,350,700);
        right.add(pane2);

        TableColumnModel columnModel = tbl.getColumnModel();
        columnModel.getColumn(0).setMinWidth(30);
        columnModel.getColumn(1).setMinWidth(230);
        columnModel.getColumn(2).setMinWidth(30);
        columnModel.getColumn(3).setMinWidth(50);
        columnModel.getColumn(4).setMinWidth(80);
        columnModel.getColumn(5).setMinWidth(80);


        TableColumnModel columnModel2 = tbl2.getColumnModel();
        columnModel2.getColumn(0).setMinWidth(230);
        columnModel2.getColumn(1).setMinWidth(70);
        columnModel2.getColumn(2).setMinWidth(50);
        loadItems("","");
        addFood.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                foodCart f = new foodCart();
                try {
                    if (!comboItem.getSelectedItem().equals("--") && quantity.getValue() != null && txtPrice.getText() != null) {
                        f.name = comboItem.getSelectedItem().toString();
                        f.size = comboSize.getSelectedItem().toString();
                        f.quantity = Integer.parseInt(quantity.getValue().toString());
                        f.unitPrice = Double.parseDouble(txtPrice.getText());
                        f.price = f.quantity * f.unitPrice;
                        refreshTable(f);
                    }else{
                        JOptionPane.showMessageDialog(null, "Fill up all the fields !" );
                    }
                }catch (Exception exc){
                    //exc.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Fill up all the fields !" );
                }
            }
    });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(null, "Confirm cancel this transaction ?", "WARNING",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    txtPrice.setText("0");
                    quantity.setValue(1);
                    txtPrice.setText("0");
                    txtTotal.setText("");
                    txtTotalAmt.setText("");
                    txtDiscountAmt.setText("");
                    txtVATAmt.setText("");
                    txtAmt.setText("");
                    dtm.setRowCount(0);
                    foodCartList = new ArrayList<>();
                }
            }
         });

        btnClear.setBounds(10,600,100,35);
        btnClear.setFont(font);
        center.add(btnClear);
        btnDelete.setBounds(280,50,120,35);
        btnDelete.setFont(font);
        left.add(btnDelete);

        JButton save = new JButton("Save & Close");
        save.setBounds(120,600,120,35);
        save.setFont(font);
        //center.add(save);

        JButton print = new JButton("Print Out");
        print.setBounds(250,600,120,35);
        print.setFont(font);
        center.add(print);

        print.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               String fullText = "";
               orderedFoodList = new ArrayList<>();
               int tableRows = tbl.getRowCount();
               String line = "";
               String text = "";

               line = "Palki Chinese Restaurant";
               String format = String.format("%-" + (47 - line.length()) / 2 + "s", text);
               fullText += format + line + format+"\n";
               line = "Hazirhat North Bazar";
               String format2 = String.format("%-" + (47 - line.length()) / 2 + "s", text);
               fullText += format2 + line + format2+"\n";

               line = "KamalNagar, Lakshmipur";
                String format3 = String.format("%-" + (47 - line.length()) / 2 + "s", text);
               fullText += format3 + line + format3+"\n\n";

               line = todayDateTime();
               format3 = String.format("%-" + (47 - line.length()) / 2 + "s", text);
               fullText += format3 + line + format3+"\n\n";

               fullText += "\n-----------------------------------------------\n";
               int products = 0;
               for(int i =0; i<tableRows; i++){
                   products ++;
                   foodCart fc = new foodCart();
                   fc.name = getString(tbl.getModel().getValueAt(i,1));
                   fc.quantity = Integer.parseInt(tbl.getModel().getValueAt(i,3).toString());
                   fc.unitPrice = Double.parseDouble(tbl.getModel().getValueAt(i,4).toString());
                   fc.price = Double.parseDouble(tbl.getModel().getValueAt(i,5).toString());
                   long rate = Math.round(fc.price);
                   line = fc.name+" ("+fc.quantity+")"+rate;
                   line = fc.name+" ("+fc.quantity+")" + String.format("%-"+(47-line.length())+"s",text)+Math.round(rate)+"\n";
                   fullText += line;
                   line = "";
               }
               line = "";
               fullText += "\n-----------------------------------------------\n";
               Double discount = getDoubleValue(txtDiscountAmt.getText());
               line = "Discount("+Utils.getString(discountPercentage.getText())+")"+discount;
               fullText += "Discount("+Utils.getString(discountPercentage.getText())+")"+String.format("%-" + (47 - line.length()) + "s", text)+Math.round(discount)+"\n";

               Double vat = getDoubleValue(txtVATAmt.getText());
               line = "VAT("+Utils.getString(vatPercentage.getText())+")"+vat;
               fullText += "VAT("+Utils.getString(vatPercentage.getText())+")"+String.format("%-" + (47 - line.length()) + "s", text)+Math.round(vat);

               fullText += "\n-----------------------------------------------\n";
               Double amount = getDoubleValue(txtAmt.getText());
               line = "Total" +amount;
               line =  "Total" + String.format("%-"+(47-line.length())+"s",text)+Math.round(amount);
               fullText += line+"\n";

               line = "Number of Item purchased: "+products;
               format3 = String.format("%-" + (47 - line.length()) / 2 + "s", text);
               fullText += format3 + line + format3+"\n\n\n";
               PrinterService printerService = new PrinterService();
               printerService.printString("58mm Series Printer(1)",fullText);
               byte[] cutP = new byte[] { 0x1d, 'V', 1 };
               printerService.printBytes("58mm Series Printer(1)", cutP);

               saveTransaction();
               JOptionPane.showMessageDialog(null, "Transaction saved & printed!");
           }
       });



        comboItem.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    Object item = event.getItem();
                    Object size = comboSize.getSelectedItem();
                    Double value = getPrice(item.toString(),size.toString());
                    if(value > 0){
                        txtPrice.setText(String.valueOf(value));
                        Double totalPrice = (value * Integer.parseInt(quantity.getValue().toString()));
                        txtTotal.setText(String.valueOf(totalPrice));
                    }else {
                        txtPrice.setText("0");
                    }
                }
            }
        });

        comboSize.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    Object size = event.getItem();
                    Object item = comboItem.getSelectedItem();
                    Double value = getPrice(item.toString(),size.toString());
                    if(value > 0){
                        txtPrice.setText(String.valueOf(value));
                        Double totalPrice = (value * Integer.parseInt(quantity.getValue().toString()));
                        txtTotal.setText(String.valueOf(totalPrice));
                    }else {
                        txtPrice.setText("0");
                    }
                }
            }
        });

        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addSelectedItem();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int rows = tbl.getRowCount();
                if (rows >0) {
                    if(tbl.getSelectedRow() >= 0){
                        int selectedRow = tbl.getSelectedRow();
                        dtm.removeRow(selectedRow);
                        int tableRows = tbl.getRowCount();
                        foodCartList = new ArrayList<>();
                        for (int i = 0; i < tableRows; i++) {
                            foodCart fc = new foodCart();
                            fc.name = tbl.getModel().getValueAt(i, 1).toString();
                            fc.quantity = Integer.parseInt(tbl.getModel().getValueAt(i, 3).toString());
                            fc.unitPrice = Double.parseDouble(tbl.getModel().getValueAt(i, 4).toString());
                            fc.price = Double.parseDouble(tbl.getModel().getValueAt(i, 5).toString());
                            System.out.println(tbl.getModel().getValueAt(i, 2).toString()+"--------------------");
                            System.out.println(fc.name+"--"+fc.quantity+"-"+fc.unitPrice+"-"+fc.price);
                            foodCartList.add(fc);
                        }
                        amtCalculator();
                    }else{
                        JOptionPane.showMessageDialog(null, "Select an item from table !" );
                    }
            }else{
                    JOptionPane.showMessageDialog(null, "Add food item to cart !" );
                }
        }
        });

        quantity.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if(!txtPrice.getText().trim().equals("")){
                    Double unitPrice = Double.parseDouble(txtPrice.getText());
                    Integer qty = Integer.parseInt(((JSpinner)e.getSource()).getValue().toString());
                    Double result = unitPrice * qty;
                    txtTotal.setText(String.valueOf(result));
                }
            }
        });

        txtPrice.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') ||
                        (c == KeyEvent.VK_BACK_SPACE) ||
                        (c == KeyEvent.VK_DELETE))) {
                    getToolkit().beep();
                    e.consume();
                }
            }
        });

        catCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    String comboItem = Utils.getString(catCombo.getSelectedItem());
                    String searchText = Utils.getString(search.getText());
                    loadItems(comboItem,searchText);
                }
            }
        });
    }


    public void addSelectedItem(){
       try{
           int row = tbl2.getSelectedRow();
           Object food = tbl2.getModel().getValueAt(row, 0);
           Object size = tbl2.getModel().getValueAt(row, 1);
           comboItem.setSelectedItem(food);
           comboSize.setSelectedItem(size);


       }catch (Exception e){
           JOptionPane.showMessageDialog(null, "Please select an item!" );
       }
    }

    public void refreshTable(foodCart food){
        foodCartList.add(food);
        index++;
        String data[] = new String[] {
                String.valueOf(index),
                food.name,
                food.size,
                String.valueOf(food.quantity),
                String.valueOf(food.unitPrice),
                String.valueOf(food.price)};
                 dtm.addRow(data);
                 comboItem.setSelectedItem("--");
                 txtPrice.setText("");
                 quantity.setValue(1);
                 txtTotal.setText("");
        amtCalculator();
    }

    public void amtCalculator(){
        Double amt = 0.0;
        for(foodCart f: foodCartList){
            amt +=f.price;
        }
        txtTotalAmt.setText(String.valueOf(amt));
        txtDiscountAmt.setText(String.valueOf((amt* Utils.getDoubleVal(discountPercentage.getText()))/100));
        txtVATAmt.setText(String.valueOf(((amt-Utils.getDoubleVal(txtDiscountAmt.getText()))*Utils.getDoubleVal(Frame2new.vat))/100));
        Double lastAmt = ((amt - getDoubleValue(txtDiscountAmt.getText())) +  getDoubleValue(txtVATAmt.getText()));
        txtAmt.setText(String.valueOf(lastAmt));
    }


    class foodCart{
        String name;
        String size;
        Double unitPrice;
        int quantity;
        Double price;
    }

    public void showButtonDemo() {
        try{
            pst = con.mkDataBase().prepareStatement("select distinct a.item_name from item a join category b on b.id = a.cat_id");
            rs = pst.executeQuery();
            while(rs.next()){
                data[itemCount] = rs.getString("item_name");
                itemCount++;
            }
        }catch(Exception e){
            //e.printStackTrace();
            //JOptionPane.showMessageDialog(null, "Getting items error !");
        }
    }

    public Double getPrice(String food,String size){
        String sql = "select price from item where item_name='"+food+"' and lower(quantity) like lower('%"+size+"%')";
        try{
            pst = con.mkDataBase().prepareStatement(sql);
            rs = pst.executeQuery();
            while(rs.next()){
                return rs.getDouble("price");
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "getting price error !");
        }
        return 0.0;
    }


    public double getDoubleValue(String value){
        if(value == null) return 0.0;
        else if(value.equals("")) return 0.0;
        else if(Double.parseDouble(value) >= 0) return Double.parseDouble(value);
        return 0.0;
    }

    public void saveBillDetails(int primaryKey){
        for(foodCart f: orderedFoodList){
            try{
                pst = con.mkDataBase().prepareStatement("insert into  bill_details(food,size,quantity,per_unit_price,total_price,bill_id)" +
                        " values (?,?,?,?,?,?)");
                pst.setString(1, f.name);
                pst.setString(2, f.size);
                pst.setInt(3, f.quantity);
                pst.setDouble(4, f.unitPrice);
                pst.setDouble(5, f.price);
                pst.setDouble(6, primaryKey);
                pst.execute();
            }catch(Exception ex){
            }finally{

            }
        }
        dtm.setRowCount(0);
        foodCartList = new ArrayList<>();
    }

    public void getCategory() {
        try{
            pst = con.mkDataBase().prepareStatement("select * from category");
            rs = pst.executeQuery();
            while(rs.next()){
                data2[itemCount2] = rs.getString("name");
                itemCount2++;
            }
        }catch(Exception e){
            //e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Getting category error !");
        }
    }

    public void loadItems(String cats,String txt){
        dtm2.setRowCount(0);
        PreparedStatement pst;
        ResultSet rs;
        DBConnection con = new DBConnection();
        String sql =  "select b.name,a.item_name,a.price,a.quantity from item a " +
                "                    inner join category b on b.id = a.cat_id Where lower(b.name) like lower('%"+cats+"%') and lower(a.item_name) like lower('%"+txt+"%') order by a.item_name";
        try{
            pst = con.mkDataBase().prepareStatement(sql);

            rs = pst.executeQuery();
            int i=1;
            while(rs.next()){
                Object row[] = new String[] {
                        rs.getString("item_name"),
                        rs.getString("quantity"),
                        rs.getString("price")};
                dtm2.addRow(row);
                i++;
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Load items error !");
        }
    }
    public void saveTransaction(){
        Double price = getDoubleValue(txtTotalAmt.getText());
        Double discount = getDoubleValue(txtDiscountAmt.getText());
        Double vat = getDoubleValue(txtVATAmt.getText());
        Double amount = getDoubleValue(txtAmt.getText());
        try{
            pst = con.mkDataBase().prepareStatement("insert into bill ( created_date, description, vat_amt, discount_amt, total,amount) values (" +
                    " CURRENT_TIMESTAMP,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
       //     pst.setDate(1, new java.sql.Date(new java.util.Date().getTime()));
            pst.setString(1, "");
            pst.setDouble(2, vat);
            pst.setDouble(3, discount);
            pst.setDouble(4, price);
            pst.setDouble(5, amount);
            if(amount >0){
                pst.execute();
                txtPrice.setText("");
                quantity.setValue(1);
                txtTotal.setText("");
                txtTotalAmt.setText("");
                txtDiscountAmt.setText("");
                txtVATAmt.setText("");
                txtAmt.setText("");
                ResultSet rs = pst.getGeneratedKeys();
                if(rs.next())
                {
                    primaryKey = rs.getInt(1);
                }
                int tableRows = tbl.getRowCount();
                for(int i =0; i<tableRows; i++){
                    foodCart fc = new foodCart();
                    fc.name = tbl.getModel().getValueAt(i,1).toString();
                    fc.quantity = Integer.parseInt(tbl.getModel().getValueAt(i,3).toString());
                    fc.unitPrice = Double.parseDouble(tbl.getModel().getValueAt(i,4).toString());
                    fc.price = Double.parseDouble(tbl.getModel().getValueAt(i,5).toString());
                    orderedFoodList.add(fc);

                }
                saveBillDetails(primaryKey);
//                JOptionPane.showMessageDialog(null, "Successfully Saved !" );
            }else{
                JOptionPane.showMessageDialog(null, "Please add some food to cart! ");
            }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, "Save transaction error " );
        }finally{

        }


    }

}