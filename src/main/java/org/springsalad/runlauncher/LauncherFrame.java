/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.runlauncher;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;

import org.springsalad.dataprocessor.DataGUI;
import org.springsalad.dataprocessor.DataProcessor;
import org.springsalad.helpersetup.PopUp;
import org.springsalad.clusteranalysis.DataGUI2;

public class LauncherFrame extends JFrame implements ActionListener,
                                                            WindowListener {
    
    private final SimulationManager sm;
    private SimulationTablePanel tablePanel;
    
    private JButton addSimButton;
    private JButton editSimButton;
    private JButton deleteSimButton;
    
    private JButton runSimButton;
    private JButton viewProgressButton;
    private JButton abortSimButton;
    
    private JButton viewSystemButton;
    private JButton viewDataButton;
    private JButton recalculateStatsButton;
    
    private final String title;
    
    public LauncherFrame(SimulationManager sm, String title){
        super(title);
        this.title = title;
        this.sm = sm;
        initGUI();
        
        addSimButton.addActionListener(this);
        editSimButton.addActionListener(this);
        deleteSimButton.addActionListener(this);
        runSimButton.addActionListener(this);
        viewProgressButton.addActionListener(this);
        abortSimButton.addActionListener(this);
        viewSystemButton.addActionListener(this);
        viewDataButton.addActionListener(this);
        recalculateStatsButton.addActionListener(this);
    }
    
    private void initGUI(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());
        tablePanel = new SimulationTablePanel(sm);
        c.add(tablePanel, "Center");
        c.add(makeTopPanel(), "North");
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setSize(c.getPreferredSize().width + 30,400);
        this.setVisible(true);
        // </editor-fold>
    }
    
    private JPanel makeTopPanel(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        addSimButton = new JButton("Add Simulation");
        editSimButton = new JButton("Edit Simulation");
        deleteSimButton = new JButton("Delete Simulation");
        runSimButton = new JButton("Run Simulation");
        viewProgressButton = new JButton("View Progress");
        abortSimButton = new JButton("Abort Simulation");
        viewSystemButton = new JButton("View 3D Snapshots");
        viewDataButton = new JButton("View Data");
        recalculateStatsButton = new JButton("Recalculate stats");
        
        JPanel p0 = new JPanel();
        p0.add(addSimButton);
        p0.add(editSimButton);
        p0.add(deleteSimButton);
        
        JPanel p1 = new JPanel();
        p1.add(runSimButton);
        p1.add(viewProgressButton);
        p1.add(abortSimButton);
        
        JPanel p2 = new JPanel();
        p2.add(viewSystemButton);
        p2.add(viewDataButton);
        p2.add(recalculateStatsButton);
        
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(3,1));
        p.add(p0);
        p.add(p1);
        p.add(p2);
        return p;
        // </editor-fold>
    }
    
    private void runSimulation(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        int index = tablePanel.getSelectedRow();
        if(index != -1){
            Simulation sim = sm.getSimulation(index);
            if(!sim.isRunning()){
                if(sim.hasResults()){
                    int pick = PopUp.doubleCheck("The selected simulation has results.\n"
                            + "Are you sure you want to erase these results?");
                    if(pick == JOptionPane.OK_OPTION){
                        sim.runSimulationWithProcessBuilder();
                    }
                } else {
                    sim.runSimulationWithProcessBuilder();
                }
            } else {
                PopUp.error("The selected simulation is already running.");
            }
        } else {
            PopUp.warning("Please select a simulation to run.");
        }
        // </editor-fold>
    }
    
    /* **************  ACTION LISTENER METHOD *****************************/
    @Override
    public void actionPerformed(ActionEvent event){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JButton source = (JButton)event.getSource();
        
        if(source == addSimButton){
            sm.createNewSimulation();
            tablePanel.updateTable();
        }
        
        else if(source == editSimButton){
            int index = tablePanel.getSelectedRow();
            if(index != -1){
                Simulation sim = sm.getSimulation(index);
                EditSimulationFrame editFrame = new EditSimulationFrame(sm.getGlobal(), sim);
            } else {
                PopUp.warning("Please select a simulation to edit.");
            }
        }
        
        else if(source == deleteSimButton){
            int index = tablePanel.getSelectedRow();
            if(index != -1){
                int pick = PopUp.doubleCheck("Are you sure you want to delete the selected simulation?");
                if(pick == JOptionPane.OK_OPTION){
                    sm.removeSimulation(index);
                }
            } else {
                PopUp.warning("Please select a simulation to delete.");
            }
            tablePanel.updateTable();
        }
        
        else if(source == runSimButton){
        	runSimulation();
//            if(Simulation.ENGINE_WARMED_UP){
//                runSimulation();
//            } else {
//                WarmUpWindow win = Simulation.warmUpSimulationEngine();
//                win.addWindowListener(this);
//            }
        }
        
        else if(source == viewProgressButton){
            int index = tablePanel.getSelectedRow();
            if(index != -1){
                Simulation sim = sm.getSimulation(index);
                if(sim.isRunning()){
                    JFrame progressFrame = sim.getProgressFrame();
                    if(progressFrame != null){
                        if(!progressFrame.isVisible()){
                            progressFrame.setVisible(true);
                        } else {
                            PopUp.information("The progress panel is already showing.");
                        }
                    }
                } else {
                    PopUp.error("The selected simulation is not running.");
                }
            } else {
                PopUp.warning("Please select a simulation.");
            }
        }
        
        else if(source == abortSimButton){
            int index = tablePanel.getSelectedRow();
            if(index != -1){
                Simulation sim = sm.getSimulation(index);
                if(sim.isRunning()){
                    sim.abortSimulation();
                } else {
                    PopUp.error("The selected simulation is not running.");
                }
            } else {
                PopUp.warning("Please select a simulation to abort.");
            }
        }
        
        else if(source == viewSystemButton){
            int index = tablePanel.getSelectedRow();
            if(index != -1){
                final Simulation sim = sm.getSimulation(index);
                if(sim.isRunning() || sim.hasResults()){
                    Thread t = new Thread(new Runnable(){
                        @Override
                        public void run(){
                            org.springsalad.viewer.ViewerGUI v = new org.springsalad.viewer.ViewerGUI(title, sim);
                            try{
                                v.loadFile(0);
                            } catch(IOException ioe){
                                ioe.printStackTrace(System.out);
                            }
                        };
                    });
                    t.start();
                } else {
                    PopUp.warning("The selected simulation has no results.");
                }
            } else {
                PopUp.warning("Please select a simulation to view.");
            }
        }
        
        else if(source == viewDataButton){
            int index = tablePanel.getSelectedRow();
            if(index != -1){
                Simulation sim = sm.getSimulation(index);
                if(!sim.hasResults()){
                    PopUp.warning("Simulation does not have data.");
                } else {
                    DataGUI dataGUI = new DataGUI2(title, sim);
                }
            } else {
                PopUp.warning("Please select a simulation.");
            }
        }
        
        else if(source == recalculateStatsButton) {
        	int index = tablePanel.getSelectedRow();
        	if (index != -1) {
        		Simulation sim = sm.getSimulation(index);
        		if (sim.hasResults()) {
        			int confirm = PopUp.doubleCheck(String.format("Are you sure you want to recalculate the statistics for %s?\nAny existing stats files will be lost.",
        															sim.getSimulationName()));
        			if (confirm == JOptionPane.OK_OPTION) {
        				DataProcessor processor = sim.getDataProcessor();
        				if(!processor.hasData()){
        	                processor.grabNames();
        	            }
        				sim.calculateStatistics();
        				sim.writeFile();        			
        			}
        		}
        		else {
        			PopUp.warning("Simulation does not have data. Cannot calculate statistics.");
        		}
        	}
        	else {
        		PopUp.warning("Please select a simulation for which to recalculate the statistics.");
        	}
        }
        // </editor-fold>
    }
    
    /* *********************************************************************\
     *                  WINDOW LISTENER METHODS                            *
    \***********************************************************************/
    @Override
    public void windowIconified(WindowEvent e){}
    @Override
    public void windowDeiconified(WindowEvent e){}
    @Override
    public void windowActivated(WindowEvent e){}
    @Override
    public void windowDeactivated(WindowEvent e){}
    @Override
    public void windowOpened(WindowEvent e){}
    @Override
    public void windowClosing(WindowEvent e){}
    @Override
    public void windowClosed(WindowEvent e){
        // Right now, only called when warmup frame is closing
        Simulation.ENGINE_WARMED_UP = true;
        runSimulation();
    }
    
}
