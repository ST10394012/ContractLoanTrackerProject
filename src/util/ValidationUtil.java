/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

//This is the class for unput validation and business rule checks

public class ValidationUtil {
  /**
     * Validates loan eligibility based on business rules
     * customer Customer applying for loan
     * loanAmount Requested loan amount
     * constructionCost Total project cost
     * @return true if customer and loan pass all eligibility criteria
     */
    public static boolean isLoanEligible(model.Customer customer, double loanAmount, 
                                       double constructionCost) {
        // Loan amount limits based on customer type
        double maxLoanPercentage = "INDIVIDUAL".equals(customer.getType()) ? 0.80 : 0.70;
        if (loanAmount > constructionCost * maxLoanPercentage) {
            return false; // Exceeds maximum allowed percentage
        }
        
        // Credit score requirements
        int minCreditScore = "INDIVIDUAL".equals(customer.getType()) ? 600 : 650;
        if (customer.getCreditScore() < minCreditScore) {
            return false; // Credit score too low
        }
        
        //  Debt-to-income ratio must be under 40%
        if (customer.getDebtToIncomeRatio() >= 40) {
            return false; // DTI ratio too high
        }
        
        return true; // All rules passed
    }
    
    /**
     * Validates positive numeric input
     * value Value to validate
     * fieldName Name of field for error message
     * @return true if value is positive number
     */
    public static boolean isValidAmount(double value, String fieldName) {
        return value > 0; // positive number check
    }
    
    /**
     * Validates if string is not empty
     * value String to validate
     * fieldName Field name for error message
     * @return true if string has content
     */
    public static boolean isValidString(String value, String fieldName) {
        return value != null && !value.trim().isEmpty(); // Checks for non-empty string
    }
}