/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Sphere;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Vector3d;
import org.springsalad.helpersetup.Colors;

public class SiteSphere extends BranchGroup {
    
    private final Appearance normal;
    private final Appearance highlight;
    
    private final Sphere sphere;
    private final TransformGroup tg;
    private final Transform3D t3d;
    
    private boolean highlighted;
    
    private final Site site;
    
    public SiteSphere(Site site){
        super();
        this.setCapability(BranchGroup.ALLOW_DETACH);
        
        sphere = new Sphere((float)site.getRadius());
        sphere.setPickable(true);
        sphere.setCapability(Sphere.ENABLE_APPEARANCE_MODIFY);
        sphere.setCapability(Shape3D.ENABLE_PICK_REPORTING);
        sphere.setCapability(Sphere.ALLOW_CHILDREN_WRITE);
        Shape3D shape = sphere.getShape();
        shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        
        tg = new TransformGroup();
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        t3d = new Transform3D();
        t3d.setTranslation(new Vector3d(site.getX(), site.getY(), site.getZ()));
        
        this.site = site;
        
        Color3f col = Colors.getColor3fByName(site.getType().getColorName());
        
        Material normalMaterial = new Material(col, Colors.BLACK3D, col, Colors.BLACK3D, 60f);
        Material highlightMaterial = new Material(col, col, col, Colors.BLACK3D, 60f);
        
        normal = new Appearance();
        highlight = new Appearance();
        
        normal.setMaterial(normalMaterial);
        highlight.setMaterial(highlightMaterial);
        
        RenderingAttributes ra = new RenderingAttributes();
        ra.setDepthBufferEnable(true);
        ra.setAlphaTestFunction( RenderingAttributes.GREATER_OR_EQUAL );
        normal.setRenderingAttributes(ra);
		highlight.setRenderingAttributes(ra);
        
        sphere.setAppearance(normal);
        highlighted = false;
        
        tg.setTransform(t3d);
        tg.addChild(sphere);
        
        this.addChild(tg);
    }
    
    public void highlight(boolean bool){
        highlighted = bool;
        if(bool){
            sphere.setAppearance(highlight);
        } else {
            sphere.setAppearance(normal);
        }
    }
    
    public Site getSite(){
        return site;
    }
    
    public boolean isHighlighted(){
        return highlighted;
    }
    
    public void translate(Vector3d vec){

        t3d.setTranslation(vec);
        tg.setTransform(t3d);
    }
    
    public Sphere getSphere(){
        return sphere;
    }
    
}
