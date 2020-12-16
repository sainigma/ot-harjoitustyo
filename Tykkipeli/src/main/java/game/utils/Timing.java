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
public class Timing {
    
    private long lastTime;
    private double timer;
    
    public Timing () {
        lastTime = System.nanoTime() / 1000000;
        timer = 0;
    }
    
    public double getDeltatimeMillis() {
        double deltatimeMillis = 0;
        long time = System.nanoTime() / 1000000;
        if (lastTime > 0) {
            deltatimeMillis = (double) (time - lastTime);
        }
        timer += deltatimeMillis;
        lastTime = time;
        return deltatimeMillis;
    }
    
    public double getTimer() {
        return timer;
    }
    
    public void resetTimer() {
        timer = 0;
    }
}
