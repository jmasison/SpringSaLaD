package org.springsalad.langevinsetup;

import org.jogamp.vecmath.Matrix3f;

import java.awt.event.ActionEvent;


public class RotationUpdateEvent extends ActionEvent{

	private boolean notifyPanel;
    private Matrix3f m3;
	
    public RotationUpdateEvent(Matrix3f m3,boolean notifyl){
    	super(new Object(), 0, ""); //TODO add something more decriptive
        this.m3 = m3;
        this.notifyPanel = notifyl;
    }

    
    public boolean notifyPanel(){
    	return this.notifyPanel;
    }
	public Matrix3f getM3() {
		return m3;
	}
}
