/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;

public class SitePropertyCounterTablePanel extends JPanel{
    
    public SitePropertyCounterTablePanel(Global g){
        this.setLayout(new BorderLayout());
        
        JTable table = new JTable(new SitePropertyCounterTableModel(g));
        table.setFillsViewportHeight(true);
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        this.add(scrollPane,"Center");
    }
    
}
