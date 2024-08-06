/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.awt.*;
import java.io.*;

public class Link {
    
    // Every link has two sites to which it connects.
    private final Site site1, site2;
    
    // Assign each link an index. This is unnecessary but makes the lists look nice.
    private int index;
    
    // Make it so link can only be created when given two sites.  There's no
    // reason to ever have a dangling bond. 
    public Link(Site site1, Site site2){
        this.site1 = site1;
        this.site2 = site2;
        site1.connectTo(site2);
        site2.connectTo(site1);
    }
    
    
    /* **********************************************************\
     *                     GET METHODS                          *
     *  Any set methods should be used on the sites themselves. *
    \************************************************************/
    
    public double getX1(){
        return site1.getX();
    }
    
    public double getY1(){
        return site1.getY();
    }
    
    public double getZ1(){
        return site1.getZ();
    }
    
    public double getX2(){
        return site2.getX();
    }
    
    public double getY2(){
        return site2.getY();
    }
    
    public double getZ2(){
        return site2.getZ();
    }

    public double getLength(){
        double dx = getX2() - getX1();
        double dy = getY2() - getY1();
        double dz = getZ2() - getZ1();
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
    
    public void setIndex(int index){
        this.index = index;
    }
    
    public int getIndex(){
        return index;
    }
    
    @Override
    public String toString(){
        return "Link " + index + " : Site " + site1.getIndex() + " :: Site " + site2.getIndex();
    }
    
    /* *********************************************************\
     *                 GET THE SITES                           *
    \***********************************************************/
 
    public Site getSite1(){
        return site1;
    }
    
    public Site getSite2(){
        return site2;
    }
    
    public Site [] getSites(){
        return new Site[]{site1,site2};
    }
    
    /* ************************************************************\
     *             UNIT VECTOR FROM SITE 1 TO SITE 2              *
    \**************************************************************/
    
    public double [] unitVector(){
        double dx = site2.getX() - site1.getX();
        double dy = site2.getY() - site1.getY();
        double dz = site2.getZ() - site1.getZ();
        double length = Math.sqrt(dx*dx + dy*dy + dz*dz);
        return new double[]{dx/length, dy/length, dz/length};
    }
    
    /* ************************************************************\
     *                      CONTAINS                              *
     *      Accepts an x and y position from the JPanel and       *
     *      tells if the point is within 2 pixels of the line     *
     *      defined by the link.                                  *
    \**************************************************************/
    
    public boolean contains(int px, int py, int pz){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        /**
         * Given a line defined by the formula Ax + By + C = 0 and a point
         * (x0, y0), the shortest distance between the point and the line 
         * is given by r = (A x0 + B y0 + C) / sqrt(A^2 + B^2). Also, given
         * two points (x1, y1) and (x2, y2), they define a line with 
         * A = y1-y2, B = x2-x1, and C = x1 y2 - x2 y1 . So we'll take our sites
         * to find A, B, and C, and then use these in combination with the 
         * mouse click to find the distance between the mouse click and 
         * the line.
         */ 
        boolean in = false;
        int pixelsPerNm = DrawPanel.PIXELS_PER_NM;
        
        int z1 = (int)(pixelsPerNm*site1.getZ());
        int z2 = (int)(pixelsPerNm*site2.getZ());
        int y1 = (int)(pixelsPerNm*site1.getY());
        int y2 = (int)(pixelsPerNm*site2.getY());
        if(z1 > z2){
            int tempx = z2;
            int tempy = y2;
            z2 = z1;
            y2 = y1;
            z1 = tempx;
            y1 = tempy;
        }
        int A = y1 - y2;
        int B = z2 - z1;
        int C = z1 * y2 - z2 * y1;
        // Check to make sure we're in the right x range
        if(z1 < pz && pz < z2){
            // Calculate distance to line
            double r = Math.abs(A * pz + B * py + C)/Math.sqrt(A*A + B*B);
            if(r < 3){
                in = true;
            }
        }
        return in;
        // </editor-fold>
    }
    
    /* *********************************************************\
     *                      DRAW METHOD                        *
     *   For now, always draw a black line.                    *
    \***********************************************************/
    
    public void draw(Graphics g){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        g.setColor(Color.black);
        int pixelsPerNm = DrawPanel.PIXELS_PER_NM;
        // Map site z to drawPanel x
        int x1 = (int)(pixelsPerNm*site1.getZ());
        int x2 = (int)(pixelsPerNm*site2.getZ());
        int y1 = (int)(pixelsPerNm*site1.getY());
        int y2 = (int)(pixelsPerNm*site2.getY());
        g.drawLine(x1, y1, x2, y2);
        // </editor-fold>
    }
    
    public void draw(Graphics2D g2){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        g2.setColor(Color.black);
        int pixelsPerNm = DrawPanel.PIXELS_PER_NM;
        // Map site z to drawPanel x
        int x1 = (int)(pixelsPerNm*site1.getZ());
        int x2 = (int)(pixelsPerNm*site2.getZ());
        int y1 = (int)(pixelsPerNm*site1.getY());
        int y2 = (int)(pixelsPerNm*site2.getY());
        g2.drawLine(x1, y1, x2, y2);
        // </editor-fold>
    }
    
    /*****************************************************************\
     *                    FILE IO METHODS                            *
     *   I won't have a readLink() method.  It'll be easiest to      *
     *   construct the links when I construct the whole molecule,    *
     *   because then I'll have the references to the sites on hand. *
     *                                                               *
     * @param p The PrintWriter.                                     *
    \*****************************************************************/
    
    public void writeLink(PrintWriter p){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(site1 == null){
            System.out.println("Site 1 is null.");
        }
        if(site2 == null){
            System.out.println("Site 2 is null.");
        }
        p.println("LINK: Site " + site1.getIndex() + " ::: Site " + site2.getIndex());
        // </editor-fold>
    }

    
    
    
}
