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
package game.simulations;

import game.utils.Vector3d;

/**
 * Abstrakti luokka fysiikkaongelmien ratkaisuun, takaisinkytkevä simulaatio. Oikean käden koordinaatisto (x eteen, y ylös, z sivulle).
 * @author Kari Suominen
 */

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
    private boolean logging = false;
    /**
     * Alustaa simulaation. Oletuksena aika-askel 0.1f
     */
    public PhysicsSolver() {
        timestep = 0.1f;
        state = null;
    }
    /**
     * Asettaa simuloitavan kappaleen massan
     * @param mass kiloissa
     */
    public void setMass(double mass) {
        this.mass = mass;
    }
    /**
     * Palauttaa simuloitavan kappaleen massan.
     * @return kiloissa
     */
    public double getMass() {
        return mass;
    }
    /**
     * Palauttaa simuloitavan kappaleen nykyisen sijainnin.
     * @return metreissä
     */
    public Vector3d getPosition() {
        return state.position;
    }
    /**
     * Palauttaa simuloitavan kappaleen nykyisen nopeuden.
     * @return metrejä sekunnissa
     */
    public Vector3d getVelocity() {
        return state.velocity;
    }
    /**
     * Asettaa simuloitavan kappaleen sijainnin ja nopeuden.
     * @param position metreissä
     * @param velocity metrejä sekunnissa
     */
    public void set(Vector3d position, Vector3d velocity) {
        state = new State();
        state.position = position;
        state.velocity = velocity;
        state.acceleration = new Vector3d(0, gravity(), 0);
    }
    /**
     * Asettaa simuloitavan kappaleen sijainnin, nopeuden sekä simulaation aika-askeleen.
     * @param position metreissä
     * @param velocity metrejä sekunnissa
     * @param timestep simulaatioaskel, pienemmät arvot pienentää integrointivirhettä mutta hidastaa simulaatiota
     */
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
    /**
     * Ratkaisee ja palauttaa kiihtyvyyden hetkelle, implementoiva luokka tarkentaa simulaatiota.
     * @return metrejä per sekunti toiseen
     */
    public Vector3d solveAcceleration() {
        return new Vector3d(0, gravity(), 0);
    }
    /**
     * Tarkistaa onko simulaation loppu saavutettu, implementoiva luokka tarkentaa ehtoja.
     * @return 
     */
    public boolean endCondition() {
        if (state.velocity.y < 0 && state.position.y <= 0) {
            return true;
        }
        return false;
    }
    /**
     * Ajaa simulaation alusta loppuun, testikäyttöön.
     */
    public void run() {
        while (!endCondition()) {
            solve();
        }
    }
    
    /**
     * Ratkaisee tilan aikaikkunassa. Pysähtyy jos ikkunan loppu tai loppuehdot saavutetaan.
     * @param time 
     */
    public void solveToTime(double time) {
        double targetTime = state.time + time;
        while (state.time < targetTime && !endCondition()) {
            solve();
            if (logging) {
                System.out.println(toString());                
            }
        }
    }
    
    /**
     * Asettaa tilan tulostuksen päälle, debuggaukseen.
     */
    public void enableLogging() {
        logging = true;
    }
    
    /**
     * Nykyisen tilan muunnos tekstiksi loggaustarkoituksiin.
     * @return
     */
    @Override
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
