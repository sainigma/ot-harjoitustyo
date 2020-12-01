/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic;

import game.logic.controllers.*;
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
    MortarLogic mortarLogic;
    ReloadLogic reloadLogic;
    
    private boolean gunMovementActive = true;
    
    private long lastTime = System.nanoTime();
    private double deltatimeMillis;
    
    public BaseGame(Level level) {
        this.level = level;
        this.renderer = renderer;
        this.mortarLogic = new MortarLogic();
        this.reloadLogic = new ReloadLogic(mortarLogic, level.mortar);
    }
    
    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }
    
    public void setInputManager(InputManager inputs) {
        this.inputs = inputs;
        reloadLogic.setInputManager(this.inputs);
    }
    
    private void shakeScreen() {
        float shake[] = level.mortar.getShake();
        if (shake[1] > 0.05f) {
            float shakeLevel = 20f * (1.1f - shake[0]) * shake[1];
            level.gameView.setScreenShake(shakeLevel);
            level.mapView.setScreenShake(shakeLevel);
        } else if (level.gameView.isShaking()) {
            level.gameView.setScreenShake(0);
            level.mapView.setScreenShake(0);
        }        
    }
    
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
    
    private void traverse(float speedModifier) {
        if (reloadLogic.isMovementBlocked()) {
            return;
        }
        float traversalSpeed = 0.05f;
        if (inputs.keyDown("traverse right")) {
            level.mortar.addToTraverseTarget(-traversalSpeed * speedModifier);
        } else if (inputs.keyDown("traverse left")) {
            level.mortar.addToTraverseTarget(traversalSpeed * speedModifier);
        }
        level.mapScreen.setTraversal(-level.mortar.getTraversal());
    }
    
    private void gameControls(float speedModifier) {
        if (!gunMovementActive || level.mortar.animator.isPlaying("mortar/firing")) {
            return;
        }
        float elevationSpeed = 0.1f;
        if (inputs.keyDown("elevate")) {
            level.mortar.addToElevationTarget(elevationSpeed * speedModifier);
        } else if (inputs.keyDown("depress")) {
            level.mortar.addToElevationTarget(-elevationSpeed * speedModifier);
        }
        traverse(speedModifier);
    }
        
    private void mapControls(float speedModifier) {
        traverse(speedModifier);
    }
    
    private void sharedControls(float speedModifier) {
        if (inputs.keyDownOnce("fire")) {
            fire();
        }
        rotateMap(speedModifier);
    }
    
    private void rotateMap(float speedModifier) {
        if (!level.mapView.isVisible()) {
            return;
        }
        if (inputs.keyDown("rotate map right")) {
            level.mapScreen.rotateMap(speedModifier);
        } else if (inputs.keyDown("rotate map left")) {
            level.mapScreen.rotateMap(-speedModifier);
        }
    }
    
    private void menuMovement() {
        if (gunMovementActive) {
            return;
        }
    }
    
    public void fire() {
        if (!reloadLogic.isReloadFinished()) {
            return;
        }
        mortarLogic.set(level.mortar.getElevation(), level.mortar.getTraversal());
        if (mortarLogic.fire()) {
            float powerModifier = (reloadLogic.getProjectile().getCartouches() + 4f) / 7f;
            level.mortar.setPowerModifier(powerModifier);
            reloadLogic.resetProjectile();
            level.mortar.animator.playAnimation("mortar/firing");
        }
    }
    
    private void toggleView() {
        level.gameView.toggleVisible();
        level.mapView.toggleMinimized();        
    }
    
    public void update() {
        long time = System.nanoTime() / 1000000;
        deltatimeMillis = (double) (time - lastTime);
        lastTime = time;
        if (inputs == null) {
            return;
        }

        float speedModifier = getSpeedModifier();
        sharedControls(speedModifier);
        if (level.gameView.isVisible()) {
            gameControls(speedModifier);
            reloadLogic.reloadControls();
        } else {
            mapControls(speedModifier);
        }

        
        if (inputs.keyDownOnce("toggle map") || (inputs.keyDownOnce("cancel") && !level.mapView.isMinimized())) {
            toggleView();
        }
        
        shakeScreen();
        mortarLogic.solve(deltatimeMillis);
        if (mortarLogic.hasActiveSolvers()) {
            level.mapScreen.setProjectile(mortarLogic.latest);            
        }
    }
}
