/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.simulations;

import game.utils.Vector3d;

/**
 *
 * @author suominka
 */

// Y is up, X is forward
public abstract class PhysicsSolver {
    private class State {        
        Vector3d position = new Vector3d();
        Vector3d velocity = new Vector3d();
        Vector3d acceleration = new Vector3d();
        double time = 0;
    }
    private double timestep;
    private double mass;
    private State state;
    public PhysicsSolver() {
        state = null;
    }
    public Vector3d getPosition() {
        return state.position;
    }
    public void set(Vector3d position, Vector3d velocity) {
        state = new State();
        state.position = position;
        state.velocity = velocity;
        state.acceleration = new Vector3d(0, gravity(), 0);
    }
    public void set(Vector3d position, Vector3d velocity, double timestep) {
        set(position, velocity);
        this.timestep = timestep;
    }
    private double gravity() {
        return -9.81;
    }
    private Vector3d solvePosition() {
        Vector3d position = new Vector3d();
        position.x = state.velocity.x * timestep + state.position.x;
        position.y = state.velocity.y * timestep + state.position.y;
        position.z = state.velocity.z * timestep + state.position.z;
        return position;
    }
    private Vector3d solveVelocity(Vector3d previousAcceleration) {
        Vector3d velocity = new Vector3d();
        Vector3d deltaA = state.acceleration.diff(previousAcceleration);
        velocity.x = timestep * (0.5 * deltaA.x + state.acceleration.x) + state.velocity.x;
        velocity.y = timestep * (0.5 * deltaA.y + state.acceleration.y) + state.velocity.y;
        velocity.z = timestep * (0.5 * deltaA.z + state.acceleration.z) + state.velocity.z;
        return velocity;
    }
    private Vector3d solveAcceleration() {
        return new Vector3d(0, gravity(), 0);
    }
    public boolean endCondition() {
        if (state.velocity.y < 0 && state.position.y <= 0) {
            return true;
        }
        return false;
    }
    public void run() {
        while (!endCondition()) {
            solve();
        }
    }
    public void solveToTime(double time) {
        double targetTime = state.time + time;
        while (state.time < targetTime && !endCondition()) {
            solve();
        }
    }
    public String toString() {
        return "Position: " + state.position + ", Velocity: " + state.velocity + ", Acceleration: " + state.acceleration;
    }
    private void solve() {
        Vector3d previousAcceleration = state.acceleration.clone();
        state.time += timestep;
        state.acceleration = solveAcceleration();
        state.velocity = solveVelocity(previousAcceleration);
        state.position = solvePosition();            
    }
}
