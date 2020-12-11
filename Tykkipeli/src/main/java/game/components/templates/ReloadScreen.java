/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components.templates;

import game.components.GameObject;
import game.utils.PID;
import game.utils.Vector3d;

/**
 *
 * @author suominka
 */
public class ReloadScreen extends GameObject {
    
    int offsetX = 970;
    
    float animatedPosition = 0f;
    float animatedTarget = 0f;
    boolean animating = false;
    
    PID animator = new PID(0.1f, 0f, 0.2f, 100f);
    
    GameObject charges[] = {null, null, null};
    GameObject warheads[] = {null, null, null};
    GameObject background;
    float viewportScale = 720f / 1080f;
    
    public ReloadScreen(String name) {
        super(name);
        init();
    }
    
    public void init() {
        background = new GameObject("reloadBackground", "reloadView/ammusvalintaTausta.png", new Vector3d()) { };
        background.setDepth(20);
        
        spawnCharges();
        spawnWarheads();
        
        append(background);
        
        animatePosition(0f);
    }
    
    private void animatePosition(float t) {
        Vector3d hidden = new Vector3d(0, 1080 * viewportScale, 10);
        Vector3d visible = new Vector3d(0, 0, 10);
        background.setPosition(new Vector3d().lerp(hidden, visible, t));
    }
    
    private void spawnCharges() {
        charges[0] = new GameObject("charge", "reloadView/kartussiIso.png", new Vector3d(0, 64, 1), viewportScale) { };
        charges[0].translate((offsetX - 102) * viewportScale, 500 * viewportScale);
        background.append(charges[0]);
        for (int i = 1; i < 3; i++) {
            Vector3d pos = charges[i - 1].getPosition();
            charges[i] = charges[0].clone();
            charges[i].setPosition(pos.add(new Vector3d(-102 * viewportScale, 0, 0)));
            background.append(charges[i]);
        }
    }
    
    public void enter() {
        animating = true;
        animatedPosition = 0f;
        animatePosition(animatedPosition);
        animatedTarget = 1f;
        animator.activate();
    }
    
    public void exit() {
        animating = true;
        animatedPosition = 1f;
        animatePosition(animatedPosition);
        animatedTarget = 0f;
        animator.activate();
    }
    
    private void spawnWarheads() {
        String names[] = {"light", "medium", "heavy"};
        int i = 0;
        for (String name : names) {
            warheads[i] = new GameObject("charge", "reloadView/" + name + "Iso.png", new Vector3d(0, 256, 1), viewportScale) { };
            warheads[i].translate((offsetX + 2) * viewportScale, 500 * viewportScale);
            background.append(warheads[i]);
            i++;
        }
    }
    
    private void animate(double deltatimeMillis) {
        if (!animating) {
            return;
        }
        float error = animatedTarget - animatedPosition;
        float control = (float) animator.getControl(error, deltatimeMillis);
        animatedPosition += control;
        if (Math.abs(error) < 0.001f) {
            animating = false;
            animatedPosition = animatedTarget;
        }
        animatePosition(animatedPosition);
    }
    
    public void setCharges(int chargesSelected) {
        int i = 1;
        for (GameObject charge : charges) {
            if (i > chargesSelected) {
                charge.setVisible(false);
            } else {
                charge.setVisible(true);
            }
            i++;
        }
    }
    
    public void setWarhead(int indexSelected) {
        int i = 0;
        for (GameObject warhead : warheads) {
            if (i == indexSelected) {
                warhead.setVisible(true);
            } else {
                warhead.setVisible(false);
            }
            i++;
        }
    }
    
    public void update() {
        animate(getDeltatime());
    }
}
 