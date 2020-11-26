/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic;

import game.components.Level;
import game.utils.InputManager;
import game.utils.Renderer;

/**
 *
 * @author suominka
 */
public class BaseGame {
    Level level = null;
    InputManager inputs = null;
    Renderer renderer = null;
    
    public BaseGame(Level level) {
        this.level = level;
        this.renderer = renderer;
    }
    
    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }
    
    public void setInputManager(InputManager inputs) {
        this.inputs = inputs;
    }
    
    private long lastTime = System.nanoTime();
    private double deltatimeMillis;
    
    private float getSpeedModifier() {
        float framerateCoeff = (float) (16f / deltatimeMillis); //1 when 60fps
        float speedModifier = framerateCoeff;
        if (inputs.keyDown("modifier faster")) {
            speedModifier *= 2f;
        } else if (inputs.keyDown("modifier slower")) {
            speedModifier *= 0.5f;
        }
        return speedModifier;
    }
    
    private void doMovement(float speedModifier) {
        if (inputs.keyDown("elevate")) {
            level.mortar.addToElevationTarget(0.1f * speedModifier);
        } else if (inputs.keyDown("depress")) {
            level.mortar.addToElevationTarget(-0.1f * speedModifier);
        }
        if (inputs.keyDown("traverse right")) {
            level.mortar.addToTraverseTarget(-1f * speedModifier);
        } else if (inputs.keyDown("traverse left")) {
            level.mortar.addToTraverseTarget(1f * speedModifier);
        }
    }
    
    private void shakeScreen() {
        float shake[] = level.mortar.getShake();
        if (shake[1] > 0.05f) {
            level.gameView.setScreenShake(20f * (1.1f - shake[0]) * shake[1]);
        } else if (level.gameView.isShaking()) {
            level.gameView.setScreenShake(0);
        }        
    }
    
    public void fire() {
        level.mortar.animator.playAnimation("mortar/firing");        
    }
    
    public void update() {
        long time = System.nanoTime() / 1000000;
        deltatimeMillis = (double) (time - lastTime);
        lastTime = time;
        if (inputs == null) {
            return;
        }
        float speedModifier = getSpeedModifier();
        doMovement(speedModifier);
        
        if (inputs.keyDown("fire")) {
            fire();
        }
        shakeScreen();
    }
}
