/*
 * I'm breaking the model-view-controller paradigm because I'm just going 
 * to give this textarea the string which holds the annotation of the 
 * object we're annotating. Eventually I should get around to fixing this.
 */

package org.springsalad.langevinsetup;

import java.awt.event.*;
import javax.swing.JTextArea;

public class AnnotationTextArea extends JTextArea implements FocusListener {
    
    private Annotation annotation;
    
    public AnnotationTextArea(Annotation annotation){
        this.annotation = annotation;
        if(annotation != null){
            this.setText(annotation.getAnnotation());
        } else {
            this.setText("");
        }
        this.addFocusListener(this);
    }
    
    public void setAnnotation(Annotation annotation){
        this.annotation = annotation;
        if(annotation != null){
            this.setText(annotation.getAnnotation());
        } else {
            this.setText("");
        }
    }
    
    @Override
    public void focusGained(FocusEvent e) {
        // Do nothing
    }

    @Override
    public void focusLost(FocusEvent e) {
        if(annotation != null){
            annotation.setAnnotation(this.getText());
        }
    }
    
    
}
