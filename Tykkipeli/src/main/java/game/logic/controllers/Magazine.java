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
 * Varastoluokka kranaattien ja panosten määrän hallintaan.
 * @author Kari Suominen
 */
public class Magazine {
    private float lightMass = 123;
    private float mediumMass = 130;
    private float heavyMass = 150;
    
    private int[] warheads;
    private float[] warheadWeights = {123f, 130f, 150f};
    private int cartouches;
    
    /**
     * Rakentaja, vastaanottaa alkuarvot kranaattien ja panosten määrälle.
     * @param light
     * @param medium
     * @param heavy
     * @param cartouches
     */
    public Magazine(int light, int medium, int heavy, int cartouches) {
        warheads = new int[] {light, medium, heavy};
        this.cartouches = cartouches;
    }
    
    /**
     * Palauttaa otetun kranaatin varastoon.
     * @param index
     */
    public void addWarhead(int index) {
        warheads[index] += 1;
    }
    
    /**
     * Palauttaa indeksin määräämän kranaatin määrän.
     * @param index
     * @return
     */
    public int getWarheadsLeft(int index) {
        if (index < 0 || index >= warheads.length) {
            return -1;
        }
        return warheads[index];
    }
    
    /**
     * Palauttaa panosten määrän.
     * @return
     */
    public int getChargesLeft() {
        return cartouches;
    }
    
    /**
     * Poistaa indeksin määräämän kranaatin varastosta ja palauttaa sen massan.
     * @param index
     * @return
     */
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

    /**
     * Onko varasto tyhjä, palauttaa toden jos joko panokset tai kranaatit on loppu.
     * @return
     */
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

    /**
     * Palauttaa panosten määrän.
     * @return
     */
    public int cartouchesLeft() {
        return cartouches;
    }

    /**
     * Palauttaa toden jos indeksin määräämiä kranaatteja on vielä jäljellä.
     * @param index
     * @return
     */
    public boolean warheadAvailable(int index) {
        return warheads[index] > 0;
    }

    /**
     * Poistaa panoksia varastosta pyydetyn verran, palauttaa sen perusteella joko saman tai suurimman mahdollisen määrän.
     * @param amount
     * @return
     */
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

    /**
     * Palauttaa merkkijonon makasiinin kranaattien määrästä.
     * @return
     */
    public String warHeadStatus() {
        return " Raskaita " + warheads[2] + "\n Puoliraskaita " + warheads[1] + "\n Kevyitä " + warheads[0];
    }
    /**
     * Palauttaa merkkijonon makasiinin panosten määrästä.
     * @return 
     */
    public String chargeStatus() {
        return " Panoksia jäljellä " + cartouches;
    }
    
    /**
     * Palauttaa merkkijonon makasiinin tilasta, testikäyttöön.
     * @return
     */
    @Override
    public String toString() {
        return "Magazine status: " + warheads[0] + " light, " + warheads[1] + " medium, " + warheads[2] + " heavy warheads, " + cartouches + " charges left.";
    }
}
