/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.runlauncher;

import javax.swing.*;
import java.awt.Container;
import java.awt.GridLayout;

public class ProgressPanelFrame extends JFrame{

    public ProgressPanelFrame(ProgressPanel [] progressPanel){
        super("Simulation Progress");
        int number = progressPanel.length;
        Container c = this.getContentPane();
        c.setLayout(new GridLayout(number, 1));
        for(int i=0;i<number;i++){
            c.add(progressPanel[i]);
        }
        
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }
}
