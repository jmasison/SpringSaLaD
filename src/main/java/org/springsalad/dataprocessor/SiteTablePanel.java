package org.springsalad.dataprocessor;

import java.awt.*;
import javax.swing.*;

import org.springsalad.helpersetup.Fonts;

public class SiteTablePanel extends JPanel{
    
    private final SiteFreeBoundTableModel freeModel;
    private final SiteStatesTableModel stateModel;
    private final SiteBondsTableModel bondModel;
    
    public SiteTablePanel(SiteFreeBoundTableModel freeModel,
            SiteStatesTableModel stateModel, SiteBondsTableModel bondModel){
        this.freeModel = freeModel;
        this.stateModel = stateModel;
        this.bondModel = bondModel;
        
        this.setLayout(new BorderLayout());
        this.add(buildEnclosingPanel(), "Center");
    }
    
    /* ***************** CONSTRUCT PANELS ********************************/
    
    private JPanel buildFreePanel(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JTable freeTable = new JTable(freeModel);
        freeTable.setFillsViewportHeight(true);
        
        JScrollPane pane = new JScrollPane(freeTable);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        pane.setPreferredSize(new Dimension(pane.getPreferredSize().width, 300));
        
        JLabel label = new JLabel("Free/Bound", JLabel.CENTER);
        label.setFont(Fonts.SUBTITLEFONT);
        
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(label, "North");
        p.add(pane, "Center");
        return p;
        // </editor-fold>
    }
    
    private JPanel buildStatePanel(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JTable stateTable = new JTable(stateModel);
        stateTable.setFillsViewportHeight(true);
        
        JScrollPane pane = new JScrollPane(stateTable);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        pane.setPreferredSize(new Dimension(pane.getPreferredSize().width, 300));
        
        JLabel label = new JLabel("State Data", JLabel.CENTER);
        label.setFont(Fonts.SUBTITLEFONT);
        
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(label, "North");
        p.add(pane, "Center");
        return p;
        // </editor-fold>
    }
    
    private JPanel buildBondPanel(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JTable bondTable = new JTable(bondModel);
        bondTable.setFillsViewportHeight(true);
        
        JScrollPane pane = new JScrollPane(bondTable);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        pane.setPreferredSize(new Dimension(pane.getPreferredSize().width, 300));
        
        JLabel label = new JLabel("Bond Data", JLabel.CENTER);
        label.setFont(Fonts.SUBTITLEFONT);
        
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(label, "North");
        p.add(pane, "Center");
        return p;
        // </editor-fold>
    }
    
    private JPanel buildEnclosingPanel(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JPanel p0 = new JPanel();
        p0.setLayout(new GridLayout(3,1));
        p0.add(buildFreePanel());
        p0.add(buildStatePanel());
        p0.add(buildBondPanel());
        
        JScrollPane pane = new JScrollPane(p0);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(pane, "Center");
        return p;
        // </editor-fold>
    }
    
}
