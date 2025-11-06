package com.rms.bill;

import com.rms.Frame2new;
import com.rms.service.AppService;
import com.rms.service.ReportService;
import com.rms.setting.Utils;
import db.DBConnection;
import dto.Role;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class BillHistory extends JFrame{

    String columns[] = new String[] {"SERIAL","INVOICE NO","BILL TIME","CUSTOMER BILL","DISCOUNT","VAT","PAID BILL", "METHOD"};
    private JFrame mainFrame;
    JTable tbl = null;
    DefaultTableModel dtm = null;
    PreparedStatement pst;
    ResultSet rs;
    DBConnection con = new DBConnection();

    JButton btnSearch = new JButton("Search");
    JButton btnVoidInvoice = new JButton("Void Invoice");
    JButton btnAdvanceReport = new JButton("Advance Search");
    JTextField fromDate = new JTextField("");
    JTextField toDate = new JTextField("");
    JButton btnExport = new JButton("Export PDF");

    // Footer components
    JPanel footerPanel;
    JLabel lblTotalItems;
    JLabel lblTotalCustomerBill;
    JLabel lblTotalDiscount;
    JLabel lblTotalVat;
    JLabel lblTotalPaidBill;

    public BillHistory(){
        mainFrame = new JFrame("Bill History");
        mainFrame.setSize(1300,750); // Increased height for footer
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
        top.setBounds(0,0,1300,100);

        JPanel middle = new JPanel(null);
        middle.setBounds(0,100,1300,500); // Reduced height for footer

        // Create footer panel
        createFooterPanel();

        fromDate.setBounds(170,50,200,35);
        fromDate.setText(Utils.getTokenDate(new Date()));
        top.add(fromDate);
        toDate.setBounds(380,50,200,35);
        toDate.setText(Utils.getTokenDate(new Date()));
        top.add(toDate);
        btnSearch.setBounds(590,50,80,35);
        top.add(btnSearch);
        if(Utils.authority.role.equals(Role.ADMIN)) {
            btnVoidInvoice.setBounds(680,50,100,35);
            top.add(btnVoidInvoice);
        }
        btnAdvanceReport.setBounds(790,50,120,35);
        top.add(btnAdvanceReport);
        btnExport.setBounds(950,50,120,35);
        top.add(btnExport);

        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchHistory();
            }
        });

        btnVoidInvoice.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tbl.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(mainFrame, "Please select an invoice to void.", "No Selection", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String invoiceNo = tbl.getValueAt(selectedRow, 1).toString(); // Column 1 is INVOICE NO
                int choice = JOptionPane.showConfirmDialog(
                        mainFrame,
                        "Do you want to void Invoice No: " + invoiceNo + "?",
                        "Confirm Void",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    AppService appService = new AppService();
                    appService.voidInvoice(Utils.getNumberValue(invoiceNo));
                    JOptionPane.showMessageDialog(mainFrame, "Invoice " + invoiceNo + " has been voided successfully.");
                    searchHistory();
                }
                // else do nothing
            }
        });

        btnAdvanceReport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AdvanceSearch();
            }
        });

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
        tbl.setFont(Utils.FONT_16);
        // tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Allow manual sizing
        tbl.getColumnModel().getColumn(0).setPreferredWidth(30);   // SERIAL - small
        tbl.getColumnModel().getColumn(1).setPreferredWidth(100);   // INVOICE NO
        tbl.getColumnModel().getColumn(2).setPreferredWidth(200);  // BILL TIME
        tbl.getColumnModel().getColumn(3).setPreferredWidth(80);   // CUSTOMER BILL
        tbl.getColumnModel().getColumn(4).setPreferredWidth(50);   // DISCOUNT
        tbl.getColumnModel().getColumn(5).setPreferredWidth(50);   // VAT
        tbl.getColumnModel().getColumn(6).setPreferredWidth(80);   // PAID BILL
        tbl.getColumnModel().getColumn(7).setPreferredWidth(70);   // METHOD

        JScrollPane pane = new JScrollPane(tbl, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setBounds(0,0,1300,500); // Adjusted height
        middle.add(pane);

        mainFrame.add(top);
        mainFrame.add(middle);
        mainFrame.add(footerPanel);

        if(!Frame2new.allow){
            btnVoidInvoice.setVisible(false);
            btnSearch.setVisible(false);
            //message.setBounds(590,55,400,25);
            //message.setText("Billing period expired, contact Administrator at 01754282387");
        }
        btnExport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("called export method");
                String inputFrom = fromDate.getText();
                String inputTo = toDate.getText();
                ReportService reportService = new ReportService();
                reportService.exportBillHistoryPDF(inputFrom,inputTo);
            }
        });
    }


    private void createFooterPanel() {
        footerPanel = new JPanel();
        footerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        footerPanel.setBackground(new Color(240, 240, 240)); // Light gray background
        footerPanel.setBorder(BorderFactory.createEtchedBorder());
        footerPanel.setBounds(0, 650, 1300, 50);

        // Initialize summary labels
        lblTotalItems = new JLabel("Items: 0");
        lblTotalCustomerBill = new JLabel("Customer Bill: 0.00");
        lblTotalDiscount = new JLabel("Discount: 0.00");
        lblTotalVat = new JLabel("VAT: 0.00");
        lblTotalPaidBill = new JLabel("Paid: 0.00");

        // Style the labels
        Font summaryFont = new Font("Arial", Font.BOLD, 12);
        lblTotalItems.setFont(summaryFont);
        lblTotalCustomerBill.setFont(summaryFont);
        lblTotalDiscount.setFont(summaryFont);
        lblTotalVat.setFont(summaryFont);
        lblTotalPaidBill.setFont(summaryFont);

        // Add color coding
        lblTotalItems.setForeground(Color.BLUE);
        lblTotalCustomerBill.setForeground(new Color(0, 128, 0)); // Dark green
        lblTotalDiscount.setForeground(Color.RED);
        lblTotalVat.setForeground(Color.MAGENTA);
        lblTotalPaidBill.setForeground(new Color(0, 100, 0)); // Darker green

        // Add labels to footer panel
        footerPanel.add(lblTotalItems);
        footerPanel.add(new JLabel("|")); // Separator
        footerPanel.add(lblTotalCustomerBill);
        footerPanel.add(new JLabel("|")); // Separator
        footerPanel.add(lblTotalDiscount);
        footerPanel.add(new JLabel("|")); // Separator
        footerPanel.add(lblTotalVat);
        footerPanel.add(new JLabel("|")); // Separator
        footerPanel.add(lblTotalPaidBill);
    }

    private void updateFooterSummary() {
        int totalItems = dtm.getRowCount();
        double totalCustomerBill = 0.0;
        double totalDiscount = 0.0;
        double totalVat = 0.0;
        double totalPaidBill = 0.0;

        // Calculate totals from table data
        for (int i = 0; i < totalItems; i++) {
            try {
                // Column 3: CUSTOMER BILL
                String customerBillStr = dtm.getValueAt(i, 3).toString();
                totalCustomerBill += Double.parseDouble(customerBillStr);

                // Column 4: DISCOUNT
                String discountStr = dtm.getValueAt(i, 4).toString();
                totalDiscount += Double.parseDouble(discountStr);

                // Column 5: VAT
                String vatStr = dtm.getValueAt(i, 5).toString();
                totalVat += Double.parseDouble(vatStr);

                // Column 6: PAID BILL
                String paidBillStr = dtm.getValueAt(i, 6).toString();
                totalPaidBill += Double.parseDouble(paidBillStr);

            } catch (NumberFormatException e) {
                // Handle any parsing errors gracefully
                System.err.println("Error parsing numeric value in row " + i + ": " + e.getMessage());
            }
        }

        // Update footer labels
        lblTotalItems.setText("Items: " + totalItems);
        lblTotalCustomerBill.setText(String.format("Customer Bill: %.2f", totalCustomerBill));
        lblTotalDiscount.setText(String.format("Discount: %.2f", totalDiscount));
        lblTotalVat.setText(String.format("VAT: %.2f", totalVat));
        lblTotalPaidBill.setText(String.format("Paid: %.2f", totalPaidBill));
    }

    public void initialFillUp(String fromDate,String toDate) {
       // message.setText(0 +" Transactions have been found!");
        String sql = "";
        if(fromDate.trim().length() >0 & toDate.trim().length() >0) {
            sql = "select * from bill where created_date between '"+(fromDate+" 00:00:01")+"' and '"+(toDate+" 23:59:59")+"' and delete = false order by id desc";
            System.out.println(sql);
        }else{
            sql = "select * from bill where  delete = false order by id desc ";
        }
        try {
            pst = con.mkDataBase().prepareStatement(sql);
            rs = pst.executeQuery();
            int i = 0;
            int row = 0;
            while (rs.next()) {
                row++;
                String data[] = new String[]{
                        String.valueOf(row),
                        rs.getString("invoice_no"),
                        Utils.dateToStr(rs.getTimestamp("created_date")),
                        rs.getString("total"),
                        rs.getString("discount_amt"),
                        rs.getString("vat_amt"),
                        rs.getString("amount"),
                        rs.getString("payment_method")
                        // , rs.getString("description")
                };
                dtm.addRow(data);
                i++;
            }
            // Update footer summary after loading data
            updateFooterSummary();
        } catch (Exception e) {
           // message.setText("Input correct date format - YYYY-MM-DD");
            JOptionPane.showMessageDialog(null, "Input correct date format - YYYY-MM-DD");
            // Clear footer on error
            updateFooterSummary();
        }
    }

    public void searchHistory(){
        dtm.setRowCount(0);
        String inputFrom = fromDate.getText();
        String inputTo = toDate.getText();
        initialFillUp(inputFrom,inputTo);
    }

}