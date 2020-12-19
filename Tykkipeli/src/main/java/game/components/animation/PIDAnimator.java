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
package game.components.animation;

import game.utils.PID;

/**
 * PID-säätimeen perustuva yksinkertainen animaattori. Siirtyy dynaamisesti 0 ja 1 välillä.
 * @author Kari Suominen
 */
public class PIDAnimator {
    private boolean animating = false;
    private float animatedPosition;
    private float animatedTarget;
    private PID animator;
    
    /**
     * Rakentaja, alustaa PID-säätimen.
     * @param p Suhdekerroin
     * @param i Integrointikerroin
     * @param d Derivointikerroin
     * @param t Aikakerroin
     */
    public PIDAnimator(float p, float i, float d, float t) {
        animator = new PID(p, i, d, t);
    }
    
    /**
     * Alustaa ja aktivoi animaattorin siirtymän ohjausarvosta 0 -> 1.
     */
    public void enter() {
        animating = true;
        animatedPosition = 0f;
        animatedTarget = 1f;
        animator.activate();
    }
    /**
     * Alustaa ja aktivoi animaattorin siirtymän ohjausarvosta 1 -> 0.
     */
    public void exit() {
        animating = true;
        animatedPosition = 1f;
        animatedTarget = 0f;
        animator.activate();
    }
    
    /**
     * Päivittää säätimen ja palauttaa nykyisen ohjausarvon.
     * @param deltatimeMillis
     * @return
     */
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
    
    /**
     * Palauttaa toden jos ohjausarvo on vielä 0 ja 1 välillä.
     * @return
     */
    public boolean animating() {
        return animating;
    }

    /**
     * Palauttaa nykyisen ohjausarvon.
     * @return
     */
    public float getAnimatedPosition() {
        return animatedPosition;
    }
}
