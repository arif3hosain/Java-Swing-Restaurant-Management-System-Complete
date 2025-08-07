package com.rms.bill;

import com.rms.PaymentType;
import com.rms.service.AppService;
import com.rms.setting.Utils;
import db.DBConnection;
import dto.Item;
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
    Map<String,Item> itemName = new HashMap<>();
    List<Item> itemList = new ArrayList<>();
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
    JComboBox<Object> catCombo=null;
    JComboBox<PaymentType> payment=null;
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
    AppService appService = new AppService();

    Font font = new Font("SansSerif", Font.BOLD, 15);

    public GenerateBill(){
        mainFrame = new JFrame("Bill Generator");
        mainFrame.setSize(1380,730);
        mainFrame.setLayout(null);
        mainFrame.setVisible(true);
        //  mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //  mainFrame.setUndecorated(true);
        mainFrame.setBackground(Color.lightGray);
        mainFrame.setLocationRelativeTo(null); // <-- Center on screen
        mainFrame.setVisible(true);
        try{
            mainFrame.setIconImage(ImageIO.read(new File(Utils.LOGO_PATH)));
        }
        catch (Exception ex){
            JOptionPane.showMessageDialog(null, Utils.LOGO_NOT_FOUND);
        }
        showButtonDemo();
        Object[] items = new Object[itemCount+1];
        items[0] = "";
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
        right.setBounds(1020,0,400,700);
        right.setBackground(Color.gray);

        SpinnerModel value = new SpinnerNumberModel(1, //initial value
                1, //minimum value
                500, //maximum value
                1); //step
        quantity = new JSpinner(value);
        txtPrice = new JTextField();
        addFood = new JButton("Add to Cart");
        mainFrame.getRootPane().setDefaultButton(addFood);
        txtTotal = new JTextField();
        comboItem=new JComboBox(items);
        comboItem.setEditable(true);


        // Get the editor component (i.e., the text field inside combo box)
        JTextField editor = (JTextField) comboItem.getEditor().getEditorComponent();

        // Replace the commented filtering code with this working version:

        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // Don't filter on Enter key or navigation keys
                if (e.getKeyCode() == KeyEvent.VK_ENTER ||
                        e.getKeyCode() == KeyEvent.VK_UP ||
                        e.getKeyCode() == KeyEvent.VK_DOWN) {
                    return;
                }

                SwingUtilities.invokeLater(() -> {
                    String input = editor.getText();
                    if (input != null && !input.trim().isEmpty()) {
                        filterComboBox(input);
                    } else {
                        // Reset to show all items when input is empty
                        resetComboBox();
                    }
                });
            }
        });




       /* editor.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                SwingUtilities.invokeLater(() -> {
                    String input = editor.getText();
                    filterComboBox(input);
                });
            }
        });*/

       /* editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && comboItem.isPopupVisible()) {
                    Object selected = comboItem.getSelectedItem();
                    if (selected != null) {
                        editor.setText(selected.toString());
                    }
                    comboItem.hidePopup(); // optionally close dropdown
                    e.consume(); // prevent duplicate event
                }
            }
        });*/

     /*   editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && comboItem.isPopupVisible()) {
                    Object selected = comboItem.getSelectedItem();
                    if (selected != null) {
                        editor.setText(selected.toString());
                        comboItem.setSelectedItem(selected);
                    }
                    comboItem.hidePopup(); // close dropdown
                    e.consume();
                }
            }
        });
*/


//        lblItem = new JLabel("Food Name");
//        lblItem.setBounds(40,60,100,30);
//        lblItem.setFont(font);
//        center.add(lblItem);

        sizeType = new JLabel("Size");
        sizeType.setBounds(40,100,100,30);
        sizeType.setFont(font);
        center.add(sizeType);


        lblPrice = new JLabel("Unit Price");
        lblPrice.setBounds(40,140,150,30);
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



//        comboItem.setBounds(160,60,200,30);
        comboItem.setBounds(40,60,330,30);
        comboItem.setFont(font);

        center.add(comboItem);
        String[] size = appService.getFoodTypes();
        comboSize = new JComboBox(size);
        comboSize.setBounds(160,100,200,30);
        comboSize.setFont(font);
        center.add(comboSize);

        txtPrice.setBounds(160,140,200,30);
       // txtPrice.setEditable(false);
        txtPrice.setFont(font);
        center.add(txtPrice);
        quantity.setBounds(160, 180, 50, 30);
        quantity.setFont(font);
        center.add(quantity);
        txtTotal.setBounds(160,220,200,30);
        txtTotal.setEditable(false);
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
                    }
                    public void keyPressed(KeyEvent e){
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        double discountAmt = Utils.getDoubleVal(discountPercentage.getText());
                        amtCalculator();
                    }
                }
        );

        txtDiscountAmt.addKeyListener(
                new KeyListener(){

                    @Override
                    public void keyTyped(KeyEvent e) {
                    }
                    public void keyPressed(KeyEvent e){
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        double discountAmt = Utils.getDoubleVal(discountPercentage.getText());
                        amtCalculator();
                    }
                }
        );

        vatPercentage.setText(String.valueOf(Utils.VAT));
        vatPercentage.setEditable(false);
        discountPercentage.setText(String.valueOf(Utils.DISCOUNT));

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
                String comboItem = getString(catCombo.getSelectedItem());
                String searchText = getString(search.getText());
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
                    if (!comboItem.getSelectedItem().equals("") && quantity.getValue() != null && txtPrice.getText() != null) {
                        f.name = comboItem.getSelectedItem().toString();
                        f.size = comboSize.getSelectedItem().toString();
                        f.quantity = Integer.parseInt(quantity.getValue().toString());
                        f.unitPrice = Double.parseDouble(txtPrice.getText());
                        f.price = f.quantity * f.unitPrice;
                        refreshTable(f);
                        editor.requestFocusInWindow();
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
                payment.setSelectedItem(PaymentType.Cash.name());
            }
        });

        btnClear.setBounds(10,600,100,35);
        btnClear.setFont(font);
        center.add(btnClear);
        btnDelete.setBounds(280,50,120,35);
        btnDelete.setFont(font);
        left.add(btnDelete);

        Object[] paymentItems = new Object[3];
        paymentItems[0] = PaymentType.Cash.name();
        paymentItems[1] = PaymentType.MFS.name();
        paymentItems[2] = PaymentType.Credit.name();
        payment = new JComboBox(paymentItems);
        payment.setBounds(120,600,120,35);
        payment.setFont(font);
        center.add(payment);

        JButton print = new JButton("Print Out");
        print.setBounds(250,600,120,35);
        print.setFont(font);
        center.add(print);

        print.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               StringBuilder fullText = new StringBuilder();
               orderedFoodList = new ArrayList<>();
               int tableRows = tbl.getRowCount();
               String line = "";
               String text = "";

                line = Utils.TITLE;
                String format = String.format("%-" + (42 - line.length()) / 2 + "s", text);
                fullText.append(format).append(line).append(format).append("\n");

                line = "Signboard, Siddhirganj, Narayanganj";
                String format2 = String.format("%-" + (42 - line.length()) / 2 + "s", text);
                fullText.append(format2).append(line).append(format2);

                line = "Cell: 01600101001";
                String format3 = "\n"+ String.format("%-" + (42 - line.length()) / 2 + "s", text);
                fullText.append(format3).append(line).append(format3).append("\n");

                line = todayDateTime();
                String format4 =  String.format("%-" + (42 - line.length()) / 2 + "s", text);
                fullText.append(format4).append(line).append(format4).append("\n");

                line = "Invoice: 0125478563";
                String format5 = String.format("%-" + (42 - line.length()) / 2 + "s", text);
                fullText.append(format5).append(line).append(format5).append("\n");

                line = "User : "+Utils.authority.username;
                String format6 = String.format("%-" + (42 - line.length()) / 2 + "s", text);
                fullText.append(format6).append(line).append(format6).append("\n\n");


                line = "Item";
                fullText.append("Item")
                       .append(String.format("%-" + (42 - 22) + "s", text)).append("Qty X Rate")
                       .append(String.format("%-" + (42 - 39) + "s", text)).append("Price")
                        .append("\n");

//                fullText.append(headerLine).append("\n");
                fullText.append("==========================================\n");
               int products = 0;
               for(int i =0; i<tableRows; i++){
                   products ++;
                   foodCart fc = new foodCart();
                   fc.name = getString(tbl.getModel().getValueAt(i,1));
                   fc.quantity = Integer.parseInt(tbl.getModel().getValueAt(i,3).toString());
                   fc.unitPrice = Double.parseDouble(tbl.getModel().getValueAt(i,4).toString());
                   fc.price = Double.parseDouble(tbl.getModel().getValueAt(i,5).toString());
                   long rate = Math.round(fc.price);
                   line = fc.name+" ("+fc.quantity+"x"+Math.round(fc.unitPrice)+")"+rate;
                   line = fc.name+" ("+fc.quantity+"x"+Math.round(fc.unitPrice)+")" + String.format("%-"+(42-line.length())+"s",text)+Math.round(rate)+"\n";
                   fullText.append(line);
                   line = "";
               }
               line = "";
               fullText.append("\n------------------------------------------\n");
               Double discount = getDoubleValue(txtDiscountAmt.getText());
               line = "Discount("+ getString(discountPercentage.getText())+")"+discount;
               fullText.append("Discount(").append(getString(discountPercentage.getText())).append(")").append(String.format("%-" + (44 - line.length()) + "s", text)).append(Math.round(discount)).append("\n");

               Double vat = getDoubleValue(txtVATAmt.getText());
               line = "VAT("+ getString(vatPercentage.getText())+")"+vat;
               fullText.append("VAT(").append(getString(vatPercentage.getText())).append(")").append(String.format("%-" + (44 - line.length()) + "s", text)).append(Math.round(vat));

               fullText.append("\n------------------------------------------\n");
               Double amount = getDoubleValue(txtAmt.getText());
               line = "Grand Total (Payment Method: "+payment.getSelectedItem()+")" +amount;
               line =  "Grand Total (Payment Method: "+payment.getSelectedItem()+")" + String.format("%-"+(44-line.length())+"s",text)+Math.round(amount);
               fullText.append(line).append("\n");
                fullText.append("==========================================\n\n");

               line = "\nNumber of Item purchased: "+products+"\n\n\n\n";
               format3 = String.format("%-" + (42 - line.length()) / 2 + "s", text);
               fullText.append(format3).append(line).append(format3).append("\n\n\n");
               // System.out.println(fullText);
               PrinterService printerService = new PrinterService();
               printerService.printString("SEWOO SLK-TS100",fullText.toString());
               byte[] cutP = new byte[] { 0x1d, 'V', 1 };
               printerService.printBytes("SEWOO SLK-TS100", cutP);
                saveTransaction();
                JOptionPane.showMessageDialog(null, "Transaction saved & printed!");
            }
        });




        comboItem.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED && itemName.containsKey(comboItem.getSelectedItem())) {
                    Item item = itemName.get(comboItem.getSelectedItem());
                    setItemSize(item.name);
                    Object size = comboSize.getSelectedItem();
                    Double value = getPrice(item.name,size.toString());
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
            @Override
            public void keyReleased(KeyEvent e) {
                Double price = Utils.getDoubleVal(txtPrice.getText());
                Integer qty = quantity.getValue() == null ? 0 : (Integer) quantity.getValue();
                Double result = price * qty;
                txtTotal.setText(String.valueOf(result));
            }
        });


        catCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    String comboItem = getString(catCombo.getSelectedItem());
                    String searchText = getString(search.getText());
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
            Object price = tbl2.getModel().getValueAt(row, 2);
            comboItem.setSelectedItem(food);
            comboSize.setSelectedItem(size);
            txtPrice.setText(price.toString());
            txtTotal.setText(price.toString());


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
        comboItem.setSelectedItem("");
        txtPrice.setText("");
        quantity.setValue(1);
        txtTotal.setText("");
        amtCalculator();
    }

    public void amtCalculator() {
        double amt = 0.0;

        // Calculate total amount from cart
        for (foodCart f : foodCartList) {
            amt += f.price;
        }
        txtTotalAmt.setText(String.valueOf(Math.round(amt)));

        // Try to get percentage discount
        double discountPercent = Utils.getDoubleVal(discountPercentage.getText());
        double discountAmt;

        if (discountPercent > 0) {
            discountAmt = (amt * discountPercent) / 100;
        } else {
            // Use manual discount if percentage is not provided or is <= 0
            discountAmt = Utils.getDoubleVal(txtDiscountAmt.getText());
        }

        txtDiscountAmt.setText(String.valueOf(Math.round(discountAmt)));

        // Calculate VAT on amount after discount
        double vatPercent = Utils.getDoubleVal(Utils.VAT);
        double vatAmt = ((amt - discountAmt) * vatPercent) / 100;
        txtVATAmt.setText(String.valueOf(Math.round(vatAmt)));

        // Final total amount
        double finalAmt = amt - discountAmt + vatAmt;
        txtAmt.setText(String.valueOf(Math.round(finalAmt)));
    }



    class foodCart{
        String name;
        String size;
        Double unitPrice;
        int quantity;
        Double price;
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

    public void showButtonDemo() {
        try{
            pst = con.mkDataBase().prepareStatement("select distinct a.id itemId,a.item_name,a.price,a.quantity from item a join category b on b.id = a.cat_id where a.deleted = false and b.deleted = false ");
            rs = pst.executeQuery();
            Item item = null;
            while(rs.next()){

                data[itemCount] = rs.getString("item_name");

                item = new Item();
                item.id = rs.getInt("itemId");
                item.name =  rs.getString("item_name");
                item.price = rs.getDouble("price");
                item.quantity = rs.getString("quantity");
                itemName.put(data[itemCount].toString(),item);
                itemList.add(item);
                itemCount++;
            }
        }catch(Exception e){
            //e.printStackTrace();
            //JOptionPane.showMessageDialog(null, "Getting items error !");
        }
    }

    public Integer getItemId(String itemName, String unit){
        for(int i=0;i<itemList.size();i++){
            if(itemList.get(i).name.equals(itemName) && itemList.get(i).quantity.equals(unit) ){
                return itemList.get(i).id;
            }
        }
        return 0;
    }
    public void setItemSize(String foodName){
        comboSize.removeAllItems();
        ArrayList<String> list = new ArrayList();
        for(Item itm: itemList){
            if(itm.name.equalsIgnoreCase(foodName)){
                comboSize.addItem(itm.quantity);
            }
        }
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
                pst = con.mkDataBase().prepareStatement("insert into  bill_details(food,size,quantity,per_unit_price,total_price,bill_id, item_id)" +
                        " values (?,?,?,?,?,?,?)");
                pst.setString(1, f.name);
                pst.setString(2, f.size);
                pst.setInt(3, f.quantity);
                pst.setDouble(4, f.unitPrice);
                pst.setDouble(5, f.price);
                pst.setDouble(6, primaryKey);
                pst.setInt(7, getItemId(f.name, f.size));
                pst.execute();
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{

            }
        }
        dtm.setRowCount(0);
        foodCartList = new ArrayList<>();
    }

    public void getCategory() {
        try{
            pst = con.mkDataBase().prepareStatement("select * from category where deleted = false");
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

    public void filterComboBox(String input) {

        comboItem.hidePopup();

        comboItem.removeAllItems();
        comboItem.addItem(input); // keep what user typed

        for (int i = 0; i < itemCount; i++) {
            String item = data[i].toString();
            if (item.toLowerCase().contains(input.toLowerCase())) {
                comboItem.addItem(item);
            }
        }

        comboItem.setSelectedItem(input); // reset editor text
        comboItem.showPopup(); // show dropdown again
    }

    public void loadItems(String cats,String txt){
        dtm2.setRowCount(0);
        PreparedStatement pst;
        ResultSet rs;
        DBConnection con = new DBConnection();
        String sql =  "select b.name,a.item_name,a.price,a.quantity from item a " +
                "                    inner join category b on b.id = a.cat_id Where a.deleted = false and b.deleted = false and lower(b.name) like lower('%"+cats+"%') and lower(a.item_name) like lower('%"+txt+"%') order by a.item_name";
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
            pst = con.mkDataBase().prepareStatement("insert into bill ( created_date, description, vat_amt, discount_amt, total,amount,payment_method,created_by) values (" +
                    " CURRENT_TIMESTAMP,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            //     pst.setDate(1, new java.sql.Date(new java.util.Date().getTime()));
            pst.setString(1, "");
            pst.setDouble(2, vat);
            pst.setDouble(3, discount);
            pst.setDouble(4, price);
            pst.setDouble(5, amount);
            pst.setString(6, payment.getSelectedItem().toString());
            pst.setString(7,Utils.authority.username);

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
                    fc.size = tbl.getModel().getValueAt(i,2).toString();
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

    payment.setSelectedItem(PaymentType.Cash.name());
    }


    // Add this method to reset the combo box to show all items
    private void resetComboBox() {
        JTextField editor = (JTextField) comboItem.getEditor().getEditorComponent();
        KeyListener[] listeners = editor.getKeyListeners();
        for (KeyListener listener : listeners) {
            editor.removeKeyListener(listener);
        }

        try {
            comboItem.hidePopup();
            comboItem.removeAllItems();
            comboItem.addItem(""); // Empty first item

            for (int i = 0; i < itemCount; i++) {
                comboItem.addItem(data[i]);
            }

            comboItem.setSelectedItem("");

        } finally {
            for (KeyListener listener : listeners) {
                editor.addKeyListener(listener);
            }
        }
    }
}