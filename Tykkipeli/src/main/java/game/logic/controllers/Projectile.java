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
 *
 * @author Kari Suominen
 */

public class Projectile {
    float weight = -1;
    float frontalArea;
    float dragCoeff = 1;
    int cartouches;
    boolean initOk;
    public Projectile(float weight, int cartouches) {
        if (weight <= 0) {
            return;
        }
        this.weight = weight;
        frontalArea = (float) (0.229f * 0.229f * Math.PI / 4f);
        this.cartouches = cartouches;
    }
    public void addCartouches(int amount) {
        cartouches += amount;
    }
    public int getCartouches() {
        return cartouches;
    }
    public double getInitialVelocity() {
        return 335 * (123f / weight) * cartouches / 3f;
    }
    public boolean initOk() {
        return weight > 0;
    }
}
