/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.springsalad.helpersetup.Fonts;
import org.springsalad.helpersetup.IOHelp;
import org.springsalad.helpersetup.PopUp;
import org.springsalad.jmolintegration.Integration;
import org.springsalad.runlauncher.LauncherFrame;
import org.springsalad.runlauncher.SimulationManager;

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
    
	private Image appImage = null;

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
                PopUp.information("The molecule must be edited in the 3D window.");
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
            System.out.println("Main");
           
            //view in jmol
            Integration integration = new Integration(molecule.getFilename());
            // integrate
            drawPanel3D.setintListener(e -> {
            	//update integration knowledge of rotation but don't notify drawpanel (false)
            	integration.rotationOccurred(new RotationUpdateEvent(((RotationUpdateEvent) e).getM3(), false));
            });
            
            integration.setdp3dListener(e -> {
            	//TODO Fill
            });
            
            frame.addWindowListener(new WindowAdapter(){
                @Override
                public void windowClosed(WindowEvent event){
                    tree.setEnabled(true);
                    edit3D.setEnabled(true);
                    edit2D.setEnabled(false);
                    
                    bottomPanel.removeAll();
                    bottomPanel.setLayout(new BorderLayout());
                    
                    JPanel panel = new JPanel();
                    panel.setBackground(Color.GRAY);
                    bottomPanel.add(panel, "Center");
                    
                    bottomPanel.add(editorDimensionPanel, "South");
                    bottomPanel.validate();
                    bottomPanel.repaint();
                    
                    event.getWindow().dispose();
                    
                    if(!drawPanel3D.getOverlaps().isEmpty()){
                    	HashMap<Site, Integer> overlapMap = drawPanel3D.getOverlaps();
                    	String message = "Please re-open editor and resolve overlap:";
                    	
                    	for(Site s: overlapMap.keySet()) {
                    		message = message + " Site " + s.getIndex() + "("+ overlapMap.get(s)+")";
                    	}
                    	PopUp.information(message);
                    }
                    
                    // close jmol window
                    integration.closeWindow();
                    System.out.println("closed");
                }
            });
            
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setSize(700,700);
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
        treePane = new org.springsalad.langevinsetup.SystemTree();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        saveItem = new javax.swing.JMenuItem();
        saveAsItem = new javax.swing.JMenuItem();
        loadItem = new javax.swing.JMenuItem();
        closeItem = new javax.swing.JMenuItem();
        exitItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuHelp = new javax.swing.JMenu();
        simulationManagerItem = new javax.swing.JMenuItem();
        websiteItem = new javax.swing.JMenuItem();
        aboutItem = new javax.swing.JMenuItem();
        PDBAdderItem = new javax.swing.JMenuItem();
        
        URL appIconUrl = getClass().getResource("/icons/springSaLaD.png");
        if(appIconUrl != null) {
        	Toolkit kit = Toolkit.getDefaultToolkit();
        	appImage = kit.createImage(appIconUrl);
        	setIconImage(appImage);
        }
        
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
        
        //
        PDBAdderItem.setText("Add PDB from file");
        PDBAdderItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	PDBAdderItemActionPerformed(evt);
            }
        });
        fileMenu.add(PDBAdderItem);
        //
        
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

        
        jMenuHelp.setText("Help");
        websiteItem.setText("SpringSaLaD Website");
        websiteItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                websiteItemActionPerformed(evt);
            }
        });
        aboutItem.setText("About SpringSaLaD");
        aboutItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutItemActionPerformed(evt);
            }
        });
        jMenuHelp.add(websiteItem);
        jMenuHelp.add(aboutItem);
        jMenuBar1.add(jMenuHelp);

        
//        private javax.swing.JMenuItem aboutItem;
//        private javax.swing.JMenuItem websiteItem;



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
                	File named = IOHelp.setFileType(jfc.getSelectedFile(), "txt");
                	
                    String child = named.getName();
                    String parent = named.getParent();
                  
                    JPanel myPanel = new JPanel();
                	myPanel.add(new JLabel("Do you want to create a project folder?\n(Press no if saving an already existing model)"));
                    int n = JOptionPane.showConfirmDialog(null, myPanel, "Create Folder Option", JOptionPane.YES_NO_OPTION);
                	if(n == JOptionPane.YES_OPTION){	
                		parent = parent + File.separator + child.substring(0, child.length() - 4);
                       	new File(parent).mkdir();
                       	File f = new File(parent + File.separator + child);
                        g.setFile(f);
                        
                        File targetDir = new File(parent + File.separator + "structure_files");
                        if (!targetDir.exists() || !targetDir.isDirectory()) {
            				targetDir.mkdir();
            			}
                       	g.copyPDBtoNewLocation();
                	} else {
                		File f = new File(parent + File.separator + child);
                        g.setFile(f);
                	}
                                                        
                    g.setDefaultFolder(new File(parent));
                    g.writeFile();
                    this.setTitle(g.getSystemName());
                    treePane.setTitle(g.getSystemName());
                }
            } else {
            	File targetDir = new File(g.getFile().getParent() + File.separator + "structure_files");
                if (targetDir.exists() && targetDir.isDirectory()) {
    				g.copyPDBtoNewLocation();
    			}
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
                File named = IOHelp.setFileType(jfc.getSelectedFile(), "txt");
     
                String child = named.getName();
                String parent = named.getParent();
                
                //ask user if they want to create a model folder to store all files for the model 
                // 	this utility should only be used the first time a model is saved
                JPanel myPanel = new JPanel();
            	myPanel.add(new JLabel("Do you want to create a project folder?\n(Press no if saving an already existing model)"));
                int n = JOptionPane.showConfirmDialog(null, myPanel, "Create Folder Option", JOptionPane.YES_NO_OPTION);
            	if(n == JOptionPane.YES_OPTION){
            		parent = parent + File.separator + child.substring(0, child.length() - 4);
                   	new File(parent).mkdir();
                   	File f = new File(parent + File.separator + child);
                    g.setFile(f);
                    
                    File targetDir = new File(parent + File.separator + "structure_files");
                    if (!targetDir.exists() || !targetDir.isDirectory()) {
        				targetDir.mkdir();
        			}
                   	g.copyPDBtoNewLocation();
            	} else {
            		File f = new File(parent + File.separator + child);
                    g.setFile(f);
            	}
                                               
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
    
    private static boolean fileFormatOkay(String filename){
		if(filename.length() > 4){
			if(filename.charAt(filename.length()-4) == '.'){
				String end = filename.substring(filename.length() - 3);
				if(end.equals("pdb") || end.equals("cif")){
					return true;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
		return false;
	}
    
    private void PDBAdderItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadItemActionPerformed
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JFileChooser jfc = new JFileChooser(g.getDefaultFolder());
        int returnValue = jfc.showOpenDialog(this);
        if(returnValue == JFileChooser.APPROVE_OPTION){
        	//check file format is okay
        	if(fileFormatOkay(jfc.getSelectedFile().getName())){
        		            
	        	File pdbFile = new File(jfc.getSelectedFile().getParentFile(), jfc.getSelectedFile().getName());
	        	
	        	String k= JOptionPane.showInputDialog("Please input number of sites wanted: ");
	            int goodK = 1;
	            boolean kIsInt = false;
	            while(!kIsInt){
	            	try{
	            		goodK = Integer.parseInt(k);
	            		kIsInt = true;
	            	}catch (Exception e) {
	            		k= JOptionPane.showInputDialog("Not a number. Please input number of sites wanted: ");
	            		kIsInt = false; 
	            	}
	            }
	            
	            
	            JTextField xField = new JTextField(5);
	            JTextField yField = new JTextField(5);
	            JTextField zField = new JTextField(5);
	            boolean noMore = false;
	            int numFixed = 0;           
	            
	            ArrayList<AtomPDB> fixedCenters = new ArrayList<>();
	            
	            while(numFixed < goodK && !noMore){
	            	JPanel myPanel = new JPanel();
	            	myPanel.add(new JLabel("Press \"OK\" to confirm or \"Cancel\" to finish.   "));
	            	myPanel.add(new JLabel("x:"));
	            	myPanel.add(xField);
	            	myPanel.add(Box.createHorizontalStrut(15)); // a spacer
	            	myPanel.add(new JLabel("y:"));
	            	myPanel.add(yField);
	            	myPanel.add(Box.createHorizontalStrut(15)); // a spacer
	            	myPanel.add(new JLabel("z:"));
	            	myPanel.add(zField);
	            	
	            	int n = JOptionPane.showConfirmDialog(null, myPanel, 
	            			"Enter fixed site values", JOptionPane.OK_CANCEL_OPTION);
	            	
	            	if(n == JOptionPane.CANCEL_OPTION){
	            		noMore = true;
	            	}else{
	            	//error handling if ok is hit and x,y or z is bad or missing
	            		try{
	            			fixedCenters.add(new AtomPDB(-1, 
			            			Double.parseDouble(xField.getText()), 
			            			Double.parseDouble(yField.getText()),
			            			Double.parseDouble(zField.getText())));
	            			
	            			numFixed++;
		            	}catch (Exception e) {
		            		PopUp.information("Could not parse center, ignoring x,y,z values");          
		            	}
		            	
		            	xField.setText("");
		            	yField.setText("");
		            	zField.setText("");	
	            	}
	            }
	            
	         	//open and process file
	            System.out.println("--- begin molecule construction...");
	            PDBHandler pdb = new PDBHandler(pdbFile.toString(), goodK, fixedCenters);
	            System.out.println("--- finished.");
	        	            
	            Molecule mol = pdb.getMol();
	            mol.setFile(pdbFile.getParent() + File.separator + pdbFile.getName());
	            g.addMolecule(mol);
	            treePane.addMolecule(mol);
        	}
        	else{
        		PopUp.error("Please load either a \"*.pdb\" or a \"*.cif\" file");
        	}
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
    
    private void aboutItemActionPerformed(java.awt.event.ActionEvent evt) {
    	
    	ImageIcon ii = new ImageIcon(getClass().getResource("/images/springSaLaDLarge.png"));
    	String aboutText = "<html><b>   SpringSaLaD App</b></html>\n      Version 2.3.4\n      Released July, 2024\n      Copyright 2016-2024 UConn Health\n" +
    	"SpringSaLaD is Supported by NIH Grant R01GM132859";
   		JOptionPane.showMessageDialog(this, aboutText, "About SpringSaLaD", JOptionPane.OK_OPTION, ii);

//    	AboutBox aboutBox = new AboutBox();
//    	aboutBox.showDialog(this, "About SpringSaLaD");
    }
    
    private void websiteItemActionPerformed(java.awt.event.ActionEvent evt) {
    	String url = "https://vcell.org/ssalad";
    	if(Desktop.isDesktopSupported()){
    		Desktop desktop = Desktop.getDesktop();
    		try {
    			desktop.browse(new URI(url));
    		} catch (IOException | URISyntaxException e) {
    			e.printStackTrace();
    		}
    	}
   	}

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
    private javax.swing.JMenu jMenuHelp;
    
    private javax.swing.JMenuItem loadItem;
    private javax.swing.JMenuItem saveAsItem;
    private javax.swing.JMenuItem saveItem;
    private javax.swing.JMenuItem simulationManagerItem;
    private javax.swing.JMenuItem aboutItem;
    private javax.swing.JMenuItem websiteItem;

    private javax.swing.JMenuItem PDBAdderItem;
    private javax.swing.JPanel topPanel;
    private org.springsalad.langevinsetup.SystemTree treePane;
    private javax.swing.JSplitPane verticalSplitPane;
    // End of variables declaration//GEN-END:variables

}
