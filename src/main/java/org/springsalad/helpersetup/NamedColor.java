/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.helpersetup;

import org.jogamp.vecmath.Color3f;

import java.awt.Color;

public class NamedColor {
    
    private final Color color;
    private final String name;
    
    public NamedColor(String name, Color color){
        this.name = name;
        this.color = color;
    }
    
    public Color getColor(){
        return color;
    }
    
    public String getName(){
        return name;
    }
    
    @Override
    public String toString(){
        return name;
    }

    public Color3f getColor3f(){
        return new Color3f(this.color.getRed()/255.0f, this.color.getGreen()/255.0f, this.color.getBlue()/255.0f);
    }
    
}
