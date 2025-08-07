package com.rms.bill;

import com.rms.Frame2new;
import com.rms.service.AppService;
import com.rms.service.ReportService;
import com.rms.setting.Utils;
import dto.Category;
import dto.Item;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Map;

public class AdvanceReport extends JFrame{

    AppService appService = new AppService();
    ReportService reportService = new ReportService();
    private JFrame mainFrame;

    // Report type selection
    JLabel reportsLabel = new JLabel("Report Type:");
    JComboBox<String> reports = new JComboBox<>(new String[]{"Category Wise Sales Report", "Item Wise Sales Report", "Payment Method Wise Sales Report"});

    // Filter components
    JLabel categoryLabel = new JLabel("Category:");
    JComboBox<String> categoryCombo = new JComboBox<>();
    JLabel itemLabel = new JLabel("Item:");
    JComboBox<String> itemCombo = new JComboBox<>();
    JLabel paymentLabel = new JLabel("Payment:");
    JComboBox<String> paymentCombo = new JComboBox<>(new String[]{"All", "Cash", "MFS", "Credit"});
    JCheckBox chkDiscount = new JCheckBox("Include Discount");
    JCheckBox chkVAT = new JCheckBox("Include VAT");

    // Export button
    JButton btnExportPDF = new JButton("Export PDF");

    // Status message
    JLabel message = new JLabel("Ready to export...", JLabel.CENTER);

    public AdvanceReport(){
        mainFrame = new JFrame("Advance Reporting");
        mainFrame.setSize(800,700);
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

        setupComponents();
        addEventListeners();

        if(!Frame2new.allow){
            btnExportPDF.setVisible(false);
            message.setText("Billing period expired, contact Administrator at 01754282387");
            message.setForeground(Color.RED);
        }

        fetchCategoryItem();
    }

    private void setupComponents() {
        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(new Color(70, 130, 180));
        mainPanel.setBounds(0, 0, 800, 900);

        int yPos = 50; // Starting Y position

        // 1. Reports ComboBox with Label
        reportsLabel.setBounds(50, yPos, 120, 35);
        reportsLabel.setFont(Utils.FONT_16);
        mainPanel.add(reportsLabel);
        reports.setBounds(180, yPos, 350, 35);
        reports.setFont(Utils.FONT_16);
        mainPanel.add(reports);
        yPos += 45;

        // 2. Category Combo with Label
        categoryLabel.setBounds(50, yPos, 120, 35);
        categoryLabel.setFont(Utils.FONT_16);
        mainPanel.add(categoryLabel);
        categoryCombo.setBounds(180, yPos, 300, 35);
        categoryCombo.setFont(Utils.FONT_16);
        mainPanel.add(categoryCombo);
        yPos += 45;

        // 3. Item Combo with Label
        itemLabel.setBounds(50, yPos, 120, 35);
        itemLabel.setFont(Utils.FONT_16);
        mainPanel.add(itemLabel);
        itemCombo.setBounds(180, yPos, 300, 35);
        itemCombo.setFont(Utils.FONT_16);
        mainPanel.add(itemCombo);
        yPos += 45;

        // 4. Payment Combo with Label
        paymentLabel.setBounds(50, yPos, 120, 35);
        paymentLabel.setFont(Utils.FONT_16);
        mainPanel.add(paymentLabel);
        paymentCombo.setBounds(180, yPos, 150, 35);
        paymentCombo.setFont(Utils.FONT_16);
        mainPanel.add(paymentCombo);
        yPos += 45;

        // 5. Include Discount Checkbox (no label)
        chkDiscount.setBounds(50, yPos, 200, 35);
        chkDiscount.setFont(Utils.FONT_16);
        chkDiscount.setSelected(true);
        chkDiscount.setBackground(Color.orange);
        mainPanel.add(chkDiscount);
        yPos += 45;

        // 6. Include VAT Checkbox (no label)
        chkVAT.setBounds(50, yPos, 150, 35);
        chkVAT.setFont(Utils.FONT_16);
        chkVAT.setSelected(true);
        chkVAT.setBackground(Color.orange);
        mainPanel.add(chkVAT);
        yPos += 60;

        // 7. Export PDF Button
        btnExportPDF.setBounds(50, yPos, 150, 40);
        btnExportPDF.setFont(Utils.FONT_16);
        btnExportPDF.setBackground(new Color(220, 53, 69));
        btnExportPDF.setForeground(Color.WHITE);
        mainPanel.add(btnExportPDF);
        yPos += 60;

        // 8. Status Message (Bottom)
        message.setBounds(3, yPos, 800, 30);
        message.setFont(Utils.FONT_16);
        mainPanel.add(message);

        mainFrame.add(mainPanel);
    }

    private void addEventListeners() {
        btnExportPDF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportToPDF();
            }
        });
    }

    private void exportToPDF() {
        message.setText("Exporting to PDF...");

        String reportType = reports.getSelectedItem().toString();
        String category = categoryCombo.getSelectedItem().toString();
        String item = itemCombo.getSelectedItem().toString();
        String paymentMethod = paymentCombo.getSelectedItem().toString();
        boolean includeDiscount = chkDiscount.isSelected();
        boolean includeVat = chkVAT.isSelected();

        try {
            if(reportType.equalsIgnoreCase("Category Wise Sales Report")){
                List<Map<String,Object>> objectList = appService.getSummaryByCategory(category, item, paymentMethod, includeDiscount, includeVat);
                reportService.exportByCategory(objectList);
            }else if (reportType.equalsIgnoreCase("Item Wise Sales Report")){
                List<Map<String,Object>> objectList = appService.getSummaryByItem(category, item, paymentMethod, includeDiscount, includeVat);
                reportService.exportByItem(objectList);
            } else if (reportType.equalsIgnoreCase("Payment Method Wise Sales Report")){
                List<Map<String,Object>> objectList = appService.getSummaryByPaymentMethod(category, item, paymentMethod, includeDiscount, includeVat);
                reportService.exportByPaymentMethod(objectList);
            }

            message.setText("Report exported: " + reportType);
            JOptionPane.showMessageDialog(mainFrame, "Report exported to PDF successfully!\nReport Type: " + reportType);

        } catch (Exception ex) {
           // ex.printStackTrace();
            message.setText("Error occurred during PDF export: " + ex.getMessage());
            JOptionPane.showMessageDialog(mainFrame, "Error: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void fetchCategoryItem(){
        try {
            AppService service = new AppService();

            // Clear existing items
            categoryCombo.removeAllItems();
            itemCombo.removeAllItems();

            // Populate categories
            List<Category> categories = service.getCategory();
            categoryCombo.addItem("All");
            for (Category category : categories) {
                categoryCombo.addItem(category.name);
            }

            // Populate items
            List<Item> items = service.getUniqueItems();
            itemCombo.addItem("All");
            for (Item item : items) {
                itemCombo.addItem(item.name);
            }

        } catch (Exception ex) {
            message.setText("Error loading categories and items: " + ex.getMessage());
            //ex.printStackTrace();
        }
    }
}