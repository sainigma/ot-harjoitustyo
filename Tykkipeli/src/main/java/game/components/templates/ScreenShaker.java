/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components.templates;

import game.components.Animator;
import game.components.GameObject;

/**
 *
 * @author suominka
 */
public class ScreenShaker extends GameObject {
    private Animator animator;
    private boolean shaking;
    private float shakeValue;
    
    public ScreenShaker() {
        super("screenshaker");
        init();
    }
    
    public void init() {
        shaking = false;
        shakeValue = 0;
        animator = new Animator();
        animator.loadAnimation("screenshaker/normalShake");
        animator.bindDriver("shaker", this);
    }
    public void shake() {
        shaking = true;
        animator.playAnimation("screenshaker/normalShake");
    }
    @Override
    public void drive(String target, double value) {
        shakeValue = (float) value;
    }
    public float getShakevalue() {
        return shakeValue;
    }
    @Override
    public void update() {
        double dt = getDeltatime();
        if (shaking) {
            animator.animate(dt);
            shaking = animator.isPlaying();
        }
    }
}
