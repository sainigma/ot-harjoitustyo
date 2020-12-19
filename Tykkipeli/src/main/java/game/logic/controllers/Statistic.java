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

import game.utils.Vector3d;
import java.util.ArrayList;

/**
 * Historia yksittäiselle projektiilille, tallentaa lähtöparametrit sekä lentoradan.
 * @author Kari Suominen
 */
public class Statistic {

    private double elevation;
    private double traversal;
    private double mass;
    private int cartouches;
    private Vector3d lastPosition;
    private ArrayList<Vector3d> positions;
    private boolean active;
    
    /**
     * Rakentaja, alustaa lähtöparametrit.
     * @param elevation tykin korotus asteissa
     * @param traversal tykin suunta asteissa
     * @param mass projektiilin massa kiloina
     * @param cartouches projektiilin panosten määrä
     */
    public Statistic(double elevation, double traversal, double mass, int cartouches) {
        this.elevation = elevation;
        this.traversal = traversal;
        this.mass = mass;
        this.cartouches = cartouches;
        positions = new ArrayList<>();
        active = true;
    }
    /**
     * Lisää sijainnin sijaintihistoriaan.
     * @param position 
     */
    public void updatePosition(Vector3d position) {
        if (lastPosition == null) {
            lastPosition = position.clone();
        } else {
            positions.add(lastPosition);
            lastPosition = position.clone();
        }
    }
    /**
     * Palauttaa viimeksi lisätyn sijainnin.
     * @return 
     */
    public Vector3d getLastPosition() {
        if (positions.isEmpty()) {
            return null;
        }
        return positions.get(positions.size() - 1);
    }
    /**
     * Palauttaa sijaintilistan.
     * @return 
     */
    public ArrayList<Vector3d> getPositions() {
        return positions;
    }
    /**
     * Merkitsee tilastoinnin päättyneeksi.
     */
    public void disable() {
        active = false;
    }
    /**
     * Palauttaa toden jos tilastointi on vielä käynnissä.
     * @return 
     */
    public boolean isActive() {
        return active;
    }
    /**
     * Palauttaa lineaarisen kertoimen projektiilin massasta, kevyt kranaatti vastaanottaa arvon 1.
     * @return 
     */
    public float getPower() {
        return (float) (mass / 123f);
    }
    /**
     * Palauttaa projektiilin massan.
     * @return 
     */
    public float getMass() {
        return (float) mass;
    }
    /**
     * Palauttaa tykin lähtökoron.
     * @return 
     */
    public float getElevation() {
        return (float) elevation;
    }
    /**
     * Tekstimuunnos testikäyttöön.
     * @return 
     */
    @Override
    public String toString() {
        return "final position: " + lastPosition;
    }
}