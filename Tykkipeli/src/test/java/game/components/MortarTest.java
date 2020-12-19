package game.components;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import game.components.templates.Mortar;

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

/**
 *
 * @author Kari Suominen
 */
public class MortarTest {
    Mortar mortar;
    @Before
    public void setUp() {
        mortar = new Mortar("mortar");
    }
    private void sleep(double target){
        long start = System.nanoTime() / 1000000;
        long now = start;
        while ((double) (now - start) < target) {
            now = System.nanoTime() / 1000000;
        }
    }
    private boolean testElevation(float target, float maxTime) {
        float updates=0;
        mortar.setElevationTarget(target);
        while(updates < 60*maxTime){
            mortar.forcedUpdate(16f);
            //sleep(16f);
            updates+=1;
        }
        float finalElevation = mortar.getElevation();
        System.out.println(finalElevation);
        if( Math.abs(finalElevation-target) < 5f ) {
            return true;
        }
        return false;
    }
    @Test
    public void elevationUpwards(){
        System.out.println("Testing mortar elevation PID controller with positive target");
        mortar.setTrueElevation(0f);
        assertTrue(testElevation(60f, 30f));
    }
    @Test
    public void elevationDownwards(){
        System.out.println("Testing mortar elevation PID controller with negative target");
        mortar.setTrueElevation(0f);
        assertTrue(testElevation(-20f, 30f));
    }
    @Test
    public void elevationFullRange(){
        System.out.println("Testing that mortar can move to minimum elevation after moving to max elevation");
        mortar.setTrueElevation(0f);
        boolean test1 = testElevation(60f, 30f);
        boolean test2 = testElevation(-20f, 60f);
        assertTrue(test1 && test2);
    }
    private boolean _testElevationSetter(float elevation){
        mortar.setTrueElevation(elevation);
        return (Math.abs(mortar.getElevation() - elevation) < 0.6f);
    }
    @Test
    public void testElevationSetter(){
        System.out.println("Testing that elevation propagates correctly through gears");
        boolean ok = true;
        float [] elevations = {-22f, 40f, 55f, 65f};
        for (float elevation : elevations) { 
            boolean testing = _testElevationSetter(elevation);
            if (!testing) {
                ok = false;
                break;
            }
        }
        assertTrue(ok);
    }
}
