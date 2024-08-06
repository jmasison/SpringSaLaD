/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.awt.*;
import javax.swing.*;

public class BondCounterTablePanel extends JPanel {
    
    public BondCounterTablePanel(Global g){
        this.setLayout(new BorderLayout());
        
        JTable table = new JTable(new BondCounterTableModel(g));
        table.setFillsViewportHeight(true);
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        this.add(scrollPane, "Center");
        
    }
    
}
