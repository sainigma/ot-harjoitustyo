package game.logic;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import game.logic.controllers.Projectile;
import game.logic.controllers.TargetLogic;
import game.utils.Vector3d;

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
        logic = new BaseGame("testing");
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
            logic.update(16f);
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
        System.out.println(" Firing two projectiles, first lands first");
        reload(20);
        logic.fire();
        for (int i=0; i < 5; i++) {
            logic.update(16);
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
        System.out.println(" Firing two projectiles, second lands first");
        reload(45);
        logic.fire();
        for (int i=0; i < 5; i++) {
            logic.update(16);
        }
        reload(10);
        logic.fire();
        if (logic.mortarLogic.activeSolvers.size() < 2) {
            assertTrue(false);
        }
        float acc = clearSky(120);
        assertTrue(logic.mortarLogic.activeSolvers.size() < 1 && acc > 32);
    }
    
    @Test
    public void levelLoadsProperly() {
        System.out.println(" Testing that level loads properly from file");
        int magazineContents [] = {20, 34, 12, 50};
        int initialShipsExpected = 2;
        String magazineExpected = "Magazine status: " + magazineContents[0] + " light, " + magazineContents[1] + " medium, " + magazineContents[2] + " heavy warheads, " + magazineContents[3] + " charges left.";
        
        int initialShips = logic.getTargetsLeft();
        String initialMagazine = logic.reloadLogic.getMagazine().toString();
        
        assertTrue(
                initialShips == initialShipsExpected && 
                magazineExpected.equals(initialMagazine)
        );
    }
    
    private boolean verifyShip(TargetLogic ship, Vector3d position, Vector3d direction, float rotation, float health) {
        boolean a = ship.getPosition().diff(position).magnitude() < 0.1f;
        boolean b = ship.getDirection().diff(direction).magnitude() < 0.1f;
        boolean c = Math.abs(ship.getRotation().y - rotation) < 0.1f;
        boolean d = Math.abs(ship.getHealth() - health) < 0.1f;
        return a && b && c && d;
    }
    
    @Test
    public void shipsSpawnProperly() {
        System.out.println(" Testing that ship reads correctly from both the level and ship catalog");
        TargetLogic ships[] = {logic.targets.get(0), logic.targets.get(1)};
        boolean a = verifyShip(
                ships[0],
                new Vector3d(4200f, -4200f, 0),
                new Vector3d(-0.70f, 0.70f, 0f),
                -135,
                30
        );
        assertTrue(a);
    }
    
    private int sinkingTest(float elevation, float traversal) {
        int initialShips = logic.getTargetsLeft();
        reload(elevation);
        logic.level.mortar.setTraversal(traversal);
        logic.fire();
        float acc = clearSky(120);
        return logic.getTargetsLeft() - initialShips;
    }
    
    @Test
    public void shipsSinksIfHit() {
        System.out.println(" Firing a projectile towards windjammer, expecting it to sink");
        int result = sinkingTest(29f, 45f);
        assertTrue(result == -1);
    }
    
    @Test
    public void shipFloatsIfNotHit() {
        System.out.println(" Firing a projectile to a safe direction, expecting nothing to sink");
        int result = sinkingTest(20f, 0f);
        assertTrue(result == 0);
    }
}
