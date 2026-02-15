/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import model.Customer;
import service.LoanService;
import util.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AddCustomerPage extends JDialog {
    private LoanService loanService; // Service for saving customers
    private DashboardPage parent; // Reference to parent dashboard
    
    // UI Components
    private JTextField nameField; // Customer name input
    private JComboBox<String> typeComboBox; // Individual/Business selector
    private JTextField creditScoreField; // Credit score input
    private JTextField incomeField; // Monthly income input
    private JTextField debtField; // Existing debt input
    
    public AddCustomerPage(DashboardPage parent) {
        super(parent, "Add New Customer", ModalityType.APPLICATION_MODAL); // Modal dialog
        this.parent = parent; // Stores parent reference
        this.loanService = new LoanService(); // Initializes service
        
        initializeUI(); // Sets up complete user interface
        setLocationRelativeTo(parent); // Centers on parent window
    }
    
    private void initializeUI() {
        setSize(450, 400); // Dialog dimensions
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // Prevents accidental close
        setLayout(new BorderLayout(10, 10)); // Main layout
        
        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("New Customer Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);
        
        // Form panel with grid layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Components fill horizontally
        
        // Customer Name
        gbc.gridx = 0; gbc.gridy = 0; // Grid position
        formPanel.add(new JLabel("Full Name/Business Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        // Customer Type
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Customer Type:"), gbc);
        gbc.gridx = 1;
        typeComboBox = new JComboBox<>(new String[]{"INDIVIDUAL", "BUSINESS"});
        formPanel.add(typeComboBox, gbc);
        
        // Credit Score
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Credit Score:"), gbc);
        gbc.gridx = 1;
        creditScoreField = new JTextField(20);
        formPanel.add(creditScoreField, gbc);
        
        // Monthly Income
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Monthly Income (R):"), gbc);
        gbc.gridx = 1;
        incomeField = new JTextField(20);
        formPanel.add(incomeField, gbc);
        
        // Existing Debt
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Monthly Debt (R):"), gbc);
        gbc.gridx = 1;
        debtField = new JTextField(20);
        formPanel.add(debtField, gbc);
        
        add(new JScrollPane(formPanel), BorderLayout.CENTER); // Scrollable form
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Customer");
        saveButton.addActionListener(new SaveCustomerListener()); // Save functionality
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose()); // Closes dialog
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Handle window close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose(); // Properly closes dialog
            }
        });
    }
    
    /**
     * Action listener for save button
     * Validates input and saves customer
     */
    private class SaveCustomerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Validate all inputs
            if (!ValidationUtil.isValidString(nameField.getText(), "Name")) {
                JOptionPane.showMessageDialog(AddCustomerPage.this, 
                    "Please enter customer name", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double income, debt;
            try {
                income = Double.parseDouble(incomeField.getText()); // Parses income
                debt = Double.parseDouble(debtField.getText()); // Parses debt
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(AddCustomerPage.this, 
                    "Please enter valid numbers for income and debt", "Invalid Number", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!ValidationUtil.isValidAmount(income, "Income") || 
                !ValidationUtil.isValidAmount(debt, "Debt")) {
                JOptionPane.showMessageDialog(AddCustomerPage.this, 
                    "Income and Debt must be positive numbers", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                int creditScore = Integer.parseInt(creditScoreField.getText()); // Parses credit score
                
                // Create new customer object
                Customer customer = new Customer(
                    nameField.getText().trim(), // Customer name
                    (String) typeComboBox.getSelectedItem(), // Customer type
                    creditScore, // Credit score
                    income, // Monthly income
                    debt // Existing debt
                );
                
                // Save customer to JSON file
                loanService.addCustomer(customer);
                
                JOptionPane.showMessageDialog(AddCustomerPage.this, 
                    "Customer saved successfully!\nID: " + customer.getId(), 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                parent.repaint(); // Refresh parent dashboard
                dispose(); // Close dialog
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(AddCustomerPage.this, 
                    "Please enter valid credit score", "Invalid Number", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
