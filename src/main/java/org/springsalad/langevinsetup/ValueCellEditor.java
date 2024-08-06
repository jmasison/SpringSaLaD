/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.awt.Component;
import javax.swing.*;

public class ValueCellEditor extends DefaultCellEditor {
    
    private final String type;
    
    public ValueCellEditor(String type, String constraint, boolean allowEmpty){
        super(new ValueTextField("0",10, type, constraint, allowEmpty));
        ValueTextField vtf = (ValueTextField)this.getComponent();
        // In this context the table calls the value text field's setData()
        // method, so the vtf itself can stop listening for events.
        vtf.enableListening(false);
        this.type = type;
    }
    
    //Override to invoke setText on the value text field.
    @Override
    public Component getTableCellEditorComponent(JTable table,
            Object value, boolean isSelected,
            int row, int column) {
        ValueTextField vtf =
            (ValueTextField)super.getTableCellEditorComponent(
                table, value, isSelected, row, column);
        if(value == null){
            vtf.setText("");
        } else {
            vtf.setText(value.toString());
        }
        vtf.setData();
        return vtf;
    }
    
    //Override to ensure that the value remains an Integer or Double
    @Override
    public Object getCellEditorValue() {
        ValueTextField vtf = (ValueTextField)getComponent();
        switch (type) {
            case ValueTextField.INTEGER:
                return vtf.getInteger();
            case ValueTextField.DOUBLE:
                return vtf.getDouble();
            default:
                return null;
        }
    }
    
    //Override to check whether the edit is valid,
    //setting the value if it is and complaining if
    //it isn't.  If it's OK for the editor to go
    //away, we need to invoke the superclass's version 
    //of this method so that everything gets cleaned up.
    @Override
    public boolean stopCellEditing() {
        ValueTextField vtf = (ValueTextField)getComponent();
        if (!vtf.setData()){
	    return false;
        }
        return super.stopCellEditing();
    }
    
    
}
