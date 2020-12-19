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
 * Empty-tyyppinen peliobjekti ruudun tärisyttämiseen.
 * @author Kari Suominen
 */
public class ScreenShaker extends GameObject {
    private Animator animator;
    private boolean shaking;
    private float shakeValue;
    
    /**
     * Rakentaja, alustaa peliobjektin ja animaattorin.
     */
    public ScreenShaker() {
        super("screenshaker");
        init();
    }
    
    private void init() {
        shaking = false;
        shakeValue = 0;
        animator = new Animator();
        animator.loadAnimation("screenshaker/normalShake");
        animator.bindDriver("shaker", this);
    }

    /**
     * Aktivoi animaattorissa ruuduntärisytyksen.
     */
    public void shake() {
        shaking = true;
        animator.playAnimation("screenshaker/normalShake");
    }
    /**
     * Rajapinta animaattorille, vastaanottaa avaimen ja ohjausarvon jolla avaimen määrittämää objektia animoidaan.
     * @param target avain
     * @param value ohjausarvo
     */
    @Override
    public void drive(String target, double value) {
        shakeValue = (float) value;
    }

    /**
     * Palauttaa tärisytyksen ohjausarvon.
     * @return
     */
    public float getShakevalue() {
        return shakeValue;
    }

    /**
     * Päivitysmetodi, päivittää animaattoria jos tärisytys on käynnissä.
     */
    @Override
    public void update() {
        double dt = getDeltatime();
        if (shaking) {
            animator.animate(dt);
            shaking = animator.isPlaying();
        }
    }
}
