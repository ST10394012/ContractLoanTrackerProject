/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import model.Customer;
import model.Loan;
import service.LoanService;
import util.ValidationUtil;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import static javax.swing.SpringLayout.SOUTH;


/**
 * SIMPLIFIED Disbursement Page - FIXED LOAN SELECTION
 */
public class DisbursementPage extends JDialog {
    
    private LoanService loanService;
    private DashboardPage parent;
    
    // UI Components
    private JComboBox<String> loanComboBox;
    private JTextField amountField;
    private JComboBox<String> stageComboBox;
    private JCheckBox inspectionCheckBox;
    private JButton disburseButton;
    
    private DecimalFormat moneyFormat = new DecimalFormat("R#,##0.00");
    
    public DisbursementPage(DashboardPage parent) {
        super(parent, "💰 Record Disbursement", ModalityType.APPLICATION_MODAL);
        this.parent = parent;
        this.loanService = new LoanService();
        
        initializeUI();
        loadApprovedLoans();
        setLocationRelativeTo(parent);
    }
    
    private void initializeUI() {
        setSize(450, 400);
        setLayout(new BorderLayout(10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("🏗️ Record Disbursement", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLUE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);
        
        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 1. Loan Selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(new JLabel("📋 Select Approved Loan:"), gbc);
        gbc.gridy = 1; gbc.gridwidth = 1;
        loanComboBox = new JComboBox<>();
        loanComboBox.setPreferredSize(new Dimension(300, 28));
        loanComboBox.addActionListener(e -> updateDisburseButton());
        formPanel.add(loanComboBox, gbc);
        
        // 2. Amount
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(new JLabel("💰 Amount (R):"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(15);
        amountField.setHorizontalAlignment(JTextField.RIGHT);
        amountField.setEditable(true);
        amountField.addActionListener(e -> updateDisburseButton());
        formPanel.add(amountField, gbc);
        
        // 3. Stage
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        formPanel.add(new JLabel("🏠 Stage:"), gbc);
        gbc.gridx = 1;
        stageComboBox = new JComboBox<>(new String[]{
            "Foundation", "Structural", "Roofing", "Finishing"
        });
        formPanel.add(stageComboBox, gbc);
        
        // 4. Inspection
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        inspectionCheckBox = new JCheckBox("✅ Inspection Approved (REQUIRED)");
        inspectionCheckBox.setFont(new Font("Arial", Font.BOLD, 14));
        inspectionCheckBox.setForeground(Color.RED);
        inspectionCheckBox.addActionListener(e -> updateDisburseButton());
        formPanel.add(inspectionCheckBox, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("❌ Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        disburseButton = new JButton("💰 Record Disbursement");
        disburseButton.setBackground(Color.GREEN);
        disburseButton.setForeground(Color.WHITE);
        disburseButton.setFont(new Font("Arial", Font.BOLD, 16));
        disburseButton.setEnabled(false);
        disburseButton.addActionListener(new DisburseListener());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(disburseButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
    }
    
    /**
     * **SIMPLIFIED LOAN LOADING - Stores full loan objects**
     */
    private void loadApprovedLoans() {
        loanComboBox.removeAllItems();
        List<Loan> allLoans = loanService.getAllLoans();
        int approvedCount = 0;
        
        for (Loan loan : allLoans) {
            if ("APPROVED".equals(loan.getStatus()) && loan.getOutstandingBalance() > 0) {
                Customer customer = loanService.getCustomerById(loan.getCustomerId());
                String customerName = (customer != null) ? customer.getName() : "Unknown";
                
                // **SIMPLE FORMAT**: Full loan ID + customer
                String displayText = String.format("%s - %s (R%s available)", 
                    loan.getId(),
                    customerName,
                    moneyFormat.format(loan.getOutstandingBalance())
                );
                loanComboBox.addItem(displayText);
                loanComboBox.putClientProperty(displayText, loan); // **STORE LOAN OBJECT**
                approvedCount++;
            }
        }
        
        if (approvedCount == 0) {
            loanComboBox.addItem("❌ No approved loans available");
        }
    }
    
    /**
     * **SIMPLE & RELIABLE: Gets loan directly from stored property**
     */
    private Loan getSelectedLoan() {
        String selectedText = (String) loanComboBox.getSelectedItem();
        if (selectedText == null || selectedText.contains("❌")) {
            return null;
        }
        
        // **FIXED: Get loan directly from stored property**
        Loan loan = (Loan) loanComboBox.getClientProperty(selectedText);
        return loan;
    }
    
    /**
     * Auto-activate button logic
     */
    private void updateDisburseButton() {
        Loan loan = getSelectedLoan();
        boolean loanSelected = loan != null;
        boolean inspectionChecked = inspectionCheckBox.isSelected();
        boolean amountValid = false;
        
        try {
            String amountText = amountField.getText().trim().replaceAll("[^0-9.]", "");
            double amount = Double.parseDouble(amountText);
            amountValid = amount > 0 && loan != null && amount <= loan.getOutstandingBalance();
        } catch (Exception e) {
            amountValid = false;
        }
        
        disburseButton.setEnabled(loanSelected && inspectionChecked && amountValid);
    }
    
    /**
     * **FIXED DisburseListener - Now works perfectly**
     */
    private class DisburseListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Loan loan = getSelectedLoan();
            
            // **DEBUG: Show what was found**
            System.out.println("Selected loan: " + (loan != null ? loan.getId() : "NULL"));
            
            if (loan == null) {
                JOptionPane.showMessageDialog(DisbursementPage.this, 
                    "⚠️ No valid loan selected. Please select from dropdown.", 
                    "No Loan", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                double amount = Double.parseDouble(amountField.getText().trim().replaceAll("[^0-9.]", ""));
                
                if (amount > loan.getOutstandingBalance()) {
                    JOptionPane.showMessageDialog(DisbursementPage.this, 
                        "❌ Amount (R" + moneyFormat.format(amount) + 
                        ") exceeds balance (R" + moneyFormat.format(loan.getOutstandingBalance()) + ")");
                    return;
                }
                
                if (!inspectionCheckBox.isSelected()) {
                    JOptionPane.showMessageDialog(DisbursementPage.this, "❌ Inspection REQUIRED!");
                    return;
                }
                
                String stage = (String) stageComboBox.getSelectedItem();
                loanService.recordDisbursement(loan.getId(), stage, amount, true);
                
                JOptionPane.showMessageDialog(DisbursementPage.this, 
                    "✅ SUCCESS!\n\n" +
                    "Loan: " + loan.getId().substring(0, 8) + "...\n" +
                    "Stage: " + stage + "\n" +
                    "Amount: R" + moneyFormat.format(amount) + "\n" +
                    "New Balance: R" + moneyFormat.format(
                        loanService.getLoanById(loan.getId()).getOutstandingBalance()) + "\n\n" +
                    "💾 Saved to loans.json", 
                    "Disbursement Complete", JOptionPane.INFORMATION_MESSAGE);
                
                parent.repaint();
                dispose();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(DisbursementPage.this, 
                    "❌ Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}