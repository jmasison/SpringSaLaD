package org.springsalad.langevinsetup;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JPanel;

public class ClusterCounterTablePanel extends JPanel{
    public ClusterCounterTablePanel(Global g){
        this.setLayout(new BorderLayout());
        
        JTable table = new JTable(new ClusterCounterTableModel(g));
        table.setFillsViewportHeight(true);
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        this.add(scrollPane,"Center");
    }
}
