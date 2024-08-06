/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.util.Scanner;

import org.springsalad.helpersetup.IOHelp;

import java.io.*;

public class BoxGeometry {
    
    /* ************* STATIC STRINGS FOR SYSTEM IO *********************/
    public final static String LX = "L_x";
    public final static String LY = "L_y";
    public final static String LZ_IN = "L_z_in";
    public final static String LZ_OUT = "L_z_out";
    public final static String NPARTX = "Partition Nx";
    public final static String NPARTY = "Partition Ny";
    public final static String NPARTZ = "Partition Nz";

    // All distances in nm
    private double x;
    private double y;
    private double zin; // Intracellular distance
    private double zout; // Extracellular distance
    
    // Number of partitions in each direction
    private final int [] npart = new int[3];
    
    // Size of partition
    private final double [] dpart = new double[3];
    
    public BoxGeometry(){
        // Assign default values
        x = 100.0;
        y = 100.0;
        zin = 90.0;
        zout = 10.0;
        
        for(int i=0;i<3;i++){
            npart[i] = 10;
            dpart[i] = 10.0;
        }
    }
    
    /* *************** SIZE MANAGEMENT **************************/
    
    public void setX(double x){
        this.x = x;
        setDpart(0);
    }
    
    public double getX(){
        return x;
    }
    
    public void setY(double y){
        this.y = y;
        setDpart(1);
    }
    
    public double getY(){
        return y;
    }
    
    public void setZin(double zin){
        this.zin = zin;
        setDpart(2);
    }
    
    public double getZin(){
        return zin;
    }
    
    public void setZout(double zout){
        this.zout = zout;
        setDpart(2);
    }
    
    public double getZout(){
        return zout;
    }
    
    public double getVolumeTotal(){
        return x*y*(zout + zin);
    }
    
    public double getVolumeIn(){
        return x*y*zin;
    }
    
    public double getVolumeOut(){
        return x*y*zout;
    }
    
    /* ****************** PARTITION MANAGEMENT ************************/
    
    public void setNpart(int i, int npart){
        this.npart[i] = npart;
        setDpart(i);
    }
    
    public int [] getNpart(){
        return npart;
    }
    
    public int getNpart(int i){
        return npart[i];
    }
    
    /* ****************  PARTITION SIZES ******************************/
    
    private void setDpart(int i){
        switch(i){
            case 0:
                dpart[0] = x/npart[0];
                break;
            case 1:
                dpart[1] = y/npart[1];
                break;
            case 2:
                dpart[2] = (zin + zout)/npart[2];
                break;
            default:
                System.out.println("Invalid input in BoxGeometry.setDpart(i).  Got i = " + i);
        }
    }
    
    public double [] getDpart(){
        return dpart;
    }
    
    public double getDpart(int i){
        return dpart[i];
    }
    
    /* ************** WRITE DATA *************************************/
    
    public void writeData(PrintWriter p){
        p.println(LX + ": " + x/1000);
        p.println(LY + ": " + y/1000);
        p.println(LZ_OUT + ": " + zout/1000);
        p.println(LZ_IN + ": " + zin/1000);
        p.println(NPARTX + ": " + npart[0]);
        p.println(NPARTY + ": " + npart[1]);
        p.println(NPARTZ + ": " + npart[2]);
    }
    
    /* ************* LOAD DATA ****************************************/
    
    public void loadData(String dataString){
        Scanner sc = new Scanner(dataString);
        while(sc.hasNextLine()){
            String [] next = sc.nextLine().split(":");
            switch(next[0]){
                case LX:
                    x = 1000*Double.parseDouble(next[1].trim());
                    break;
                case LY:
                    y = 1000*Double.parseDouble(next[1].trim());
                    break;
                case LZ_OUT:
                    zout = 1000*Double.parseDouble(next[1].trim());
                    break;
                case LZ_IN:
                    zin = 1000*Double.parseDouble(next[1].trim());
                    break;
                case NPARTX:
                    npart[0] = Integer.parseInt(next[1].trim());
                    setDpart(0);
                    break;
                case NPARTY:
                    npart[1] = Integer.parseInt(next[1].trim());
                    setDpart(1);
                    break;
                case NPARTZ:
                    npart[2] = Integer.parseInt(next[1].trim());
                    setDpart(2);
                    break;
                default:
                    System.out.println("BoxGeometry loadData received "
                            + "unexpected input. Input = " + IOHelp.printArray(next));
            }
        }
        sc.close();
    }
    
    /* ************* RESET TO DEFAULTS ********************************/
    
    public void reset(){
        x = 100.0;
        y = 100.0;
        zin = 90.0;
        zout = 10.0;
        
        for(int i=0;i<3;i++){
            npart[i] = 10;
            dpart[i] = 10.0;
        }
    }
    
}
