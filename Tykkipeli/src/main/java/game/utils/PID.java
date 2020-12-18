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
package game.utils;

/**
 * Takaisinkytkevä PID-säädin ajastettuun ohjailuun.
 * @author Kari Suominen
 */
public class PID {
    private float pFactor;
    private float dFactor;
    private float iFactor;
    private float timeMultiplier;
    private boolean active = false;
    
    /**
     * Rakentaja säätimelle. Todelliselle säätimelle valitse 0.001f aika-askel (pelin logiikka millisekunneissa), yleiselle 1f (vakaampi)
     * @param p suhdekerroin
     * @param i integrointikerroin
     * @param d derivointikerroin
     * @param timeMultiplier aika-askel
     */
    public PID(float p, float i, float d, float timeMultiplier) {
        pFactor = p;
        iFactor = i;
        dFactor = d;
        this.timeMultiplier = timeMultiplier;
    }
    
    private double errorPrev;
    private double errorSum;
    private double control;
    
    private void update(float error, double dt) {
        dt *= timeMultiplier;
        errorSum += error * dt;
        double errorSlope = (error - errorPrev) / dt;
        control = pFactor * error + iFactor * errorSum + dFactor * errorSlope;
        errorPrev = error;
    }
    /**
     * Palauttaa säätimen ohjausarvon virheen korjaamiseen.
     * @param error nykyinen virhe
     * @param dt aika sekunneissa
     * @return ohjausarvo
     */
    public double getControl(float error, double dt) {
        if (!active) {
            return 0f;
        }
        update(error, dt);
        return control;
    }
    /**
     * Nollaa säätimen kumuloituneet virheet ja laittaa sen päälle.
     */
    public void activate() {
        active = true;
        errorPrev = 0;
        errorSum = 0;
        control = 0;
    }
    /**
     * Kytkee säätimen pois päältä.
     */
    public void deactivate() {
        active = false;
    }
    /**
     * Palauttaa säätimen aktiivisuustilan.
     * @return 
     */
    public boolean isActive() {
        return active;
    }
}
