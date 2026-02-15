/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import model.Customer;
import service.EligibilityService;
import service.LoanService;
import service.InterestCalculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import model.Loan;

/**
 * Complete loan application and approval page (FIXED VERSION)
 * Handles application creation, eligibility check, and approval workflow
 */
public class LoanPage extends JDialog {
    
    private LoanService loanService; // Main business service
    private EligibilityService eligibilityService; // Eligibility checker
    private InterestCalculator interestCalculator; // Interest calculations
    private DashboardPage parent; // Parent dashboard reference
    
    // UI Components - PROPER REFERENCES (FIXED)
    private JComboBox<String> customerComboBox; // Customer selector
    private JTextField constructionCostField; // Project cost input
    private JTextField loanAmountField; // Requested loan amount
    private JComboBox<String> interestMethodComboBox; // Fixed/Reducing selector
    private JTextArea eligibilityResultArea; // Shows eligibility feedback
    private JButton checkEligibilityButton; // Triggers eligibility check
    private JButton approveButton; // APPROVE button (NOW PROPERLY REFERENCED)
    
    public LoanPage(DashboardPage parent) {
        super(parent, "Loan Application & Approval", ModalityType.APPLICATION_MODAL);
        this.parent = parent;
        this.loanService = new LoanService();
        this.eligibilityService = new EligibilityService();
        this.interestCalculator = new InterestCalculator();
        
        initializeUI(); // Sets up complete user interface
        loadCustomers(); // Populates customer dropdown
        setLocationRelativeTo(parent); // Centers on parent
    }
    
    private void initializeUI() {
        setSize(600, 550); // Dialog dimensions
        setLayout(new BorderLayout(10, 10)); // Main layout
        
        // Header panel
        JLabel titleLabel = new JLabel("Loan Application", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLUE);
        add(titleLabel, BorderLayout.NORTH);
        
        // Main form panel with GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Customer Selection (Row 0)
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(new JLabel("Select Customer:", JLabel.CENTER), gbc);
        gbc.gridy = 1;
        customerComboBox = new JComboBox<>();
        customerComboBox.setPreferredSize(new Dimension(300, 25));
        formPanel.add(customerComboBox, gbc);
        
        // Construction Cost (Row 2)
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Construction Cost (R):"), gbc);
        gbc.gridx = 1;
        constructionCostField = new JTextField(15);
        formPanel.add(constructionCostField, gbc);
        
        // Loan Amount (Row 3)
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Requested Loan Amount (R):"), gbc);
        gbc.gridx = 1;
        loanAmountField = new JTextField(15);
        formPanel.add(loanAmountField, gbc);
        
        // Interest Method (Row 4)
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Interest Method:"), gbc);
        gbc.gridx = 1;
        interestMethodComboBox = new JComboBox<>(new String[]{"FIXED", "REDUCING"});
        formPanel.add(interestMethodComboBox, gbc);
        
        // Eligibility Check Button (Row 5)
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        checkEligibilityButton = new JButton("✅ Check Eligibility");
        checkEligibilityButton.addActionListener(new EligibilityListener()); // Adds click handler
        formPanel.add(checkEligibilityButton, gbc);
        
        // Eligibility Results Area (Row 6)
        gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.weighty = 1.0; // Expands to fill available space
        eligibilityResultArea = new JTextArea(6, 30);
        eligibilityResultArea.setEditable(false); // Read-only
        eligibilityResultArea.setBorder(BorderFactory.createTitledBorder("Eligibility Result"));
        formPanel.add(new JScrollPane(eligibilityResultArea), gbc);
        
        add(formPanel, BorderLayout.CENTER); // Adds form to dialog
        
        // Button panel (Bottom)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("❌ Cancel");
        cancelButton.addActionListener(e -> dispose()); // Closes dialog
        
        approveButton = new JButton("✅ APPROVE LOAN"); // **PROPER REFERENCE HERE**
        approveButton.setEnabled(false); // Initially disabled
        approveButton.setBackground(Color.GREEN); // Visual indicator
        approveButton.setForeground(Color.WHITE);
        approveButton.setFont(new Font("Arial", Font.BOLD, 14));
        approveButton.addActionListener(new ApproveListener()); // Adds approve functionality
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(approveButton); // **CORRECT APPROVE BUTTON REFERENCE**
        add(buttonPanel, BorderLayout.SOUTH); // Adds buttons to bottom
        
        pack(); // Auto-sizes dialog
    }
    
    private void loadCustomers() {
        // Loads and displays all customers from JSON file
        for (Customer customer : loanService.getAllCustomers()) {
            customerComboBox.addItem(customer.getName() + " (ID: " + customer.getId() + ")");
        }
    }
    
    private Customer getSelectedCustomer() {
        String selected = (String) customerComboBox.getSelectedItem();
        if (selected == null) return null;
        // Extracts customer ID from dropdown text
        String customerId = selected.substring(selected.lastIndexOf("ID: ") + 4, selected.length() - 1);
        return loanService.getCustomerById(customerId);
    }
    
    /**
     * FIXED EligibilityListener - Now properly enables approveButton
     */
    private class EligibilityListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Customer customer = getSelectedCustomer();
            if (customer == null) {
                JOptionPane.showMessageDialog(LoanPage.this, 
                    "⚠️ Please select a customer first", "No Customer", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                double constructionCost = Double.parseDouble(constructionCostField.getText().trim());
                double loanAmount = Double.parseDouble(loanAmountField.getText().trim());
                
                if (constructionCost <= 0 || loanAmount <= 0) {
                    JOptionPane.showMessageDialog(LoanPage.this, 
                        "⚠️ Amounts must be positive numbers", "Invalid Amount", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Perform complete eligibility check
                EligibilityService.EligibilityResult result = 
                    eligibilityService.checkEligibility(customer, loanAmount, constructionCost);
                
                // **FIXED: Proper approveButton reference**
                approveButton.setEnabled(result.isEligible()); // Enables/disables CORRECTLY
                
                // Updates result area with detailed feedback
                eligibilityResultArea.setText(result.getReason());
                eligibilityResultArea.setForeground(result.isEligible() ? Color.GREEN : Color.RED);
                
                // Visual feedback
                checkEligibilityButton.setEnabled(false);
                if (result.isEligible()) {
                    JOptionPane.showMessageDialog(LoanPage.this, 
                        "✅ CUSTOMER IS ELIGIBLE!\nClick APPROVE LOAN to proceed", 
                        "Eligible", JOptionPane.INFORMATION_MESSAGE);
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(LoanPage.this, 
                    "⚠️ Please enter valid numbers for amounts", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Approve loan functionality
     */
    private class ApproveListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Customer customer = getSelectedCustomer();
            double constructionCost = Double.parseDouble(constructionCostField.getText().trim());
            double loanAmount = Double.parseDouble(loanAmountField.getText().trim());
            String interestMethod = (String) interestMethodComboBox.getSelectedItem();
            
            // **STEP 1: Create PENDING loan application**
            Loan newLoan = loanService.createLoanApplication(customer.getId(), 
                                                           constructionCost, 
                                                           loanAmount, interestMethod);
            
            // **STEP 2: IMMEDIATELY APPROVE it (for demo - in real app, separate approval step)**
            loanService.approveLoan(newLoan.getId(), true);
            
            JOptionPane.showMessageDialog(LoanPage.this, 
                "🎉 Loan APPROVED & SAVED!\n" +
                "📋 ID: " + newLoan.getId() + "\n" +
                "💰 Amount: R" + String.format("%.2f", loanAmount) + "\n" +
                "📊 Status: APPROVED ✅\n\n" +
                "💾 Check data/loans.json to verify", 
                "APPROVED", JOptionPane.INFORMATION_MESSAGE);
            
            parent.repaint();
            dispose();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(LoanPage.this, 
                "❌ Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
  }
}