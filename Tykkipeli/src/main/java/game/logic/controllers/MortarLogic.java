/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic.controllers;

import game.simulations.cases.Ballistics;
import game.utils.Vector3d;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

/**
 * Tykin ampumisen liittyvä logiikka, päivittää myös aktiivisten projektiilien solvereita.
 * @author suominka
 */
public class MortarLogic {    
    public HashMap<Ballistics, Statistic> history;
    public HashSet<Ballistics> activeSolvers;
    private Stack<Ballistics> hits;
    private Projectile currentProjectile;
    double elevation;
    double traversal;
    
    double aboveSeaLevel = 15f;
    double chamberHeight = 2.2f;
    
    boolean useWind = false;
    double windSpeed = 0f;
    double windDirection = 0f;
    
    public Vector3d latest = new Vector3d();
    
    public MortarLogic() {
        elevation = 0;
        traversal = 0;
        currentProjectile = null;
        history = new HashMap<>();
        activeSolvers = new HashSet<>();
        hits = new Stack<>();
    }
    
    /**
     * Asettaa tykkilogiikalle projektiilin. Projektiilin läsnäolo vaaditaan tulitukseen.
     * @param newProjectile
     * @return 
     */
    public boolean addProjectile(Projectile newProjectile) {
        if (currentProjectile != null) {
            return false;
        }
        currentProjectile = newProjectile;
        return true;
    }
    
    /**
     * Asettaa tykkilogiikalle tykin korotuksen ja siirron.
     * @param elevation
     * @param traversal 
     */
    public void set(double elevation, double traversal) {
        this.elevation = elevation;
        this.traversal = traversal;
    }
    
    public void setWind(double windSpeed, double direction) {
        this.windSpeed = windSpeed;
        this.windDirection = direction;
        useWind = true;
    }
    
    private void startSolver() {
        Ballistics solver = new Ballistics();
        float mass = currentProjectile.weight;
        solver.setMass(mass);
        int cartouches = currentProjectile.cartouches;
        Vector3d direction = new Vector3d();
        direction.setByAzimuthAltitude(traversal, elevation);
        Vector3d velocity = direction.clone().scale(currentProjectile.getInitialVelocity());
        solver.set(new Vector3d(0, aboveSeaLevel + chamberHeight, 0), velocity, mass, 0.001f);
        if (useWind) {
            solver.setWind(windSpeed, windDirection);
        }
        activeSolvers.add(solver);
        history.put(solver, new Statistic(elevation, traversal, mass, cartouches, solver));
    }
    
    /**
     * Metodi aktiivisten solverien päivittämiseen.
     * Vastaanottaa parametrina edellisen piirtoon kuluneen ajan millisekunteina ja päivittää aktiiviset solverit tämän perusteella.
     * Poistaa myös aktiiviset solverit päivityslistasta ja lisää ne osumapinoon jos niiden lopputavoite on saavutettu.
     * Huomautuksena tämän voisi joskus muuttaa piirtoajan keskiarvoa käyttäväksi..
     * @param dtMillis 
     */
    public void solve(double dtMillis) {
        if (activeSolvers.isEmpty()) {
            return;
        }
        Iterator<Ballistics> iterator = activeSolvers.iterator();
        while (iterator.hasNext()) {
            Ballistics solver = iterator.next();
            Statistic solverStats = history.get(solver);
            if (solver.endCondition()) {
                hits.push(solver);
                solverStats.disable();
                iterator.remove();
                System.out.println(solverStats);
            } else {
                solver.solveToTime(dtMillis / 1000f);
                latest.set(solver.getPosition());
                solverStats.updatePosition(latest);
            }
        }
    }
    
    /**
     * Palauttaa false jos aktiivisia solvereita ei enää ole.
     * @return 
     */
    public boolean hasActiveSolvers() {
        return !activeSolvers.isEmpty();
    }
    
    /**
     * Apumetodi osumapinon tyhjennykseen, palauttaa epätoden jos osumapino on tyhjä.
     * @return 
     */
    public boolean hasHits() {
        return !hits.isEmpty();
    }
    
    /**
     * Poppaa ja palauttaa viimeisimmän osuman osumapinosta.
     * @return 
     */
    public Statistic getHit() {
        if (!hasHits()) {
            return null;
        }
        Ballistics solver = hits.pop();
        return history.get(solver);
    }
    
    /**
     * Tulitusmetodi, käynnistää itsenäisen solverin projektiilille ja resetoi asetetun ammuksen jos ammus on asetettu.
     * @return 
     */
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
