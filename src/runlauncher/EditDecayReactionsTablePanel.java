/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package runlauncher;

import helpersetup.Constraints;
import java.awt.BorderLayout;
import javax.swing.*;
import langevinsetup.Global;
import langevinsetup.ValueCellEditor;
import langevinsetup.ValueTextField;

public class EditDecayReactionsTablePanel extends JPanel {
    
    public EditDecayReactionsTablePanel(Global g, Simulation simulation){
        this.setLayout(new BorderLayout());
        
        JTable table = new JTable(new EditDecayReactionsTableModel(g, simulation));
        table.setFillsViewportHeight(true);
        table.setDefaultEditor(Double.class, new ValueCellEditor(ValueTextField.DOUBLE, Constraints.NONNEGATIVE, true));
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        this.add(scrollPane, "Center");
        
    }
}
