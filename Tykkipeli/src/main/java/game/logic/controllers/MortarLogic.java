/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic.controllers;

import game.simulations.cases.Ballistics;
import game.utils.Vector3d;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author suominka
 */
public class MortarLogic {    
    HashMap<Ballistics, Statistic> history;
    HashSet<Ballistics> activeSolvers;
    Projectile currentProjectile;
    double elevation;
    double traversal;
    
    double aboveSeaLevel = 15f;
    double chamberHeight = 2.2f;
    
    public Vector3d latest = new Vector3d();
    
    public MortarLogic() {
        elevation = 0;
        traversal = 0;
        currentProjectile = null;
        history = new HashMap<>();
        activeSolvers = new HashSet<>();
    }
    
    public boolean addProjectile(Projectile newProjectile) {
        if (currentProjectile != null) {
            return false;
        }
        currentProjectile = newProjectile;
        return true;
    }
    
    public void set(double elevation, double traversal) {
        this.elevation = elevation;
        this.traversal = traversal;
    }
    
    private void startSolver() {
        Ballistics solver = new Ballistics();
        float mass = currentProjectile.weight;
        int cartouches = currentProjectile.cartouches;
        Vector3d direction = new Vector3d();
        direction.setByAzimuthAltitude(traversal, elevation);
        Vector3d velocity = direction.clone().scale(currentProjectile.getInitialVelocity());
        solver.set(new Vector3d(0, aboveSeaLevel + chamberHeight, 0), velocity, mass, 0.001f);
        //solver.enableLogging();
        activeSolvers.add(solver);
        history.put(solver, new Statistic(elevation, traversal, mass, cartouches, solver));
    }
    
    public void solve(double dtMillis) {
        if (activeSolvers.isEmpty()) {
            return;
        }
        for (Ballistics solver : activeSolvers) {
            Statistic solverStats = history.get(solver);
            if (solver.endCondition()) {
                activeSolvers.remove(solver);
                System.out.println(solverStats);
            } else {
                solver.solveToTime(dtMillis / 1000f);
                latest.set(solver.getPosition());
                solverStats.updatePosition(latest);
            }
        }
    }
    
    public boolean hasActiveSolvers() {
        return !activeSolvers.isEmpty();
    }
    
    public Statistic getPosition() {
        if (activeSolvers.isEmpty()) {
            return null;
        }
        for (Ballistics solver : activeSolvers) {
            return history.get(solver);
        }
        return null;
    }
    
    public boolean fire() {
        if (currentProjectile == null) {
            System.out.println("no projectile set");
            return false;
        }
        System.out.println("firing for effect");
        startSolver();
        currentProjectile = null;
        return true;
    }
}
