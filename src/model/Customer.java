/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.util.UUID; //Imports UUID for generating unique customer IDs
import com.google.gson.annotations.SerializedName; //Gson annotation for Json serialization

public class Customer {
  @SerializedName("id") // Maps this field to "id" key in JSON
    private String id; // Unique identifier for the customer
    
    @SerializedName("name") // Maps to "name" in JSON
    private String name; // Customer's full name or business name
    
    @SerializedName("type") // Maps to "type" in JSON - "INDIVIDUAL" or "BUSINESS"
    private String type; // Customer type determining eligibility rules
    
    @SerializedName("creditScore") // Maps to "creditScore" in JSON
    private int creditScore; // Customer's credit score for eligibility check
    
    @SerializedName("income") // Maps to "income" in JSON
    private double income; // Monthly income for debt-to-income calculation
    
    @SerializedName("debt") // Maps to "debt" in JSON
    private double debt; // Existing monthly debt payments
    
    // Default constructor required for JSON deserialization
    public Customer() {}
    
    // Constructor with all parameters for easy object creation
    public Customer(String name, String type, int creditScore, double income, double debt) {
        this.id = UUID.randomUUID().toString(); // Generates unique ID using UUID
        this.name = name; // Sets customer name
        this.type = type; // Sets customer type
        this.creditScore = creditScore; // Sets credit score
        this.income = income; // Sets monthly income
        this.debt = debt; // Sets existing debt
    }
    
    // Getter for customer ID
    public String getId() { return id; }
    
    // Setter for customer ID (used during JSON deserialization)
    public void setId(String id) { this.id = id; }
    
    // Getter for customer name
    public String getName() { return name; }
    
    // Setter for customer name
    public void setName(String name) { this.name = name; }
    
    // Getter for customer type
    public String getType() { return type; }
    
    // Setter for customer type
    public void setType(String type) { this.type = type; }
    
    // Getter for credit score
    public int getCreditScore() { return creditScore; }
    
    // Setter for credit score
    public void setCreditScore(int creditScore) { this.creditScore = creditScore; }
    
    // Getter for monthly income
    public double getIncome() { return income; }
    
    // Setter for monthly income
    public void setIncome(double income) { this.income = income; }
    
    // Getter for existing monthly debt
    public double getDebt() { return debt; }
    
    // Setter for existing monthly debt
    public void setDebt(double debt) { this.debt = debt; }
    
    // Calculates debt-to-income ratio as a percentage
    public double getDebtToIncomeRatio() {
        return (debt / income) * 100; // Returns DTI ratio (debt/income * 100)
    }
}