/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;


import javax.swing.*;

import org.springsalad.helpersetup.PopUp;

import java.awt.event.*;
import java.util.ArrayList;


public class NameTextField extends JTextField implements ActionListener, 
                                                        FocusListener {
    
    private final ArrayList<String> disallowedNames = new ArrayList<>();
    
    public NameTextField(){
        super(10);
        this.addFocusListener(this);
        this.addActionListener(this);
    }
    
    public NameTextField(String text, int columns){
        super(text, columns);
        this.addFocusListener(this);
        this.addActionListener(this);
    }
    
    public void addDisallowedName(String name){
        disallowedNames.add(name);
    }
    
    public void addDisallowedNames(ArrayList<String> names){
        for(String name : names){
            disallowedNames.add(name);
        }
    }
    
    protected boolean nameOK(){
        boolean ok = true;
        String text = this.getText();
        for(String name : disallowedNames){
            if(name.equals(text)){
                ok = false;
                PopUp.error("The name " + text + " is either protected or already in use.");
                break;
            }
        }
        return ok;
    }
    
    @Override
    public void actionPerformed(ActionEvent event){
        this.removeFocusListener(this);
        if(!nameOK()){
            this.requestFocusInWindow();
        }
        this.addFocusListener(this);
    }
    
    @Override
    public void focusGained(FocusEvent event){
        // Do nothing.
    }
    
    @Override
    public void focusLost(FocusEvent event){
        if(!nameOK()){
            this.requestFocusInWindow();
        }
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
