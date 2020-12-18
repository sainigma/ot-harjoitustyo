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
 *
 * @author Kari Suominen
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
