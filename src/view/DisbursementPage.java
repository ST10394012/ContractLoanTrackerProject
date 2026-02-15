/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import model.Loan;
import service.LoanService;
import util.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DisbursementPage extends JDialog{
    private LoanService loanService; // Business service
    private DashboardPage parent; // Parent reference
    
    // UI Components
    private JComboBox<String> loanComboBox; // Approved loan selector
    private JComboBox<String> stageComboBox; // Construction stage selector
    private JTextField amountField; // Disbursement amount
    private JCheckBox inspectionCheckBox; // Inspection approval
    
    public DisbursementPage(DashboardPage parent) {
        super(parent, "Record Disbursement", ModalityType.APPLICATION_MODAL);
        this.parent = parent;
        this.loanService = new LoanService();
        
        initializeUI();
        loadApprovedLoans(); // Only shows APPROVED loans
        setLocationRelativeTo(parent);
    }
    
    private void initializeUI() {
        setSize(500, 400);
        setLayout(new BorderLayout(10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Record Construction Disbursement");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Select Loan
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Select Approved Loan:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        loanComboBox = new JComboBox<>();
        loanComboBox.setPreferredSize(new Dimension(250, 25));
        formPanel.add(loanComboBox, gbc);
        
        // Construction Stage
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Construction Stage:"), gbc);
        gbc.gridx = 1;
        stageComboBox = new JComboBox<>(new String[]{
            "Foundation", "Structural", "Roofing", "Finishing"
        });
        formPanel.add(stageComboBox, gbc);
        
        // Amount
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Disbursement Amount (R):"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(15);
        formPanel.add(amountField, gbc);
        
        // Inspection Approval
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        inspectionCheckBox = new JCheckBox("Inspection Approved (Required)");
        formPanel.add(inspectionCheckBox, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton disburseButton = new JButton("Record Disbursement");
        disburseButton.addActionListener(new DisburseListener());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(disburseButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadApprovedLoans() {
        // Only loads loans with APPROVED status
        for (Loan loan : loanService.getAllLoans()) {
            if ("APPROVED".equals(loan.getStatus())) {
                loanComboBox.addItem("Loan ID: " + loan.getId() + 
                                   " | Customer: " + loan.getLoanAmount() + "R");
            }
        }
    }
    
    private Loan getSelectedLoan() {
        String selected = (String) loanComboBox.getSelectedItem();
        if (selected == null) return null;
        String loanId = selected.substring(8, selected.indexOf(" |"));
        return loanService.getLoanById(loanId);
    }
    
    private class DisburseListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Loan loan = getSelectedLoan();
            if (loan == null) {
                JOptionPane.showMessageDialog(DisbursementPage.this, 
                    "Please select an approved loan");
                return;
            }
            
            try {
                double amount = Double.parseDouble(amountField.getText());
                
                if (!ValidationUtil.isValidAmount(amount, "Amount")) {
                    JOptionPane.showMessageDialog(DisbursementPage.this, 
                        "Amount must be positive");
                    return;
                }
                
                if (amount > loan.getOutstandingBalance()) {
                    JOptionPane.showMessageDialog(DisbursementPage.this, 
                        "Amount exceeds remaining balance: R" + 
                        String.format("%.2f", loan.getOutstandingBalance()));
                    return;
                }
                
                if (!inspectionCheckBox.isSelected()) {
                    JOptionPane.showMessageDialog(DisbursementPage.this, 
                        "Inspection approval is REQUIRED for disbursements");
                    return;
                }
                
                // Record disbursement
                loanService.recordDisbursement(loan.getId(), 
                    (String) stageComboBox.getSelectedItem(), amount, true);
                
                JOptionPane.showMessageDialog(DisbursementPage.this, 
                    "Disbursement recorded successfully!\n" +
                    "Stage: " + stageComboBox.getSelectedItem() + "\n" +
                    "Amount: R" + String.format("%.2f", amount));
                
                parent.repaint();
                dispose();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(DisbursementPage.this, 
                    "Please enter valid amount");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(DisbursementPage.this, 
                    "Error: " + ex.getMessage());
            }
        }
    }
}