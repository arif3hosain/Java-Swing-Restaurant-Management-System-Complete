package com.rms.bill;

import com.rms.Frame2new;
import com.rms.service.AppService;
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

public class ReportBuilder extends JFrame{

    AppService appService = new AppService();
    private JFrame mainFrame;

    // Header components
    JLabel titleLabel = new JLabel("ADVANCE REPORTING SYSTEM", JLabel.CENTER);

    // Filter components (Row 1)
    JLabel categoryLabel = new JLabel("Category:");
    JComboBox<String> categoryCombo = new JComboBox<>();
    JLabel paymentLabel = new JLabel("Payment:");
    JComboBox<String> paymentCombo = new JComboBox<>(new String[]{"All", "Cash", "MFS", "Credit"});

    // Filter components (Row 2)
    JLabel itemLabel = new JLabel("Item:");
    JComboBox<String> itemCombo = new JComboBox<>();
    JCheckBox chkDiscount = new JCheckBox("Include Discount");
    JCheckBox chkVAT = new JCheckBox("Include VAT");

    // Date filter components (Row 3)
    JLabel fromDateLabel = new JLabel("From Date:");
    JTextField fromDateField = new JTextField("dd/mm/yyyy");
    JLabel toDateLabel = new JLabel("To Date:");
    JTextField toDateField = new JTextField("dd/mm/yyyy");

    // Action buttons (Row 4)
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
    }

    private void setupComponents() {
        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBounds(0, 0, 1500, 900);

        int yPos = 10; // Starting Y position

        // 1. Title Header (Top)
        titleLabel.setBounds(0, yPos, 1500, 40);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.DARK_GRAY);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(240, 240, 240));
        mainPanel.add(titleLabel);
        yPos += 50;

        // 2. First Filter Row (Category and Payment)
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

        // 3. Second Filter Row (Item and Checkboxes)
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

        // 4. Date Filter Row
        fromDateLabel.setBounds(50, yPos, 100, 30);
        fromDateLabel.setFont(Utils.FONT_16);
        mainPanel.add(fromDateLabel);

        fromDateField.setBounds(150, yPos, 120, 30);
        fromDateField.setFont(Utils.FONT_16);
        mainPanel.add(fromDateField);

        toDateLabel.setBounds(290, yPos, 80, 30);
        toDateLabel.setFont(Utils.FONT_16);
        mainPanel.add(toDateLabel);

        toDateField.setBounds(370, yPos, 120, 30);
        toDateField.setFont(Utils.FONT_16);
        mainPanel.add(toDateField);
        yPos += 40;

        // 5. Action Buttons Row
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

        // 6. Results Label
        resultLabel.setBounds(50, yPos, 200, 25);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(resultLabel);
        yPos += 30;

        // 7. Results Table
        String[] columnNames = {"Bill ID", "Date", "Category", "Item", "Quantity", "Price", "Discount", "VAT", "Total", "Payment"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
        resultTable.setFont(new Font("Arial", Font.PLAIN, 12));
        resultTable.setRowHeight(25);
        resultTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        resultTable.getTableHeader().setBackground(new Color(108, 117, 125));
        resultTable.getTableHeader().setForeground(Color.WHITE);

        tableScrollPane = new JScrollPane(resultTable);
        tableScrollPane.setBounds(50, yPos, 1400, 400);
        mainPanel.add(tableScrollPane);
        yPos += 420;

        // 8. Summary Panel
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

        // 9. Progress Bar
        progressBar.setBounds(50, yPos, 1400, 20);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        mainPanel.add(progressBar);
        yPos += 30;

        // 10. Status Message (Bottom)
        message.setBounds(0, yPos, 1500, 30);
        message.setFont(Utils.FONT_16);
        message.setOpaque(true);
        message.setBackground(new Color(240, 240, 240));
        mainPanel.add(message);

        mainFrame.add(mainPanel);
    }

    private void addEventListeners() {
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

        try {
            List<Map<String,Object>> objectList = appService.getBillDetails(category, item, paymentMethod, includeDiscount, includeVat);

            // Clear existing data
            tableModel.setRowCount(0);

            // Populate table with results
            double totalAmount = 0, totalDiscount = 0, totalVAT = 0;

            for (Map<String, Object> row : objectList) {
                Object[] tableRow = {
                        row.get("billId"),
                        row.get("date"),
                        row.get("category"),
                        row.get("item"),
                        row.get("quantity"),
                        row.get("price"),
                        row.get("discount"),
                        row.get("vat"),
                        row.get("total"),
                        row.get("payment")
                };
                tableModel.addRow(tableRow);

                // Calculate totals
                if (row.get("total") != null) totalAmount += Double.parseDouble(row.get("total").toString());
                if (row.get("discount") != null) totalDiscount += Double.parseDouble(row.get("discount").toString());
                if (row.get("vat") != null) totalVAT += Double.parseDouble(row.get("vat").toString());
            }

            // Update summary
            totalRecordsLabel.setText("Total Records: " + objectList.size());
            totalAmountLabel.setText(String.format("Total Amount: %.2f", totalAmount));
            totalDiscountLabel.setText(String.format("Total Discount: %.2f", totalDiscount));
            totalVATLabel.setText(String.format("Total VAT: %.2f", totalVAT));

            message.setText("Search completed. Found " + objectList.size() + " records.");

        } catch (Exception ex) {
            message.setText("Error occurred during search: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            progressBar.setVisible(false);
        }
    }

    private void exportToPDF() {
        message.setText("Exporting to PDF...");
        // TODO: Implement PDF export functionality
        JOptionPane.showMessageDialog(mainFrame, "PDF export functionality will be implemented here.");
    }

    private void clearFilters() {
        categoryCombo.setSelectedIndex(0);
        itemCombo.setSelectedIndex(0);
        paymentCombo.setSelectedIndex(0);
        chkDiscount.setSelected(true);
        chkVAT.setSelected(true);
        fromDateField.setText("YYYY-MM-DD");
        toDateField.setText("YYYY-MM-DD");
        tableModel.setRowCount(0);
        totalRecordsLabel.setText("Total Records: 0");
        totalAmountLabel.setText("Total Amount: 0.00");
        totalDiscountLabel.setText("Total Discount: 0.00");
        totalVATLabel.setText("Total VAT: 0.00");
        message.setText("Filters cleared. Ready to search...");
    }

    private void refreshData() {
        message.setText("Refreshing data...");
        fetchCategoryItem();
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
            List<Item> items = service.getItems();
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