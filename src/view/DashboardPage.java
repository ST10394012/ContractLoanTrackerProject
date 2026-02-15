/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import service.LoanService;
import javax.swing.*;
import java.awt.*;

/**
 * FIXED DashboardPage - Now displays properly
 */
public class DashboardPage extends JFrame {
    
    private LoanService loanService;
    private String userRole;
    
    public DashboardPage(String role) {
        this.userRole = role;
        this.loanService = new LoanService();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("🏗️ Construction Loan Tracker - " + userRole + " Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // **CRITICAL**
        setLocationRelativeTo(null); // Center screen
        
        // Menu bar with ALL features
        JMenuBar menuBar = new JMenuBar();
        
        // Customers menu
        JMenu customerMenu = new JMenu("👥 Customers");
        JMenuItem addCustomerItem = new JMenuItem("➕ Add Customer");
        addCustomerItem.addActionListener(e -> new AddCustomerPage(this).setVisible(true));
        customerMenu.add(addCustomerItem);
        
        // Loans menu  
        JMenu loanMenu = new JMenu("💰 Loans");
        JMenuItem newLoanItem = new JMenuItem("📝 New Application");
        newLoanItem.addActionListener(e -> new LoanPage(this).setVisible(true));
        loanMenu.add(newLoanItem);
        
        // Disbursements menu
        JMenu disbursementMenu = new JMenu("🏗️ Disbursements");
        JMenuItem disbursementItem = new JMenuItem("💸 Record Disbursement");
        disbursementItem.addActionListener(e -> new DisbursementPage(this).setVisible(true));
        disbursementMenu.add(disbursementItem);
        
        // Repayments menu
        JMenu repaymentMenu = new JMenu("💳 Repayments");
        JMenuItem repaymentItem = new JMenuItem("🔄 Record Repayment");
        repaymentItem.addActionListener(e -> new RepaymentPage(this).setVisible(true));
        repaymentMenu.add(repaymentItem);
        
        // Reports menu
        JMenu reportsMenu = new JMenu("📊 Reports");
        JMenuItem reportsItem = new JMenuItem("📈 Generate Reports");
        reportsItem.addActionListener(e -> new ReportsPage(DashboardPage.this).setVisible(true)); // **FIXED**
        reportsMenu.add(reportsItem);

        // Add menus to menu bar
        menuBar.add(customerMenu);
        menuBar.add(loanMenu);
        menuBar.add(disbursementMenu);
        menuBar.add(repaymentMenu);
        menuBar.add(reportsMenu);
        
        setJMenuBar(menuBar);
        
        // **FIXED Status Panel** - CORRECT BorderLayout.SOUTH
        JPanel statusPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        statusPanel.setBorder(BorderFactory.createTitledBorder("📊 Summary"));
        
        statusPanel.add(new JLabel("Customers: " + loanService.getAllCustomers().size()));
        statusPanel.add(new JLabel("Loans: " + loanService.getAllLoans().size()));
        statusPanel.add(new JLabel("Role: " + userRole));
        statusPanel.add(new JLabel("Status: Active"));
        
        add(statusPanel, BorderLayout.SOUTH); // **FIXED: No more .S SOUTH typo**
        
        // Welcome panel
        JLabel welcomeLabel = new JLabel("Welcome to Construction Loan Tracker!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.BLUE);
        add(welcomeLabel, BorderLayout.CENTER);
        
        setVisible(true); // **ENSURES DISPLAY**
    }
}