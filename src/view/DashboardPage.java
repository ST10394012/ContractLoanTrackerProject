/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import service.LoanService;
import javax.swing.*;
import java.awt.*;

public class DashboardPage extends JFrame {

   private LoanService loanService; // Main business service
    private String userRole; // Current user's role
    
    public DashboardPage(String role) {
        this.userRole = role; // Stores user role
        this.loanService = new LoanService(); // Initializes service
        initializeUI(); // Sets up interface
    }
    
    private void initializeUI() {
        setTitle("Construction Loan Tracker - " + userRole + " Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create menu bar with all features
        JMenuBar menuBar = new JMenuBar();
        
        JMenu customerMenu = new JMenu("Customers");
        JMenuItem addCustomerItem = new JMenuItem("Add Customer");
        addCustomerItem.addActionListener(e -> new AddCustomerPage(this).setVisible(true));
        customerMenu.add(addCustomerItem);
        
        JMenu loanMenu = new JMenu("Loans");
        JMenuItem newLoanItem = new JMenuItem("New Loan Application");
        newLoanItem.addActionListener(e -> new LoanPage(this).setVisible(true));
        loanMenu.add(newLoanItem);
        
        JMenu disbursementMenu = new JMenu("Disbursements");
        JMenuItem disbursementItem = new JMenuItem("Record Disbursement");
        disbursementMenu.add(disbursementItem);
        
        JMenu reportsMenu = new JMenu("Reports");
        JMenuItem reportsItem = new JMenuItem("Generate Reports");
        reportsMenu.add(reportsItem);
        
        menuBar.add(customerMenu);
        menuBar.add(loanMenu);
        menuBar.add(disbursementMenu);
        menuBar.add(reportsMenu);
        
        setJMenuBar(menuBar);
        
        // Status panel showing summary stats
        JPanel statusPanel = new JPanel(new GridLayout(1, 4));
        statusPanel.add(new JLabel("Total Customers: " + loanService.getAllCustomers().size()));
        statusPanel.add(new JLabel("Total Loans: " + loanService.getAllLoans().size()));
        add(statusPanel, BorderLayout.SOUTH);
    }
}
