/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

//Service for all interest calculations(Support both fixed and reduction balance methods)
public class InterestCalculator {
  /**
     * Calculates monthly interest for fixed rate method
     * @param principal Original loan amount
     * @param annualRate Annual interest rate (%)
     * @return Monthly interest amount
     */
    public double calculateFixedMonthlyInterest(double principal, double annualRate) {
        double monthlyRate = annualRate / 12 / 100; // Converts annual rate to monthly decimal
        return principal * monthlyRate; // Interest = Principal × Monthly Rate
    }
    
    /**
     * Calculates monthly interest for reducing balance method
     * @param outstandingBalance Current unpaid balance
     * @param annualRate Annual interest rate (%)
     * @return Monthly interest on current balance
     */
    public double calculateReducingMonthlyInterest(double outstandingBalance, double annualRate) {
        double monthlyRate = annualRate / 12 / 100; // Converts to monthly rate
        return outstandingBalance * monthlyRate; // Interest on current balance only
    }
    
    /**
     * Calculates Equated Monthly Installment (EMI)
     * @param principal Loan amount
     * @param annualRate Annual interest rate (%)
     * @param loanTermMonths Total loan duration in months
     * @return Monthly repayment amount
     */
    public double calculateEMI(double principal, double annualRate, int loanTermMonths) {
        double monthlyRate = annualRate / 12 / 100; // Monthly interest rate
        double emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, loanTermMonths)) /
                    (Math.pow(1 + monthlyRate, loanTermMonths) - 1); // Standard EMI formula
        return emi;
    }
    
    /**
     * Calculates late payment penalty (5% of payment)
     * @param paymentAmount Original payment amount
     * @return Penalty amount
     */
    public double calculateLatePenalty(double paymentAmount) {
        return paymentAmount * 0.05; // 5% penalty
    }
}
