/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import components.Projectile;
import simulations.Ballistics;

/**
 *
 * @author suominka
 */
public class Cannon {
    Ballistics solver;
    Projectile currentProjectile;
    float elevation;
    float traversal;
    float temperature;
    
    public Cannon(){
        elevation = 0;
        traversal = 0;
        temperature = 20;
        currentProjectile = null;
        solver = new Ballistics();
    }
    
    public boolean addProjectile(Projectile newProjectile){
        if( currentProjectile != null ) return false;
        currentProjectile = newProjectile;
        return true;
    }
    
    public boolean fire(){
        if( currentProjectile == null ) return false;
        currentProjectile = null;
        return true;
    }
}
