/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDate; //For date tracking

public class Disbursement {
   @SerializedName("id") // Unique disbursement ID
    private String id;
    
    @SerializedName("loanId") // Links to parent loan
    private String loanId;
    
    @SerializedName("stage") 
    private String stage; 
    
    @SerializedName("amount") 
    private double amount;
    
    @SerializedName("date") 
    private String date; 
    
    @SerializedName("inspectionApproved") 
    private boolean inspectionApproved;
    
    // Default constructor
    public Disbursement() {}
    
    // Constructor for new disbursements
    public Disbursement(String loanId, String stage, double amount, boolean inspectionApproved) {
        this.id = java.util.UUID.randomUUID().toString(); // Generates unique ID
        this.loanId = loanId; 
        this.stage = stage; 
        this.amount = amount; 
        this.date = LocalDate.now().toString(); 
        this.inspectionApproved = inspectionApproved; 
    }
    
    // All getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getLoanId() { return loanId; }
    public void setLoanId(String loanId) { this.loanId = loanId; }
    
    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public boolean isInspectionApproved() { return inspectionApproved; }
    public void setInspectionApproved(boolean inspectionApproved) { 
        this.inspectionApproved = inspectionApproved; 
    }
}