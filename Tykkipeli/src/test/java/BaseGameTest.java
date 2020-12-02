import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import game.components.Level;
import game.logic.BaseGame;
import game.logic.controllers.Projectile;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author suominka
 */
public class BaseGameTest {
    BaseGame logic;
    
    @Before
    public void setUp() {
        logic = new BaseGame(new Level("testlevel"));
    }
    
    private void _reload(float elevation) {
        Projectile projectile = new Projectile(120,3);
        logic.level.mortar.setTrueElevation(elevation);
        logic.reloadLogic.setProjectile(2, 3);
        logic.mortarLogic.addProjectile(logic.reloadLogic.getProjectile());
    }
    
    private void reload(float elevation) {
        _reload(elevation);
    }
    
    private void reload() {
        _reload(45f);
    }
    
    @Test
    public void reloadAndFiringSequenceTest() {
        reload();
        logic.fire();
        assertTrue(logic.mortarLogic.activeSolvers.size() > 0);
    }
    
    @Test
    public void cannotFireWithoutProjectile() {
        logic.fire();
        assertTrue(logic.mortarLogic.activeSolvers.size() < 1);
    }

    private float clearSky(float maxTime) {
        maxTime *= 1000;
        float acc = 0;
        while (acc < maxTime && logic.mortarLogic.activeSolvers.size() > 0) {
            logic.forcedUpdate(16f);
            acc += 16f;
        }
        return acc;
    }
    
    @Test
    public void firedProjectileLands() {
        reload(20);
        logic.fire();
        float acc = clearSky(60);
        assertTrue(logic.mortarLogic.activeSolvers.size() < 1 && acc > 32);
    }
    
    @Test
    public void multipleProjectilesFirstLandsFirst() {
        reload(20);
        logic.fire();
        for (int i=0; i < 5; i++) {
            logic.forcedUpdate(16);
        }
        reload(45);
        logic.fire();
        if (logic.mortarLogic.activeSolvers.size() < 2) {
            assertTrue(false);
        }
        float acc = clearSky(120);
        assertTrue(logic.mortarLogic.activeSolvers.size() < 1 && acc > 32);
    }
    
    @Test
    public void multipleProjectilesSecondLandsFirst() {
        reload(45);
        logic.fire();
        for (int i=0; i < 5; i++) {
            logic.forcedUpdate(16);
        }
        reload(10);
        logic.fire();
        if (logic.mortarLogic.activeSolvers.size() < 2) {
            assertTrue(false);
        }
        float acc = clearSky(120);
        assertTrue(logic.mortarLogic.activeSolvers.size() < 1 && acc > 32);
    }
}
