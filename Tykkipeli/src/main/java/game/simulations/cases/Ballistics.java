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
package game.simulations.cases;

import game.simulations.PhysicsSolver;
import game.utils.Vector3d;

/**
 * Implementaatio ratkaisijasta ballistisen projektiilin lentoradan laskuun ilmakehässä. Ottaa simulaatiossa huomioon ilmanvastuksen, koriolisilmiön sekä korkeudesta riippuvat gravitaation ja ilman tiheyden muutokset.
 * @author Kari Suominen
 */
public class Ballistics extends PhysicsSolver {
    private double dragCoeff;
    private double frontalArea;
    private double standardGravity;
    private double earthRadius;
    private double earthAngularVelocity;
    private double airMolarMass;
    private double molarGasConstant;
    private double airTemperature;
    private double airDensityGround;
    private double latitudeRadians;
    private Vector3d wind = new Vector3d();
    private boolean useWind = false;
    
    /**
     * Rakentaja, alustaa abstraktin luokan korkeammalla tarkkuudella sekä asettaa implementaatiolle vakiot.
     */
    public Ballistics() {
        super();
        set(new Vector3d(), new Vector3d(), 0.0001f);
        setConstants();
    }

    private void setConstants() {
        dragCoeff = 0.295;
        standardGravity = 9.80665f;
        earthRadius = 6362077f;
        airMolarMass = 0.0289644f;
        molarGasConstant = 8.3144598f;
        airDensityGround = 1.225f;
        earthAngularVelocity = Math.PI / (3600f * 12f);
        latitudeRadians = Math.PI * 60.1454f / 180f;
        setTemperature(15);
        setArea(0.280f);
    }
    
    private double gravity(double altitude) {
        return -standardGravity * Math.pow(earthRadius / (earthRadius + altitude), 2);
    }
    
    private double getAirDensity(double altitude) {
        return airDensityGround * Math.exp(-standardGravity * airMolarMass * altitude / (molarGasConstant * airTemperature));
    }
    
    /**
     * Asettaa nopeuden suuntaisen pinta-alan, jota käytetään ilmanvastuksen laskussa.
     * @param diameter 
     */
    public void setArea(double diameter) {
        frontalArea = Math.pow(diameter, 2) * Math.PI / 4f;
    }
    
    /**
     * Kytkee tuulen vaikutuksen päälle.
     * @param speed Tuulen nopeus metreinä sekunnissa
     * @param direction Suunta josta tuuli puhaltaa
     */
    public void setWind(double speed, double direction) {
        useWind = true;
        wind = new Vector3d(0, 0, 1);
        wind.rotateY(direction);
        wind = wind.scale(speed);
    }
    
    private void setTemperature(double temperature) {
        airTemperature = 273.15f + temperature;
    }
    
    private Vector3d getDrag(Vector3d velocity) {
        double trueAirDensity = getAirDensity(getPosition().y);
        double scalarVelocity = velocity.magnitude();
        double localCoeff = Math.pow(scalarVelocity, 2) * trueAirDensity * dragCoeff * frontalArea / (2 * getMass());
        Vector3d drag = velocity.clone();
        drag.normalize();
        drag = drag.scale(localCoeff);
        return drag;
    }
    
    private double getCoriolisAcceleration(Vector3d velocity) {
        return -2f * earthAngularVelocity * Math.sin(latitudeRadians) * velocity.z;
    }
    
    /**
     * Tarkennettu implementaatio kiihtyvyyden laskusta hetkellä. Ottaa ilmanvastuksen ja koriolisilmiön huomioon.
     * @return kiihtyvyys metreinä per sekunti toiseen
     */
    @Override
    public Vector3d solveAcceleration() {
        Vector3d velocity = getVelocity();
        Vector3d drag = getDrag(velocity);
        if (useWind) {
            drag = drag.add(getDrag(wind));
        }
        double coriolisAcceleration = getCoriolisAcceleration(velocity);
        return new Vector3d(-drag.x + coriolisAcceleration, gravity(0) - drag.y, -drag.z);
    }
    
    /**
     * Asettaa lähtöparametrit simulaatiolle.
     * @param position metreinä
     * @param velocity metreinä sekunnissa
     * @param mass kiloina
     * @param timestep simulaatioaskel, pienemmät arvot pienentää integrointivirhettä mutta hidastaa simulaatiota
     */
    public void set(Vector3d position, Vector3d velocity, double mass, double timestep) {
        set(position, velocity, timestep);
        setMass(mass);
    }
}
