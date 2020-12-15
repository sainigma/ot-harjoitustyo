/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components.animation;

import game.utils.PID;

/**
 *
 * @author suominka
 */
public class PIDAnimator {
    boolean animating = false;
    float animatedPosition;
    float animatedTarget;
    PID animator;
    
    public PIDAnimator(float p, float i, float d, float t) {
        animator = new PID(p, i, d, t);
    }
    
    public void enter() {
        animating = true;
        animatedPosition = 0f;
        animatedTarget = 1f;
        animator.activate();
    }
    
    public void exit() {
        animating = true;
        animatedPosition = 1f;
        animatedTarget = 0f;
        animator.activate();
    }
    
    public float animate(double deltatimeMillis) {
        if (!animating) {
            return -1;
        }
        float error = animatedTarget - animatedPosition;
        float control = (float) animator.getControl(error, deltatimeMillis);
        animatedPosition += control;
        if (Math.abs(error) < 0.001f) {
            animating = false;
            animatedPosition = animatedTarget;
        }
        return animatedPosition;
    }
    
    public boolean animating() {
        return animating;
    }

    public float getAnimatedPosition() {
        return animatedPosition;
    }
}
