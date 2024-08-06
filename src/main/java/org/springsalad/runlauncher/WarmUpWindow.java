/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.runlauncher;

import java.io.File;
import java.awt.Container;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.springsalad.helpersetup.Fonts;

public class WarmUpWindow extends JFrame implements Runnable {
    
    Thread t;
    ProgressPanel pp;
    
    public WarmUpWindow(File outputFile){
        super("Initializing");
        t = new Thread(this);
        Container c = this.getContentPane();
        c.setLayout(new FlowLayout());
        JLabel label0 = new JLabel("The simulation engine is warming up."
                                                            , JLabel.CENTER);
        JLabel label1 = new JLabel("Please be patient, "
                    + "this will only take a few seconds.", JLabel.CENTER);
        label0.setFont(Fonts.SUBTITLEFONT);
        label1.setFont(Fonts.SUBTITLEFONT);
        pp = new ProgressPanel("", -1, outputFile);
        pp.startProgressBar();
        c.add(label0);
        c.add(label1);
        c.add(pp);

    }
    
    @Override
    public void run(){
        watchProgress();
    }
    
    public void showWarmUpWindow(){
        this.setSize(350, 125);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        t.start();
    }
    
    public void watchProgress(){
        while(true){
            try{
                Thread.sleep(2000);
            } catch(InterruptedException ie){
                ie.printStackTrace(System.out);
            }
            if(pp.isFinished()){
                break;
            }
        }
        this.setVisible(false);
        this.dispose();
    }
    
    
}
