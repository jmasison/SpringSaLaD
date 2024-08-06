/**
 *                    CLASS ValueTextField
 * 
 * This class is an extension of JTextField which must contain either an integer
 * or a double. It makes sure that the entries are in the appropriate form.  
 * If the number should either be positive or non-negative, then it also 
 * enforces this criteria.  It will give a pop-up message if the user enters an 
 * incorrect value and will give the user an option of restoring the old value
 * or editing the text.
 * 
 * @author pmichalski
 */

package org.springsalad.langevinsetup;

import javax.swing.*;

import org.springsalad.helpersetup.Constraints;
import org.springsalad.helpersetup.PopUp;

import java.awt.event.*;

public class ValueTextField extends JTextField implements ActionListener, FocusListener{
    
    public final static String INTEGER = "integer";
    public final static String DOUBLE = "double";
    
    private final String className;
    // The constraint type
    private final String constraint;
    // The current text
    private String currentText;
    // Tells us if the value can be empty
    private final boolean allowEmpty;
    
    /**
     *                      CONSTRUCTOR
     * I assume that the initial text of the JTextField is in the correct
     * format.
     * 
     *  @param text The initial text
     *  @param columns The size of the JTextField.
     *  @param className The name of the class, as defined in the static constants.
     *  @param constraint An integer representing the constraint.
     *  @param allowEmpty Flag to indicate if this field can be empty.
     */
    
    public ValueTextField(String text, int columns, 
            String className, String constraint, boolean allowEmpty){
        super(text, columns);
        this.currentText = text;
        this.constraint = constraint;
        this.className = className;
        this.allowEmpty = allowEmpty;
        
        this.addFocusListener(this);
        this.addActionListener(this);
    }
    
    /* 
     * I'm annoyed I can't figure out how to use generics to just define a 
     * a single "getValue()" method which would return either a double or an 
     * integer. I tried to use <N extends Number>, but that didn't work.
     */
    public Double getDouble(){
        if(allowEmpty && this.getText().equals("")){
            return null;
        } else {
            return Double.parseDouble(this.getText());
        }
    }
    
    public Integer getInteger(){
        if(allowEmpty && this.getText().equals("")){
            return null;
        } else {
            return Integer.parseInt(this.getText());
        }
    }
    
    public boolean setData(){
        boolean setOK = true;
        if(allowEmpty && this.getText().equals("")){
            currentText = this.getText();
            return setOK;
        } else {
            
            if(!correctFormat()){
                setOK = false;
                int pick = PopUp.errorWithOption("The value " + this.getText() + 
                        " could not be interpretted as a " + className + ".\n"
                + "Do you want to edit this value? "
                        + "(If not, the previous valid value will be restored.)");
                if(pick == 1){
                    this.setText(currentText);
                } else {
                    this.requestFocusInWindow();
                }

            } else if(!satisfiesConstraint()){
                setOK = false;
                int pick = PopUp.errorWithOption("The value of this field must be " 
                        + constraint + ", but received " + this.getText() + ".\n"
                + "Do you want to edit this value? "
                        + "(If not, the previous valid value will be restored.)");

                if(pick == 1){
                    this.setText(currentText);
                } else {
                    this.requestFocusInWindow();
                }
            } else {
                currentText = this.getText();
            }
            return setOK;
            
        }
    }
    
    private boolean correctFormat(){
        boolean correctFormat = true;
        String s = this.getText();
        try{
            switch (className) {
                case INTEGER:
                    Integer.parseInt(s);
                    break;
                case DOUBLE:
                    Double.parseDouble(s);
                    break;
            }
        } catch(NumberFormatException nfe){
            correctFormat = false;
        }
        return correctFormat;
    }
    
    private boolean satisfiesConstraint(){
        boolean ok;
        switch (className) {
            case INTEGER:
                ok = checkInteger();
                break;
            case DOUBLE:
                ok = checkDouble();
                break;
            default:
                ok = true;
                break;
        }
        return ok;
    }
    
    private boolean checkInteger(){
        boolean ok;
        int i = Integer.parseInt(this.getText());
        switch(constraint){
            case Constraints.NO_CONSTRAINT:
                ok = true;
                break;
            case Constraints.POSITIVE:
                ok = i>0;
                break;
            case Constraints.NEGATIVE:
                ok = i<0;
                break;
            case Constraints.NONPOSITIVE:
                ok = i<=0;
                break;
            case Constraints.NONNEGATIVE:
                ok = i>=0;
                break;
            default:
                ok = true;
                break;
        }
        return ok;
    }
    
    private boolean checkDouble(){
        boolean ok;
        double i = Double.parseDouble(this.getText());
        switch(constraint){
            case Constraints.NO_CONSTRAINT:
                ok = true;
                break;
            case Constraints.POSITIVE:
                ok = i>0;
                break;
            case Constraints.NEGATIVE:
                ok = i<0;
                break;
            case Constraints.NONPOSITIVE:
                ok = i<=0;
                break;
            case Constraints.NONNEGATIVE:
                ok = i>=0;
                break;
            default:
                ok = true;
                break;
        }
        return ok;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.removeFocusListener(this);
        setData();
        this.addFocusListener(this);
    }

    @Override
    public void focusGained(FocusEvent e) {
        // Do nothing
    }

    @Override
    public void focusLost(FocusEvent e) {
        setData();
    }
    
    public void enableListening(boolean bool){
        if(bool){
            this.addActionListener(this);
            this.addFocusListener(this);
        } else {
            this.removeActionListener(this);
            this.removeFocusListener(this);
        }
    }
    
}
