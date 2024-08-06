
package org.springsalad.langevinsetup;

import javax.swing.*;

import org.springsalad.helpersetup.Constraints;

import java.awt.*;

public class InitialPositionFrame extends JFrame {
    
    private final InitialCondition ic;
    
    public InitialPositionFrame(InitialCondition ic){
        super("Set Initial Positions");
        this.ic = ic;
        this.getContentPane().setLayout(new BorderLayout());
        buildNorthPanel();
        buildCenterPanel();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }
    
    private void buildNorthPanel(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JTextArea warningArea = new JTextArea(10,30);
        warningArea.setWrapStyleWord(true);
        warningArea.setLineWrap(true);
        warningArea.setEditable(false);
        warningArea.setBackground(Color.LIGHT_GRAY);
        warningArea.setForeground(Color.BLACK);
        warningArea.setFont(org.springsalad.helpersetup.Fonts.LABELFONT);
        warningArea.setMargin(new Insets(10,10,10,10));
        warningArea.setText("WARNING: This part of the code does minimal error"
                + " checking. It will not check for site overlap, nor"
                + " does it check if part of the molecule will be initially"
                + " positioned outside the system space. Any molecules that"
                + " cannot be placed with the given initial positions will be"
                + " placed randomly.\n"
                + " The system coordinates are defined so that the x coordinate"
                + " goes from -x_length/2 to x_length/2, and similarly for y."
                + " The z coordinate goes from -z_extracellular to z_intracellular."
                + "\n\n"
                + "USAGE: The coordinates here will define the initial position"
                + " of Site \"0\"."
                + "\n\n"
                + "UNITS: All lengths are in nanometers.");
        JPanel warningPanel = new JPanel();
        warningPanel.add(warningArea);
        // warningPanel.setBackground(Color.RED);
        this.getContentPane().add(warningPanel, "North");
        // </editor-fold>
    }
    
    private void buildCenterPanel(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JTable table = new JTable(new InitialPositionTableModel(ic));
        table.setFillsViewportHeight(true);
        table.setDefaultEditor(Double.class, new ValueCellEditor(ValueTextField.DOUBLE, Constraints.NO_CONSTRAINT, false));
        JScrollPane scrollPane = new JScrollPane(table);
        
        this.getContentPane().add(scrollPane,"Center");
        
        // </editor-fold>
    }

    
}
