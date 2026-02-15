/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import model.Loan;
import service.LoanService;
import service.InterestCalculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class RepaymentPage extends JDialog{
    private LoanService loanService; // Main service
    private InterestCalculator interestCalculator; // Interest service
    private DashboardPage parent; // Parent dashboard
    
    // UI Components
    private JComboBox<String> loanComboBox; // Active loan selector
    private JTextField paymentAmountField; // Payment amount input
    private JTextField dueDateField; // Expected due date
    private JLabel penaltyLabel; // Shows calculated penalty
    private JLabel balanceLabel; // Shows current balance
    
    public RepaymentPage(DashboardPage parent) {
        super(parent, "Record Repayment", ModalityType.APPLICATION_MODAL);
        this.parent = parent;
        this.loanService = new LoanService();
        this.interestCalculator = new InterestCalculator();
        
        initializeUI();
        loadActiveLoans(); // Loads loans that are not fully repaid
        setLocationRelativeTo(parent);
    }
    
    private void initializeUI() {
        setSize(500, 450);
        setLayout(new BorderLayout(10, 10));
        
        // Header
        JLabel titleLabel = new JLabel("Record Loan Repayment", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.GREEN);
        add(titleLabel, BorderLayout.NORTH);
        
        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Loan Selection
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Select Loan:"), gbc);
        gbc.gridx = 1;
        loanComboBox = new JComboBox<>();
        loanComboBox.addActionListener(e -> updateLoanInfo());
        loanComboBox.setPreferredSize(new Dimension(250, 25));
        formPanel.add(loanComboBox, gbc);
        
        // Current Balance
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Outstanding Balance:"), gbc);
        gbc.gridx = 1;
        balanceLabel = new JLabel("R0.00");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        balanceLabel.setForeground(Color.RED);
        formPanel.add(balanceLabel, gbc);
        
        // Payment Amount
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Payment Amount (R):"), gbc);
        gbc.gridx = 1;
        paymentAmountField = new JTextField(15);
        paymentAmountField.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { calculatePenalty(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { calculatePenalty(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { calculatePenalty(); }
            });
        formPanel.add(paymentAmountField, gbc);
        
        // Due Date
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Due Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        dueDateField = new JTextField(LocalDate.now().plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE), 15);
        formPanel.add(dueDateField, gbc);
        
        // Penalty
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formPanel.add(new JLabel("Late Penalty (5%):"), gbc);
        gbc.gridy = 5;
        penaltyLabel = new JLabel("R0.00");
        penaltyLabel.setFont(new Font("Arial", Font.BOLD, 12));
        penaltyLabel.setForeground(Color.ORANGE);
        formPanel.add(penaltyLabel, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton recordButton = new JButton("Record Payment");
        recordButton.addActionListener(new RecordPaymentListener());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(recordButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadActiveLoans() {
        // Loads loans with outstanding balance > 0
        for (Loan loan : loanService.getAllLoans()) {
            if (loan.getOutstandingBalance() > 0) {
                loanComboBox.addItem("ID: " + loan.getId() + " | Balance: R" + 
                                   String.format("%.2f", loan.getOutstandingBalance()));
            }
        }
    }
    
    private Loan getSelectedLoan() {
        String selected = (String) loanComboBox.getSelectedItem();
        if (selected == null) return null;
        String loanId = selected.substring(3, selected.indexOf(" |"));
        return loanService.getLoanById(loanId);
    }
    
    private void updateLoanInfo() {
        Loan loan = getSelectedLoan();
        if (loan != null) {
            balanceLabel.setText("R" + String.format("%.2f", loan.getOutstandingBalance()));
            calculatePenalty();
        }
    }
    
    private void calculatePenalty() {
        Loan loan = getSelectedLoan();
        try {
            double paymentAmount = Double.parseDouble(paymentAmountField.getText());
            LocalDate dueDate = LocalDate.parse(dueDateField.getText());
            boolean isLate = dueDate.isBefore(LocalDate.now());
            
            double penalty = isLate ? interestCalculator.calculateLatePenalty(paymentAmount) : 0;
            penaltyLabel.setText("R" + String.format("%.2f", penalty) + 
                               (isLate ? " (LATE)" : ""));
        } catch (Exception e) {
            penaltyLabel.setText("R0.00");
        }
    }
    
    private class RecordPaymentListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Loan loan = getSelectedLoan();
            if (loan == null) {
                JOptionPane.showMessageDialog(RepaymentPage.this, "Please select a loan");
                return;
            }
            
            try {
                double amount = Double.parseDouble(paymentAmountField.getText());
                String dueDate = dueDateField.getText();
                
                if (amount <= 0 || amount > loan.getOutstandingBalance()) {
                    JOptionPane.showMessageDialog(RepaymentPage.this, 
                        "Invalid amount. Must be positive and <= balance.");
                    return;
                }
                
                // Record repayment
                loanService.recordRepayment(loan.getId(), amount, dueDate);
                
                JOptionPane.showMessageDialog(RepaymentPage.this, 
                    "Payment recorded successfully!\n" +
                    "Amount: R" + String.format("%.2f", amount) + "\n" +
                    "New Balance: R" + String.format("%.2f", 
                    loanService.getLoanById(loan.getId()).getOutstandingBalance()));
                
                parent.repaint();
                dispose();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(RepaymentPage.this, 
                    "Error: " + ex.getMessage());
            }
        }
    }
}
