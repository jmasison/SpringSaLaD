/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.*;

import org.springsalad.helpersetup.Fonts;

public class AnnotationPanel extends JPanel {
    
    private final AnnotationTextArea ata;
    
    public AnnotationPanel(Annotation annotation){
        
        ata = new AnnotationTextArea(annotation);
        JScrollPane taScrollPane = new JScrollPane(ata);
        taScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        taScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        JLabel annotationLabel = new JLabel("Annotation", JLabel.CENTER);
        annotationLabel.setFont(Fonts.SUBTITLEFONT);
        
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(300,300));
        this.add(annotationLabel, "North");
        this.add(taScrollPane, "Center");
    }
    
    public void setAnnotation(Annotation annotation){
        ata.setAnnotation(annotation);
    }
}
