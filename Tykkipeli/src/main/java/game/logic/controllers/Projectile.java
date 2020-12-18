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
package game.logic.controllers;

/**
 * Projektiilin luokka, käytetään simulaation alustamiseen ja vahingon laskemiseen.
 * @author Kari Suominen
 */

public class Projectile {
    private float weight = -1;
    private float frontalArea;
    private float dragCoeff = 1;
    private int cartouches;
    private boolean initOk;

    /**
     * Rakentaja, alustaa projektiilin.
     * @param weight Paino kiloina
     * @param cartouches Panosten määrä, 1-3
     */
    public Projectile(float weight, int cartouches) {
        if (weight <= 0) {
            return;
        }
        this.weight = weight;
        frontalArea = (float) (0.229f * 0.229f * Math.PI / 4f);
        this.cartouches = cartouches;
    }

    /**
     * Lisää panoksia projektiiliin.
     * @param amount
     */
    public void addCartouches(int amount) {
        cartouches += amount;
    }
    /**
     * Palauttaa panosten määrän.
     * @return 
     */
    public int getCartouches() {
        return cartouches;
    }
    
    /**
     * Palauttaa projektiilin painon.
     * @return kiloja.
     */
    public float getWeight() {
        return weight;
    }

    /**
     * Palauttaa projektiilin panosten määrästä ja painosta riippuvan lähtönopeuden.
     * @return metrejä sekunnissa
     */
    public double getInitialVelocity() {
        return 335 * (123f / weight) * cartouches / 3f;
    }

    /**
     * Palauttaa toden jos projektiililla on massa.
     * @return
     */
    public boolean initOk() {
        return weight > 0;
    }
}
