/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.springsalad.langevinsetup;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.springsalad.helpersetup.Fonts;
import org.springsalad.helpersetup.PopUp;

public class AllostericReactionTopPanel extends JPanel
        implements ListSelectionListener {

    private final ArrayList<Molecule> molecules;
    private final AllostericReaction reaction;

    private final DefaultListModel moleculeModel = new DefaultListModel();
    private final DefaultListModel siteModel = new DefaultListModel();
    private final DefaultListModel stateModel = new DefaultListModel();
    private final DefaultListModel allostericStateModel = new DefaultListModel();

    private final JList moleculeList;
    private final JList[] siteList = new JList[2];
    private final JList[] stateList = new JList[2];
    private final JList allostericStateList;

    public AllostericReactionTopPanel(Global g, AllostericReaction reaction) {
        this.reaction = reaction;
        this.molecules = g.getMolecules();

        moleculeList = new JList(moleculeModel);
        moleculeList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        moleculeList.addListSelectionListener(this);

        for (int i = 0; i < siteList.length; i++) {
            siteList[i] = new JList(siteModel);
            siteList[i].getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            siteList[i].addListSelectionListener(this);
        }

        for (int i = 0; i < stateList.length; i++) {
            stateList[i] = new JList(stateModel);
            stateList[i].getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            stateList[i].addListSelectionListener(this);
        }

        allostericStateList = new JList(allostericStateModel);
        allostericStateList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        allostericStateList.addListSelectionListener(this);

        moleculeModel.clear();
        for (Molecule molecule : molecules) {
            moleculeModel.addElement(molecule);
        }
        
        initialSetUp();
        
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2,1));
        p.add(makeReactionPanel());
        p.add(makeAllostericPanel());
        
        this.setLayout(new FlowLayout());
        this.add(p);

    }

    /* **************** INITIAL SETUP *********************************/
    private void initialSetUp() {
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Molecule molecule = reaction.getMolecule();
        if (molecule != null) {
            moleculeList.setSelectedValue(molecule, true);
            updateSiteLists();
            Site site = reaction.getSite();
            if(site != null){
                siteList[0].setSelectedValue(site, true);
                updateStateLists();
                State initialState = reaction.getInitialState();
                State finalState = reaction.getFinalState();
                if(initialState != null){
                    stateList[0].setSelectedValue(initialState, true);
                }
                if(finalState != null){
                    stateList[1].setSelectedValue(finalState, true);
                }
            }
            
            Site allostericSite = reaction.getAllostericSite();
            if(allostericSite != null){
                siteList[1].setSelectedValue(allostericSite, true);
                updateAllostericStateList();
                State alloState = reaction.getAllostericState();
                if(alloState != null){
                    allostericStateList.setSelectedValue(alloState, true);
                }
            }
        }
        // </editor-fold>
    }

    /* ************** LIST UPDATE METHODS ******************************/
    private void updateSiteLists() {
        siteModel.clear();
        Molecule molecule = (Molecule) moleculeList.getSelectedValue();
        if (molecule != null) {
            for (Site site : molecule.getSiteArray()) {
                siteModel.addElement(site);
            }
        }
    }

    private void updateStateLists() {
        stateModel.clear();
        Site site = (Site) siteList[0].getSelectedValue();
        if (site != null) {
            SiteType type = site.getType();
            if (type.getName().equals(SiteType.ANCHOR)) {
                PopUp.warning("Membrane anchors may not participate in reactions.");
                siteList[0].clearSelection();
            } else {
                for (State state : type.getStates()) {
                    stateModel.addElement(state);
                }
            }
        }
    }

    private void updateAllostericStateList() {
        allostericStateModel.clear();
        Site site = (Site) siteList[1].getSelectedValue();
        if (site != null) {
            SiteType type = site.getType();
            if (type.getName().equals(SiteType.ANCHOR)) {
                PopUp.warning("Membrane anchors may not participate in reactions.");
                siteList[1].clearSelection();
            } else {
                for (State state : type.getStates()) {
                    allostericStateModel.addElement(state);
                }
            }
        }
    }

    /* ***************** MAKE THE PANELS *****************************/
    private JPanel makeReactionPanel() {
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        int listWidth = 150;
        int listHeight = 100;

        JLabel titleLabel = new JLabel("Select the reaction's states.", JLabel.CENTER);
        titleLabel.setPreferredSize(new Dimension(6 * (listWidth + 15) + 40, titleLabel.getPreferredSize().height));
        titleLabel.setFont(Fonts.SUBTITLEFONT);

        JLabel moleculeLabel = new JLabel("Molecule", JLabel.CENTER);
        JLabel typeLabel = new JLabel("Site Type", JLabel.CENTER);
        JLabel[] stateLabel = new JLabel[2];
        stateLabel[0] = new JLabel("Initial State", JLabel.CENTER);
        stateLabel[1] = new JLabel("Final State", JLabel.CENTER);

        JScrollPane moleculePane = new JScrollPane(moleculeList);
        moleculePane.setPreferredSize(new Dimension(listWidth, listHeight));
        moleculePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        moleculePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JScrollPane typePane = new JScrollPane(siteList[0]);
        typePane.setPreferredSize(new Dimension(listWidth, listHeight));
        typePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        typePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JScrollPane[] statePane = new JScrollPane[2];
        for (int i = 0; i < 2; i++) {
            statePane[i] = new JScrollPane(stateList[i]);
            statePane[i].setPreferredSize(new Dimension(listWidth, listHeight));
            statePane[i].setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            statePane[i].setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }

        JPanel moleculePanel = new JPanel();
        moleculePanel.setLayout(new FlowLayout());
        moleculePanel.setPreferredSize(new Dimension(listWidth + 15, listHeight + 30));
        moleculePanel.add(moleculeLabel);
        moleculePanel.add(moleculePane);

        JPanel typePanel = new JPanel();
        typePanel.setLayout(new FlowLayout());
        typePanel.setPreferredSize(new Dimension(listWidth + 15, listHeight + 30));
        typePanel.add(typeLabel);
        typePanel.add(typePane);

        JPanel[] statePanel = new JPanel[2];
        for (int i = 0; i < 2; i++) {
            statePanel[i] = new JPanel();
            statePanel[i].setLayout(new FlowLayout());
            statePanel[i].setPreferredSize(new Dimension(listWidth + 15, listHeight + 30));
            statePanel[i].add(stateLabel[i]);
            statePanel[i].add(statePane[i]);
        }

        JLabel arrowLabel = new JLabel(" --> ", JLabel.CENTER);
        arrowLabel.setPreferredSize(new Dimension(40, listHeight + 30));
        arrowLabel.setVerticalAlignment(JLabel.CENTER);

        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new FlowLayout());
        innerPanel.setPreferredSize(new Dimension(6 * (listWidth + 15) + 60, listHeight + 60));
        innerPanel.add(titleLabel);
        innerPanel.add(moleculePanel);
        innerPanel.add(typePanel);
        innerPanel.add(statePanel[0]);
        innerPanel.add(arrowLabel);
        innerPanel.add(statePanel[1]);

        JPanel p = new JPanel();
        p.add(innerPanel);
        return p;
        // </editor-fold>
    }

    private JPanel makeAllostericPanel() {
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        int listWidth = 150;
        int listHeight = 100;

        JLabel alloLabel = new JLabel("Select allosteric site and state", JLabel.CENTER);
        alloLabel.setFont(Fonts.SUBTITLEFONT);

        JLabel siteLabel = new JLabel("Allosteric Site", JLabel.CENTER);
        JLabel stateLabel = new JLabel("Allosteric State", JLabel.CENTER);

        JScrollPane sitePane = new JScrollPane(siteList[1]);
        sitePane.setPreferredSize(new Dimension(listWidth, listHeight));
        sitePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sitePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JScrollPane statePane = new JScrollPane(allostericStateList);
        statePane.setPreferredSize(new Dimension(listWidth, listHeight));
        statePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        statePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JPanel sitePanel = new JPanel();
        sitePanel.setLayout(new FlowLayout());
        sitePanel.setPreferredSize(new Dimension(listWidth + 15, listHeight + 30));
        sitePanel.add(siteLabel);
        sitePanel.add(sitePane);

        JPanel statePanel = new JPanel();
        statePanel.setLayout(new FlowLayout());
        statePanel.setPreferredSize(new Dimension(listWidth + 15, listHeight + 30));
        statePanel.add(stateLabel);
        statePanel.add(statePane);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new GridLayout(1, 2));
        listPanel.setPreferredSize(new Dimension(2 * (listWidth + 15), listHeight + 60));

        JPanel siteContainer = new JPanel();
        siteContainer.setLayout(new FlowLayout());
        siteContainer.add(sitePanel);

        JPanel stateContainer = new JPanel();
        stateContainer.setLayout(new FlowLayout());
        stateContainer.add(statePanel);

        listPanel.add(siteContainer);
        listPanel.add(stateContainer);

        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(listPanel.getPreferredSize().width, listPanel.getPreferredSize().height + 20));
        p.setLayout(new BorderLayout());
        p.add(alloLabel, "North");
        p.add(listPanel, "Center");

        JPanel returnPanel = new JPanel();
        returnPanel.setLayout(new FlowLayout());
        returnPanel.add(p);

        return returnPanel;
        // </editor-fold>
    }

    /* ***************** LIST SELECTION LISTENER METHOD *************/
    @Override
    public void valueChanged(ListSelectionEvent event) {
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if (!event.getValueIsAdjusting()) {
            JList source = (JList) event.getSource();

            if (source == moleculeList) {
                reaction.setMolecule((Molecule) moleculeList.getSelectedValue());
                updateSiteLists();
            }

            if (source == siteList[0]) {
                reaction.setSite((Site) siteList[0].getSelectedValue());
                updateStateLists();
            }

            if (source == stateList[0]) {
                reaction.setInitialState((State) stateList[0].getSelectedValue());
            }

            if (source == stateList[1]) {
                reaction.setFinalState((State) stateList[1].getSelectedValue());
            }

            if (source == siteList[1]) {
                reaction.setAllostericSite((Site) siteList[1].getSelectedValue());
                updateAllostericStateList();
            }

            if (source == allostericStateList) {
                reaction.setAllostericState((State) allostericStateList.getSelectedValue());
            }
        }
        // </editor-fold>
    }

}
