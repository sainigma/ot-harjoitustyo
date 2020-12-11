package game.components;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import game.components.templates.Mortar;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author suominka
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
        mortar.addElevation(0.5f);
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
