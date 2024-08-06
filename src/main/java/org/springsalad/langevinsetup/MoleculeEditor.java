/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.springsalad.helpersetup.Colors;
import org.springsalad.helpersetup.IOHelp;
import org.springsalad.helpersetup.PopUp;

import java.util.ArrayList;
import java.util.List;

public class MoleculeEditor extends JPanel implements WindowListener,
            ItemListener, ListSelectionListener, MoleculeSelectionListener {

    private final Molecule molecule;
    private final Global g;
    
    /* It helps to keep lists of the selected sites and links */
    private ArrayList<Site> selectedSites = new ArrayList<>();
    private ArrayList<Link> selectedLinks = new ArrayList<>();
    
    // List models
    private final DefaultListModel typeModel = new DefaultListModel();
    private final DefaultListModel stateModel = new DefaultListModel();
    private final DefaultListModel siteModel = new DefaultListModel<>();
    private final DefaultListModel linkModel = new DefaultListModel();
    private final DefaultComboBoxModel initialStateModel = new DefaultComboBoxModel();
    
    // Type creator
    private TypeCreator typeCreator;
    // Site creator
    private SiteCreator siteCreator;
    // Site array creator
    private SiteArrayCreator siteArrayCreator;
    // Cluster creator
    private ClusterCreator clusterCreator;
    // Link creator
    private LinkCreator linkCreator;
    
    // Array of MoleculeSelectionListeners
    private final ArrayList<MoleculeSelectionListener> listeners = new ArrayList<>();
    
    /**
     * Creates new form MoleculeEditor
     * @param molecule The molecule we're editing.
     * @param g The global information list
     */
    public MoleculeEditor(Molecule molecule, Global g) {
        initComponents();
        typeScrollPane.setViewportView(typeList);
        stateScrollPane.setViewportView(stateList);
        siteScrollPane.setViewportView(siteList);
        linkScrollPane.setViewportView(linkList);
        
        this.g = g;
        this.molecule = molecule;
        titlePanel.remove(nameTF);
        nameTF = new MoleculeNameTextField(molecule, g);
        titlePanel.add(nameTF, 2);
        titlePanel.validate();
        titlePanel.repaint();
        
        locationComboBox.setSelectedItem(molecule.getLocation());
        if(molecule.getLocation().equals(SystemGeometry.MEMBRANE)){
            check2D.setEnabled(true);
            check2D.setSelected(molecule.is2D());
        } else {
            check2D.setEnabled(false);
            check2D.setSelected(false);
        }
        
        locationComboBox.addItemListener(this);
        initialStateBox.addItemListener(this);
        typeList.addListSelectionListener(this);
        siteList.addListSelectionListener(this);
        linkList.addListSelectionListener(this);
        check2D.addItemListener(this);
        
        updateTypeList();
        updateSiteList();
        updateLinkList();
        
        manageButtons();
    }
    
    /* *************   GET THE MOLECULE *************************/
    
    public Molecule getMolecule(){
        return molecule;
    }
    
    /* ******* PROGRAMMATICALLY ADD AN ANCHOR SITE TYPE AND SITE *******/
    private void addMembraneAnchor(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        SiteType anchorType = new SiteType(molecule, SiteType.ANCHOR);
        molecule.addType(anchorType);
        anchorType.setD(2.0);
        anchorType.setRadius(1.0);
        anchorType.setColor(Colors.GRAY);
        anchorType.addState(new State(anchorType, SiteType.ANCHOR));
        anchorType.removeState(anchorType.getState("State0"));
        
        Site anchorSite = new Site(molecule, anchorType);
        anchorSite.setLocation(SystemGeometry.MEMBRANE);
        anchorSite.setInitialState(anchorType.getState(0));
        anchorSite.setY(4);
        anchorSite.setZ(4);
        molecule.addSite(anchorSite);
        // </editor-fold>
    }
    
    /* **************   LIST MANAGEMENT METHODS ****************/
    
    private void updateTypeList(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        int index = typeList.getSelectedIndex();
        ArrayList<SiteType> types = molecule.getTypeArray();
        typeModel.clear();
        for(int i=0;i<types.size();i++){
            typeModel.add(i, types.get(i));
        }
        if(index < types.size()){
            typeList.setSelectedIndex(index);
        } else {
            typeList.setSelectedIndex(typeModel.size()-1);
        }
        // </editor-fold>
    }
    
    private void updateStateList(SiteType type){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        stateModel.clear();
        if(type != null){
            ArrayList<State> states = type.getStates();
            for(int i=0;i<states.size();i++){
                stateModel.add(i, states.get(i));
            }
        }
        // </editor-fold>
    }
    
    private void updateSiteList(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        // DO NOT LISTEN FOR CHANGES IN SELECTED VALUES WHEN WE'RE UPDATING THE LIST!
        siteList.removeListSelectionListener(this);
        ArrayList<Site> sites = molecule.getSiteArray();
        siteModel.clear();
        for(int i=0;i<sites.size();i++){
            sites.get(i).setIndex(i);
            siteModel.add(i, sites.get(i));
        }
//        System.out.println("Molecule editor selectedSites = " + selectedSites.toString());
        int [] index = new int[selectedSites.size()];
        for(int i=0;i<index.length;i++){
            index[i] = selectedSites.get(i).getIndex();
        }
        siteList.setSelectedIndices(index);
        siteList.addListSelectionListener(this);
        // </editor-fold>
    }
    
    private void updateInitialStateBox(Site site){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        initialStateBox.removeItemListener(this);
        initialStateModel.removeAllElements();
        if(site != null){
            SiteType type = site.getType();
            for(State state: type.getStates()){
                initialStateModel.addElement(state);
            }
            initialStateBox.setSelectedItem(site.getInitialState());
        }
        initialStateBox.addItemListener(this);
        // </editor-fold>
    }
    
    private void updateLinkList(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        // DO NOT LISTEN FOR CHANGES IN SELECTED VALUES WHEN WE'RE UPDATING THE LIST!
        linkList.removeListSelectionListener(this);
        siteList.removeListSelectionListener(this);
        
        // Clear the site connection list
        for(Site site : molecule.getSiteArray()){
            site.clearConnectedSites();
        }
        
        ArrayList<Link> links = molecule.getLinkArray();
        linkModel.clear();
        for(int i=0;i<links.size();i++){
            Link link = links.get(i);
            link.setIndex(i);
            link.getSite1().connectTo(link.getSite2());
            link.getSite2().connectTo(link.getSite1());
            linkModel.add(i, links.get(i));
        }
        int [] index = new int[selectedLinks.size()];
        for(int i=0;i<index.length;i++){
            index[i] = selectedLinks.get(i).getIndex();
        }
        
        linkList.setSelectedIndices(index);
        
        siteList.addListSelectionListener(this);
        linkList.addListSelectionListener(this);
        // </editor-fold>
    }
    
    private void listsEnabled(boolean bool){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        typeList.setEnabled(bool);
        siteList.setEnabled(bool);
        linkList.setEnabled(bool);
        // </editor-fold>
    }
    
    /* ************  MANAGE THE TYPE DATA FIELDS ***************/
    
    private void manageTypeDataFields(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        SiteType type = (SiteType)typeList.getSelectedValue();
        if(type == null){
            typeRadiusTF.setText("");
            typeDTF.setText("");
            colorTF.setText("");
            updateStateList(null);
        } else {
            typeRadiusTF.setText(Double.toString(type.getRadius()));
            typeDTF.setText(Double.toString(type.getD()));
            colorTF.setText(type.getColorName());
            updateStateList(type);
        }
        // </editor-fold>
    }
    
    /* ************ MANAGE THE SITE DATA FIELDS ******************/
    
    private void manageSiteDataFields(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Site site = (Site)siteList.getSelectedValue();
        if(site == null || siteList.getSelectedValuesList().size() > 1){
            siteRadiusTF.setText("");
            siteDTF.setText("");
            locationTF.setText("");
            xTF.setText("");
            yTF.setText("");
            zTF.setText("");
            updateInitialStateBox(null);
        } else {
            siteRadiusTF.setText(Double.toString(site.getRadius()));
            siteDTF.setText(Double.toString(site.getD()));
            locationTF.setText(site.getLocation());
            xTF.setText(IOHelp.DF[2].format(site.getX()));
            yTF.setText(IOHelp.DF[2].format(site.getY()));
            zTF.setText(IOHelp.DF[2].format(site.getZ()));
            updateInitialStateBox(site);
        }
        // </editor-fold>
    }
    
    /* ************ MANAGE LINK DATA FIELD *********************/
    
    private void manageLinkDataField(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Link link = (Link)linkList.getSelectedValue();
        if(link == null || linkList.getSelectedValuesList().size() > 1){
            linkLengthTF.setText("");
        } else {
            linkLengthTF.setText(IOHelp.DF[3].format(link.getLength()));
        }
        // </editor-fold>
    }
    
    /* ************  BUTTON MANAGEMENT METHODS *****************/
    
    private void disableButtons(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        addTypeButton.setEnabled(false);
        editTypeButton.setEnabled(false);
        removeTypeButton.setEnabled(false);
        
        addSiteButton.setEnabled(false);
        addSiteArrayButton.setEnabled(false);
        editSiteButton.setEnabled(false);
        removeSiteButton.setEnabled(false);
        setPositionButton.setEnabled(false);
        createClusterButton.setEnabled(false);
        
        addLinkButton.setEnabled(false);
        removeLinkButton.setEnabled(false);
        setLinkLengthButton.setEnabled(false);
        // </editor-fold>
    }
    
    private void manageButtons(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        // Begin by disabling all buttons
        disableButtons();
        addTypeButton.setEnabled(true);
        if(!typeModel.isEmpty()){
            editTypeButton.setEnabled(true);
            removeTypeButton.setEnabled(true);

            addSiteButton.setEnabled(true);
            addSiteArrayButton.setEnabled(true);
            
            if(!siteModel.isEmpty()){
                editSiteButton.setEnabled(true);
                removeSiteButton.setEnabled(true);
                setPositionButton.setEnabled(true);
                
                if(molecule.getAnchorSites().size() == 1){
                    createClusterButton.setEnabled(true);
                }
                
                if(siteModel.getSize() > 1){
                    addLinkButton.setEnabled(true);
                    
                    if(!linkModel.isEmpty()){
                        removeLinkButton.setEnabled(true);
                        setLinkLengthButton.setEnabled(true);
                    }
                }
            }
        }
        // </editor-fold>
    }
    
    /* ***********  UPDATE EVERYTHING AND NOTIFY LISTENERS **************/
    
    private void updateAllAndNotify(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        updateTypeList();
        updateSiteList();
        updateLinkList();
        manageSiteDataFields();
        manageLinkDataField();
        listsEnabled(true);
        manageButtons();
        // System.out.println("Notifying from window closed");
        notifyListeners();
        // </editor-fold>
    }
    
    /* ***********  ITEM LISTENER METHOD ************************/
    
    @Override
    public void itemStateChanged(ItemEvent event){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Object source = event.getSource();
        if(source == locationComboBox){
            locationComboBox.removeItemListener(this);
            if(event.getStateChange() == ItemEvent.SELECTED){
                if(molecule.hasAnchorType()){
                    PopUp.error("A molecule with a membrane anchor type must reside on the membrane.");
                    locationComboBox.setSelectedIndex(1);
                } else {
                    String location = (String)locationComboBox.getSelectedItem();
                    molecule.setLocation(location);
                    if(location.equals(SystemGeometry.INSIDE) || location.equals(SystemGeometry.OUTSIDE)){
                        for(Site site : molecule.getSiteArray()){
                            site.setLocation(location);
                        }
                        check2D.setEnabled(false);
                        check2D.setSelected(false);
                    } else {
                        addMembraneAnchor();
                        updateAllAndNotify();
                        check2D.setEnabled(true);
                    } 
                }
                notifyListeners();
            }
            locationComboBox.addItemListener(this);
        }
        
        if(source == initialStateBox){
            if(event.getStateChange() == ItemEvent.SELECTED){
                Site site = (Site)siteList.getSelectedValue();
                if(site != null){
                    State state = (State)initialStateBox.getSelectedItem();
                    // System.out.println("Assigning to initial state " + state);
                    site.setInitialState(state);
                }
            }
        }
        
        if(source == check2D){
            if(check2D.isEnabled()){
                molecule.set2D(check2D.isSelected());
            }
        }
        // </editor-fold>
    }
    
    /* **************** LIST SELECTION LISTENER METHOD ***************/
    @Override
    public void valueChanged(ListSelectionEvent event){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(!event.getValueIsAdjusting()){
            Object source = event.getSource();
            
            if(source == typeList){
                manageTypeDataFields();
            }

            if(source == siteList){
                selectedSites.clear();
                for(Object site : siteList.getSelectedValuesList()){
                    selectedSites.add((Site)site);
                }
                manageSiteDataFields();
            }

            if(source == linkList){
                selectedLinks.clear();
                for(Object link : linkList.getSelectedValuesList()){
                    selectedLinks.add((Link)link);
                }
                manageLinkDataField();
            }
            // System.out.println("Notifying from value changed.");
            notifyListeners();
        }
        // </editor-fold>
    }
    
    /* ************** MOLECULE LISTENER METHOD *********************/
    @Override
    public void selectionOccurred(MoleculeSelectionEvent event){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        selectedSites = event.getSelectedSites();
        selectedLinks = event.getSelectedLinks();
        // System.out.println("Before molecule editor calls updateSiteList, selectedSites = " + selectedSites.toString());
        updateSiteList();
        manageSiteDataFields();
        updateLinkList();
        manageLinkDataField();
        // </editor-fold>
    }
    
    /* **********   WINDOW LISTENER METHODS  ***************/
    @Override
    public void windowActivated(WindowEvent event){};
    @Override
    public void windowDeactivated(WindowEvent event){};
    @Override
    public void windowIconified(WindowEvent event){};
    @Override
    public void windowDeiconified(WindowEvent event){};
    @Override
    public void windowOpened(WindowEvent event){};
    @Override
    public void windowClosing(WindowEvent event){};
    @Override
    public void windowClosed(WindowEvent event){
        updateAllAndNotify();
    };
    
    /* NOTIFY MOLECULE SELECTION LISTENERS AND RELATED METHODS ***********/
    
    public void addMoleculeSelectionListener(MoleculeSelectionListener listener){
        listeners.add(listener);
    }
    
    public void removeMoleculeSelectionListener(MoleculeSelectionListener listener){
        listeners.remove(listener);
    }
    
    public void notifyListeners(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        MoleculeSelectionEvent event = new MoleculeSelectionEvent(selectedSites, selectedLinks);
        // System.out.println("Sending selectedSites = " + selectedSites);
        for(MoleculeSelectionListener listener : listeners){
            listener.selectionOccurred(event);
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

        titlePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        nameTF = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        locationComboBox = new javax.swing.JComboBox();
        check2D = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        typeScrollPane = new javax.swing.JScrollPane();
        typeList = new JList(typeModel);
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        stateScrollPane = new javax.swing.JScrollPane();
        stateList = new JList(stateModel);
        jLabel8 = new javax.swing.JLabel();
        typeRadiusTF = new javax.swing.JTextField();
        typeDTF = new javax.swing.JTextField();
        colorTF = new javax.swing.JTextField();
        addTypeButton = new javax.swing.JButton();
        editTypeButton = new javax.swing.JButton();
        removeTypeButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        siteScrollPane = new javax.swing.JScrollPane();
        siteList = new JList(siteModel);
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        siteRadiusTF = new javax.swing.JTextField();
        siteDTF = new javax.swing.JTextField();
        xTF = new javax.swing.JTextField();
        yTF = new javax.swing.JTextField();
        zTF = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        initialStateBox = new JComboBox(initialStateModel);
        locationTF = new javax.swing.JTextField();
        addSiteButton = new javax.swing.JButton();
        addSiteArrayButton = new javax.swing.JButton();
        editSiteButton = new javax.swing.JButton();
        removeSiteButton = new javax.swing.JButton();
        setPositionButton = new javax.swing.JButton();
        createClusterButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        linkScrollPane = new javax.swing.JScrollPane();
        linkList = new JList(linkModel);
        jLabel19 = new javax.swing.JLabel();
        linkLengthTF = new javax.swing.JTextField();
        addLinkButton = new javax.swing.JButton();
        removeLinkButton = new javax.swing.JButton();
        setLinkLengthButton = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Molecule Editor");
        titlePanel.add(jLabel1);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Name:");
        jLabel2.setPreferredSize(new java.awt.Dimension(50, 14));
        titlePanel.add(jLabel2);

        nameTF.setColumns(15);
        titlePanel.add(nameTF);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Location:");
        jLabel3.setPreferredSize(new java.awt.Dimension(60, 14));
        titlePanel.add(jLabel3);

        locationComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Intracellular", "Membrane", "Extracellular" }));
        titlePanel.add(locationComboBox);

        check2D.setText("Set as 2D");
        titlePanel.add(check2D);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Types");

        typeScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        typeList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        typeScrollPane.setViewportView(typeList);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Radius (nm): ");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("D (um^2/s): ");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Color:");

        stateScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        stateList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        stateList.setEnabled(false);
        stateScrollPane.setViewportView(stateList);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("States");

        typeRadiusTF.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        typeRadiusTF.setEnabled(false);

        typeDTF.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        typeDTF.setEnabled(false);

        colorTF.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        colorTF.setEnabled(false);

        addTypeButton.setText("Add Type");
        addTypeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTypeButtonActionPerformed(evt);
            }
        });

        editTypeButton.setText("Edit Type");
        editTypeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editTypeButtonActionPerformed(evt);
            }
        });

        removeTypeButton.setText("Remove Type");
        removeTypeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeTypeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(typeScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addComponent(jLabel5))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(typeDTF)
                                            .addComponent(colorTF)
                                            .addComponent(typeRadiusTF, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                                        .addComponent(stateScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(addTypeButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(editTypeButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(removeTypeButton)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(typeRadiusTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(typeDTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(colorTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stateScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(typeScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addTypeButton)
                    .addComponent(editTypeButton)
                    .addComponent(removeTypeButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Sites");

        siteScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        siteScrollPane.setViewportView(siteList);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Radius (nm): ");

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("D (um^2/s): ");

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Location: ");

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Position (nm)");

        jLabel14.setText("x: ");

        jLabel15.setText("y :");

        jLabel16.setText("z: ");

        siteRadiusTF.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        siteRadiusTF.setEnabled(false);

        siteDTF.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        siteDTF.setEnabled(false);

        xTF.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xTF.setEnabled(false);

        yTF.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        yTF.setEnabled(false);

        zTF.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        zTF.setEnabled(false);

        jLabel17.setText("Initial State: ");

        locationTF.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        locationTF.setEnabled(false);

        addSiteButton.setText("Add Site");
        addSiteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSiteButtonActionPerformed(evt);
            }
        });

        addSiteArrayButton.setText("Add Site Array");
        addSiteArrayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSiteArrayButtonActionPerformed(evt);
            }
        });

        editSiteButton.setText("Edit Site");
        editSiteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editSiteButtonActionPerformed(evt);
            }
        });

        removeSiteButton.setText("Remove Site");
        removeSiteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeSiteButtonActionPerformed(evt);
            }
        });

        setPositionButton.setText("Set Position");
        setPositionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setPositionButtonActionPerformed(evt);
            }
        });

        createClusterButton.setText("Create Cluster");
        createClusterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createClusterButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(siteScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(siteRadiusTF, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(siteDTF, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(locationTF)
                                                .addGap(15, 15, 15))))
                                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel14)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(xTF, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel15)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(yTF, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel16)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(zTF, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel17)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(initialStateBox, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(addSiteArrayButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(addSiteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(removeSiteButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(editSiteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(setPositionButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(createClusterButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(siteRadiusTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(siteDTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(locationTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(xTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15)
                            .addComponent(yTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16)
                            .addComponent(zTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(initialStateBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(siteScrollPane))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addSiteButton)
                    .addComponent(editSiteButton)
                    .addComponent(setPositionButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addSiteArrayButton)
                    .addComponent(removeSiteButton)
                    .addComponent(createClusterButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Links");

        linkScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        linkScrollPane.setViewportView(linkList);

        jLabel19.setText("Length (nm): ");

        linkLengthTF.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        linkLengthTF.setEnabled(false);

        addLinkButton.setText("Add Link");
        addLinkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLinkButtonActionPerformed(evt);
            }
        });

        removeLinkButton.setText("Remove Links");
        removeLinkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeLinkButtonActionPerformed(evt);
            }
        });

        setLinkLengthButton.setText("Set Link Length");
        setLinkLengthButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setLinkLengthButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(linkScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(linkLengthTF))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 18, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(removeLinkButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(addLinkButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(setLinkLengthButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(11, 11, 11)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(linkScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(linkLengthTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addLinkButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeLinkButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(setLinkLengthButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titlePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
 
    private void addTypeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTypeButtonActionPerformed
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        SiteType newType = new SiteType(molecule, "Type" + typeModel.size());
        molecule.addType(newType);
        
        disableButtons();
        listsEnabled(false);
        typeCreator = new TypeCreator(g, molecule, newType);
        typeCreator.addWindowListener(this);
        // </editor-fold>
    }//GEN-LAST:event_addTypeButtonActionPerformed

    private void editTypeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editTypeButtonActionPerformed
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        disableButtons();
        listsEnabled(false);
        SiteType type = (SiteType)typeList.getSelectedValue();
        if(type == null){
            PopUp.warning("No type selected for editing.");
            manageButtons();
            listsEnabled(true);
        } else {
            typeCreator = new TypeCreator(g, molecule, type);
            typeCreator.addWindowListener(this);
        }
        // </editor-fold>
    }//GEN-LAST:event_editTypeButtonActionPerformed

    private void removeTypeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeTypeButtonActionPerformed
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        listsEnabled(false);
        disableButtons();
        SiteType type = (SiteType)typeList.getSelectedValue();
        if(type == null){
            PopUp.warning("No type selected.");
        } else if(g.typeInTransitionReaction(type)){
            PopUp.error("The selected site type participates in a transition reaction."
                    + " Please delete that reaction before removing this site type.");
        } else {
            if(molecule.typeAssignedToSite(type)){
                PopUp.warning("The selected type is assigned to one or more sites.\n"
                        + "Please remove those sites before deleting type.");
            } else {
                int confirm = PopUp.doubleCheck("Are you sure you want to remove type " + type.getName() + "?");
                if(confirm == 0){
                    molecule.removeType(type);
                    updateTypeList();
                    manageTypeDataFields();
                }
            }
        }
        listsEnabled(true);
        manageButtons();
        // </editor-fold>
    }//GEN-LAST:event_removeTypeButtonActionPerformed

    private void addSiteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSiteButtonActionPerformed
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        disableButtons();
        listsEnabled(false);
        siteCreator = new SiteCreator(molecule, null);
        siteCreator.addWindowListener(this);
        // </editor-fold>
    }//GEN-LAST:event_addSiteButtonActionPerformed

    private void editSiteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editSiteButtonActionPerformed
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        disableButtons();
        listsEnabled(false);
        List sites = siteList.getSelectedValuesList();
        if(sites.isEmpty()){
            PopUp.warning("No site selected.");
            manageButtons();
            listsEnabled(true);
        } else if(sites.size() > 1){
            PopUp.warning("Please select a single site to edit.");
            manageButtons();
            listsEnabled(true);
        } else {
            Site site = (Site)sites.get(0);
            if(site.getTypeName().equals(SiteType.ANCHOR)){
                PopUp.warning("Membrane anchors may not be edited.");
                manageButtons();
                listsEnabled(true);
            } else {
                siteCreator = new SiteCreator(molecule, site);
                siteCreator.addWindowListener(this);
            }
        }
        // </editor-fold>
    }//GEN-LAST:event_editSiteButtonActionPerformed

    private void removeSiteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeSiteButtonActionPerformed
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        listsEnabled(false);
        disableButtons();
        List sites = siteList.getSelectedValuesList();
        if(sites.isEmpty()){
            PopUp.warning("No site selected.");
        } else {
            boolean hasLink = false;
            for(Object o : sites){
                Site site = (Site)o;
                if(site.hasLink()){
                    PopUp.warning("A selected site is linked to one or more sites.\n"
                        + "Please remove those links before deleting this site.");
                    hasLink = true;
                    break;
                }
            }
            if(!hasLink){
                int confirm = PopUp.doubleCheck("Are you sure you want to remove the selected sites?");
                if(confirm == 0){
                    for(Object o : sites){
                        molecule.removeSite((Site)o);
                        selectedSites.remove((Site)o);
                    }
                    updateSiteList();
                    manageSiteDataFields();
                }
            }
        }
        listsEnabled(true);
        manageButtons();
        // System.out.println("Notifying from removeSiteButton");
        notifyListeners();
        // </editor-fold>
    }//GEN-LAST:event_removeSiteButtonActionPerformed

    private void addSiteArrayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSiteArrayButtonActionPerformed
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        disableButtons();
        listsEnabled(false);
        siteArrayCreator = new SiteArrayCreator(molecule);
        siteArrayCreator.addWindowListener(this);
        // </editor-fold>
    }//GEN-LAST:event_addSiteArrayButtonActionPerformed

    private void addLinkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLinkButtonActionPerformed
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        disableButtons();
        listsEnabled(false);
        linkCreator = new LinkCreator(molecule);
        linkCreator.addWindowListener(this);
        // </editor-fold>
    }//GEN-LAST:event_addLinkButtonActionPerformed

    private void removeLinkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeLinkButtonActionPerformed
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        List links = linkList.getSelectedValuesList();
        if(links.isEmpty()){
            PopUp.warning("No link selected.");
        } else {
            int confirm = PopUp.doubleCheck("Do you want to remove the selected link(s)?");
            if(confirm == 0){
                for(Object link : links){
                    molecule.removeLink((Link)link);
                    selectedLinks.remove((Link)link);
                }
                updateLinkList();
                manageLinkDataField();
            }
        }
        
        listsEnabled(true);
        manageButtons();
        // System.out.println("Notifying from removeLinkButton");
        notifyListeners();
        // </editor-fold>
    }//GEN-LAST:event_removeLinkButtonActionPerformed

    private void setLinkLengthButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setLinkLengthButtonActionPerformed
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        disableButtons();
        listsEnabled(false);
        List links = (List)linkList.getSelectedValuesList();
        if(links.isEmpty()){
            PopUp.warning("Please select a link to edit.");
            manageButtons();
            listsEnabled(true);
        } else if (links.size()>1){
            PopUp.warning("Please select a single link to edit.");
            manageButtons();
            listsEnabled(true);
        } else {
            LinkLengthEditor linkLengthEditor = new LinkLengthEditor((Link)links.get(0));
            linkLengthEditor.addWindowListener(this);
        }
        // </editor-fold>
    }//GEN-LAST:event_setLinkLengthButtonActionPerformed

    private void setPositionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setPositionButtonActionPerformed
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        disableButtons();
        listsEnabled(false);
        List sites = siteList.getSelectedValuesList();
        if(sites.isEmpty()){
            PopUp.warning("Please select a site.");
            manageButtons();
            listsEnabled(true);
        } else if(sites.size() > 1){
            PopUp.warning("Please select a single site.");
            manageButtons();
            listsEnabled(true);
        } else {
            Site site = (Site)sites.get(0);
            if(site.getTypeName().equals(SiteType.ANCHOR)){
                PopUp.warning(("This feature not supported for membrane anchors."));
                manageButtons();
                listsEnabled(true);
            } else {
                SitePositionEditor positionEditor = new SitePositionEditor(site);
                positionEditor.addWindowListener(this);
            }
        }
        // </editor-fold>
    }//GEN-LAST:event_setPositionButtonActionPerformed

    private void createClusterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createClusterButtonActionPerformed
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        disableButtons();
        listsEnabled(false);
        clusterCreator = new ClusterCreator(molecule);
        clusterCreator.addWindowListener(this);
        // </editor-fold>
    }//GEN-LAST:event_createClusterButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addLinkButton;
    private javax.swing.JButton addSiteArrayButton;
    private javax.swing.JButton addSiteButton;
    private javax.swing.JButton addTypeButton;
    private javax.swing.JCheckBox check2D;
    private javax.swing.JTextField colorTF;
    private javax.swing.JButton createClusterButton;
    private javax.swing.JButton editSiteButton;
    private javax.swing.JButton editTypeButton;
    private javax.swing.JComboBox initialStateBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField linkLengthTF;
    private javax.swing.JList linkList;
    private javax.swing.JScrollPane linkScrollPane;
    private javax.swing.JComboBox locationComboBox;
    private javax.swing.JTextField locationTF;
    private javax.swing.JTextField nameTF;
    private javax.swing.JButton removeLinkButton;
    private javax.swing.JButton removeSiteButton;
    private javax.swing.JButton removeTypeButton;
    private javax.swing.JButton setLinkLengthButton;
    private javax.swing.JButton setPositionButton;
    private javax.swing.JTextField siteDTF;
    private javax.swing.JList siteList;
    private javax.swing.JTextField siteRadiusTF;
    private javax.swing.JScrollPane siteScrollPane;
    private javax.swing.JList stateList;
    private javax.swing.JScrollPane stateScrollPane;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JTextField typeDTF;
    private javax.swing.JList typeList;
    private javax.swing.JTextField typeRadiusTF;
    private javax.swing.JScrollPane typeScrollPane;
    private javax.swing.JTextField xTF;
    private javax.swing.JTextField yTF;
    private javax.swing.JTextField zTF;
    // End of variables declaration//GEN-END:variables
}
