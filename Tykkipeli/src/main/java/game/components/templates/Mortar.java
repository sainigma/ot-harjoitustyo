/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components.templates;

import game.components.GameObject;
import game.utils.PID;
import game.utils.Vector3d;

/**
 *
 * @author suominka
 */
public class Mortar extends GameObject {
    private float viewportScale;
    long start = System.currentTimeMillis();
    
    private float elevationMaxSpeed = 10f;
    private float traversalMaxSpeed = 0.05f;
    private float elevationTarget = 0;
    private float traverseTarget = 0;
    private PID elevationControl = new PID(1f, 0, 2f);
    private PID traversalControl = new PID(0.05f, 0f, 0.5f);

    private float traversal = 0;
    private float elevationWheelRot = 0;

    private float[] gunLimits = {-22, 65};
    private Vector3d[] cradleLimits = {
        new Vector3d(710, 236),
        new Vector3d(500, 204.5)
    };
    
    private GameObject mount;
    private GameObject gun;
    private GameObject elevationGear;
    private GameObject elevationWheel;
    private GameObject cradle;
    private GameObject craneWheel;
    private GameObject craneGear;
    private GameObject mountGrooves;
    
    public Mortar(String name, float viewportScale) {
        super(name);
        this.viewportScale = viewportScale;
        init();
    }
    
    private void spawnChildren() {
        mount = new GameObject("mortarstand", "mortar/jalusta.png", new Vector3d(0), viewportScale) { };
        mountGrooves = new GameObject("mortargrooves", "mortar/urat.png", new Vector3d(512, 512, 0), viewportScale * 0.58f) { };
        gun = new GameObject("mortartube", "mortar/tykki.png", new Vector3d(512, 512, 1), viewportScale) { };
        cradle = new GameObject("mortarcar", "mortar/karry.png", new Vector3d(398, 147, -1), viewportScale) { };
        elevationWheel = new GameObject("mortarwheel", "mortar/ruori.png", new Vector3d(128, 128, -3), viewportScale) { };
        elevationGear = new GameObject("mortargear", "mortar/ratas.png", new Vector3d(64, 64, -2), viewportScale) { };
        craneWheel = elevationWheel.clone();
        craneGear = elevationGear.clone();
    }
    private void appendChildren() {
        cradle.append(gun);
        cradle.append(elevationGear);
        cradle.append(elevationWheel);
        mount.append(cradle);
        mount.append(craneWheel);
        mount.append(craneGear);
        mount.append(mountGrooves);
        append(mount);
    }
    
    private void init() {   
        spawnChildren();
        appendChildren();
        
        elevationWheel.translate(20, 106);
        elevationGear.translate(50, 90);
        cradle.translate(1065f * viewportScale, 355f * viewportScale);
        
        craneWheel.translate(545, 385);
        craneGear.translate(495, 398);
        
        cradle.translate(-210, -0.15f * 210);

        mountGrooves.translate(997 * viewportScale, (730) * viewportScale);
        mountGrooves.setVertexOffset(
                new float[]{235, -30},
                new float[]{-220, 80},
                new float[]{220, 80},
                new float[]{-235, -30}
        );

        //setElevationTarget(60);
        setTraversal(90);
        //setTraverseTarget(95);
    }
    
    private float getControl(PID controller, float target, float current, float maxSpeed) {
        float error = target - current;
        if (Math.abs(error) > 0.1f) {
            float control = (float) controller.getControl(error, dt);
            if (control > maxSpeed) {
                control = maxSpeed;
            } else if (control < -maxSpeed) { 
                control = maxSpeed;
            }
            return control;
        } else { 
            controller.deactivate();
            return 0;
        }
    }
    
    private void elevate() {
        if (!elevationControl.isActive()) {
            return;
        }
        addElevation(getControl(
                elevationControl,
                elevationTarget,
                getElevation(),
                elevationMaxSpeed
        ));
    }
    
    private void traverse() {
        if (!traversalControl.isActive()) {
            return;
        }
        addTraversal(getControl(
                traversalControl,
                traverseTarget,
                getLocalTraversal(),
                traversalMaxSpeed
        ));
    }
    
    public float getElevationFactor() {
        return (float) Math.cos(Math.PI * (getElevation() - 10) / 180);
    }
    
    public void setCradle(float t) {
        cradle.setPosition(new Vector3d().lerp(cradleLimits[0], cradleLimits[1], t * getElevationFactor()));
    }
    public float getElevation() {
        return -gun.rotation;
    }
    public void setElevationTarget(float r) {
        if (r >= gunLimits[0] && r <= gunLimits[1]) {
            elevationTarget = r;            
            elevationControl.activate();
        }
    }
    public void setTraverseTarget(float r) {
        traverseTarget = r;
        traversalControl.activate();
    }

    public void addTraversal(float r) {
        setTraversal(traversal + r);
    }
    private void setTraversal(float r) {
        traversal = r;
        mountGrooves.setTexOffset(getTraversal() / 90, 0);
    }
    private float getLocalTraversal() {
        return traversal;
    }
    public float getTraversal() {
        return traversal % 360;
    }

    public void addElevation(float r) {
        setElevation(-elevationWheelRot + r);
    }
    public void setTrueElevation(float r) {
        float gunRot = -r;
        float gearRot = -gunRot / (12f / 142f);
        elevationWheelRot = -gearRot / (12f / 60f);
        elevationWheel.setRotation(elevationWheelRot);
        elevationGear.setRotation(gearRot);
        gun.setRotation(gunRot);
    }
    private void setElevation(float r) {
        elevationWheelRot = -r;
        float gearRot = -elevationWheelRot * (12f / 60f);
        float gunRot = -gearRot * (12f / 142f);
        if (getElevation() > gunLimits[0] && getElevation() < gunLimits[1]) {
            elevationWheel.setRotation(elevationWheelRot);
            elevationGear.setRotation(gearRot);
            gun.setRotation(gunRot);            
        }
    }
    
    private double dt = 0;
    private long lastTime = System.nanoTime();
    public void update() {
        long time = System.nanoTime() / 1000000;
        dt = (double) (time - lastTime);
        float t = (float) (Math.cos((System.currentTimeMillis() - start) * 0.001) * 0.5f + 0.5f);
        setCradle(t);
        elevate();
        traverse();
        lastTime = time;
    }
}
