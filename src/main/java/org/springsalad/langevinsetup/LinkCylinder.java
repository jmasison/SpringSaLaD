/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

//import com.sun.j3d.utils.geometry.Cylinder;
//
//import javax.media.j3d.Appearance;
//import javax.media.j3d.Material;
//import javax.media.j3d.RenderingAttributes;
//import javax.media.j3d.Shape3D;
//import javax.media.j3d.TransformGroup;
//import javax.media.j3d.Transform3D;
//import javax.media.j3d.BranchGroup;
//import javax.vecmath.Color3f;
//import javax.vecmath.Vector3d;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Cylinder;
import org.jogamp.vecmath.AxisAngle4d;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Vector3d;
import org.springsalad.helpersetup.Colors;

import java.util.Enumeration;
import java.util.Iterator;
//import javax.media.j3d.BadTransformException;
//import javax.vecmath.AxisAngle4d;

public class LinkCylinder extends BranchGroup {
    
    private final Appearance normal;
    private final Appearance highlight;
    
    private final Cylinder cylinder;
    private final TransformGroup tg;
    private final Transform3D t3d;
    
    private boolean highlighted;
    
    private final Link link;
    
    public LinkCylinder(Link link){
        super();
        this.setCapability(BranchGroup.ALLOW_DETACH);
        
        this.link = link;
        
        cylinder = new Cylinder(0.04f, 1f);
        cylinder.setPickable(true);
        cylinder.setCapability(Cylinder.ENABLE_PICK_REPORTING);
        cylinder.setCapability(Cylinder.ENABLE_APPEARANCE_MODIFY);
        cylinder.setCapability(Cylinder.ALLOW_CHILDREN_WRITE);
        Iterator<Node> iter = cylinder.getAllChildren();
        while (iter.hasNext()){
            Shape3D shape = (Shape3D) iter.next();
            shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        }
        
        tg = new TransformGroup();
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        t3d = new Transform3D();
        updatePosition();
        
        Color3f col = Colors.LIGHTGRAY3D;
        
        Material normalMaterial = new Material(col, Colors.BLACK3D, col, Colors.BLACK3D, 60f);
        Material highlightMaterial = new Material(col, col, col, Colors.BLACK3D, 60f);
        
        normal = new Appearance();
        highlight = new Appearance();
        
        normal.setMaterial(normalMaterial);
        highlight.setMaterial(highlightMaterial);
        
        RenderingAttributes ra = new RenderingAttributes();
        ra.setDepthBufferEnable(true);
        ra.setAlphaTestFunction( RenderingAttributes.GREATER );
		normal.setRenderingAttributes(ra);
		highlight.setRenderingAttributes(ra);
        
        cylinder.setAppearance(normal);
        highlighted = false;
        
        tg.addChild(cylinder);
        this.addChild(tg);
        
    }
    
    public void highlight(boolean bool){
        highlighted = bool;
        if(bool){
            cylinder.setAppearance(highlight);
        } else {
            cylinder.setAppearance(normal);
        }
    }
    
    public Link getLink(){
        return link;
    }
    
    public boolean isHighlighted(){
        return highlighted;
    }
    
    public void updatePosition(){
        /**
         * For some reason that I don't care to determine, the transforms 
         * defined below do not work if either the two x positions are identical
         * or the two z positions are identical, and in these cases  
         * the system chokes on a BadTransformException.  To avoid this, I'll
         * check to see if they are identical, and if they are, I'll shift pos2
         * by 1e-5 nm, which is a completely negligible distance for the systems
         * under consideration.
         */
       
        // Get the site positions
        Site site1 = link.getSite1();
        Site site2 = link.getSite2();
        
        double [] v1 = {site1.getX(), site1.getY(), site1.getZ()};
        double [] v2 = {site2.getX(), site2.getY(), site2.getZ()};
        
        for(int i=0;i<3;i++){
            if(v1[i] == v2[i]){
                v2[i] += 1e-5;
            }
        }
        
        Vector3d vec1 = new Vector3d(v1[0], v1[1], v1[2]);
        Vector3d vec2 = new Vector3d(v2[0], v2[1], v2[2]);
        
        // Now get the orientation
        Vector3d orientation = new Vector3d();
        orientation.sub(vec2, vec1);
        
        // I'll need the angles between the orientation and the y and z axes
        Vector3d projectXZ = new Vector3d();
        projectXZ.x = orientation.x;
        projectXZ.z = orientation.z;
        
        double theta = projectXZ.angle(DrawPanel3D.z_axis);
        if(orientation.x < 0){
            theta = 2.0*Math.PI - theta;
        }
        
        double phi = orientation.angle(DrawPanel3D.y_axis);
        
        t3d.setIdentity();
        
        Transform3D rotation_y = new Transform3D();
        Transform3D rotation_z = new Transform3D();
        Transform3D translation = new Transform3D();
        Transform3D translateUp = new Transform3D();
        Transform3D scale = new Transform3D();
        Vector3d shiftUp = new Vector3d(0,0,0);
        Vector3d translationVec = new Vector3d(0,0,0);
        
        rotation_y.setRotation(new AxisAngle4d(DrawPanel3D.y_axis, theta));
        rotation_z.setRotation(new AxisAngle4d(DrawPanel3D.x_axis, phi));
        shiftUp.y = orientation.length()/2;
        translateUp.setTranslation(shiftUp);
        translationVec = vec1;
        translation.setTranslation(translationVec);
        scale.setScale(new Vector3d(1,orientation.length(), 1));
        
        t3d.mul(translation);
        t3d.mul(rotation_y);
        t3d.mul(rotation_z);
        t3d.mul(translateUp);
        t3d.mul(scale);
        try{
            tg.setTransform(t3d);
        } catch(BadTransformException e){
            System.out.println("Choked when setting transform.");
            System.out.println("Translation vector was " + translationVec.toString());
            System.out.println("Shift up was " + shiftUp.toString());
            
        }

    }
    
    public Cylinder getCylinder(){
        return cylinder;
    }
    
}
