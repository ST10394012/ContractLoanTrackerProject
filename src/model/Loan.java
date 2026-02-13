/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList; // For storing disbursements and repayments
import java.util.List; // Interface for collections
import com.google.gson.annotations.SerializedName;


public class Loan {
    @SerializedName("id") // JSON field name for loan ID
    private String id; // Unique loan identifier
    
    @SerializedName("customerId") // Links loan to specific customer
    private String customerId; // Reference to customer who owns this loan
    
    @SerializedName("constructionCost") 
    private double constructionCost; 
    
    @SerializedName("loanAmount") 
    private double loanAmount; 
    
    @SerializedName("status") // Current loan status
    private String status; // "PENDING", "APPROVED", "REJECTED", "DISBURSED", "REPAID"
    
    @SerializedName("interestRate") // Annual interest rate
    private double interestRate; // Calculated based on customer type
    
    @SerializedName("interestMethod") 
    private String interestMethod; 
    
    @SerializedName("disbursements") 
    private List<Disbursement> disbursements; 
    
    @SerializedName("repayments") 
    private List<Repayment> repayments; 
    
    @SerializedName("outstandingBalance") 
    private double outstandingBalance; 
    
    
    public Loan() {
        this.disbursements = new ArrayList<>(); // Initializes empty disbursements list
        this.repayments = new ArrayList<>(); // Initializes empty repayments list
    }
    
    // Constructor for new loan applications
    public Loan(String customerId, double constructionCost, double loanAmount, 
                double interestRate, String interestMethod) {
        this.id = java.util.UUID.randomUUID().toString(); // Generates unique loan ID
        this.customerId = customerId; // Links to customer
        this.constructionCost = constructionCost; 
        this.loanAmount = loanAmount; 
        this.status = "PENDING"; 
        this.interestRate = interestRate; 
        this.interestMethod = interestMethod; 
        this.disbursements = new ArrayList<>(); 
        this.repayments = new ArrayList<>(); 
        this.outstandingBalance = loanAmount; 
    }
    
    // All standard getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public double getConstructionCost() { return constructionCost; }
    public void setConstructionCost(double constructionCost) { this.constructionCost = constructionCost; }
    
    public double getLoanAmount() { return loanAmount; }
    public void setLoanAmount(double loanAmount) { this.loanAmount = loanAmount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
    
    public String getInterestMethod() { return interestMethod; }
    public void setInterestMethod(String interestMethod) { this.interestMethod = interestMethod; }
    
    public List<Disbursement> getDisbursements() { return disbursements; }
    public void setDisbursements(List<Disbursement> disbursements) { this.disbursements = disbursements; }
    
    public List<Repayment> getRepayments() { return repayments; }
    public void setRepayments(List<Repayment> repayments) { this.repayments = repayments; }
    
    public double getOutstandingBalance() { return outstandingBalance; }
    public void setOutstandingBalance(double outstandingBalance) { this.outstandingBalance = outstandingBalance; }
    
    // Calculates monthly repayment amount
    public double calculateMonthlyRepayment(int loanTermMonths) {
        double monthlyRate = interestRate / 12 / 100; // Converts annual rate to monthly decimal
        return (loanAmount * monthlyRate * Math.pow(1 + monthlyRate, loanTermMonths)) /
               (Math.pow(1 + monthlyRate, loanTermMonths) - 1); // EMI formula
    }
}
    

