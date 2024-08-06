/*
 * Used for the simulation listener interface.
 */

package org.springsalad.runlauncher;

public class SimulationEvent {
    
    private final Simulation simulation;
    
    public SimulationEvent(Simulation simulation){
        this.simulation = simulation;
    }
    
    public Simulation getSimulation(){
        return simulation;
    }
    
    public String getSimulationStatus(){
        return simulation.getStatus();
    }
    
}
