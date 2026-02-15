/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.*;
import util.JsonUtil;
import java.util.List;
import java.util.stream.Collectors;

public class LoanService {
  private static final String CUSTOMERS_PATH = "data/customers.json"; // Customer data file
    private static final String LOANS_PATH = "data/loans.json"; // Loans data file
    private static final String REPAYMENTS_PATH = "data/repayments.json"; // Repayments data file
    
    private EligibilityService eligibilityService; // Eligibility checker
    private InterestCalculator interestCalculator; // Interest calculator
    
    // Constructor initializes services
    public LoanService() {
        this.eligibilityService = new EligibilityService(); // Creates eligibility service
        this.interestCalculator = new InterestCalculator(); // Creates interest calculator
    }
    
    /**
     * Creates new loan application
     */
    public Loan createLoanApplication(String customerId, double constructionCost, 
                                    double requestedAmount, String interestMethod) {
        // Load customer to get type for interest rate calculation
        Customer customer = getCustomerById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found"); // Validates customer exists
        }
        
        // Check eligibility first
        if (!eligibilityService.isEligible(customer, requestedAmount, constructionCost)) {
            throw new IllegalArgumentException("Customer not eligible"); // Rejects ineligible
        }
        
        // Create loan with calculated interest rate
        double interestRate = eligibilityService.calculateInterestRate(customer.getType());
        Loan loan = new Loan(customerId, constructionCost, requestedAmount, 
                           interestRate, interestMethod);
        
        // Save new loan
        List<Loan> loans = JsonUtil.readFromJson(LOANS_PATH, Loan.class);
        loans.add(loan); // Adds to loan list
        JsonUtil.writeToJson(LOANS_PATH, loans); // Persists to file
        
        return loan; // Returns created loan
    }
    
    /**
     * Approves or rejects pending loan
     */
   public void approveLoan(String loanId, boolean approve) {
    // Load ALL loans from JSON file
    List<Loan> loans = JsonUtil.readFromJson(LOANS_PATH, Loan.class);
    
    // Find and update the specific loan
    boolean found = false;
    for (Loan loan : loans) {
        if (loan.getId().equals(loanId)) {
            loan.setStatus(approve ? "APPROVED" : "REJECTED"); // Update status
            System.out.println("✅ Loan " + loanId + " updated to: " + loan.getStatus()); // DEBUG
            found = true;
            break; // Found and updated
        }
    }
    
    if (!found) {
        System.out.println("❌ Loan ID not found: " + loanId); // DEBUG
        throw new IllegalArgumentException("Loan not found");
    }
    
    // **CRITICAL FIX: SAVE BACK TO JSON**
    JsonUtil.writeToJson(LOANS_PATH, loans);
    System.out.println("💾 Loans saved to JSON - Total loans: " + loans.size()); // DEBUG
}
    
    /**
     * Records disbursement for approved loan
     */
    public void recordDisbursement(String loanId, String stage, double amount, 
                                 boolean inspectionApproved) {
        Loan loan = getLoanById(loanId);
        if (!"APPROVED".equals(loan.getStatus())) {
            throw new IllegalStateException("Loan must be approved first"); // Validates status
        }
        
        if (amount > loan.getOutstandingBalance()) {
            throw new IllegalArgumentException("Amount exceeds remaining balance"); // Validates amount
        }
        
        Disbursement disbursement = new Disbursement(loanId, stage, amount, inspectionApproved);
        loan.getDisbursements().add(disbursement); // Adds to loan disbursements
        loan.setOutstandingBalance(loan.getOutstandingBalance() - amount); // Updates balance
        
        // Save updated loan
        saveLoans(JsonUtil.readFromJson(LOANS_PATH, Loan.class));
    }
    
    /**
     * Records repayment
     */
    public void recordRepayment(String loanId, double amount, String dueDate) {
        Loan loan = getLoanById(loanId);
        Repayment repayment = new Repayment(loanId, amount, dueDate);
        
        loan.getRepayments().add(repayment); // Adds to repayment history
        loan.setOutstandingBalance(Math.max(0, loan.getOutstandingBalance() - amount)); // Updates balance
        
        // Save repayment history
        List<Repayment> repayments = JsonUtil.readFromJson(REPAYMENTS_PATH, Repayment.class);
        repayments.add(repayment);
        JsonUtil.writeToJson(REPAYMENTS_PATH, repayments);
        
        // Save updated loan
        saveLoans(JsonUtil.readFromJson(LOANS_PATH, Loan.class));
    }
    
    // Data access helper methods
    public List<Customer> getAllCustomers() {
        return JsonUtil.readFromJson(CUSTOMERS_PATH, Customer.class); // Loads all customers
    }
    
    public Customer getCustomerById(String id) {
        return getAllCustomers().stream()
                .filter(c -> c.getId().equals(id)) // Finds matching customer
                .findFirst().orElse(null); // Returns first match or null
    }
    
    public List<Loan> getAllLoans() {
        return JsonUtil.readFromJson(LOANS_PATH, Loan.class); // Loads all loans
    }
    
    public Loan getLoanById(String id) {
        return getAllLoans().stream()
                .filter(loan -> loan.getId().equals(id))
                .findFirst().orElse(null);
    }
    
    public List<Loan> getLoansByCustomer(String customerId) {
        return getAllLoans().stream()
                .filter(loan -> loan.getCustomerId().equals(customerId))
                .collect(Collectors.toList()); // Returns customer-specific loans
    }
    
    private void saveLoans(List<Loan> loans) {
        JsonUtil.writeToJson(LOANS_PATH, loans); // Saves loan list to file
    }
    
    public void addCustomer(Customer customer) {
        List<Customer> customers = JsonUtil.readFromJson(CUSTOMERS_PATH, Customer.class);
        customers.add(customer); // Adds new customer
        JsonUtil.writeToJson(CUSTOMERS_PATH, customers); // Persists to file
    }
}
