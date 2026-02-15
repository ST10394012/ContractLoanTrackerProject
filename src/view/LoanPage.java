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

public class LoanPage extends JDialog{
    private LoanService loanService; // Main business service
    private EligibilityService eligibilityService; // Eligibility checker
    private InterestCalculator interestCalculator; // Interest calculations
    private DashboardPage parent; // Parent dashboard reference
    
    // UI Components
    private JComboBox<String> customerComboBox; // Customer selector
    private JTextField constructionCostField; // Project cost input
    private JTextField loanAmountField; // Requested loan amount
    private JComboBox<String> interestMethodComboBox; // Fixed/Reducing selector
    private JTextArea eligibilityResultArea; // Shows eligibility feedback
    private JButton checkEligibilityButton; // Triggers eligibility check
    
    public LoanPage(DashboardPage parent) {
        super(parent, "Loan Application & Approval", ModalityType.APPLICATION_MODAL);
        this.parent = parent;
        this.loanService = new LoanService();
        this.eligibilityService = new EligibilityService();
        this.interestCalculator = new InterestCalculator();
        
        initializeUI();
        loadCustomers(); // Populates customer dropdown
        setLocationRelativeTo(parent);
    }
    
    private void initializeUI() {
        setSize(600, 550);
        setLayout(new BorderLayout(10, 10));
        
        // Header
        JLabel titleLabel = new JLabel("Loan Application", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLUE);
        add(titleLabel, BorderLayout.NORTH);
        
        // Main form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Customer Selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(new JLabel("Select Customer:", JLabel.CENTER), gbc);
        gbc.gridy = 1;
        customerComboBox = new JComboBox<>();
        customerComboBox.setPreferredSize(new Dimension(300, 25));
        formPanel.add(customerComboBox, gbc);
        
        // Construction Cost
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Construction Cost (R):"), gbc);
        gbc.gridx = 1;
        constructionCostField = new JTextField(15);
        formPanel.add(constructionCostField, gbc);
        
        // Loan Amount
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Requested Loan Amount (R):"), gbc);
        gbc.gridx = 1;
        loanAmountField = new JTextField(15);
        formPanel.add(loanAmountField, gbc);
        
        // Interest Method
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Interest Method:"), gbc);
        gbc.gridx = 1;
        interestMethodComboBox = new JComboBox<>(new String[]{"FIXED", "REDUCING"});
        formPanel.add(interestMethodComboBox, gbc);
        
        // Eligibility Check Button
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        checkEligibilityButton = new JButton("Check Eligibility");
        checkEligibilityButton.addActionListener(new EligibilityListener());
        formPanel.add(checkEligibilityButton, gbc);
        
        // Eligibility Results
        gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.weighty = 1.0; // Expands to fill space
        eligibilityResultArea = new JTextArea(6, 30);
        eligibilityResultArea.setEditable(false);
        eligibilityResultArea.setBorder(BorderFactory.createTitledBorder("Eligibility Result"));
        formPanel.add(new JScrollPane(eligibilityResultArea), gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton approveButton = new JButton("Approve Loan");
        approveButton.setEnabled(false); // Disabled until eligible
        approveButton.addActionListener(new ApproveListener());
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(approveButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadCustomers() {
        // Populates dropdown with all customers from JSON
        for (Customer customer : loanService.getAllCustomers()) {
            customerComboBox.addItem(customer.getName() + " (ID: " + customer.getId() + ")");
        }
    }
    
    private Customer getSelectedCustomer() {
        String selected = (String) customerComboBox.getSelectedItem();
        if (selected == null) return null;
        String customerId = selected.substring(selected.lastIndexOf("ID: ") + 4, selected.length() - 1);
        return loanService.getCustomerById(customerId);
    }
    
    private class EligibilityListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Customer customer = getSelectedCustomer();
            if (customer == null) {
                JOptionPane.showMessageDialog(LoanPage.this, "Please select a customer first");
                return;
            }
            
            try {
                double constructionCost = Double.parseDouble(constructionCostField.getText());
                double loanAmount = Double.parseDouble(loanAmountField.getText());
                
                // Perform eligibility check
                EligibilityService.EligibilityResult result = 
                    eligibilityService.checkEligibility(customer, loanAmount, constructionCost);
                
                eligibilityResultArea.setText(result.getReason()); // Shows detailed result
                
                // Enable/disable approve button based on eligibility
                checkEligibilityButton.setEnabled(false);
                ((AbstractButton) getContentPane().getComponent(3)).setEnabled(result.isEligible());
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(LoanPage.this, "Please enter valid amounts");
            }
        }
    }
    
    private class ApproveListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Customer customer = getSelectedCustomer();
                double constructionCost = Double.parseDouble(constructionCostField.getText());
                double loanAmount = Double.parseDouble(loanAmountField.getText());
                String interestMethod = (String) interestMethodComboBox.getSelectedItem();
                
                // Create and save loan application
                loanService.createLoanApplication(customer.getId(), constructionCost, 
                                                loanAmount, interestMethod);
                
                JOptionPane.showMessageDialog(LoanPage.this, 
                    "Loan application created successfully!\nStatus: PENDING");
                parent.repaint();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(LoanPage.this, 
                    "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
