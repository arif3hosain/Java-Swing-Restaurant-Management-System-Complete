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
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReportBuilder extends JFrame{

    AppService appService = new AppService();
    private JFrame mainFrame;
    ReportService reportService = new ReportService();
    JLabel logoLabel;
    // Header components
    JLabel titleLabel = new JLabel("ADVANCE REPORTING SYSTEM", JLabel.CENTER);

    // Report Type (New - First Row)
    JLabel reportTypeLabel = new JLabel("Report Type:");
    JComboBox<String> reportTypeCombo = new JComboBox<>(new String[]{"Category Wise Sales Report", "Item Wise Sales Report", "Payment Method Wise Sales Report"});

    // Filter components (Row 2)
    JLabel categoryLabel = new JLabel("Category:");
    JComboBox<String> categoryCombo = new JComboBox<>();
    JLabel paymentLabel = new JLabel("Payment:");
    JComboBox<String> paymentCombo = new JComboBox<>(new String[]{"All", "Cash", "MFS", "Credit"});

    // Filter components (Row 3)
    JLabel itemLabel = new JLabel("Item:");
    JComboBox<String> itemCombo = new JComboBox<>();
    JCheckBox chkDiscount = new JCheckBox("Include Discount");
    JCheckBox chkVAT = new JCheckBox("Include VAT");

    // Date filter components (Row 4)
    JLabel fromDateLabel = new JLabel("From Date:");
    JTextField fromDateField = new JTextField("");
    JLabel toDateLabel = new JLabel("To Date:");
    JTextField toDateField = new JTextField("");

    // Action buttons (Row 5)
    JButton btnSearch = new JButton("Search");
    JButton btnExportPDF = new JButton("Export PDF");
    JButton btnClear = new JButton("Clear Filters");
    JButton btnRefresh = new JButton("Refresh");

    // Results display components
    JLabel resultLabel = new JLabel("Search Results:");
    JTable resultTable;
    JScrollPane tableScrollPane;
    DefaultTableModel tableModel;

    // Summary components
    JPanel summaryPanel = new JPanel();
    JLabel totalRecordsLabel = new JLabel("Total Records: 0");
    JLabel totalAmountLabel = new JLabel("Total Amount: 0.00");
    JLabel totalDiscountLabel = new JLabel("Total Discount: 0.00");
    JLabel totalVATLabel = new JLabel("Total VAT: 0.00");

    // Status and message components
    JLabel message = new JLabel("Ready to search...", JLabel.CENTER);
    JProgressBar progressBar = new JProgressBar();

    public ReportBuilder(){
        mainFrame = new JFrame("Report Builder");
        mainFrame.setSize(1500,900);
        mainFrame.setResizable(false);
        mainFrame.setLayout(null);
        mainFrame.setLocationRelativeTo(null);
//        mainFrame.setBackground(Color.orange);
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
            disableComponents();
        }

        fetchCategoryItem();
        updateTableColumnsBasedOnReportType(); // Initialize table columns
    }

    private void setupComponents() {
        JPanel mainPanel = new JPanel(null);
        mainPanel.setBounds(0, 0, 1500, 900);

        int yPos = 10; // Starting Y position

        // 1. Title Header (Top)
        titleLabel.setBounds(0, yPos, 1500, 40);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setOpaque(true);
//        titleLabel.setBackground(new Color(240, 240, 240));
        mainPanel.add(titleLabel);
        yPos += 50;

        // 2. Report Type Row (NEW)
        reportTypeLabel.setBounds(50, yPos, 100, 30);
        reportTypeLabel.setFont(Utils.FONT_16);
        mainPanel.add(reportTypeLabel);

        reportTypeCombo.setBounds(150, yPos, 350, 30);
        reportTypeCombo.setFont(Utils.FONT_16);
        mainPanel.add(reportTypeCombo);
        yPos += 40;

        // 3. First Filter Row (Category and Payment)
        categoryLabel.setBounds(50, yPos, 100, 30);
        categoryLabel.setFont(Utils.FONT_16);
        mainPanel.add(categoryLabel);

        categoryCombo.setBounds(150, yPos, 250, 30);
        categoryCombo.setFont(Utils.FONT_16);
        mainPanel.add(categoryCombo);

        paymentLabel.setBounds(450, yPos, 100, 30);
        paymentLabel.setFont(Utils.FONT_16);
        mainPanel.add(paymentLabel);

        paymentCombo.setBounds(550, yPos, 150, 30);
        paymentCombo.setFont(Utils.FONT_16);
        mainPanel.add(paymentCombo);
        yPos += 40;

        // 4. Second Filter Row (Item and Checkboxes)
        itemLabel.setBounds(50, yPos, 100, 30);
        itemLabel.setFont(Utils.FONT_16);
        mainPanel.add(itemLabel);

        itemCombo.setBounds(150, yPos, 250, 30);
        itemCombo.setFont(Utils.FONT_16);
        mainPanel.add(itemCombo);

        chkDiscount.setBounds(450, yPos, 150, 30);
        chkDiscount.setFont(Utils.FONT_16);
        chkDiscount.setSelected(true);
        mainPanel.add(chkDiscount);

        chkVAT.setBounds(610, yPos, 120, 30);
        chkVAT.setFont(Utils.FONT_16);
        chkVAT.setSelected(true);
        mainPanel.add(chkVAT);
        yPos += 40;

        // 5. Date Filter Row
        fromDateLabel.setBounds(50, yPos, 100, 30);
        fromDateLabel.setFont(Utils.FONT_16);
        mainPanel.add(fromDateLabel);

        fromDateField.setBounds(150, yPos, 120, 30);
        fromDateField.setFont(Utils.FONT_16);
        fromDateField.setText(Utils.getTokenDate(new Date()));
        mainPanel.add(fromDateField);

        toDateLabel.setBounds(290, yPos, 80, 30);
        toDateLabel.setFont(Utils.FONT_16);
        mainPanel.add(toDateLabel);

        toDateField.setBounds(370, yPos, 120, 30);
        toDateField.setFont(Utils.FONT_16);
        toDateField.setText(Utils.getTokenDate(new Date()));
        mainPanel.add(toDateField);
        yPos += 40;

        // 6. Action Buttons Row
        btnSearch.setBounds(50, yPos, 100, 35);
        btnSearch.setFont(Utils.FONT_16);
        btnSearch.setBackground(new Color(0, 123, 255));
        btnSearch.setForeground(Color.WHITE);
        mainPanel.add(btnSearch);

        btnClear.setBounds(160, yPos, 120, 35);
        btnClear.setFont(Utils.FONT_16);
        btnClear.setBackground(new Color(108, 117, 125));
        btnClear.setForeground(Color.WHITE);
        mainPanel.add(btnClear);

        btnRefresh.setBounds(290, yPos, 100, 35);
        btnRefresh.setFont(Utils.FONT_16);
        btnRefresh.setBackground(new Color(40, 167, 69));
        btnRefresh.setForeground(Color.WHITE);
        mainPanel.add(btnRefresh);

        btnExportPDF.setBounds(400, yPos, 120, 35);
        btnExportPDF.setFont(Utils.FONT_16);
        btnExportPDF.setBackground(new Color(220, 53, 69));
        btnExportPDF.setForeground(Color.WHITE);
        mainPanel.add(btnExportPDF);
        yPos += 50;

        // 7. Results Label
        resultLabel.setBounds(50, yPos, 200, 25);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(resultLabel);
        yPos += 30;

        logoLabel = new JLabel();
        logoLabel.setBounds(1100, 60, 250, 200); // Centered in left panel
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setVerticalAlignment(SwingConstants.CENTER);
        try {
            // Try loading from resources first
            InputStream logoStream = getClass().getResourceAsStream(Utils.LOGO_PATH);
            if (logoStream != null) {
                ImageIcon logoIcon = new ImageIcon(ImageIO.read(logoStream));
                // Scale the image to fit the label
                Image scaledImage = logoIcon.getImage().getScaledInstance(250, 200, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(scaledImage));
                logoStream.close();
            } else {
                // Fallback to file system
                ImageIcon logoIcon = new ImageIcon(Utils.LOGO_PATH);
                if (logoIcon.getIconWidth() > 0) {
                    Image scaledImage = logoIcon.getImage().getScaledInstance(230, 180, Image.SCALE_SMOOTH);
                    logoLabel.setIcon(new ImageIcon(scaledImage));
                } else {
                    // Show company name if logo not found
                    logoLabel.setText("<html><div style='text-align: center;'><font color='white' size='6'><b>COMPANY<br>LOGO</b></font></div></html>");
                }
            }
        } catch (Exception ex) {
            // Show company name if logo not found
            logoLabel.setText("<html><div style='text-align: center;'><font color='white' size='6'><b>COMPANY<br>LOGO</b></font></div></html>");
        }
        mainPanel.add(logoLabel);
        // 8. Results Table (Initialize with default columns)
        String[] columnNames = {"Category", "Amount", "Discount", "VAT", "Billed Amount"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
        resultTable.setFont(new Font("Arial", Font.PLAIN, 15));
        resultTable.setRowHeight(25);
        resultTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        resultTable.getTableHeader().setBackground(Color.ORANGE);

       // resultTable.getTableHeader().setForeground(Color.WHITE);

        tableScrollPane = new JScrollPane(resultTable);
        tableScrollPane.setBounds(50, yPos, 1400, 350);
        mainPanel.add(tableScrollPane);
        yPos += 370;

        // 9. Summary Panel
        summaryPanel.setBounds(50, yPos, 1400, 80);
        summaryPanel.setLayout(new GridLayout(2, 4, 10, 5));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        summaryPanel.setBackground(new Color(248, 249, 250));

        totalRecordsLabel.setFont(Utils.FONT_16);
        totalAmountLabel.setFont(Utils.FONT_16);
        totalDiscountLabel.setFont(Utils.FONT_16);
        totalVATLabel.setFont(Utils.FONT_16);

        summaryPanel.add(totalRecordsLabel);
        summaryPanel.add(totalAmountLabel);
        summaryPanel.add(totalDiscountLabel);
        summaryPanel.add(totalVATLabel);

        mainPanel.add(summaryPanel);
        yPos += 90;

        // 10. Progress Bar
        progressBar.setBounds(50, yPos, 1400, 20);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        mainPanel.add(progressBar);
        yPos += 30;

        // 11. Status Message (Bottom)
        message.setBounds(0, yPos, 1500, 30);
        message.setFont(Utils.FONT_16);
        message.setOpaque(true);
        message.setBackground(new Color(240, 240, 240));
        mainPanel.add(message);

        mainFrame.add(mainPanel);
    }

    private void addEventListeners() {
        // Report type change listener
        reportTypeCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTableColumnsBasedOnReportType();
                clearTableData();
            }
        });

        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchHistory();
            }
        });

        btnExportPDF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportToPDF();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearFilters();
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
    }

    private void updateTableColumnsBasedOnReportType() {
        String selectedReport = reportTypeCombo.getSelectedItem().toString();
        String[] columnNames;

        switch (selectedReport) {
            case "Category Wise Sales Report":
                columnNames = new String[]{"Category ID", "Category Name", "Amount", "Discount", "VAT", "Billed Amount"};
                break;
            case "Item Wise Sales Report":
                columnNames = new String[]{"Item Name", "Quantity", "Unit of Measurement", "Unit Price", "Total Rate"};
                break;
            case "Payment Method Wise Sales Report":
                columnNames = new String[]{"Payment Method", "Total Amount"};
                break;
            default:
                columnNames = new String[]{"Category", "Amount", "Discount", "VAT", "Billed Amount"};
                break;
        }

        // Update table model with new columns
        tableModel.setColumnIdentifiers(columnNames);
        tableModel.setRowCount(0); // Clear existing data
    }

    private void clearTableData() {
        tableModel.setRowCount(0);
        updateSummaryLabels(0, 0, 0, 0);
    }

    private void disableComponents() {
        btnExportPDF.setVisible(false);
        btnSearch.setVisible(false);
        btnClear.setVisible(false);
        btnRefresh.setVisible(false);
        message.setText("Billing period expired, contact Administrator at 01754282387");
        message.setForeground(Color.RED);
    }

    public void searchHistory(){
        // Show progress
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        message.setText("Searching...");

        String category = categoryCombo.getSelectedItem().toString();
        String item = itemCombo.getSelectedItem().toString();
        String paymentMethod = paymentCombo.getSelectedItem().toString();
        boolean includeDiscount = chkDiscount.isSelected();
        boolean includeVat = chkVAT.isSelected();
        String reportType = reportTypeCombo.getSelectedItem().toString();
        String fromDate = fromDateField.getText();
        String toDate = toDateField.getText();
        try {
            List<Map<String,Object>> objectList;

            // Call appropriate method based on report type
            switch (reportType) {
                case "Category Wise Sales Report":
                    objectList = appService.getSummaryByCategory(category, item, paymentMethod, includeDiscount, includeVat, fromDate, toDate);
                    break;
                case "Item Wise Sales Report":
                    objectList = appService.getSummaryByItem(category, item, paymentMethod, includeDiscount, includeVat,fromDate,toDate);
                    break;
                case "Payment Method Wise Sales Report":
                    objectList = appService.getSummaryByPaymentMethod(category, item, paymentMethod, includeDiscount, includeVat,fromDate,toDate);
                    break;
                default:
                    objectList = appService.getSummaryByCategory(category, item, paymentMethod, includeDiscount, includeVat,fromDate,toDate);
                    break;
            }

            // Clear existing data
            tableModel.setRowCount(0);

            // Populate table with results based on report type
            double totalAmount = 0, totalDiscount = 0, totalVAT = 0, totalPaid = 0;

            for (Map<String, Object> row : objectList) {
                Object[] tableRow = createTableRowBasedOnReportType(row, reportType);
                tableModel.addRow(tableRow);

                // Calculate totals
                totalAmount += getDoubleValue(row, "paid", null);
                totalDiscount -= getDoubleValue(row, "discount", null);
                totalVAT += getDoubleValue(row, "vat", null);
                totalPaid +=  getDoubleValue(row, "customerBill", "amount");
            }

            updateSummaryLabels(totalAmount, totalPaid, Math.abs(totalDiscount), totalVAT);
            message.setText("Search completed. Found " + objectList.size() + " records.");

        } catch (Exception ex) {
            message.setText("Error occurred during search: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            progressBar.setVisible(false);
        }
    }

    private Object[] createTableRowBasedOnReportType(Map<String, Object> row, String reportType) {
        switch (reportType) {
            case "Category Wise Sales Report":
                return new Object[]{
                        row.get("id"),
                        row.get("name"),
                        row.get("paid"),
                        row.get("discount"),
                        row.get("vat"),
                        row.get("customerBill"),
                };
            case "Item Wise Sales Report":
                return new Object[]{
                        row.get("name"),
                        row.get("quantity"),
                        row.get("size"),
                        row.get("unitPrice"),
                        row.get("amount"),

                };
            case "Payment Method Wise Sales Report":
                return new Object[]{
                        row.get("name"),
                        row.get("amount")
                };
            default:
                return new Object[]{
                        row.get("name"),
                        row.get("paid"),
                        row.get("discount"),
                        row.get("vat"),
                        row.get("customerBill"),
                };
        }
    }

    private void addFooterRow(String reportType, double totalAmount, double totalDiscount, double totalVAT, double totalPaid) {
        Object[] footerRow;

        switch (reportType) {
            case "Category Wise Sales Report":
                footerRow = new Object[]{
                        "",
                        "TOTAL",
                        String.format("%.2f", totalAmount),
                        String.format("%.2f", totalDiscount),
                        String.format("%.2f", totalVAT),
                        String.format("%.2f", totalPaid)
                };
                break;
            case "Item Wise Sales Report":
                footerRow = new Object[]{
                        "TOTAL",
                        "",
                        String.format("%.2f", totalAmount),
                        String.format("%.2f", totalDiscount),
                        String.format("%.2f", totalVAT),
                        String.format("%.2f", totalPaid)
                };
                break;
            default:
                return; // No footer for other report types
        }

        tableModel.addRow(footerRow);
    }

    private double getDoubleValue(Map<String, Object> row, String primaryKey, String alternateKey) {
        Object value = row.get(primaryKey);
        if (value == null && alternateKey != null) {
            value = row.get(alternateKey);
        }
        if (value != null) {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    private void updateSummaryLabels(double amount, double billedAmount, double totalDiscount, double totalVAT) {
        totalRecordsLabel.setText("Total Amount: " + amount);
        totalAmountLabel.setText(String.format("Total Billed Amount: %.2f", billedAmount));
        totalDiscountLabel.setText(String.format("Total Discount: %.2f", totalDiscount));
        totalVATLabel.setText(String.format("Total VAT: %.2f", totalVAT));
    }

    private void exportToPDF() {
      /*  message.setText("Exporting to PDF...");
        // TODO: Implement PDF export functionality
        JOptionPane.showMessageDialog(mainFrame, "PDF export functionality will be implemented here.");*/
        message.setText("Exporting to PDF...");

        String reportType = reportTypeCombo.getSelectedItem().toString();
        String category = categoryCombo.getSelectedItem().toString();
        String item = itemCombo.getSelectedItem().toString();
        String paymentMethod = paymentCombo.getSelectedItem().toString();
        boolean includeDiscount = chkDiscount.isSelected();
        boolean includeVat = chkVAT.isSelected();
        String fromDate = fromDateField.getText();
        String toDate = toDateField.getText();

        try {
            if(reportType.equalsIgnoreCase("Category Wise Sales Report")){
                List<Map<String,Object>> objectList = appService.getSummaryByCategory(category, item, paymentMethod, includeDiscount, includeVat,fromDate,toDate);
                reportService.exportByCategory(objectList);
            }else if (reportType.equalsIgnoreCase("Item Wise Sales Report")){
                List<Map<String,Object>> objectList = appService.getSummaryByItem(category, item, paymentMethod, includeDiscount, includeVat,fromDate,toDate);
                reportService.exportByItem(objectList);
            } else if (reportType.equalsIgnoreCase("Payment Method Wise Sales Report")){
                List<Map<String,Object>> objectList = appService.getSummaryByPaymentMethod(category, item, paymentMethod, includeDiscount, includeVat,fromDate,toDate);
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

    private void clearFilters() {
        reportTypeCombo.setSelectedIndex(0);
        categoryCombo.setSelectedIndex(0);
        itemCombo.setSelectedIndex(0);
        paymentCombo.setSelectedIndex(0);
        chkDiscount.setSelected(true);
        chkVAT.setSelected(true);
        fromDateField.setText(Utils.getTokenDate(new Date()));
        toDateField.setText(Utils.getTokenDate(new Date()));
        clearTableData();
        updateTableColumnsBasedOnReportType();
        message.setText("Filters cleared. Ready to search...");
    }

    private void refreshData() {
        message.setText("Refreshing data...");
        fetchCategoryItem();
        clearTableData();
        message.setText("Data refreshed successfully.");
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
            ex.printStackTrace();
        }
    }
}