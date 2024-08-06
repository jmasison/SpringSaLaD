/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.springsalad.helpersetup.IOHelp;

public class Site {
  
    // Position, given in nm.  
    double x = 0;
    double y = 0; 
    double z = 0;
    
    // Every site is part of a molecule. Give each site a reference to its
    // molecule and an index to identify it within the molecule.
    private final Molecule molecule;
    private int index;
    
    // Every site has a type, which determines all its properties.
    private SiteType type;
    
    // The initial state of the site
    private State initialState;
    
    // Keep a list of the sites connected to this site.  This makes it easy 
    // to determine if all sites in a molecule are connected.
    private final ArrayList<Site> connectedSites = new ArrayList<>();
    // A boolean to tell us if we've checked the connectivity of this site.
    private boolean checked = false;
    
    // Sites are not assigned a location initially
    private String location = null;
    // Boolean to tell us if the site is positioned correctly
    private boolean positionOK = true;
    
    // Each site has it's own site property counter
    private final SitePropertyCounter sitePropertyCounter;
    
    public Site(Molecule molecule, SiteType type){
        this.molecule = molecule;
        this.type = type;
        this.sitePropertyCounter = new SitePropertyCounter(this);
    }
    
    public Site(Molecule molecule){
        this.molecule = molecule;
        this.sitePropertyCounter = new SitePropertyCounter(this);
    }
    
    /* ****************** TOSTRING ***************************************/
    
    @Override
    public String toString(){
        return "Site " + index + " : " + getTypeName();
    }
    
    /* **************** METHODS RELATED TO PARENT MOLECULE *****************/
    
    public Molecule getMolecule(){
        return molecule;
    }
    
    public void setIndex(int i){
        index = i;
    }
    
    public int getIndex(){
        return index;
    }
    
    /* ************** GET AND SET THE POSITION COMPONENTS ****************/
    
    public void setX(double x){
        this.x = x;
    }
    
    public void setY(double y){
        this.y = y;
    }
    
    public void setZ(double z){
        this.z = z;
    }
    
    public void setPosition(double x, double y, double z){
        setX(x); setY(y); setZ(z);
    }
    
    public double getX(){
        return x;
    }
    
    public double getY(){
        return y;
    }
    
    public double getZ(){
        return z;
    }
    
    public double [] getPosition(){
        return new double[]{x,y,z};
    }
    
    public void translate(double dx, double dy, double dz){
        x += dx;
        y += dy;
        z += dz;
    }
    
    /* ***********  METHODS RELATED TO THE SITE'S TYPE *******************/
    
    public void setType(SiteType type){
        this.type = type;
    }
    
    public SiteType getType(){
        return type;
    }
    
    public double getRadius(){
        return type.getRadius();
    }
    
    public double getD(){
        return type.getD();
    }
    
    public String getTypeName(){
        return type.getName();
    }
    
    public void setInitialState(State state){
        this.initialState = state;
    }
    
    public State getInitialState(){
        return initialState;
    }
    
    /* ********** METHODS RELATED TO SITE LOCATION ******************/
    
           
    public void setLocation(String location){
        if(location.equals(SystemGeometry.INSIDE) || location.equals(SystemGeometry.OUTSIDE) || location.equals(SystemGeometry.MEMBRANE)){
            this.location = location;
        } else {
            System.out.println("Tried to set site to an invalid location. Given string " + location + ".");
        }
    }
    
    public void setPositionOK(boolean bool){
        positionOK = bool;
    }
    
    public String getLocation(){
        return location;
    }
    
    public boolean getPositionOK(){
        return positionOK;
    }
    
    /* **************    CONNECTIVITY METHODS   ******************/
    
    public void connectTo(Site site){
        if(!connectedSites.contains(site)){
            connectedSites.add(site);
        }
    }
    
    public boolean hasLink(){
        return !connectedSites.isEmpty();
    }
    
    public void clearConnectedSites(){
        connectedSites.clear();
    }
    
    public ArrayList<Site> getConnectedSites(){
        return connectedSites;
    }
    
    public void setChecked(boolean bool){
        checked = bool;
    }
    
    public boolean getChecked(){
        return checked;
    }
    
    /* ***************** GET THE PROPERTY COUNTER **********************/
    
    public SitePropertyCounter getPropertyCounter(){
        return sitePropertyCounter;
    }

    
    /**************************************************************\
     *                      CONTAINS                              *
     *      Accepts an x and y position from the JPanel and       *
     *      tells if the point is within the sphere defined by    *
     *      the site.  Won't be using pz for a long time, if      *
     *      ever, but might as well put it in now.                *
     *                                                            *
     * @param px                                                  *
     * @param py                                                  *
     * @param pz                                                  *
     * @return                                                    *
    \**************************************************************/
    
    public boolean contains(int px, int py, int pz){
        boolean in = true;
        // Site radius
        double r = type.getRadius();
        // Convert pixel locations to nm using conversion.
        double convertInv = 1.00/DrawPanel.PIXELS_PER_NM;
        double pxn = px*convertInv;
        double pyn = py*convertInv;
        double pzn = pz*convertInv;
        // Calculate distance from center
        double l2 = (x-pxn)*(x-pxn) + (y-pyn)*(y-pyn) + (z-pzn)*(z-pzn);
        
        if(l2 > r*r){
            in = false;
        }
        
        return in;
    }
    
    /**************************************************************\
     *         Drawing method for the molecule editor panel       *
     * @param g                                                   *
    \**************************************************************/
    
    public void draw(Graphics g){
        double r = type.getRadius();
        Color c = type.getColor();
        g.setColor(c);
        int pixelsPerNm = DrawPanel.PIXELS_PER_NM;
        // In the drawPanel we map the z-coordinate to the x-coordinate
        int xint = (int)Math.round(pixelsPerNm*z);
        int yint = (int)Math.round(pixelsPerNm*y);
        int rint = (int)Math.round(pixelsPerNm*r);
        g.fillOval(xint-rint, yint-rint, 2*rint, 2*rint);
        g.setColor(Color.GRAY);
        g.drawOval(xint-rint, yint-rint, 2*rint, 2*rint);
    }
    
    public void drawSelected(Graphics2D g2){
        double r = type.getRadius();
        Color c = type.getColor();
        g2.setColor(c);
        int pixelsPerNm = DrawPanel.PIXELS_PER_NM;
        // In the drawPanel we map the z-coordinate to the x-coordinate
        int xint = (int)Math.round(pixelsPerNm*z);
        int yint = (int)Math.round(pixelsPerNm*y);
        int rint = (int)Math.round(pixelsPerNm*r);
        g2.fillOval(xint-rint, yint-rint, 2*rint, 2*rint);
        g2.setColor(Color.BLACK);
        g2.drawOval(xint-rint, yint-rint, 2*rint, 2*rint);
    }
    
    // Give the string and horizontal and verical offsets so that the label is 
    // drawn in the center. 
    public void drawLabel(Graphics g, String label, int hoffset, int voffset){
        g.setColor(Color.black);
        int pixelsPerNm = DrawPanel.PIXELS_PER_NM;
        int xint = (int)Math.round(pixelsPerNm*z);
        int yint = (int)Math.round(pixelsPerNm*y);
        g.drawString(label, xint-hoffset , yint + voffset);
    }
    
    public void drawNotOK(Graphics g, int widthX, int heightX){
        int pixelsPerNm = DrawPanel.PIXELS_PER_NM;
        int xint = (int)Math.round(pixelsPerNm*z);
        int yint = (int)Math.round(pixelsPerNm*y);
        int rint = (int)Math.round(pixelsPerNm*type.getRadius());
        g.setColor(Color.white);
        g.fillRect(xint-rint, yint+rint-heightX, widthX, heightX);
        g.setColor(Color.black);
        g.drawRect(xint-rint, yint+rint-heightX, widthX, heightX);
        g.drawString("X", xint - rint, yint + rint);
    }
    
    public boolean equals(Site s){
    	if(this.getPosition() == s.getPosition()){
    		return true;
    	}
    	return false;
    }
    
    /*********************************************************************\
     *                      FILE IO METHODS                              *
     * @param p                                                          *
    \*********************************************************************/
    
    public void writeSite(PrintWriter p){
        p.println("SITE " + this.getIndex() + " : " + this.location + " : Initial State '" + initialState + "'");
        p.print("          ");
        this.getType().writeType(p);
        p.println("          " + "x " + IOHelp.DF[5].format(getX()) + " y " + IOHelp.DF[5].format(getY()) + " z " + IOHelp.DF[5].format(getZ()) + " ");
    }
    
}
