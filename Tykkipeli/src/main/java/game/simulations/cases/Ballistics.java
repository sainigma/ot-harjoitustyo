/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.simulations.cases;

import game.simulations.PhysicsSolver;
import game.utils.Vector3d;

/**
 *
 * @author suominka
 */
public class Ballistics extends PhysicsSolver {
    public Ballistics() {
        super(); //Sets world specific parameters
    }
    public void set(Vector3d position, Vector3d velocity, double mass, double timestep) {
        set(position, velocity, timestep);
        setMass(mass);
    }
}
