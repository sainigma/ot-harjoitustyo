/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic.controllers;

import game.simulations.cases.Ballistics;
import game.utils.Vector3d;
import java.util.ArrayList;

/**
 *
 * @author suominka
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
    
    public void disable() {
        active = false;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public float getPower() {
        return (float) (mass / 123f);
    }
    
    @Override
    public String toString() {
        return "final position: " + lastPosition;
    }
}