/*
 * This class lets me write a single ReactionNameTextField class that will
 * manage the name for any reaction.
 */

package org.springsalad.langevinsetup;

import java.util.Scanner;

public abstract class Reaction {
    
    private final Annotation annotation = new Annotation();
    
    
    /* ******* ABSTRACT METHODS TO GET AND SET NAME *********************/
    
    public abstract String getName();
    
    public abstract void setName(String name);
    
    /* ******* ABSTRACT METHOD TO WRITE AND READ FILE ******************/
    
    public abstract String writeReaction();
    
    public abstract void loadReaction(Global g, Scanner sc);
    
    /* ******* CONCRETE METHODS TO WORK ON ANNOTATION *******************/
    
    public void setAnnotationString(String annotationString){
        this.annotation.setAnnotation(annotationString);
    }
    
    public Annotation getAnnotation(){
        return annotation;
    }
    
    public String getAnnotationString(){
        return annotation.getAnnotation();
    }
    
}
