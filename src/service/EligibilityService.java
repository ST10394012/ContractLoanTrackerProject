/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.Customer;
import util.ValidationUtil;

// This class implements all loan eligibility business logic
public class EligibilityService {
   /**
     * Performs complete eligibility check with detailed results
     * @param customer Customer data
     * @param loanAmount Requested amount
     * @param constructionCost Project cost
     * @return Eligibility result with reason
     */
    public EligibilityResult checkEligibility(Customer customer, double loanAmount, 
                                            double constructionCost) {
        
        // Check 1: Loan amount limits (80% individual, 70% business)
        double maxLoanPercentage = "INDIVIDUAL".equals(customer.getType()) ? 0.80 : 0.70;
        double maxAllowedAmount = constructionCost * maxLoanPercentage;
        if (loanAmount > maxAllowedAmount) {
            return new EligibilityResult(false, 
                "Loan amount exceeds " + (maxLoanPercentage * 100) + 
                "% of construction cost (Max: R" + String.format("%.2f", maxAllowedAmount) + ")");
        }
        
        // Check 2: Credit score requirements (600 individual, 650 business)
        int minCreditScore = "INDIVIDUAL".equals(customer.getType()) ? 600 : 650;
        if (customer.getCreditScore() < minCreditScore) {
            return new EligibilityResult(false, 
                "Credit score too low. Minimum required: " + minCreditScore);
        }
        
        // Check 3: Debt-to-income ratio < 40%
        double dtiRatio = customer.getDebtToIncomeRatio();
        if (dtiRatio >= 40) {
            return new EligibilityResult(false, 
                "Debt-to-income ratio too high: " + String.format("%.2f", dtiRatio) + "% (Max: 40%)");
        }
        
        // All checks passed
        return new EligibilityResult(true, "Customer eligible for loan approval");
    }
    
    
    public boolean isEligible(Customer customer, double loanAmount, double constructionCost) {
        return checkEligibility(customer, loanAmount, constructionCost).isEligible();
    }
    
    /**
     * Calculates interest rate based on customer type
     * Individual: Prime + 2%, Business: Prime + 3.5%
     * Assuming current prime rate of 11.75%
     */
    public double calculateInterestRate(String customerType) {
        double primeRate = 11.75; // Current South African prime rate
        return customerType.equals("INDIVIDUAL") ? primeRate + 2.0 : primeRate + 3.5;
    }
    
    /**
     * Inner result class for detailed eligibility feedback
     */
    public static class EligibilityResult {
        private boolean eligible; // True if customer qualifies
        private String reason; // Explanation of result
        
        public EligibilityResult(boolean eligible, String reason) {
            this.eligible = eligible; // Sets eligibility status
            this.reason = reason; // Sets explanation message
        }
        
        public boolean isEligible() { return eligible; }
        public String getReason() { return reason; }
    }
}