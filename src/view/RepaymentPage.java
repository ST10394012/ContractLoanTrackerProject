/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import model.Customer;
import model.Loan;
import service.LoanService;
import service.InterestCalculator;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * FIXED RepaymentPage - Shows customer name + Auto-activates Record button
 */
public class RepaymentPage extends JDialog {
    
    private LoanService loanService;
    private InterestCalculator interestCalculator;
    private DashboardPage parent;
    
    private JComboBox<String> loanComboBox;
    private JTextField paymentAmountField;
    private JTextField dueDateField;
    private JButton recordButton;
    private DecimalFormat moneyFormat = new DecimalFormat("R#,##0.00");
    
    public RepaymentPage(DashboardPage parent) {
        super(parent, "💳 Record Repayment", ModalityType.APPLICATION_MODAL);
        this.parent = parent;
        this.loanService = new LoanService();
        this.interestCalculator = new InterestCalculator();
        
        initializeUI();
        loadActiveLoans(); // **NOW SHOWS CUSTOMER NAMES**
        setLocationRelativeTo(parent);
    }
    
    private void initializeUI() {
        setSize(450, 450);
        setLayout(new BorderLayout(10, 10));
        
        // Header
        JLabel titleLabel = new JLabel("💳 Record Loan Repayment", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.GREEN);
        add(titleLabel, BorderLayout.NORTH);
        
        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 1. Loan Selection - **SHOWS CUSTOMER NAME**
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(new JLabel("📋 Select Loan (Customer):"), gbc);
        gbc.gridy = 1; gbc.gridwidth = 1;
        loanComboBox = new JComboBox<>();
        loanComboBox.setPreferredSize(new Dimension(350, 28));
        loanComboBox.addActionListener(e -> updateRecordButton()); // Auto-update
        formPanel.add(loanComboBox, gbc);
        
        // 2. Payment Amount
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(new JLabel("💰 Payment Amount (R):"), gbc);
        gbc.gridx = 1;
        paymentAmountField = new JTextField(15);
        paymentAmountField.setHorizontalAlignment(JTextField.RIGHT);
        paymentAmountField.addActionListener(e -> updateRecordButton()); // **AUTO-ACTIVATE**
        formPanel.add(paymentAmountField, gbc);
        
        // 3. Due Date
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        formPanel.add(new JLabel("📅 Due Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        dueDateField = new JTextField("2026-03-16", 15); // Default date
        formPanel.add(dueDateField, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("❌ Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        recordButton = new JButton("💳 Record Payment");
        recordButton.setBackground(Color.ORANGE);
        recordButton.setForeground(Color.WHITE);
        recordButton.setFont(new Font("Arial", Font.BOLD, 16));
        recordButton.setEnabled(false); // Starts disabled
        recordButton.addActionListener(new RecordPaymentListener());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(recordButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
    }
    
    /**
     * **FIXED: Shows CUSTOMER NAME in dropdown**
     * Format: "ID: abc123 | John Doe | Balance: R500,000.00"
     */
    private void loadActiveLoans() {
        loanComboBox.removeAllItems();
        List<Loan> allLoans = loanService.getAllLoans();
        int activeCount = 0;
        
        for (Loan loan : allLoans) {
            if (loan.getOutstandingBalance() > 0) { // Loans with balance
                // **GET CUSTOMER NAME**
                Customer customer = loanService.getCustomerById(loan.getCustomerId());
                String customerName = (customer != null) ? customer.getName() : "Unknown";
                
                // **DISPLAY FORMAT WITH CUSTOMER**
                String displayText = String.format("ID: %s | %s | Balance: %s", 
                    loan.getId().substring(0, 8) + "...", // Short ID
                    customerName, // **CUSTOMER NAME SHOWN**
                    moneyFormat.format(loan.getOutstandingBalance())
                );
                
                loanComboBox.addItem(displayText);
                loanComboBox.putClientProperty(displayText, loan); // Store loan object
                activeCount++;
            }
        }
        
        if (activeCount == 0) {
            loanComboBox.addItem("❌ No active loans with balance");
        } else {
            System.out.println("✅ Loaded " + activeCount + " active loans");
        }
    }
    
    /**
     * **RELIABLE loan retrieval**
     */
    private Loan getSelectedLoan() {
        String selectedText = (String) loanComboBox.getSelectedItem();
        if (selectedText == null || selectedText.contains("❌")) {
            return null;
        }
        Loan loan = (Loan) loanComboBox.getClientProperty(selectedText);
        System.out.println("Selected loan ID: " + (loan != null ? loan.getId() : "NULL"));
        return loan;
    }
    
    /**
     * **CRITICAL FIX: AUTO-ACTIVATES Record button**
     * Activates when loan selected + valid amount entered
     */
    private void updateRecordButton() {
        Loan loan = getSelectedLoan();
        boolean loanSelected = loan != null;
        boolean amountValid = false;
        
        try {
            String amountText = paymentAmountField.getText().trim().replaceAll("[^0-9.]", "");
            if (!amountText.isEmpty()) {
                double amount = Double.parseDouble(amountText);
                amountValid = amount > 0 && amount <= loan.getOutstandingBalance();
            }
        } catch (Exception e) {
            amountValid = false;
        }
        
        // **AUTO-ACTIVATE**
        boolean canRecord = loanSelected && amountValid;
        recordButton.setEnabled(canRecord);
        
        // Visual feedback
        if (canRecord) {
            recordButton.setBackground(Color.GREEN);
        } else {
            recordButton.setBackground(Color.ORANGE);
        }
    }
    
    private class RecordPaymentListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Loan loan = getSelectedLoan();
            
            if (loan == null) {
                JOptionPane.showMessageDialog(RepaymentPage.this, 
                    "⚠️ Please select a loan from dropdown", 
                    "No Loan Selected", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                double amount = Double.parseDouble(paymentAmountField.getText().trim().replaceAll("[^0-9.]", ""));
                String dueDate = dueDateField.getText();
                
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(RepaymentPage.this, "⚠️ Enter valid amount");
                    return;
                }
                
                if (amount > loan.getOutstandingBalance()) {
                    JOptionPane.showMessageDialog(RepaymentPage.this, 
                        "❌ Amount exceeds balance: " + moneyFormat.format(loan.getOutstandingBalance()));
                    return;
                }
                
                // **RECORD PAYMENT**
                loanService.recordRepayment(loan.getId(), amount, dueDate);
                
                JOptionPane.showMessageDialog(RepaymentPage.this, 
                    "✅ PAYMENT RECORDED SUCCESSFULLY!\n\n" +
                    "💰 Amount Paid: " + moneyFormat.format(amount) + "\n" +
                    "💳 New Balance: " + moneyFormat.format(
                        loanService.getLoanById(loan.getId()).getOutstandingBalance()) + "\n" +
                    "📅 Due Date: " + dueDate + "\n\n" +
                    "💾 Saved to loans.json & repayments.json", 
                    "Payment Complete", JOptionPane.INFORMATION_MESSAGE);
                
                parent.repaint();
                dispose();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(RepaymentPage.this, 
                    "❌ Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}