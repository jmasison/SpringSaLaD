/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.awt.Component;
import javax.swing.*;

public class ReactionNameCellEditor extends DefaultCellEditor {
    
    public ReactionNameCellEditor(Global g, Reaction reaction){
        super(new ReactionNameTextField(g, reaction));
        ReactionNameTextField rtf = (ReactionNameTextField)this.getComponent();
        rtf.enableListening(false);
    }
    
    //Override to invoke setText on the value text field.
    @Override
    public Component getTableCellEditorComponent(JTable table,
            Object value, boolean isSelected,
            int row, int column) {
        ReactionNameTextField rtf =
            (ReactionNameTextField)super.getTableCellEditorComponent(
                table, value, isSelected, row, column);
        if(value == null){
            rtf.setText("");
        } else {
            rtf.setText(value.toString());
        }
        return rtf;
    }
    
    @Override
    public Object getCellEditorValue() {
        ReactionNameTextField rtf = (ReactionNameTextField)getComponent();
        return rtf.getText();
    }
    
    //Override to check whether the edit is valid,
    //setting the value if it is and complaining if
    //it isn't.  If it's OK for the editor to go
    //away, we need to invoke the superclass's version 
    //of this method so that everything gets cleaned up.
    @Override
    public boolean stopCellEditing() {
        ReactionNameTextField rtf = (ReactionNameTextField)getComponent();
        if (!rtf.nameOK()){
	    return false;
        }
        return super.stopCellEditing();
    }
    
}
