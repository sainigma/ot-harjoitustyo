/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulations;

import simulations.PhysicsSolver;

/**
 *
 * @author suominka
 */
public class Ballistics extends PhysicsSolver{
    public Ballistics(){
        super(); //Sets world specific parameters
    }
    public void set(){
        //Sets flight path relevant parameters for simulation
    }
    public void get(){
        solve();
    } //Think about the result later
}
