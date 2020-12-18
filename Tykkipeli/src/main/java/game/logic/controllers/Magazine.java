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
public class Magazine {
    float lightMass = 123;
    float mediumMass = 130;
    float heavyMass = 150;
    
    private int[] warheads;
    private float[] warheadWeights = {123f, 130f, 150f};
    private int cartouches;
    
    public Magazine(int light, int medium, int heavy, int cartouches) {
        warheads = new int[] {light, medium, heavy};
        this.cartouches = cartouches;
    }
    
    public void addWarhead(int index) {
        warheads[index] += 1;
    }
    
    public int getWarheadsLeft(int index) {
        if (index < 0 || index >= warheads.length) {
            return -1;
        }
        return warheads[index];
    }
    
    public int getChargesLeft() {
        return cartouches;
    }
    
    public float getWarhead(int index) {
        if (index < 0 || index >= warheads.length) {
            return -1;
        }
        if (warheads[index] > 0) {
            warheads[index] -= 1;
            return warheadWeights[index];
        }
        return -1;
    }
    public boolean isEmpty() {
        if (cartouches <= 0) {
            return true;
        }
        int totalWarheads = 0;
        for (int warhead : warheads) {
            totalWarheads += warhead;
        }
        return totalWarheads <= 0;
    }
    public int cartouchesLeft() {
        return cartouches;
    }
    public boolean warheadAvailable(int index) {
        return warheads[index] > 0;
    }
    public int getCartouche(int amount) {
        if (cartouches - amount >= 0) {
            cartouches -= amount;
            return amount;
        } else if (cartouches > 0) {
            int ret = cartouches;
            cartouches = 0;
            return ret;
        }
        return 0;
    }

    public String warHeadStatus() {
        return " Raskaita " + warheads[2] + "\n Puoliraskaita " + warheads[1] + "\n Kevyitä " + warheads[0];
    }
    
    public String chargeStatus() {
        return " Panoksia jäljellä " + cartouches;
    }
    
    public String toString() {
        return "Magazine status: " + warheads[0] + " light, " + warheads[1] + " medium, " + warheads[2] + " heavy warheads, " + cartouches + " charges left.";
    }
}
