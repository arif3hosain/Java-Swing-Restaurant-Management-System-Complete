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
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AdvanceSearch extends JFrame{

    String columns[] = new String[] {"SERIAL","INVOICE_NO","CATEGORY","ITEM NAME","SIZE","RATE", "BILLED AT","METHOD"};
    AppService appService = new AppService();
    private JFrame mainFrame;
    JTable tbl = null;
    DefaultTableModel dtm = null;
    PreparedStatement pst;
    ResultSet rs;
    DBConnection con = new DBConnection();

    // Existing components
    JCheckBox chkDiscount = new JCheckBox();
    JCheckBox chkVAT = new JCheckBox();
    JButton btnSearch = new JButton("Search");
    JButton btnClear = new JButton("Clear");
    JComboBox<String> categoryCombo = new JComboBox<>();
    JComboBox<String> itemCombo = new JComboBox<>();
    JComboBox<String> paymentCombo = new JComboBox<>(new String[]{"All", "Cash", "MFS", "Credit"});

    // New date and invoice search components
    JTextField fromDate = new JTextField("");
    JTextField toDate = new JTextField("");
    JTextField invoiceNoField = new JTextField("");

    JLabel message = new JLabel("...");

    public AdvanceSearch(){
        mainFrame = new JFrame("Advance Filter");
        mainFrame.setSize(1500,950); // Increased height for additional controls
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
        top.setBounds(0,0,1500,150); // Increased height for two rows

        // First row of controls
        JLabel lblCategory = new JLabel("Category:");
        lblCategory.setBounds(10, 15, 60, 25);
        lblCategory.setFont(Utils.FONT_16);
        top.add(lblCategory);

        categoryCombo.setBounds(80, 15, 180, 30);
        categoryCombo.setFont(Utils.FONT_15);
        top.add(categoryCombo);

        JLabel lblItem = new JLabel("Item:");
        lblItem.setBounds(275, 15, 40, 25);
        lblItem.setFont(Utils.FONT_16);
        top.add(lblItem);

        itemCombo.setBounds(320, 15, 180, 30);
        itemCombo.setFont(Utils.FONT_15);
        top.add(itemCombo);

        JLabel lblPayment = new JLabel("Payment:");
        lblPayment.setBounds(515, 15, 60, 25);
        lblPayment.setFont(Utils.FONT_16);
        top.add(lblPayment);

        paymentCombo.setBounds(580, 15, 100, 30);
        paymentCombo.setFont(Utils.FONT_15);
        top.add(paymentCombo);

        chkDiscount.setText("Include Discount");
        chkDiscount.setBounds(700, 15, 150, 30);
        chkDiscount.setFont(Utils.FONT_15);
        chkDiscount.setSelected(true);
        top.add(chkDiscount);

        chkVAT.setText("Include VAT");
        chkVAT.setBounds(860, 15, 120, 30);
        chkVAT.setFont(Utils.FONT_15);
        chkVAT.setSelected(true);
        top.add(chkVAT);

        // Second row of controls - Date range and Invoice number with DOUBLE WIDTH LABELS
        JLabel lblFromDate = new JLabel("From Date:");
        lblFromDate.setBounds(10, 55, 140, 25); // Double width: 70 -> 140
        lblFromDate.setFont(Utils.FONT_16);
        lblFromDate.setHorizontalAlignment(SwingConstants.RIGHT); // Right-align for better form appearance
        top.add(lblFromDate);

        fromDate.setBounds(155, 55, 120, 30); // Adjusted position due to wider label
        fromDate.setFont(Utils.FONT_15);
        fromDate.setText(Utils.getTokenDate(new Date()));
        fromDate.setToolTipText("Format: YYYY-MM-DD");
        top.add(fromDate);

        JLabel lblToDate = new JLabel("To Date:");
        lblToDate.setBounds(290, 55, 110, 25); // Double width: 55 -> 110
        lblToDate.setFont(Utils.FONT_16);
        lblToDate.setHorizontalAlignment(SwingConstants.RIGHT); // Right-align for better form appearance
        top.add(lblToDate);

        toDate.setBounds(405, 55, 120, 30); // Adjusted position due to wider label
        toDate.setFont(Utils.FONT_15);
        toDate.setText(Utils.getTokenDate(new Date()));
        toDate.setToolTipText("Format: YYYY-MM-DD");
        top.add(toDate);

        JLabel lblInvoiceNo = new JLabel("Invoice No:");
        lblInvoiceNo.setBounds(540, 55, 150, 25); // Double width: 75 -> 150
        lblInvoiceNo.setFont(Utils.FONT_16);
        lblInvoiceNo.setHorizontalAlignment(SwingConstants.RIGHT); // Right-align for better form appearance
        top.add(lblInvoiceNo);

        invoiceNoField.setBounds(695, 55, 120, 30); // Adjusted position due to wider label
        invoiceNoField.setFont(Utils.FONT_15);
        invoiceNoField.setToolTipText("Leave empty for all invoices");
        top.add(invoiceNoField);

        // Buttons - adjusted positions
        btnSearch.setBounds(830, 55, 80, 30); // Adjusted position
        btnSearch.setFont(Utils.FONT_15);
        top.add(btnSearch);

        btnClear.setBounds(920, 55, 70, 30); // Adjusted position
        btnClear.setFont(Utils.FONT_15);
        top.add(btnClear);

        // Message label - adjusted position
        message.setBounds(1000, 55, 400, 25); // Adjusted position
        message.setFont(Utils.FONT_15);
        top.add(message);

        // Bottom panel for table
        JPanel bottom = new JPanel(null);
        bottom.setBounds(0,150,1500,750); // Adjusted position and height

        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchHistory();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearFilters();
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
        tbl.getColumnModel().getColumn(0).setPreferredWidth(20);   // SERIAL
        tbl.getColumnModel().getColumn(1).setPreferredWidth(60);   // INVOICE_NO
        tbl.getColumnModel().getColumn(2).setPreferredWidth(250);  // CATEGORY
        tbl.getColumnModel().getColumn(3).setPreferredWidth(250);  // ITEM NAME
        tbl.getColumnModel().getColumn(4).setPreferredWidth(50);   // SIZE
        tbl.getColumnModel().getColumn(5).setPreferredWidth(50);   // RATE
        tbl.getColumnModel().getColumn(6).setPreferredWidth(170);  // BILLED AT
        tbl.getColumnModel().getColumn(7).setPreferredWidth(50);   // METHOD

        JScrollPane pane = new JScrollPane(tbl, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setBounds(0,0,1500,700);
        bottom.add(pane);

        mainFrame.add(top);
        mainFrame.add(bottom);

        if(!Frame2new.allow){
            btnSearch.setVisible(false);
            message.setBounds(830,60,400,25); // Adjusted position for message when button is hidden
            message.setText("Billing period expired, contact Administrator at 01754282387");
        }
        fetchCategoryItem();
    }

    public void searchHistory(){
        dtm.setRowCount(0);

        // Get all filter values
        String category = categoryCombo.getSelectedItem().toString();
        String item = itemCombo.getSelectedItem().toString();
        String paymentMethod = paymentCombo.getSelectedItem().toString();
        boolean includeDiscount = chkDiscount.isSelected();
        boolean includeVat = chkVAT.isSelected();
        String fromDateStr = fromDate.getText().trim();
        String toDateStr = toDate.getText().trim();
        String invoiceNo = invoiceNoField.getText().trim();

        try {
            // Call enhanced service method with additional parameters
            List<Map<String,Object>> objectList = appService.advanceSearch(
                    category, item, paymentMethod, includeDiscount, includeVat,
                    fromDateStr, toDateStr, invoiceNo
            );

            int i = 1;
            for (Map<String,Object> obj : objectList) {
                String data[] = new String[]{
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

            message.setText(objectList.size() + " records found");

        } catch (Exception ex) {
            message.setText("Error: " + ex.getMessage());
            JOptionPane.showMessageDialog(mainFrame,
                    "Search failed: " + ex.getMessage(),
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void clearFilters() {
        // Reset all filter components to default values
        categoryCombo.setSelectedIndex(0); // Select "All"
        itemCombo.setSelectedIndex(0); // Select "All"
        paymentCombo.setSelectedIndex(0); // Select "All"
        chkDiscount.setSelected(true);
        chkVAT.setSelected(true);
        fromDate.setText(Utils.getTokenDate(new Date()));
        toDate.setText(Utils.getTokenDate(new Date()));
        invoiceNoField.setText("");

        // Clear table
        dtm.setRowCount(0);
        message.setText("Filters cleared");
    }

    public void fetchCategoryItem(){
        AppService service = new AppService();
        List<Category> categories = service.getCategory();
        categoryCombo.addItem("All");
        for (int i = 0; i < categories.size(); i++){
            categoryCombo.addItem(categories.get(i).name);
        }

        List<Item> items = service.getUniqueItems();
        itemCombo.addItem("All");
        for (int i = 0; i < items.size(); i++){
            itemCombo.addItem(items.get(i).name);
        }
    }
}