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

import game.simulations.cases.Ballistics;
import game.utils.Vector3d;
import java.util.ArrayList;

/**
 *
 * @author Kari Suominen
 */
public class Statistic {

    private double elevation;
    private double traversal;
    private double mass;
    private int cartouches;
    private Ballistics solver;
    private Vector3d lastPosition;
    private ArrayList<Vector3d> positions;
    private boolean active;
    
    public Statistic(double elevation, double traversal, double mass, int cartouches, Ballistics solver) {
        this.elevation = elevation;
        this.traversal = traversal;
        this.mass = mass;
        this.cartouches = cartouches;
        this.solver = solver;
        positions = new ArrayList<>();
        active = true;
    }

    public void updatePosition(Vector3d position) {
        if (lastPosition == null) {
            lastPosition = position.clone();
        } else {
            positions.add(lastPosition);
            lastPosition = position.clone();
        }
    }
    
    public Vector3d getLastPosition() {
        if (positions.isEmpty()) {
            return null;
        }
        return positions.get(positions.size() - 1);
    }
    
    public ArrayList<Vector3d> getPositions() {
        return positions;
    }
    
    public void disable() {
        active = false;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public float getPower() {
        return (float) (mass / 123f);
    }
    
    public float getMass() {
        return (float) mass;
    }
    
    public float getElevation() {
        return (float) elevation;
    }
    
    @Override
    public String toString() {
        return "final position: " + lastPosition;
    }
}