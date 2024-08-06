
package org.springsalad.langevinsetup;

import java.util.ArrayList;

import javax.swing.*;

import org.springsalad.helpersetup.PopUp;

import java.awt.*;
import java.awt.event.*;

public class ClusterCreator extends JFrame implements ActionListener {
    
    
    private final Molecule molecule;
    
    private final JButton makeButton = new JButton("Make cluster");
    private final JButton cancelButton = new JButton("Cancel");
    
    private final JTextField rowTF = new JTextField("5", 5);
    private final JTextField columnTF = new JTextField("5", 5);
    private final JTextField spacingTF = new JTextField("5", 5);
    
    public ClusterCreator(Molecule molecule){
        super("Cluster Creator");
        this.molecule = molecule;
        
        init();
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }
    
    private void init(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JLabel rowLabel = new JLabel("Rows: ", JLabel.RIGHT);
        JLabel columnLabel = new JLabel("Columns: ", JLabel.RIGHT);
        JLabel spacingLabel = new JLabel("Center-to-center distance between anchors (nm): ", JLabel.RIGHT);
        
        Container c = this.getContentPane();
        c.setLayout(new GridLayout(3,1));
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new FlowLayout());
        rowPanel.add(rowLabel);
        rowPanel.add(rowTF);
        topPanel.add(rowPanel);
        
        JPanel columnPanel = new JPanel();
        columnPanel.setLayout(new FlowLayout());
        columnPanel.add(columnLabel);
        columnPanel.add(columnTF);
        topPanel.add(columnPanel);
        
        c.add(topPanel);
        
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new FlowLayout());
        middlePanel.add(spacingLabel);
        middlePanel.add(spacingTF);
        c.add(middlePanel);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());
        bottomPanel.add(makeButton);
        bottomPanel.add(cancelButton);
        c.add(bottomPanel);
        
        makeButton.addActionListener(this);
        cancelButton.addActionListener(this);
        // </editor-fold>
    }
    
    private boolean checkInput(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        boolean ok = true;
        
        try{
            int row = Integer.parseInt(rowTF.getText());
            if(row < 0){
                ok = false;
                org.springsalad.helpersetup.PopUp.error("Number of rows must be non-negative.");
            }
        } catch(NumberFormatException nfe){
            ok = false;
            PopUp.error("Number of rows is not an integer value.");
        }
        
        try{
            int column = Integer.parseInt(columnTF.getText());
            if(column < 0){
                ok = false;
                org.springsalad.helpersetup.PopUp.error("Number of columns must be non-negative.");
            }
        } catch(NumberFormatException nfe){
            ok = false;
            PopUp.error("Number of columns is not an integer value.");
        }
        
        try{
            double space = Double.parseDouble(spacingTF.getText());
            if(space <= 0){
                ok = false;
                org.springsalad.helpersetup.PopUp.error("Spacing must be positive.");
            }
        } catch(NumberFormatException nfe){
            ok = false;
            PopUp.error("Space between anchors is not a number.");
        }
        
        return ok;
        // </editor-fold>
    }
    
    // Return a reference to the new anchor, so it's easier to link up the anchors
    private Site cloneMolecule(ArrayList<Site> originalSites, ArrayList<Link> originalLinks, double dx, double dy){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        ArrayList<Site> newSites = new ArrayList<>();
        ArrayList<Link> newLinks = new ArrayList<>();
        Site newAnchor = null;
        for(Site site : originalSites){
            Site newSite = new Site(molecule, site.getType());
//            System.out.println("Location of original site: " + site.getX() + ", " + site.getY() + ", " + site.getZ());
            if(site.getType().getName().equals(SiteType.ANCHOR)){
                newAnchor = newSite;
            }
            newSite.setInitialState(site.getInitialState());
            newSite.setLocation(site.getLocation());
            newSite.setX(site.getX() + dx);
            newSite.setY(site.getY() + dy);
            newSite.setZ(site.getZ());
//            System.out.println("Location of new site: " + newSite.getX() + ", " + newSite.getY() + ", " + newSite.getZ());
            newSites.add(newSite);
        }
        
        molecule.addSiteArray(newSites);
        
        for(Link link : originalLinks){
            Site site1 = link.getSite1();
            Site site2 = link.getSite2();
            
            Site newSite1 = null;
            Site newSite2 = null;
//            System.out.println("Location of site 1: " + site1.getX() + ", " + site1.getY() + ", " + site1.getZ());
//            System.out.println("Location of site 2: " + site2.getX() + ", " + site2.getY() + ", " + site2.getZ());
            for(Site site : newSites){
//                System.out.println("Location of new site: " + site.getX() + ", " + site.getY() + ", " + site.getZ());
                if(site.getX() > site1.getX() + dx - 0.001 && site.getX() < site1.getX() + dx + 0.001 
                        && site.getY() > site1.getY()+ dy -0.001 && site.getY() < site1.getY() + dy + 0.001
                        && site.getZ() > site1.getZ()-0.001 && site.getZ() < site1.getZ()+0.001){
                    newSite1 = site;
                } else if(site.getX() > site2.getX() + dx - 0.001 && site.getX() < site2.getX() + dx + 0.001 
                        && site.getY() > site2.getY()+ dy -0.001 && site.getY() < site2.getY() + dy + 0.001
                        && site.getZ() > site2.getZ()-0.001 && site.getZ() < site2.getZ()+0.001){
                    newSite2 = site;
                } else {
                    // System.out.println("Could not match site: " + site.toString());
                }
            }
            
            if(newSite1 == null){
                System.out.println("Could not match site1.");
            }
            if(newSite2 == null){
                System.out.println("Count not match site2.");
            }
            
            newLinks.add(new Link(newSite1, newSite2));
        }
        
        for(Link link : newLinks){
            molecule.addLink(link);
        }
        
        return newAnchor;
        // </editor-fold>
    }
    
    private void createCluster(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        int rows = Integer.parseInt(rowTF.getText());
        int columns = Integer.parseInt(columnTF.getText());
        double spacing = Double.parseDouble(spacingTF.getText());
        
        ArrayList<Site> originalSites = new ArrayList<>();
        for(Site site : molecule.getSiteArray()){
            originalSites.add(site);
        }
        ArrayList<Link> originalLinks = new ArrayList<>();
        for(Link link : molecule.getLinkArray()){
            originalLinks.add(link);
        }
        
        // Build up a list of anchors
        Site [][] anchors = new Site[rows][columns];
        anchors[0][0] = molecule.getAnchorSites().get(0);
        
        for(int i=0;i<rows;i++){
            for(int j=0;j<columns;j++){
                if(!(i==0 && j==0)){
                    anchors[i][j] = cloneMolecule(originalSites, originalLinks, i*spacing, j*spacing);
                }
            }
        }
        
        ArrayList<Link> links = new ArrayList<>();
        // Now link up the anchors
        for(int i=0;i<rows;i++){
            for(int j=0;j<columns;j++){
                if(i != rows-1){
                    links.add(new Link(anchors[i][j], anchors[i+1][j]));
                }
                if(j != columns-1){
                    links.add(new Link(anchors[i][j], anchors[i][j+1]));
                }
            }
        }
        
        for(Link link : links){
            molecule.addLink(link);
        }
        
        // </editor-fold>
    }
    
    @Override
    public void actionPerformed(ActionEvent event){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JButton button = (JButton)event.getSource();
        
        if(button == makeButton){
            if(checkInput()){
                createCluster();
                this.setVisible(false);
                this.dispose();
            }
        }
        
        if(button == cancelButton){
            this.setVisible(false);
            this.dispose();
        }
        // </editor-fold>
    }
    
//    public static void main(String [] args){
//        Molecule mol = new Molecule("Hi");
//        new ClusterCreator(mol);
//    }
}
