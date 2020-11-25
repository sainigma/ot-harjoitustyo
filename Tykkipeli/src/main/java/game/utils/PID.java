/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.utils;

/**
 *
 * @author suominka
 */
public class PID {
    private float pFactor;
    private float dFactor;
    private float iFactor;
    private float timeMultiplier;
    private boolean active = false;
    
    //use timeMultiplier 0.001 for true pid
    public PID(float p, float i, float d, float timeMultiplier) {
        pFactor = p;
        iFactor = i;
        dFactor = d;
        this.timeMultiplier = timeMultiplier;
    }
    
    private double errorPrev;
    private double errorSum;
    private double control;
    
    private void update(float error, double dt) {
        dt *= timeMultiplier;
        errorSum += error * dt;
        double errorSlope = (error - errorPrev) / dt;
        control = pFactor * error + iFactor * errorSum + dFactor * errorSlope;
        errorPrev = error;
    }
    public double getControl(float error, double dt) {
        if (!active) {
            return 0f;
        }
        update(error, dt);
        return control;
    }
    public void activate() {
        active = true;
        errorPrev = 0;
        errorSum = 0;
        control = 0;
    }
    public void deactivate() {
        active = false;
    }
    public boolean isActive() {
        return active;
    }
}
