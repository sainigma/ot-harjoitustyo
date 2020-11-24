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
public class GameObjectTest {
    Mortar mortar;
    @Before
    public void setUp() {
        mortar = new Mortar("mortar", 1);
    }
    private boolean testElevation(float target) {
        float timer=0;
        float maxTime=60;
        mortar.setTrueElevation(0f);
        mortar.setElevationTarget(target);
        float initialElevation = mortar.getElevation();
        while(timer < maxTime){
            mortar.update();
            timer+=16f/1000f;
        }
        float finalElevation = mortar.getElevation();
        System.out.println(finalElevation);
        if( initialElevation == 0 && Math.abs(finalElevation-target)<1f ) {
            return true;
        }
        return false;
    }
    @Test
    public void elevationUpwards(){
        System.out.println("Testing mortar elevation PID controller with positive target");
        assertTrue(testElevation(60f));
    }
    /* PID controller code is still work in progress
    @Test
    public void elevationDownwards(){
        System.out.println("Testing mortar elevation PID controller with negative target");
        assertTrue(testElevation(-1f));
    }
    */
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
