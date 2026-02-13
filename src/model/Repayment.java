/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDate;

public class Repayment {
  @SerializedName("id") // Unique repayment ID
    private String id;
    
    @SerializedName("loanId") 
    private String loanId;
    
    @SerializedName("amount") 
    private double amount;
    
    @SerializedName("dueDate") 
    private String dueDate;
    
    @SerializedName("paymentDate") 
    private String paymentDate;
    
    @SerializedName("isLate") 
    private boolean isLate;
    
    @SerializedName("penalty") 
    private double penalty;
    
    // Default constructor
    public Repayment() {}
    
    // Constructor for repayments
    public Repayment(String loanId, double amount, String dueDate) {
        this.id = java.util.UUID.randomUUID().toString(); // Generates unique ID
        this.loanId = loanId; 
        this.amount = amount; 
        this.dueDate = dueDate; 
        this.paymentDate = LocalDate.now().toString(); 
        this.isLate = LocalDate.parse(dueDate).isBefore(LocalDate.now()); // Checks if late
        this.penalty = isLate ? amount * 0.05 : 0; // 5% penalty if late
    }
    
    // All getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getLoanId() { return loanId; }
    public void setLoanId(String loanId) { this.loanId = loanId; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    
    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
    
    public boolean isLate() { return isLate; }
    public void setLate(boolean isLate) { this.isLate = isLate; }
    
    public double getPenalty() { return penalty; }
    public void setPenalty(double penalty) { this.penalty = penalty; }
}