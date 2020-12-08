/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic;

import game.logic.controllers.*;
import game.components.Level;
import game.components.templates.ScreenShaker;
import game.utils.InputManager;
import game.graphics.Renderer;
import game.utils.JSONLoader;
import game.utils.Vector3d;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author suominka
 */
public class BaseGame {
    private InputManager inputs = null;
    private Renderer renderer = null;
    
    private boolean guiInitialized = false;
    public MortarLogic mortarLogic;
    public ReloadLogic reloadLogic;
    public ArrayList<TargetLogic> targets;
    public Level level = null;
    
    private int targetsLeft = 0;
    
    public ScreenShaker screenShaker;
    private boolean gunMovementActive = true;
    
    private long lastTime;
    private double deltatimeMillis;
    
    public BaseGame() {
        loadLevel("e1m1");
    }
    
    public BaseGame(String level) {
        loadLevel(level);
    }
    
    public void setRenderer(Renderer renderer) {
        if (renderer != null) {
            renderer.appendToRenderQueue(level);
            guiInitialized = true;
        }
    }
    
    private void loadLevel(String name) {
        targetsLeft = 0;
        targets = new ArrayList<>();
        spawnLevel();
        spawnObjects();
        JSONObject levelData = new JSONLoader("assets/levels/").read(name);
        JSONObject magazine = levelData.getJSONObject("magazine");
        reloadLogic.setMagazine(
                magazine.getInt("light"),
                magazine.getInt("medium"),
                magazine.getInt("heavy"),
                magazine.getInt("charges")
        );
        spawnTargets(levelData.getJSONArray("ships"));
        lastTime = System.nanoTime() / 1000000;
    }
    
    private void spawnLevel() {
        if (level != null) {
            level.reset();
        } else {
            level = new Level("basegame");
        }
    }
    
    private void spawnObjects() {
        screenShaker = new ScreenShaker();
        mortarLogic = new MortarLogic();
        reloadLogic = new ReloadLogic(mortarLogic, level.mortar);        
    }
    
    private void spawnTargets(JSONArray ships) {
        for (Object ship : ships) {
            targetsLeft += 1;
            JSONObject shipObj = (JSONObject) ship;
            TargetLogic target = new TargetLogic(shipObj.getString("name"));
            target.setWaypoints(shipObj.getJSONArray("waypoints"));
            targets.add(target);
            level.mapScreen.spawnTarget(target);
        }
    }
    
    public void setInputManager(InputManager inputs) {
        this.inputs = inputs;
        reloadLogic.setInputManager(this.inputs);
    }
    
    private int lastSolversActive = 0;
    private void shakeScreen() {
        int solversActive = mortarLogic.activeSolvers.size();
        if (solversActive < lastSolversActive) {
            screenShaker.shake();
        }
        lastSolversActive = solversActive;

        screenShaker.update();
        float shake[] = level.mortar.getShake();
        float altShake = screenShaker.getShakevalue();
        if (shake[1] + altShake > 0.05f) {
            float shakeLevel = 20f * (1.1f - shake[0]) * shake[1] + altShake;
            level.gameView.setScreenShake(shakeLevel);
            level.mapView.setScreenShake(shakeLevel);
        } else if (level.gameView.isShaking()) {
            level.gameView.setScreenShake(0);
            level.mapView.setScreenShake(0);
        }        
    }
    
    private float getSpeedModifier() {
        float framerateCoeff = (float) (16f / deltatimeMillis); //1 when 60fps
        float speedModifier = framerateCoeff;
        if (inputs.keyDown("modifier faster")) {
            speedModifier *= 2f;
        } else if (inputs.keyDown("modifier slower")) {
            speedModifier *= 0.5f;
        }
        return speedModifier;
    }
    
    private void traverse(float speedModifier) {
        if (reloadLogic.isMovementBlocked()) {
            return;
        }
        float traversalSpeed = 0.05f;
        if (inputs.keyDown("traverse right")) {
            level.mortar.addToTraverseTarget(-traversalSpeed * speedModifier);
        } else if (inputs.keyDown("traverse left")) {
            level.mortar.addToTraverseTarget(traversalSpeed * speedModifier);
        }
        level.mapScreen.setTraversal(-level.mortar.getTraversal());
    }
    
    private void gameControls(float speedModifier) {
        if (!gunMovementActive || level.mortar.animator.isPlaying("mortar/firing")) {
            return;
        }
        float elevationSpeed = 0.1f;
        if (inputs.keyDown("elevate")) {
            level.mortar.addToElevationTarget(elevationSpeed * speedModifier);
        } else if (inputs.keyDown("depress")) {
            level.mortar.addToElevationTarget(-elevationSpeed * speedModifier);
        }
        traverse(speedModifier);
    }
        
    private void mapControls(float speedModifier) {
        traverse(speedModifier);
    }
    
    private void sharedControls(float speedModifier) {
        if (inputs.keyDownOnce("fire")) {
            fire();
        }
        if (inputs.keyDownOnce("toggle map") || (inputs.keyDownOnce("cancel") && !level.mapView.isMinimized())) {
            toggleView();
        }
        rotateMap(speedModifier);
    }
    
    private void rotateMap(float speedModifier) {
        if (!level.mapView.isVisible()) {
            return;
        }
        if (inputs.keyDown("rotate map right")) {
            level.mapScreen.rotateMap(speedModifier);
        } else if (inputs.keyDown("rotate map left")) {
            level.mapScreen.rotateMap(-speedModifier);
        }
    }
    
    private void menuMovement() {
        if (gunMovementActive) {
            return;
        }
    }
    
    public void fire() {
        if (!reloadLogic.isReloadFinished()) {
            return;
        }
        mortarLogic.set(level.mortar.getElevation(), level.mortar.getTraversal());
        if (mortarLogic.fire()) {
            float powerModifier = (reloadLogic.getProjectile().getCartouches() + 4f) / 7f;
            level.mortar.setPowerModifier(powerModifier);
            reloadLogic.resetProjectile();
            level.mortar.animator.playAnimation("mortar/firing");
        }
    }
    
    private void toggleView() {
        level.gameView.toggleVisible();
        level.mapView.toggleMinimized();        
    }
    
    private void getDeltatimeMillis() {
        long time = System.nanoTime() / 1000000;
        deltatimeMillis = (double) (time - lastTime);
        lastTime = time;
    }
    
    private void gameViewLogic(float speedModifier) {
        if (!level.gameView.isVisible()) {
            return;
        }
        gameControls(speedModifier);
        reloadLogic.reloadControls();
    }
    
    private void mapViewLogic(float speedModifier) {
        if (level.gameView.isVisible()) {
            return;
        }
        mapControls(speedModifier);
    }
    
    private void updateTargets() {
        for (TargetLogic target : targets) {
            target.update(deltatimeMillis);
            level.mapScreen.updateTarget(target);
        }
    }
    
    private void getHits() {
        if (mortarLogic.hasHits()) {
            ArrayList<Statistic> hits = new ArrayList<>();
            while (mortarLogic.hasHits()) {
                hits.add(mortarLogic.getHit());
            }
            checkHits(hits);
        }
    }
    
    private void damageTarget(TargetLogic target, Vector3d hitPosition, double maxDamage, double maxDistance) {
        if (target.getHealth() < 0f) {
            return;
        }
        Vector3d targetPos = target.getPosition();
        double distance = new Vector3d(hitPosition.x - targetPos.x, hitPosition.z - targetPos.y, 0f).magnitude();
        double damageFactor = distance / maxDistance;
        if (damageFactor < 1f) {
            double damage = maxDamage * (1 - Math.pow(damageFactor, 2));
            target.reduceHealth((float) damage);
            System.out.println("Target hit, inflicted " + damage + " damage");
            if (target.isSinking()) {
                targetsLeft -= 1;
            }
        }
    }
    
    private void checkHits(ArrayList<Statistic> hits) {
        for (Statistic hit : hits) {
            double maxDamage = hit.getPower() * 150f;
            double maxDistance = 1000f;
            Vector3d hitPosition = hit.getLastPosition();
            
            for (TargetLogic target : targets) {
                damageTarget(target, hitPosition, maxDamage, maxDistance);
            }
        }
    }
    
    public int getTargetsLeft() {
        return targetsLeft;
    }
    
    private void endLevel() {
        //System.out.println("No targets left, ending game");

    }
    private boolean historyDebouncer;    
    private void linkMapscreenToSolvers() {
        if (mortarLogic.hasActiveSolvers()) {
            level.mapScreen.setByHistory(mortarLogic.history);
            historyDebouncer = true;
        } else {
            if (historyDebouncer) {
                level.mapScreen.setByHistory(mortarLogic.history);
                historyDebouncer = false;
            }
            level.mapScreen.freeProjectiles(0);
        }        
    }
    

    private void updateGUI() {
        if (!guiInitialized) {
            return;
        }
        float speedModifier = getSpeedModifier();
        sharedControls(speedModifier);
        gameViewLogic(speedModifier);
        mapViewLogic(speedModifier);

        shakeScreen();        

        linkMapscreenToSolvers();
    }
    
    private void _update() {
        if (guiInitialized && inputs == null) {
            return;
        }
        
        mortarLogic.solve(deltatimeMillis);
        updateGUI();
        updateTargets();
        getHits();
        if (targetsLeft <= 0) {
            endLevel();
        }
    }
    
    public void update() {
        getDeltatimeMillis();
        _update();
    }
    
    public void update(double dtMillis) {
        deltatimeMillis = dtMillis;
        _update();
    }
}
