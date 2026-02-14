/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends JFrame {
    
    private JTextField usernameField; // Username input field
    private JPasswordField passwordField; // Password input (masked)
    private JComboBox<String> roleComboBox; // Role selector
    
    public LoginPage() {
        initializeUI(); // Sets up complete user interface
        createDataDirectories(); // Ensures data folder exists
    }
    
    private void initializeUI() {
        setTitle("Construction Loan Tracker - Login"); // Window title
        setSize(400, 300); // Window dimensions
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Closes app when window closed
        setLocationRelativeTo(null); // Centers window on screen
        
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title label
        JLabel titleLabel = new JLabel("Construction Loan Tracker", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLUE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Login form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        
        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);
        
        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);
        
        formPanel.add(new JLabel("Role:"));
        roleComboBox = new JComboBox<>(new String[]{"Loan Officer", "Finance Admin", "Branch Manager"});
        formPanel.add(roleComboBox);
        
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginListener()); // Adds login functionality
        formPanel.add(new JPanel()); // Empty cell for layout
        formPanel.add(loginButton);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        add(mainPanel); // Adds main panel to frame
    }
    
    private void createDataDirectories() {
        new java.io.File("data").mkdirs(); // Creates data directory if missing
    }
    
    // Login button action listener
    private class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText(); // Gets username input
            String password = new String(passwordField.getPassword()); // Gets password input
            
            // Simple authentication (in production, use proper security)
            if (username.equals("admin") && password.equals("password123")) {
                dispose(); // Closes login window
                new DashboardPage((String) roleComboBox.getSelectedItem()).setVisible(true); // Opens dashboard
            } else {
                JOptionPane.showMessageDialog(LoginPage.this, 
                    "Invalid credentials!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
