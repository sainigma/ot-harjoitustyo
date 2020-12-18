package game.simulations;

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

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import game.simulations.cases.Ballistics;
import game.simulations.cases.Parabola;
import game.utils.Vector3d;

/**
 *
 * @author Kari Suominen
 */
public class PhysicsTest {
    Parabola parabola;
    Ballistics ballistics;
    @Before
    public void setUp() {
        parabola = new Parabola();
        ballistics = new Ballistics();
        ballistics.setMass(120);
    }
    private double timeToRun(PhysicsSolver solver, double maxTime){
        double time = 0;
        double timestep = 0.016; //60Hz
        while( !solver.endCondition() && time < maxTime ){
            solver.solveToTime(timestep);
            time+=timestep;
        }
        return time;
    }
    @Test
    public void toStringWorks(){
        System.out.println(" Testing string output");
        Vector3d position = new Vector3d(234);
        Vector3d velocity = new Vector3d(129);
        Vector3d acceleration = new Vector3d(0,-9.81,0);
        parabola.set(position, velocity);
        assertEquals("Position: "+position+", Velocity: "+velocity+", Acceleration: "+acceleration,parabola.toString());
    }
    @Test
    public void simulationHasEnding(){
        System.out.println(" Testing that endgoal is reached within a set number of iterations");
        double maxTime = 120; //seconds
        double time = timeToRun(parabola, maxTime);
        assertTrue( time < maxTime );
    }
    private double getMagnitudeForDirection(PhysicsSolver solver, Vector3d direction){
        solver.set(new Vector3d(0,1,0), direction);
        solver.run();
        return solver.getPosition().magnitude();
    }
    private boolean testDistance(PhysicsSolver solver){
        double errorMargin = 0.5;
        Vector3d[] directions = { // 45 degree shot to all compass directions
            new Vector3d(100,100,0),
            new Vector3d(70.71068,100,70.71069),
            new Vector3d(0,100,100),
            new Vector3d(-70.71068,100,70.71069),
            new Vector3d(-100,100,0),
            new Vector3d(-70.71068,100,-70.71068),
            new Vector3d(0,100,-100),
            new Vector3d(70.71068,100,-70.71068)
        };
        double[] magnitudes = new double[8];
        int i=0;
        for(Vector3d direction : directions){
            magnitudes[i] = getMagnitudeForDirection(parabola, direction);
            i+=1;
        }
        boolean passesTest = true;
        i=1;
        for(double magnitude : magnitudes){
            if( Math.abs(magnitude - magnitudes[0])>errorMargin ){
                passesTest = false;
            }
        }
        return passesTest;        
    }
    
    @Test
    public void distanceTraveledOnlyDependantOnElevationForParabola(){
        System.out.println(" Testing that distance is dependant only on the initial elevation for parabolic system");
        assertTrue(testDistance(parabola));
    }
    
    @Test
    public void distanceTraveledOnlyDependantOnElevationForBallistics(){
        System.out.println(" Testing that distance is dependant only on the initial elevation for ballistic system");
        assertTrue(testDistance(ballistics));
    }
    
    @Test
    public void dragAffectsDistance(){
        System.out.println(" Testing that drag affected system decays faster than parabolic one");
        Vector3d direction = new Vector3d(100,100,0);
        double parabolicDistance = getMagnitudeForDirection(parabola, direction);
        double ballisticDistance = getMagnitudeForDirection(ballistics, direction);
        System.out.println(" final distances: parabolic: " + parabolicDistance + " ballistic: " + ballisticDistance);
        assertTrue(parabolicDistance > ballisticDistance);
    }
}
