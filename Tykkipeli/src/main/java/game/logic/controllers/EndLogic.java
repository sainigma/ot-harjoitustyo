/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic.controllers;

import game.components.templates.EndScreen;

/**
 *
 * @author suominka
 */
public class EndLogic {
    private boolean active = false;
    
    public EndLogic(EndScreen endScreen) {
        
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void activate() {
        active = true;
    }
    
    public void setWinState(boolean winState) {
        if (winState) {
            
        } else {
            
        }
    }
    
    public void endControls() {
        if (!active) {
            return;
        }
    }
}
