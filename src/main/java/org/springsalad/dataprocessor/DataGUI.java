
package org.springsalad.dataprocessor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import org.springsalad.helpersetup.Fonts;
import org.springsalad.runlauncher.Simulation;

import java.util.ArrayList;

public class DataGUI extends JFrame implements ItemListener, ActionListener,
                                                ListSelectionListener {
    
    /* ********** COMBO BOXES FOR TOP PANEL *******************************/
    protected JComboBox dataClassBox;
    protected JComboBox dataTypeBox;
    
    /* **********  DATA CLASSES AND TYPES *********************************/
    private static final String MOLECULECOUNTS = "MOLECULE COUNTS";
    private static final String BONDDATA = "BOND DATA"; 
    private static final String STATEDATA = "STATE DATA"; 
    private static final String SITEDATA = "SITE DATA";
    private static final String RUNNINGTIMES = "RUNNING TIMES";
    private final String [] dataClasses = {MOLECULECOUNTS, BONDDATA, 
                                    STATEDATA, SITEDATA, RUNNINGTIMES};
    public static final String [] dataTypes = {"AVERAGE", "HISTOGRAM", "RAW DATA"};
    
    /* **********  THE DATA PROCESSOR *************************************/
    protected final DataProcessor processor;
    
    /* ****************** THE SIMULATION *********************************/
    protected final Simulation simulation;
    
    /* ****************** THE LIST ***************************************/
    protected JLabel listLabel;
    protected JList<String> list;
    protected DefaultListModel<String> listModel;
    
    /* ****************** LIST LABEL OPTIONS *****************************/
    public static final String MOLECULES = "Molecules";
    public static final String BONDS = "Bonds";
    public static final String STATES = "States";
    public static final String SITES = "Sites";
    public static final String RUNNING_TIMES = "Running Times";
    
    protected String currentPick;
    
    /* ****************  THE DATA HOLDERS ********************************/
    private AverageDataHolder averageMoleculeData;
    private AverageDataHolder averageBondData;
    private AverageDataHolder averageStateData;
    private RunningTimesDataHolder runningTimesData;
    private final HistogramBuilder histogramBuilder = new HistogramBuilder();
    private final SiteDataHolder siteData;
    
    /* **************** THE TABLES ***************************************/
    private JTable moleculeDataTable;
    private JTable bondDataTable;
    private JTable stateDataTable;
    
    /* *******************  TABLE MODELS *********************************/
    private AverageDataTableModel moleculeDataModel;
    private AverageDataTableModel bondDataModel;
    private AverageDataTableModel stateDataModel;
    private RawDataTableModel rawDataModel;
    private HistogramTableModel histogramModel;
    private SiteFreeBoundTableModel siteFBModel;
    private SiteStatesTableModel siteStateModel;
    private SiteBondsTableModel siteBondModel;
    
    /* **************** THE SCROLLPANE TO HOLD THE TABLES *****************/
    private JScrollPane pane;
    
    /* **************** PANELS TO SWAP OUT ********************************/
    protected final JPanel centerPanel;
    private JPanel panePanel;
    private RunningTimesTablePanel runningTimesPanel;
    private JPanel rawDataPanel;
    private HistogramTablePanel histogramPanel;
    private JPanel sitePanel;
    
    /* ************* THE CONSTRUCTOR ************************************/
    public DataGUI(String title, Simulation simulation){
        super(title);
        this.simulation = simulation;
        this.processor = simulation.getDataProcessor();
        siteData = new SiteDataHolder(this.processor);
        
        buildDataHoldersAndTables();
        buildScrollPanePanel();
        buildRunningTimesPanel();
        buildRawDataPanel();
        buildHistogramPanels();
        buildSitePanel();
        
        Container c = this.getContentPane();
        c.add(buildTopPanel(), "North");
        c.add(buildLeftPanel(), "West");
        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(panePanel, "Center");
        this.add(centerPanel,"Center");
        swapAverageTables();
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }
    
    /* **************** BUILD THE TOP PANEL ********************************/
    
    private JPanel buildTopPanel(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JLabel titleLabel = new JLabel("Simulation: " + simulation.getSimulationName(), JLabel.CENTER);
        titleLabel.setFont(Fonts.SUBTITLEFONT);
        
        JLabel dataClassLabel = new JLabel("Data Class: ", JLabel.RIGHT);
        dataClassLabel.setFont(Fonts.SUBTITLEFONT);
        dataClassBox = new JComboBox(dataClasses);
        // dataClassBox.setFont(Fonts.SUBTITLEFONT);
        dataClassBox.addItemListener(this);
        
        JLabel dataTypeLabel = new JLabel("Data Type: ", JLabel.RIGHT);
        dataTypeLabel.setFont(Fonts.SUBTITLEFONT);
        dataTypeBox = new JComboBox(dataTypes);
        // dataTypeBox.setFont(Fonts.SUBTITLEFONT);
        dataTypeBox.addItemListener(this);
        
        JPanel classPanel = new JPanel();
        classPanel.add(dataClassLabel);
        classPanel.add(dataClassBox);
        
        JPanel typePanel = new JPanel();
        typePanel.add(dataTypeLabel);
        typePanel.add(dataTypeBox);
        
        JPanel p0 = new JPanel();
        p0.setLayout(new GridBagLayout());
        
        GridBagConstraints c0 = new GridBagConstraints();
        c0.gridx = 0;
        c0.gridy = 0;
        c0.gridwidth = GridBagConstraints.REMAINDER;
        p0.add(titleLabel, c0);
        
        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridx = 0;
        c1.gridy = 1;      
        p0.add(classPanel, c1);
        
        c1.gridx = 1;
        p0.add(typePanel, c1);
        
        JPanel p = new JPanel();
        p.add(p0);
        
        return p;
        // </editor-fold>
    }
    
    /* **************** BUILD THE LEFT PANEL ******************************/
    private JPanel buildLeftPanel(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        // Always load molecules first
        currentPick = MOLECULES;
        listLabel = new JLabel(MOLECULES, JLabel.CENTER);
        listLabel.setFont(Fonts.SUBTITLEFONT);
        
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        updateList();
        list.addListSelectionListener(this);
        
        JPanel p0 = new JPanel();
        p0.add(listLabel);
        JScrollPane pane = new JScrollPane(list);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        pane.setPreferredSize(new Dimension(250,300));
        p0.add(pane);
        p0.setPreferredSize(new Dimension(260,500));
        
        JPanel p = new JPanel();
        p.add(p0);
        
        return p;
        // </editor-fold>
    }
    
    /* **************** UPDATE LIST ***************************************/
    private void updateList(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        list.setEnabled(true);
        ArrayList<String> strings;
        switch(currentPick){
            case MOLECULES:
                strings = processor.getMoleculeNames();
                break;
            case BONDS:
                strings = processor.getBondNames();
                break;
            case STATES:
                strings = processor.getStateNames();
                break;
            case SITES:
                strings = processor.getSiteNames();;
                break;
            case RUNNING_TIMES:
                strings = new ArrayList<>();
                strings.add("");
                list.setEnabled(false);
                break;
            default:
                strings = new ArrayList<>();
                strings.add("Error: Hit default.");
        }
        listModel.clear();
        for(String string : strings){
            listModel.addElement(string);
        }
        list.clearSelection();
        // </editor-fold>
    }
    
    /* ************  CONSTRUCT THE DATA HOLDERS AND TABLES ******************/
    private void buildDataHoldersAndTables(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        averageMoleculeData = new AverageDataHolder(processor.getMoleculeNames(), processor.getMoleculeAverageDataFile());
        averageBondData = new AverageDataHolder(processor.getBondNames(), processor.getBondAverageDataFile());
        averageStateData = new AverageDataHolder(processor.getStateNames(), processor.getStateAverageDataFile());
        runningTimesData = new RunningTimesDataHolder(processor.getRunningTimesFile());
        
        moleculeDataModel = new AverageDataTableModel(averageMoleculeData);
        bondDataModel = new AverageDataTableModel(averageBondData);
        stateDataModel = new AverageDataTableModel(averageStateData);
        
        moleculeDataTable = new JTable(moleculeDataModel);
        bondDataTable = new JTable(bondDataModel);
        stateDataTable = new JTable(stateDataModel);
        
        moleculeDataTable.setFillsViewportHeight(true);
        bondDataTable.setFillsViewportHeight(true);
        stateDataTable.setFillsViewportHeight(true);
        
        // </editor-fold>
    }
    
    /* ************   BUILD SCROLL PANE PANEL *******************************/
    private void buildScrollPanePanel(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        pane = new JScrollPane(moleculeDataTable);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        panePanel = new JPanel();
        panePanel.setLayout(new BorderLayout());
        panePanel.add(pane,"Center");
        // </editor-fold>
    }
    
    /* **************  BUILD RUNNING TIMES PANEL ****************************/
    private void buildRunningTimesPanel(){
        runningTimesPanel = new RunningTimesTablePanel(runningTimesData);
    }
    
    /* *************  BUILD RAW DATA PANEL **********************************/
    private void buildRawDataPanel(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        rawDataModel = new RawDataTableModel(null);
        JTable rawDataTable = new JTable(rawDataModel);
        rawDataTable.setFillsViewportHeight(true);
        
        JScrollPane rawPane = new JScrollPane(rawDataTable);
        rawPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        rawPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        rawDataPanel = new JPanel();
        rawDataPanel.setLayout(new BorderLayout());
        rawDataPanel.add(rawPane, "Center");
        // </editor-fold>
    }
    
    /* *************** BUILD THE HISTOGRAM PANEL ****************************/
    private void buildHistogramPanels(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        histogramModel = new HistogramTableModel(histogramBuilder);
        histogramPanel = new HistogramTablePanel(histogramModel);

        // </editor-fold>
    }
    
    /* ************** BUILD THE SITE DATA PANEL *****************************/
    private void buildSitePanel(){
        siteFBModel = new SiteFreeBoundTableModel(siteData);
        siteStateModel = new SiteStatesTableModel(siteData);
        siteBondModel = new SiteBondsTableModel(siteData);
        sitePanel = new SiteTablePanel(siteFBModel, siteStateModel, siteBondModel);
    }
    
    /* ************  SWAP TABLES IN SCROLLPANE ******************************/
    private void swapAverageTables(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        // pane.removeAll();
        switch(currentPick){
            case MOLECULES:
                pane.setViewportView(moleculeDataTable);
                break;
            case BONDS:
                pane.setViewportView(bondDataTable);
                break;
            case STATES:
                pane.setViewportView(stateDataTable);
                break;
            default:
                // do nothing
        }
        pane.validate();
        pane.repaint();
        // </editor-fold>
    }
    
    /* ***********  SWAP PANELS ********************************************/
    private void swapPanels(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        centerPanel.removeAll();
        switch(currentPick){
            case MOLECULES:
            case BONDS:
            case STATES:
                int type = dataTypeBox.getSelectedIndex();
                if(type == 0){
                    centerPanel.add(panePanel,"Center");
                    swapAverageTables();
                } else if(type == 1){
                    centerPanel.add(histogramPanel, "Center");
                } else if(type == 2){
                    centerPanel.add(rawDataPanel, "Center");
                }
                break;
            case SITES:
                centerPanel.add(sitePanel, "Center");
                break;
            case RUNNING_TIMES:
                centerPanel.add(runningTimesPanel, "Center");
                break;
        }
        centerPanel.validate();
        centerPanel.repaint();
        // </editor-fold>
    }
    
    /* *********** ACTION LISTENER METHOD **********************************/
    @Override
    public void actionPerformed(ActionEvent event){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        // </editor-fold>
    }
    
    /* *********** ITEM LISTENER METHOD ***********************************/
    @Override
    public void itemStateChanged(ItemEvent event){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(event.getStateChange() == ItemEvent.SELECTED){
            list.clearSelection();
            Object source = event.getSource();
            if(source == dataClassBox){
                dataTypeBox.setEnabled(true);
                if (dataTypeBox.getItemCount()==2) {
                	dataTypeBox.addItem(dataTypes[2]);
                }
                switch((String) dataClassBox.getSelectedItem()){
                    case MOLECULECOUNTS:
                        currentPick = MOLECULES;
                        break;
                    case BONDDATA:
                        currentPick = BONDS;
                        break;
                    case STATEDATA:
                        currentPick = STATES;
                        break;
                    case SITEDATA:
                        currentPick = SITES;
                        dataTypeBox.setEnabled(false);
                        // At this point no way to view histograms or raw data for site properties
                        dataTypeBox.setSelectedIndex(0);
                        list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        break;
                    case RUNNING_TIMES:
                        currentPick = RUNNING_TIMES;
                        dataTypeBox.setEnabled(false);
                        // Only raw data makes sense for the running times, although that includes average data too
                        dataTypeBox.setSelectedIndex(2);
                        break;
                    default:
                        System.out.println("Not implemented yet.");
                }
                listLabel.setText(currentPick);
                updateList();
                swapPanels();
            }
            
            if(source == dataTypeBox){
                int index = dataTypeBox.getSelectedIndex();
                if(index == 0){
                    if(currentPick.equals(SITES)){
                        list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    } else {
                        list.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                    }
                } else {
                    list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                }
                swapPanels();
            }
        }
        // </editor-fold>
    }

    /* *************** LIST SELECTION LISTENER METHOD *********************/
    @Override
    public void valueChanged(ListSelectionEvent event) {
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(!event.getValueIsAdjusting()){
            moleculeDataModel.clearNamesToShow();
            bondDataModel.clearNamesToShow();
            stateDataModel.clearNamesToShow();
            int type = dataTypeBox.getSelectedIndex();
            switch(type){
                case 0:
                    switch(currentPick){
                        case MOLECULES:
                            for(String name : list.getSelectedValuesList()){
                                moleculeDataModel.addNameToShow(name);
                            }
                            break;
                        case BONDS:
                            for(String name: list.getSelectedValuesList()){
                                bondDataModel.addNameToShow(name);
                            }
                            break;
                        case STATES:
                            for(String name: list.getSelectedValuesList()){
                                stateDataModel.addNameToShow(name);
                            }
                            break;
                        case SITES:
                            String name = (String)list.getSelectedValue();
                            if(name != null){
                                siteData.setSiteData(name);
                                siteFBModel.updateData();
                                siteStateModel.updateData();
                                siteBondModel.updateData();
                            }
                            break;
                        
                    }
                    break;
                case 1:
                    switch(currentPick){
                        case MOLECULES:
                        case BONDS:
                        case STATES:
                            String name1 = (String)list.getSelectedValue();
                            if(name1 != null){
                                histogramBuilder.setRawDataFile(processor.getRawDataFile(name1));
                                histogramBuilder.constructHistogram();
                                histogramModel.updateBuilder();
                                histogramPanel.updateOptionsTable();
                            }
                            break;
                    }
                    
                    break;
                case 2:
                    String name = (String)list.getSelectedValue();
                    if(name != null){
                        RawDataHolder rdt = new RawDataHolder(processor.getRawDataFile(name));
                        rawDataModel.setRawData(rdt);
                    }
            }
        }
        // </editor-fold>
    }
}
