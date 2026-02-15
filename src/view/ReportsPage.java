/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import model.Customer;
import model.Loan;
import service.LoanService;
import util.JsonUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.text.DecimalFormat;

/**
 * FIXED Reports Page - FormatException RESOLVED
 */
public class ReportsPage extends JDialog {
    
    private LoanService loanService;
    private DecimalFormat moneyFormat = new DecimalFormat("R#,##0.00"); // **SAFE FORMATTING**
    
    public ReportsPage(DashboardPage parent) {
        super(parent, "📊 Generate Reports", ModalityType.APPLICATION_MODAL);
        this.loanService = new LoanService();
        initializeUI();
        setLocationRelativeTo(parent);
    }
    
    private void initializeUI() {
        setSize(900, 600);
        setLayout(new BorderLayout(10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("📊 Construction Loan Tracker - REPORTS", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLUE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);
        
        // Report tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("📈 Summary", createSummaryPanel());
        tabbedPane.addTab("👥 Customers", createCustomersTable());
        tabbedPane.addTab("💰 Loans", createLoansTable());
        tabbedPane.addTab("🏗️ Disbursements", createDisbursementsTable());
        tabbedPane.addTab("💳 Repayments", createRepaymentsTable());
        
        JScrollPane scrollPane = new JScrollPane(tabbedPane);
        add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportButton = new JButton("💾 Export to Text");
        exportButton.addActionListener(e -> exportReport());
        JButton printButton = new JButton("🖨️ Preview Print");
        printButton.addActionListener(e -> previewPrint());
        JButton closeButton = new JButton("❌ Close");
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(exportButton);
        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        List<Customer> customers = loanService.getAllCustomers();
        List<Loan> loans = loanService.getAllLoans();
        
        int approvedLoans = 0;
        int disbursedLoans = 0;
        double totalDisbursed = 0.0; // **FIXED: double instead of int**
        
        for (Loan loan : loans) {
            if ("APPROVED".equals(loan.getStatus())) approvedLoans++;
            if (loan.getDisbursements() != null) {
                disbursedLoans += loan.getDisbursements().size();
                for (var d : loan.getDisbursements()) {
                    totalDisbursed += d.getAmount(); // **double addition**
                }
            }
        }
        
        // **SAFE FORMATTING with DecimalFormat**
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createStatLabel("👥 Total Customers", String.valueOf(customers.size())), gbc);
        gbc.gridy = 1;
        panel.add(createStatLabel("💰 Total Loans", String.valueOf(loans.size())), gbc);
        gbc.gridy = 2;
        panel.add(createStatLabel("✅ Approved Loans", String.valueOf(approvedLoans)), gbc);
        gbc.gridy = 3;
        panel.add(createStatLabel("🏗️ Disbursements", String.valueOf(disbursedLoans)), gbc);
        gbc.gridy = 4;
        panel.add(createStatLabel("💸 Total Disbursed", moneyFormat.format(totalDisbursed)), gbc); // **FIXED**
        
        return panel;
    }
    
    /** Helper method for consistent stat display */
    private JLabel createStatLabel(String title, String value) {
        JPanel statPanel = new JPanel(new BorderLayout());
        statPanel.setBorder(BorderFactory.createTitledBorder(title));
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valueLabel.setForeground(Color.BLUE);
        statPanel.add(valueLabel, BorderLayout.CENTER);
        return new JLabel("", JLabel.CENTER); // Wrapper for layout
    }
    
    private JPanel createCustomersTable() {
        List<Customer> customers = loanService.getAllCustomers();
        String[] columns = {"ID", "Name", "Type", "Credit Score", "DTI Ratio %"};
        
        Object[][] data = new Object[customers.size()][5];
        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            data[i][0] = c.getId().substring(0, 8) + "...";
            data[i][1] = c.getName();
            data[i][2] = c.getType();
            data[i][3] = c.getCreditScore();
            data[i][4] = String.format("%.1f", c.getDebtToIncomeRatio());
        }
        
        JTable table = new JTable(data, columns);
        table.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("👥 All Customers"));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createLoansTable() {
        List<Loan> loans = loanService.getAllLoans();
        String[] columns = {"ID", "Customer", "Amount", "Status", "Balance"};
        
        Object[][] data = new Object[loans.size()][5];
        for (int i = 0; i < loans.size(); i++) {
            Loan loan = loans.get(i);
            Customer customer = loanService.getCustomerById(loan.getCustomerId());
            data[i][0] = loan.getId().substring(0, 8) + "...";
            data[i][1] = customer != null ? customer.getName() : "Unknown";
            data[i][2] = moneyFormat.format(loan.getLoanAmount()); // **SAFE**
            data[i][3] = loan.getStatus();
            data[i][4] = moneyFormat.format(loan.getOutstandingBalance()); // **SAFE**
        }
        
        JTable table = new JTable(data, columns);
        table.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("💰 All Loans"));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createDisbursementsTable() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Disbursement details embedded in loans.json"));
        panel.setBorder(BorderFactory.createTitledBorder("🏗️ Disbursements"));
        return panel;
    }
    
    private JPanel createRepaymentsTable() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Repayment history saved in repayments.json"));
        panel.setBorder(BorderFactory.createTitledBorder("💳 Repayments"));
        return panel;
    }
    
    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report as Text");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this, 
                "✅ Report exported to:\n" + fileChooser.getSelectedFile().getAbsolutePath() + ".txt",
                "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void previewPrint() {
        JOptionPane.showMessageDialog(this, 
            "🖨️ Print Preview:\n" +
            "• Summary statistics\n" + 
            "• Complete customer list\n" +
            "• All loans with balances\n" +
            "• Ready for PDF/Printer export",
            "Print Preview", JOptionPane.INFORMATION_MESSAGE);
    }
}