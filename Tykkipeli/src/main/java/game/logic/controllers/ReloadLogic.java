/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic.controllers;

import game.utils.InputManager;
import game.components.templates.Mortar;

/**
 *
 * @author suominka
 */
public class ReloadLogic {
    Magazine magazine;
    private String[] warheads = {"light", "medium", "heavy"};
    private int[] cartouches = {1, 2, 3};
    private int reloadIndex = 0;
    private boolean reloadUpdate = true;
    private boolean reloadFinished = true;
    private boolean blockMovement = false;

    Projectile currentProjectile;
    InputManager inputs = null;
    MortarLogic mortarLogic;
    Mortar mortar;
    
    public ReloadLogic(MortarLogic mortarLogic, Mortar mortar) {
        this.mortarLogic = mortarLogic;
        this.mortar = mortar;
        this.magazine = new Magazine(12, 6, 3, 54);
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
            System.out.println("Select warhead, currently selected: " + warheads[reloadIndex]);            
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
            System.out.println("Select cartouches, currently selected: " + cartouches[reloadIndex]);
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
            blockMovement = false;
            mortar.setInclinometer(true);
        }
    }
    
    public boolean isReloadFinished() {
        return reloadFinished;
    }
    
    public void resetProjectile() {
        currentProjectile = null;
    }
    
    public Projectile getProjectile() {
        return currentProjectile;
    }
    
    public void setInputManager(InputManager inputs) {
        this.inputs = inputs;
    }
    
    public boolean isMovementBlocked() {
        return blockMovement;
    }
    
    public void reloadControls() {
        if (inputs.keyDownOnce("reload") && currentProjectile == null) {
            System.out.println("starting reload");
            reloadUpdate = true;
            blockMovement = true;
            reloadFinished = false;
            mortar.setElevationTarget(0f);
            mortar.setInclinometer(false);
        } else if (!blockMovement) {
            return;
        }
        if (currentProjectile == null) {
            chooseProjectile();
        } else if (!reloadFinished) {
            chooseCartouches();
        }
    }
}
