/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package construction.loan.tracker;

import view.LoginPage;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ConstructionLoanTracker {

    
    public static void main(String[] args) {
       try{
           UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
           
       }catch(Exception e){
           e.printStackTrace();
       }
       
       SwingUtilities.invokeLater(() ->{
           new LoginPage().setVisible(true);
       });
    }
    
}
