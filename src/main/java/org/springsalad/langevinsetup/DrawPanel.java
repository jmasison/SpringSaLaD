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

import org.springsalad.helpersetup.Fonts;

public class DrawPanel extends JPanel implements MouseListener,
                MouseMotionListener, KeyListener, MoleculeSelectionListener {
    
    // Helps to have some fonts and related fontmetrics
    private final FontMetrics fm;
    private final FontMetrics rfm;
    private final FontMetrics axesfm;
    
    // The conversion factor between pixels and nanometers
    public static int PIXELS_PER_NM = 20;
    public static double NM_PER_PIXEL = 0.05;
    
    // The molecule we're drawing
    private final Molecule molecule;
    
    /* ********  Arrays of selected sites and links ******/
    private ArrayList<Site> selectedSites = new ArrayList<>();
    private ArrayList<Link> selectedLinks = new ArrayList<>();
    
    /* ********* Array of MoleculeSelectionListeners to notify *******/
    private final ArrayList<MoleculeSelectionListener> listeners = new ArrayList<>();
    
    /* ********  Array of anchors and the anchor positions ****/
    // Location of anchor, in nm
    private double anchorZ;
    // Define a default membrane location, in nm
    public static double DEFAULT_MEMBRANE_LOCATION = 15;
    
    // Boolean to tell us if the membrane is selected
    private boolean membraneSelected = false;
    
    /* Booleans to tell me whether or not to drag sites or the membrane */
    private boolean dragSites = false;
    private boolean dragMembrane = false;
    
    /* Boolean to indicate if the ctrl key is pressed */
    private boolean ctrlPressed = false;
    
    /* ************** Pixel locations of mouse clicks ***************/
    private int zOld = 0;
    private int yOld = 0;
    
    /* ************** Labels for the membrane ***********************/
    
    private final String INTRACELLULAR = "Intracellular";
    private final String EXTRACELLULAR = "Extracellular";
    private final int extraLabelWidth;
    private final int labelHeight;
    
    /* ************** THE SELECTION RECTANGLE ************************/
    private int z0rect; // The original click location
    private int y0rect;
    private int y0; // The upper left corner of the rectangle
    private int z0;
    private int Wrect;
    private int Hrect;
    private boolean dragSelectionRectangle = false;
    private Rectangle selectionRectangle = null;
    
    /* ******************* CONSTRUCTOR ************************/
    
    public DrawPanel(Molecule molecule){
        this.molecule = molecule;
        
        // Shift the molecule so that it is visible on the screen
        shiftMolecule();
        
        // Shift the membrane to be at the location of the anchor sites
        shiftMembrane();
        
        this.setOpaque(true);
        this.setBackground(Color.white);
        this.setPreferredSize(new Dimension(1000,1000));
        // Implicitly using the panel's graphics object to get the fontMetrics.
        fm = getFontMetrics(Fonts.SITE_FONT);
        rfm = getFontMetrics(Fonts.RULER_FONT);
        axesfm = getFontMetrics(Fonts.SUBTITLEFONT);
        
        extraLabelWidth = rfm.stringWidth(EXTRACELLULAR);
        labelHeight = rfm.getAscent() - rfm.getDescent();
        
        // This panel should listen for its own events
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    /* ******* Static method to change the pixels per nm *****/ 
    public static void setPixelsPerNm(int pixels){
        PIXELS_PER_NM = pixels;
        NM_PER_PIXEL = 1.0/pixels;        
    }
    
    /* * METHOD TO SELECT SITES AND LINK BASED ON THE SELECTION RECTANGLE **/
    private void getSelectionRectanglePicks(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        for(Site site : molecule.getSiteArray()){
            // Make sure it's not already selected
            if(!selectedSites.contains(site)){
                int px = (int)Math.round(PIXELS_PER_NM*site.getZ());
                int py = (int)Math.round(PIXELS_PER_NM*site.getY());
                if(selectionRectangle.contains(px,py)){
                    selectedSites.add(site);
                }
            }
        }
        
        for(Link link : molecule.getLinkArray()){
            // Make sure it's not already selected
            if(!selectedLinks.contains(link)){
                int px0 = (int)Math.round(PIXELS_PER_NM*link.getSite1().getZ());
                int py0 = (int)Math.round(PIXELS_PER_NM*link.getSite1().getY());
                int px1 = (int)Math.round(PIXELS_PER_NM*link.getSite2().getZ());
                int py1 = (int)Math.round(PIXELS_PER_NM*link.getSite2().getY());
                if(selectionRectangle.contains(px0, py0)
                        && selectionRectangle.contains(px1, py1)){
                    selectedLinks.add(link);
                }
            }
        }
        // </editor-fold>
    }
    
    /* ********** METHOD TO SHIFT THE WHOLE MOLECULE TO PUT IT ON SCREEN ***/
    
    private void shiftMolecule(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        double minY = 0;
        double minZ = 0;
        for(Site site : molecule.getSiteArray()){
            if(site.getZ() < minZ){
                minZ = site.getZ();
            }
            if(site.getY() < minY){
                minY = site.getY();
            }
        }
        // Now reduce them further by 10 nm
        minY -= 2;
        minZ -= 2;
        
        // Now translate the molecule so it will appear on the screen
        molecule.translate(0, -minY, -minZ);
        // </editor-fold>
    }
    
    private void shiftMembrane(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Double membranePosition = molecule.membranePosition();
        if(membranePosition == null){
            anchorZ = DEFAULT_MEMBRANE_LOCATION;
        } else {
            anchorZ = membranePosition;
        }
        // </editor-fold>
    }
    
    /* ******* MOUSE LISTENER METHODS **************/
    
    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent me) {
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        // System.out.println("Requesting focus.");
        this.requestFocusInWindow();
        // System.out.println("Should have focus.");
        boolean clickedLink = false;
        boolean clickedSite = false;
        dragSelectionRectangle = false;
        
        int xm = 0;
        int ym = me.getY();
        int zm = me.getX();
        
//        System.out.println("Received mouse press at (" + xm + ", " + ym + ", " + zm + ")");
        // System.out.println("Clearing selected sites.");
        if(!ctrlPressed){
            selectedSites.clear();
            selectedLinks.clear();
            this.notifyListeners();
        }
        
        membraneSelected = false;
        
        if(molecule.hasAnchorSites()){
            int anchorPixel = (int)Math.round(PIXELS_PER_NM*anchorZ);
            if(zm > (anchorPixel-2) && zm < (anchorPixel+2)){
                membraneSelected = true;
                // System.out.println("Clearing in mousePressed : hasAnchorSites");
                selectedSites.clear();
                selectedLinks.clear();
                dragMembrane = true;
            }
        }

        // Links take precedence over membrane
        for(Link link : molecule.getLinkArray()){
            if(link.contains(xm, ym, zm)){
                clickedLink = true;
                membraneSelected = false;
                dragMembrane = false;
                if(!selectedLinks.remove(link)){
                    selectedLinks.add(link);
                }
                notifyListeners();
                break;
            }
        }
        
        // Sites take precedence over links
        for(Site site : molecule.getSiteArray()){
//            System.out.println("Site is at (" + site.getX()*PIXELS_PER_NM + ", " + site.getY()*PIXELS_PER_NM + ")");
            if(site.contains(xm,ym,zm)){
//                System.out.println("Hit site!");
                dragSites = true;
                clickedSite = true;
                clickedLink = false;
                membraneSelected = false;
                dragMembrane = false;
                // If the site was previously selected we should remove it
                // Otherwise, add it to the list of selected sites
                if(!selectedSites.remove(site)){
                    selectedSites.add(site);
                    // System.out.println("Added site.  selectedSites = " + selectedSites);
                } 
                notifyListeners();
                break;
            }
        }
        
//        System.out.println("The selected sites are " + selectedSites.toString());
        
        if(!clickedSite && !clickedLink && !membraneSelected){
            z0rect = zm;
            y0rect = ym;
            Wrect = 0;
            Hrect = 0;
            dragSelectionRectangle = true;
            selectionRectangle = new Rectangle(z0rect, y0rect, Wrect, Hrect);
        }
        
        zOld = zm;
        yOld = ym;
        // System.out.println("Repainting from mouse clicked.");
        repaint();
        // </editor-fold>
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        if(!ctrlPressed){
            dragMembrane = false;
            dragSites = false;
        }
        if(selectionRectangle != null){
            getSelectionRectanglePicks();
            selectionRectangle = null;
            dragSelectionRectangle = false;
            notifyListeners();
        }
        repaint();
        // </editor-fold>
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    /* ********** MOUSE MOTION LISTENER METHODS **********/
    
    @Override
    public void mouseDragged(MouseEvent me) {
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        if(dragSites){
            int zcurr = me.getX();
            int ycurr = me.getY();
            int zshift = zcurr - zOld;
            int yshift = ycurr - yOld;
//            System.out.println("(xold, yold) = (" + zOld + ", " + yOld + ")");
//            System.out.println("(zcurr, ycurr) = (" + zcurr + ", " + ycurr + ")");
//            System.out.println("(zshift, yshift) = (" + zshift + ", " + yshift + ")");
            for(Site site : selectedSites){
                if(molecule.getAnchorSites().contains(site)){
                    zshift = 0;
                    break;
                }
            }
            for(Site site : selectedSites){
                site.setZ(site.getZ() + zshift*NM_PER_PIXEL);
                site.setY(site.getY() + yshift*NM_PER_PIXEL);
//                System.out.println("(xpos, ypos) = (" + site.getX()*PIXELS_PER_NM + ", " + site.getY()*PIXELS_PER_NM + ")");
            }
            zOld = zcurr;
            yOld = ycurr;
            
            notifyListeners();
            // System.out.println("Repainting from mouseDragged.");
            repaint();
        }
        
        else if(dragMembrane){
            int xcurr = me.getX();
            int zshift = xcurr - zOld;
            anchorZ += NM_PER_PIXEL*zshift;
            for(Site site : molecule.getAnchorSites()){
                site.setZ(site.getZ() + zshift*NM_PER_PIXEL);
            }
            zOld = xcurr;
            // System.out.println("Repainting from mouseDragged dragMembrane.");
            repaint();
        }
        
        else if(dragSelectionRectangle){
            int zcurr = me.getX();
            int ycurr = me.getY();
            Wrect = zcurr - z0rect;
            Hrect = ycurr - y0rect;
            z0 = z0rect;
            y0 = y0rect;
            if(Wrect < 0){
                z0 = z0rect + Wrect;
                Wrect = -Wrect;
            }
            if(Hrect < 0){
                y0 = y0rect + Hrect;
                Hrect = -Hrect;
            }
            selectionRectangle.setRect(z0, y0, Wrect, Hrect);
            repaint();
        }
        // </editor-fold>
    }

    @Override
    public void mouseMoved(MouseEvent e) {}
    
    /* ************ KEY LISTENER METHODS ******************/
    @Override
    public void keyReleased(KeyEvent event){
        if(ctrlPressed){
            ctrlPressed = false;
        }
    }
    
    @Override
    public void keyPressed(KeyEvent event){
        if(event.isControlDown() && !ctrlPressed){
            ctrlPressed = true;
        }
    }
    
    @Override
    public void keyTyped(KeyEvent event){}
    
    /* ************ LISTEN FOR MOLECULE SELECTION EVENTS ************/
    
    @Override
    public void selectionOccurred(MoleculeSelectionEvent event){
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        // System.out.println("Setting selected sites to " + selectedSites);
        selectedSites = event.getSelectedSites();
        selectedLinks = event.getSelectedLinks();
        // System.out.println("After setting selectedSites = " + selectedSites);
        Double membranePosition = molecule.membranePosition();
        if(membranePosition != null){
            anchorZ = membranePosition;
        }
        // System.out.println("Repainting from selection occurred. selectedSites = " + selectedSites);
        this.repaint();
        // </editor-fold>
    }
    
    /* MOLECULE SELECTION LISTENER NOTIFICATION AND RELATED METHODS *********/
    
    public void addMoleculeSelectionListener(MoleculeSelectionListener listener){
        listeners.add(listener);
    }
    
    public void removeMoleculeSelectionListener(MoleculeSelectionListener listener){
        listeners.remove(listener);
    }
    
    private void notifyListeners(){
        // System.out.println("Sending the molecule editor selectedSites = " + selectedSites);
        MoleculeSelectionEvent event = new MoleculeSelectionEvent(selectedSites, selectedLinks);
        for(MoleculeSelectionListener listener : listeners){
            listener.selectionOccurred(event);
        }
    }
    
    /* ********  DRAWING METHODS ************************/
    
    private void drawAxes(Graphics g){
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        int d = PIXELS_PER_NM;
        int yshift = 40;
        int zshift = 50;
        g.setColor(Color.black);
        g.drawLine(zshift, yshift, 5*d + zshift + 10, yshift);
        g.fillPolygon(new int[]{5*d+zshift+10,5*d+zshift+20,5*d+zshift+10},
                new int[]{yshift+5,yshift,yshift-5}, 3);
        g.drawLine(zshift, yshift, zshift, 5*d + yshift + 10);
        g.fillPolygon(new int[]{zshift-5, zshift, zshift+5},
                new int[]{5*d+yshift+10, 5*d+yshift+20, 5*d+yshift+10}, 3);
        g.setFont(Fonts.RULER_FONT);
        int h = rfm.getAscent()-rfm.getDescent();
        int [] w = new int[6];
        for(int i=0;i<6;i++){
            w[i] = rfm.stringWidth(Integer.toString(i));
            g.drawString(Integer.toString(i), zshift + i*d - w[i]/2, yshift-8);
            g.drawLine(zshift + i*d, yshift-4, zshift + i*d, yshift+4);
            g.drawString(Integer.toString(i), zshift - 8 - w[i], yshift + i*d + h/2);
            g.drawLine(zshift - 4, yshift + i*d, zshift+4, yshift+i*d);
        }
        String zLabel = "z (nm)";
        String yLabel0 = "y";
        String yLabel1 = "(nm)";
        int zLabelWidth = axesfm.stringWidth(zLabel);
        int yLabelWidth0 = axesfm.stringWidth(yLabel0);
        int yLabelWidth1 = axesfm.stringWidth(yLabel1);
        int h1 = axesfm.getAscent() - axesfm.getDescent();
        g.setFont(Fonts.SUBTITLEFONT);
        g.drawString(zLabel, zshift + 5*d/2 - zLabelWidth/2, yshift-8-h-6);
        g.drawString(yLabel0, zshift-8-w[0]-4-yLabelWidth1/2-yLabelWidth0/2, yshift + 5*d/2 - h1/2 - 3);
        g.drawString(yLabel1, zshift-8-w[0]-4-yLabelWidth1, yshift + 5*d/2 + h1/2 + 3);
        
        // </editor-fold>
    }
    
    private void drawMembrane(Graphics g){
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        int anchorPixel = (int)Math.round(anchorZ*PIXELS_PER_NM);
        g.setColor(Color.orange);
        int h = this.getHeight();
        g.fillRect(anchorPixel - 2, 0, 4, h);
        if(membraneSelected){
            g.setColor(Color.BLACK);
            g.drawRect(anchorPixel-2, 0, 4, h);
        }
        
        g.setColor(Color.BLACK);
        g.setFont(Fonts.RULER_FONT);
        g.drawString(INTRACELLULAR, anchorPixel + 4, labelHeight + 5);
        g.drawString(EXTRACELLULAR, anchorPixel-extraLabelWidth-4, labelHeight + 5);
        // </editor-fold>
    }
    
    private void drawSelectionRectangle(Graphics g){
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        if(selectionRectangle != null){
            g.setColor(Color.CYAN);
            g.drawRect(z0, y0, Wrect, Hrect);
        }
        // </editor-fold>
    }
    
    
    /* ****************  PANEL PAINT METHOD ****************/
    
    @Override
    public void paintComponent(Graphics g){
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        super.paintComponent(g);
        // Draw ruler first
        drawAxes(g);
        
        // Paint the membrane second
        if(molecule.hasAnchorSites()){
            drawMembrane(g);
        }
        
        // Draw the selection rectangle if not null 
        drawSelectionRectangle(g);

        // Paint links before sites
        for(Link link : molecule.getLinkArray()){
            link.draw(g);
        }
        
        // Paint sites second
        g.setFont(Fonts.SITE_FONT);
        int height = (fm.getAscent() - fm.getDescent());
        int widthX = fm.stringWidth("X");
        
        for(Site site : molecule.getSiteArray()){
            site.draw(g);
            String label = Integer.toString(site.getIndex());
            int width = fm.stringWidth(label);
            site.drawLabel(g, label , width/2, height/2);
            
            if(molecule.getLocation().equals(SystemGeometry.MEMBRANE)){
                if(site.getLocation().equals(SystemGeometry.INSIDE)){
                    if((site.getZ()-site.getRadius()) < anchorZ){
                        site.setPositionOK(false);
                        site.drawNotOK(g, widthX, height);
                    } else {
                        site.setPositionOK(true);
                    }
                } else if(site.getLocation().equals(SystemGeometry.OUTSIDE)){
                    if((site.getZ()+site.getRadius()) > anchorZ){
                        site.setPositionOK(false);
                        site.drawNotOK(g, widthX, height);
                    } else {
                        site.setPositionOK(true);
                    }
                } else {
                    site.setPositionOK(true);
                }
            }
            
        }
        
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ));
        // If a link is selected, then highlight it last so that it will 
        // overlay all the non-selected sites.
        for(Link link : selectedLinks){
            link.draw(g2);
        }
        
        // System.out.println("selectedSites just before drawing them:" + selectedSites);
        for(Site site : selectedSites){
            site.drawSelected(g2);
            String label = Integer.toString(site.getIndex());
            int width = fm.stringWidth(label);
            site.drawLabel(g, label , width/2, height/2);
            if(!site.getPositionOK()){
                site.drawNotOK(g, widthX, height);
            }
        }
        // </editor-fold>
    }
    
}
