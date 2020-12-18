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

import game.components.Animator;
import game.components.GameObject;

/**
 *
 * @author Kari Suominen
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
