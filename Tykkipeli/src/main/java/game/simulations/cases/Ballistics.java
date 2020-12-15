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
    double dragCoeff;
    double frontalArea;
    double standardGravity;
    double earthRadius;
    double earthAngularVelocity;
    double airMolarMass;
    double molarGasConstant;
    double airTemperature;
    double airDensityGround;
    double latitudeRadians;
    
    private Vector3d wind = new Vector3d();
    private boolean useWind = false;
    
    public Ballistics() {
        super();
        set(new Vector3d(), new Vector3d(), 0.0001f);
        setConstants();
    }
    
    public void setArea(double diameter) {
        frontalArea = Math.pow(diameter, 2) * Math.PI / 4f;
    }
    
    public void setWind(boolean state) {
        useWind = state;
    }
    
    public void setWind(double speed, double direction) {
        useWind = true;
        wind = new Vector3d(0, 0, 1);
        wind.rotateY(direction);
        wind = wind.scale(speed);
    }
    
    public void setTemperature(double temperature) {
        airTemperature = 273.15f + temperature;
    }
    
    private double getAirDensity(double altitude) {
        return airDensityGround * Math.exp(-standardGravity * airMolarMass * altitude / (molarGasConstant * airTemperature));
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
    
    public double getCoriolisAcceleration(Vector3d velocity) {
        return -2f * earthAngularVelocity * Math.sin(latitudeRadians) * velocity.z;
    }
    
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
    
    public void set(Vector3d position, Vector3d velocity, double mass, double timestep) {
        set(position, velocity, timestep);
        setMass(mass);
    }
}
