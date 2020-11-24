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
    private float Kp;
    private float Kd;
    private float Ki;
    
    private boolean active = false;
    
    public PID(){
        Kp = 1;
        Ki = 1;
        Kd = 1;
    }
    public PID(float p, float i, float d){
        Kp = p;
        Ki = i;
        Kd = d;
    }
    
    private double errorPrev;
    private double errorSum;
    private double control;
    
    private void update(float error, double dt){
        errorSum += error * dt;
        double errorSlope = (error - errorPrev)/dt;
        control = Kp*error + Ki*errorSum + Kd*errorSlope;
        errorPrev = error;
    }
    public double getControl(float error, double dt){
        update(error,dt);
        return control;
    }
    public void activate(){
        active = true;
        errorPrev = 0;
        errorSum = 0;
        control = 0;
    }
    public void deactivate(){
        active = false;
    }
    public boolean isActive(){
        return active;
    }
}
