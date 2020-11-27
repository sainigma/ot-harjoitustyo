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
    Projectile currentProjectile;
    Magazine magazine;
    
    private boolean gunMovementActive = true;
    
    public BaseGame(Level level) {
        this.level = level;
        this.renderer = renderer;
        this.mortarLogic = new MortarLogic();
        this.magazine = new Magazine(12,6,3,54);
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
        if (!gunMovementActive || level.mortar.animator.isPlaying("mortar/firing")) {
            return;
        }
        float elevationSpeed = 0.1f;
        float traversalSpeed = 0.05f;
        if (inputs.keyDown("elevate")) {
            level.mortar.addToElevationTarget(elevationSpeed * speedModifier);
        } else if (inputs.keyDown("depress")) {
            level.mortar.addToElevationTarget(-elevationSpeed * speedModifier);
        }
        if (inputs.keyDown("traverse right")) {
            level.mortar.addToTraverseTarget(-traversalSpeed * speedModifier);
        } else if (inputs.keyDown("traverse left")) {
            level.mortar.addToTraverseTarget(traversalSpeed * speedModifier);
        }
        if (inputs.keyDownOnce("fire")) {
            fire();
        }
    }
    
    private void menuMovement() {
        if (gunMovementActive) {
            return;
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
    
    private String[] warheads = {"light","medium","heavy"};
    private int[] cartouches = {1,2,3};
    private int reloadIndex = 0;
    private boolean reloadUpdate = true;
    private boolean reloadFinished = true;
    public void reloadProcedure() {
        if (gunMovementActive && inputs.keyDownOnce("reload") && currentProjectile == null) {
            System.out.println("starting reload");
            reloadUpdate = true;
            gunMovementActive = false;
            reloadFinished = false;
            level.mortar.setElevationTarget(0f);
        } else if (gunMovementActive) {
            return;
        }
        if (currentProjectile == null) {
            chooseProjectile();
        } else if (!reloadFinished){
            chooseCartouches();
        }
    }
    
    private void reloadSelector(int length) {
        if (inputs.keyDownOnce("left")) {
            reloadIndex -= 1;
            reloadUpdate = true;
        } else if (inputs.keyDownOnce("right")) {
            reloadIndex += 1;
            reloadUpdate = true;    
        }
        if (reloadIndex >= length) {
            reloadIndex = 0;
        } else if (reloadIndex < 0) {
            reloadIndex = length - 1;
        }
    }
    
    private void chooseProjectile() {
        if (reloadUpdate) {
            reloadUpdate = false;
            System.out.println("Select warhead, currently selected: "+warheads[reloadIndex]);            
        }
        reloadSelector(warheads.length);
        
        if (inputs.keyDownOnce("ok")) {
            currentProjectile = new Projectile(magazine.getWarhead(reloadIndex), magazine.getCartouche(1));
            if (!currentProjectile.initOk()) {
                currentProjectile = null;
            }
            reloadUpdate = true;
        }
    }
    
    private void chooseCartouches() {
        if (reloadUpdate) {
            reloadUpdate = false;
            System.out.println("Select cartouches, currently selected: "+cartouches[reloadIndex]);
        }
        reloadSelector(cartouches.length);
        
        if (inputs.keyDownOnce("ok")) {
            currentProjectile.addCartouches(magazine.getCartouche(cartouches[reloadIndex] - 1));
            reload();
        }
    }
    
    private void reload() {
        if (mortarLogic.addProjectile(currentProjectile)) {
            System.out.println("reload finished");
            reloadFinished = true;
            gunMovementActive = true;
        }
    }
    
    public void fire() {
        if (!reloadFinished) {
            return;
        }
        mortarLogic.set(level.mortar.getElevation(), level.mortar.getTraversal());
        if (mortarLogic.fire()) {
            float powerModifier = ( currentProjectile.getCartouches() + 4f ) / 7f;
            level.mortar.setPowerModifier(powerModifier);
            currentProjectile = null;
            level.mortar.animator.playAnimation("mortar/firing");
        }
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
        reloadProcedure();

        shakeScreen();
        mortarLogic.solve(deltatimeMillis);
    }
}
