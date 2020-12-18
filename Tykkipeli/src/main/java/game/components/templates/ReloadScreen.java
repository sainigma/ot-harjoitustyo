/*
 * Copyright (C) 2020 Kari Suominen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package game.components.templates;

import game.components.GameObject;
import game.components.animation.PIDAnimator;
import game.utils.PID;
import game.utils.Vector3d;

/**
 *
 * @author Kari Suominen
 */
public class ReloadScreen extends GameObject {
    
    int offsetX = 970;
    
    PIDAnimator animator = new PIDAnimator(0.1f, 0f, 0.2f, 100f);
    
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

    public void enter() {
        animator.enter();
        animatePosition(0);
    }
    
    public void exit() {
        animator.exit();
        animatePosition(1f);
    }    

    private void animatePosition(float t) {
        Vector3d hidden = new Vector3d(0, 1080 * viewportScale, 10);
        Vector3d visible = new Vector3d(0, 0, 10);
        background.setPosition(new Vector3d().lerp(hidden, visible, t));
    }

    private void animate(double deltatimeMillis) {
        if (!animator.animating()) {
            return;
        }
        animatePosition(animator.animate(deltatimeMillis));
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
 