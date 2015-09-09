/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package langevinsetup;

import helpersetup.Fonts;
import helpersetup.IOHelp;
import helpersetup.PopUp;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import runlauncher.LauncherFrame;
import runlauncher.SimulationManager;

/**
 *
 * @author pmichalski
 */
public class MainGUI extends JFrame implements TreeSelectionListener {
    
    private final Global g;
    private final JTree tree;
    
    private final JTabbedPane systemPane = new JTabbedPane();
    
    private MoleculeEditor moleculeEditor;
    
    private DrawPanel drawPanel;
    private DrawPanelPanel drawPanelPanel;
    
    private DrawPanel3D drawPanel3D;
    private DrawPanel3DPanel drawPanel3DPanel;
    
    private final AnnotationPanel systemAnnotationPanel;
    private final AnnotationPanel moleculeAnnotationPanel;
    private final AnnotationPanel reactionAnnotationPanel;

    /**
     * Creates new form MainGUI
     */
    public MainGUI() {
        super("Langevin Dynamics System Setup: New Model");
        initComponents();
        
        g = new Global();
        tree = treePane.getTree();
        
        BoxGeometryTablePanel boxGeometryTablePanel 
                                = new BoxGeometryTablePanel(g.getBoxGeometry());
        systemPane.add(boxGeometryTablePanel);
        systemPane.setTitleAt(0, "System Geometry");
        
        SystemTimesTablePanel systemTimesTablePanel
                                = new SystemTimesTablePanel(g.getSystemTimes());
        systemPane.add(systemTimesTablePanel);
        systemPane.setTitleAt(1, "System Times");
        
        InitialConditionTablePanel initialConditionTablePanel
                                = new InitialConditionTablePanel(g);
        systemPane.add(initialConditionTablePanel);
        systemPane.setTitleAt(2, "Initial Conditions");
        
        systemAnnotationPanel = new AnnotationPanel(g.getSystemAnnotation());
        moleculeAnnotationPanel = new AnnotationPanel(null);
        reactionAnnotationPanel = new AnnotationPanel(null);
        
        tree.addTreeSelectionListener(this);
        
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    
    private void clearPanel(JPanel p){
        p.removeAll();
        p.validate();
        p.repaint();
    }
    
    
    /* *******************************************************************\
     *                    TREE SELECTION LISTENER                        *
     *  Mostly we just remove and add different panels to the splitpane. *
    \*********************************************************************/
    
    @Override
    public void valueChanged(TreeSelectionEvent event){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
        if(node != null){
            String nodeString = node.toString();
            // Look to see if we've found one of the defined strings
            switch(nodeString){
                
                case SystemTree.SYSTEM_INFORMATION:{
                    topPanel.removeAll();
                    topPanel.setLayout(new BorderLayout());
                    topPanel.add(systemPane);
                    topPanel.validate();
                    topPanel.repaint();
                    
                    clearPanel(bottomPanel);
                    break;
                }
                case SystemTree.MOLECULES:{
                    topPanel.removeAll();
                    topPanel.setLayout(new BorderLayout());
                    topPanel.add(new ListPanel(ListPanel.MOLECULE, g, treePane, moleculeAnnotationPanel));
                    topPanel.validate();
                    topPanel.repaint();
                    
                    bottomPanel.removeAll();
                    bottomPanel.setLayout(new BorderLayout());
                    bottomPanel.add(moleculeAnnotationPanel);
                    bottomPanel.validate();
                    bottomPanel.repaint();
                    break;
                }
                case SystemTree.CREATION_DECAY_REACTIONS:{
                    topPanel.removeAll();
                    topPanel.setLayout(new BorderLayout());
                    topPanel.add(new DecayReactionTablePanel(g));
                    topPanel.validate();
                    topPanel.repaint();
                    
                    clearPanel(bottomPanel);
                    break;
                }
                
                case SystemTree.TRANSITION_REACTIONS:{
                    topPanel.removeAll();
                    topPanel.setLayout(new BorderLayout());
                    topPanel.add(new ListPanel(ListPanel.TRANSITION_REACTION, g, treePane, reactionAnnotationPanel));
                    topPanel.validate();
                    topPanel.repaint();
                    
                    bottomPanel.removeAll();
                    bottomPanel.setLayout(new BorderLayout());
                    bottomPanel.add(reactionAnnotationPanel);
                    bottomPanel.validate();
                    bottomPanel.repaint();
                    break;
                }
                
                case SystemTree.ALLOSTERIC_REACTIONS:{
                    topPanel.removeAll();
                    topPanel.setLayout(new BorderLayout());
                    topPanel.add(new ListPanel(ListPanel.ALLOSTERIC_REACTION, g, treePane, reactionAnnotationPanel));
                    topPanel.validate();
                    topPanel.repaint();
                    
                    bottomPanel.removeAll();
                    bottomPanel.setLayout(new BorderLayout());
                    bottomPanel.add(reactionAnnotationPanel);
                    bottomPanel.validate();
                    bottomPanel.repaint();
                    break;
                }
                
                case SystemTree.BINDING_REACTIONS:{
                    topPanel.removeAll();
                    topPanel.setLayout(new BorderLayout());
                    topPanel.add(new ListPanel(ListPanel.BINDING_REACTION, g, treePane, reactionAnnotationPanel));
                    topPanel.validate();
                    topPanel.repaint();
                    
                    bottomPanel.removeAll();
                    bottomPanel.setLayout(new BorderLayout());
                    bottomPanel.add(reactionAnnotationPanel);
                    bottomPanel.validate();
                    bottomPanel.repaint();
                    break;
                }
                
                case SystemTree.MOLECULE_COUNTERS:{
                    topPanel.removeAll();
                    topPanel.setLayout(new BorderLayout());
                    topPanel.add(new MoleculeCounterTablePanel(g));
                    topPanel.validate();
                    topPanel.repaint();
                    
                    clearPanel(bottomPanel);
                    break;
                }
                
                case SystemTree.STATE_COUNTERS:{
                    topPanel.removeAll();
                    topPanel.setLayout(new BorderLayout());
                    topPanel.add(new StateCounterTablePanel(g));
                    topPanel.validate();
                    topPanel.repaint();
                    
                    clearPanel(bottomPanel);
                    break;
                }
                
                case SystemTree.BOND_COUNTERS:{
                    topPanel.removeAll();
                    topPanel.setLayout(new BorderLayout());
                    topPanel.add(new BondCounterTablePanel(g));
                    topPanel.validate();
                    topPanel.repaint();
                    
                    clearPanel(bottomPanel);
                    break;
                }
                
                case SystemTree.SITE_PROPERTY_COUNTERS:{
                    topPanel.removeAll();
                    topPanel.setLayout(new BorderLayout());
                    topPanel.add(new SitePropertyCounterTablePanel(g));
                    topPanel.validate();
                    topPanel.repaint();
                    
                    clearPanel(bottomPanel);
                    break;
                }
                
                case SystemTree.CLUSTER_COUNTERS:{
                    topPanel.removeAll();
                    topPanel.setLayout(new BorderLayout());
                    topPanel.add(new ClusterCounterTablePanel(g));
                    topPanel.validate();
                    topPanel.repaint();
                    
                    clearPanel(bottomPanel);
                    break;
                }
                default:{
                    clearPanel(topPanel);
                    clearPanel(bottomPanel);
                }
            }
            
            // The switch has cleared the panels at this point. 
            // Look to see if we found a specific molecule
            Molecule molecule = g.getMolecule(nodeString);
            if(molecule != null){
                topPanel.setLayout(new BorderLayout());
                moleculeEditor = new MoleculeEditor(molecule, g);
                JPanel p = new JPanel();
                p.add(moleculeEditor, "Center");
                topPanel.add(p);
                topPanel.validate();
                topPanel.repaint();
                
                switchTo2D();
            }
            
            // Look to see if we've found a transition reaction
            TransitionReaction transitionReaction = g.getTransitionReaction(nodeString);
            if(transitionReaction != null){
                topPanel.setLayout(new BorderLayout());
                topPanel.add(new TransitionReactionTopPanel(g, transitionReaction), "Center");
                topPanel.validate();
                topPanel.repaint();
                
                bottomPanel.setLayout(new BorderLayout());
                bottomPanel.add(new TransitionReactionBottomPanel(g, transitionReaction), "Center");
                bottomPanel.validate();
                bottomPanel.repaint();
            }
            
            // Look to see if we've found an allosteric reaction
            AllostericReaction allostericReaction = g.getAllostericReaction(nodeString);
            if(allostericReaction != null){
                topPanel.setLayout(new BorderLayout());
                topPanel.add(new AllostericReactionTopPanel(g, allostericReaction), "Center");
                topPanel.validate();
                topPanel.repaint();
                
                bottomPanel.setLayout(new BorderLayout());
                bottomPanel.add(new AllostericReactionBottomPanel(g, allostericReaction), "Center");
                bottomPanel.validate();
                bottomPanel.repaint();
            }
            
            // Look to see if we found a binding reaction
            BindingReaction reaction = g.getBindingReaction(nodeString);
            if(reaction != null){
                topPanel.setLayout(new BorderLayout());
                topPanel.add(new BindingReactionTopPanel(g, reaction), "Center");
                topPanel.validate();
                topPanel.repaint();
                
                bottomPanel.setLayout(new BorderLayout());
                bottomPanel.add(new BindingReactionBottomPanel(g,reaction), "Center");
                bottomPanel.validate();
                bottomPanel.repaint();
            }
            
            // Found root node?
            if(node == treePane.getRootNode()){
                topPanel.setLayout(new BorderLayout());
                JLabel sLabel = new JLabel(g.getSystemName(), JLabel.CENTER);
                sLabel.setFont(Fonts.TITLEFONT);
                topPanel.add(sLabel, "North");
                topPanel.add(systemAnnotationPanel,"Center");
                systemAnnotationPanel.setAnnotation(g.getSystemAnnotation());
                topPanel.validate();
                topPanel.repaint();
            }
        }
        // </editor-fold>
    }
    
    /************ SWITCH BETWEEN 2D AND 3D MOLECULE DRAW PANELS *********/
    private void switchTo2D(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Molecule molecule = moleculeEditor.getMolecule();
        
        if(molecule != null){
            if(molecule.is3D()){
                PopUp.error("The molecule is no longer 2D and must be edited in the 3D window.");
                switchTo3D();
            } else {
                bottomPanel.removeAll();
                bottomPanel.setLayout(new BorderLayout());
                drawPanel = new DrawPanel(molecule);
                drawPanelPanel = new DrawPanelPanel(drawPanel);
                bottomPanel.add(drawPanelPanel, "Center");
                bottomPanel.add(editorDimensionPanel, "South");
                bottomPanel.validate();
                bottomPanel.repaint();

                moleculeEditor.addMoleculeSelectionListener(drawPanel);
                moleculeEditor.removeMoleculeSelectionListener(drawPanel3D);
                drawPanel.addMoleculeSelectionListener(moleculeEditor);
                
                edit2D.setEnabled(false);
                edit3D.setEnabled(true);
            }
        }
        // </editor-fold>
    }
    
    private void switchTo3D(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        tree.setEnabled(false);
        Molecule molecule = moleculeEditor.getMolecule();
        if(molecule != null){
            if(drawPanelPanel != null){
                if(drawPanelPanel.getParent() == bottomPanel){
                    bottomPanel.remove(drawPanelPanel);
                }
            }
            JPanel panel = new JPanel();
            panel.setBackground(Color.GRAY);
            bottomPanel.add(panel, "Center");
            bottomPanel.validate();
            bottomPanel.repaint();
            
            edit2D.setEnabled(false);
            edit3D.setEnabled(false);
            
            JFrame frame = new JFrame("3D Molecule Editor");
            
            drawPanel3D = new DrawPanel3D(molecule);
            drawPanel3DPanel = new DrawPanel3DPanel(drawPanel3D);
            
            Container c = frame.getContentPane();
            c.add(drawPanel3DPanel, "Center");
            
            drawPanel3D.addMoleculeSelectionListener(moleculeEditor);
            moleculeEditor.addMoleculeSelectionListener(drawPanel3D);
            moleculeEditor.removeMoleculeSelectionListener(drawPanel);
            
            frame.addWindowListener(new WindowAdapter(){
                @Override
                public void windowClosed(WindowEvent event){
                    tree.setEnabled(true);
                    edit3D.setEnabled(true);
                    edit2D.setEnabled(true);
                }
            });
            
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setSize(700,400);
            frame.setVisible(true);
        }
        // </editor-fold>
    }
    
      
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        editorDimensionPanel = new javax.swing.JPanel();
        edit2D = new javax.swing.JButton();
        edit3D = new javax.swing.JButton();
        verticalSplitPane = new javax.swing.JSplitPane();
        horizontalSplitPane = new javax.swing.JSplitPane();
        topPanel = new javax.swing.JPanel();
        bottomPanel = new javax.swing.JPanel();
        treePane = new langevinsetup.SystemTree();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        saveItem = new javax.swing.JMenuItem();
        saveAsItem = new javax.swing.JMenuItem();
        loadItem = new javax.swing.JMenuItem();
        closeItem = new javax.swing.JMenuItem();
        exitItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        simulationManagerItem = new javax.swing.JMenuItem();

        edit2D.setText("Edit in 2D");
        edit2D.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit2DActionPerformed(evt);
            }
        });
        editorDimensionPanel.add(edit2D);

        edit3D.setText("Edit in 3D");
        edit3D.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit3DActionPerformed(evt);
            }
        });
        editorDimensionPanel.add(edit3D);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        verticalSplitPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        verticalSplitPane.setDividerLocation(200);

        horizontalSplitPane.setDividerLocation(350);
        horizontalSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        horizontalSplitPane.setTopComponent(topPanel);

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 863, Short.MAX_VALUE)
        );
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 264, Short.MAX_VALUE)
        );

        horizontalSplitPane.setRightComponent(bottomPanel);

        verticalSplitPane.setRightComponent(horizontalSplitPane);
        verticalSplitPane.setLeftComponent(treePane);

        fileMenu.setText("File");

        saveItem.setText("Save");
        saveItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveItem);

        saveAsItem.setText("Save As");
        saveAsItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsItem);

        loadItem.setText("Load");
        loadItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadItemActionPerformed(evt);
            }
        });
        fileMenu.add(loadItem);

        closeItem.setText("Close");
        closeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeItemActionPerformed(evt);
            }
        });
        fileMenu.add(closeItem);

        exitItem.setText("Exit");
        exitItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitItem);

        jMenuBar1.add(fileMenu);

        jMenu1.setText("Tools");

        simulationManagerItem.setText("Simulation Manager");
        simulationManagerItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simulationManagerItemActionPerformed(evt);
            }
        });
        jMenu1.add(simulationManagerItem);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(verticalSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1071, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(verticalSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveItemActionPerformed
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        // Check to see if each molecule is fully connected
        if(g.moleculesFullyConnected() && g.bindingReactionsWellDefined()
                && g.transitionReactionsWellDefined()
                && g.membraneMoleculesHaveAnchors()){
            // Check to see if we've set the file yet
            if(g.getFile() == null){
                JFileChooser jfc = new JFileChooser(g.getDefaultFolder());
                int ret = jfc.showSaveDialog(null);
                if(ret == JFileChooser.APPROVE_OPTION){
                    g.setFile(IOHelp.setFileType(jfc.getSelectedFile(), "txt"));
                    String parent = g.getFile().getParent();
                    g.setDefaultFolder(new File(parent));
                    g.writeFile();
                    this.setTitle(g.getSystemName());
                    treePane.setTitle(g.getSystemName());
                }
            } else {
                g.writeFile();
            }
        }
        // </editor-fold>
    }//GEN-LAST:event_saveItemActionPerformed

    private void saveAsItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsItemActionPerformed
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(g.moleculesFullyConnected() && g.bindingReactionsWellDefined()
                && g.transitionReactionsWellDefined()
                && g.membraneMoleculesHaveAnchors()){
            JFileChooser jfc = new JFileChooser(g.getDefaultFolder());
            if(g.getFile() != null){
                jfc.setSelectedFile(g.getFile());
            }
            int ret = jfc.showSaveDialog(null);
            if(ret == JFileChooser.APPROVE_OPTION){
                g.setFile(IOHelp.setFileType(jfc.getSelectedFile(), "txt"));
                String parent = g.getFile().getParent();
                g.setDefaultFolder(new File(parent));
                g.writeFile();
                this.setTitle(g.getSystemName());
                treePane.setTitle(g.getSystemName());
            }
        }
        // </editor-fold>
    }//GEN-LAST:event_saveAsItemActionPerformed

    private void loadItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadItemActionPerformed
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JFileChooser jfc = new JFileChooser(g.getDefaultFolder());
        int returnValue = jfc.showOpenDialog(this);
        if(returnValue == JFileChooser.APPROVE_OPTION){
            // If the previous file wasn't closed, then close it now
            if(g.getFile() != null){
                this.closeItemActionPerformed(evt);
            }
            g.setFile(IOHelp.setFileType(jfc.getSelectedFile(), "txt"));
            String parent = g.getFile().getParent();
            g.setDefaultFolder(new File(parent));
            g.loadFile();
            this.setTitle(g.getSystemName());
            treePane.setTitle(g.getSystemName());
            for(Molecule molecule : g.getMolecules()){
                treePane.addMolecule(molecule);
            }
            for(TransitionReaction reaction : g.getTransitionReactions()){
                treePane.addTransitionReaction(reaction);
            }
            for(AllostericReaction reaction : g.getAllostericReactions()){
                treePane.addAllostericReaction(reaction);
            }
            for(BindingReaction reaction : g.getBindingReactions()){
                treePane.addBindingReaction(reaction);
            }
            // First select the allosteric reaction node, then go to the root
            // node. This forces the root node to throw a selection event even
            // if it was originally selected. 
            treePane.selectNode(SystemTree.ALLOSTERIC_REACTIONS);
            treePane.selectRootNode();
        }
        // </editor-fold>
    }//GEN-LAST:event_loadItemActionPerformed

    private void closeItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeItemActionPerformed
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        for(Molecule molecule : g.getMolecules()){
            treePane.removeMolecule(molecule);
        }
        for(TransitionReaction reaction : g.getTransitionReactions()){
            treePane.removeTransitionReaction(reaction);
        }
        for(AllostericReaction reaction : g.getAllostericReactions()){
            treePane.removeAllostericReaction(reaction);
        }
        for(BindingReaction reaction : g.getBindingReactions()){
            treePane.removeBindingReaction(reaction);
        }
        g.reset();
        topPanel.removeAll();
        bottomPanel.removeAll();
        this.setTitle(SystemTree.NEW_MODEL);
        treePane.setTitle(SystemTree.NEW_MODEL);
        // If we were on the root node, we need to move away and then back to 
        // force a selection evento occur.  We'll jump to the allosteric 
        // reactions tab, then to the root node. 
        treePane.selectNode(SystemTree.ALLOSTERIC_REACTIONS);
        treePane.selectRootNode();
        this.validate();
        this.repaint();
        // </editor-fold>
    }//GEN-LAST:event_closeItemActionPerformed

    private void exitItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitItemActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        this.dispose();
        System.exit(0);
    }//GEN-LAST:event_exitItemActionPerformed

    private void edit2DActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit2DActionPerformed
        // TODO add your handling code here:
        switchTo2D();
    }//GEN-LAST:event_edit2DActionPerformed

    private void edit3DActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit3DActionPerformed
        // TODO add your handling code here:
        switchTo3D();
    }//GEN-LAST:event_edit3DActionPerformed

    private void simulationManagerItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simulationManagerItemActionPerformed
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(g.getFile() == null){
            PopUp.error("Please either load a completed biomodel or save the current biomodel.");
        } else {
            SimulationManager sm = new SimulationManager(g, g.getFile());
            LauncherFrame lfr = new LauncherFrame(sm, g.getSystemName());
        }
        // </editor-fold>
    }//GEN-LAST:event_simulationManagerItemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
        new MainGUI();

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JMenuItem closeItem;
    private javax.swing.JButton edit2D;
    private javax.swing.JButton edit3D;
    private javax.swing.JPanel editorDimensionPanel;
    private javax.swing.JMenuItem exitItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JSplitPane horizontalSplitPane;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem loadItem;
    private javax.swing.JMenuItem saveAsItem;
    private javax.swing.JMenuItem saveItem;
    private javax.swing.JMenuItem simulationManagerItem;
    private javax.swing.JPanel topPanel;
    private langevinsetup.SystemTree treePane;
    private javax.swing.JSplitPane verticalSplitPane;
    // End of variables declaration//GEN-END:variables

}