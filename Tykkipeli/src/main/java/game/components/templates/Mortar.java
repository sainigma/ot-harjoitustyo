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
package game.components.templates;

import game.components.Animator;
import game.components.GameObject;
import game.utils.PID;
import game.utils.Vector3d;

/**
 * Peliobjekti tykille.
 * @author Kari Suominen
 */
public class Mortar extends GameObject {
    private double dt;
    
    private float viewportScale;
    
    private float elevationMaxSpeed = 10f;
    private float traversalMaxSpeed = 100f;
    private float traversalCoeff = 0.025f;
    private float elevationTarget = 0;
    private float traverseTarget = 0;
    private PID elevationControl = new PID(1f, 0f, 2f, 1f);
    private PID traversalControl = new PID(5f, 0f, 5f, 1f);
    
    /**
     * Peliobjektin animaattori, yksittäisten animaatioiden hallitsemiseen.
     */
    public Animator animator;
    
    private float traversal = 0;
    private float elevationWheelRot = 0;

    private float shakeCoeff = 0;
    private float powerModifier = 1f;
    
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
    private GameObject inclinometer;
    
    /**
     * Rakentaja luokalle, alustaa objektit sekä asettaa tekstuureille ominaisen skaalauskertoimen (tekstuurit 1080p tarkkuudella, pelinäkymä 720p).
     * @param name
     */
    public Mortar(String name) {
        super(name);
        this.viewportScale = 720f / 1080f;
        init();
    }
    
    private void spawnChildren() {
        mount = new GameObject("mortarstand", "mortar/jalusta.png", new Vector3d(0)) { };
        mountGrooves = new GameObject("mortargrooves", "mortar/urat.png", new Vector3d(512, 512, 0), viewportScale * 0.58f) { };
        gun = new GameObject("mortartube", "mortar/tykki.png", new Vector3d(512, 512, 0)) { };
        cradle = new GameObject("mortarcradle", "mortar/karry.png", new Vector3d(398, 147, -1)) { };
        elevationWheel = new GameObject("mortarwheel", "mortar/ruori.png", new Vector3d(128, 128, -3)) { };
        elevationGear = new GameObject("mortargear", "mortar/ratas.png", new Vector3d(64, 64, -2)) { };
        inclinometer = new GameObject("inclinometer", "mortar/inklinometri.png", new Vector3d(32, 354)) { };
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
        mount.append(inclinometer);
        append(mount);
    }
    private void setDepth() {
        mount.setDepth(3);
        cradle.setDepth(1);
        craneWheel.setDepth(2);
        inclinometer.setDepth(-3);

        gun.setDepth(-2);
        elevationGear.setDepth(1);
        elevationWheel.setDepth(2);
    }
    
    private void initAnimator() {
        animator = new Animator();
        animator.loadAnimation("mortar/firing");
        animator.bindDriver("cradle", this);
    }
    
    private void setChildParameters() {
        inclinometer.setVisible(false);
        
        elevationWheel.translate(20, 106);
        elevationGear.translate(50, 90);
        cradle.translate(1065f * viewportScale, 355f * viewportScale);
        
        craneWheel.translate(545, 385);
        craneGear.translate(495, 398);
        
        cradle.translate(-210, -0.15f * 210);
        inclinometer.translate(710, 235);
        
        mountGrooves.translate(997 * viewportScale, (730) * viewportScale);
        mountGrooves.setVertexOffset(
                new float[]{235, -30},
                new float[]{-220, 80},
                new float[]{220, 80},
                new float[]{-235, -30}
        );
        setCradle(0);
    }
    
    private void init() {   
        spawnChildren();
        appendChildren();
        setDepth();
        initAnimator();
        setChildParameters();
    }
    
    private float getControl(PID controller, float target, float current, float maxSpeed, float coeff) {
        float error = target - current;
        float control = (float) controller.getControl(error, dt);
        if (control > maxSpeed) {
            control = maxSpeed;
        } else if (control < -maxSpeed) { 
            control = -maxSpeed;
        }
        if (Math.abs(control) < 0.1f) {
            controller.deactivate();
        }
        return control * coeff;
    }
    
    private void elevate() {
        if (!elevationControl.isActive()) {
            return;
        }
        addElevation(getControl(
                elevationControl,
                elevationTarget,
                getElevation(),
                elevationMaxSpeed,
                1f
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
                traversalMaxSpeed,
                traversalCoeff
        ));
    }

    /**
     * Palauttaa animaation tilasta ja tykin korotuskulmasta riippuvan tärinäkertoimen.
     * @return
     */
    public float[] getShake() {
        float ret[] = {getElevationFactor(), shakeCoeff};
        return ret;
    }

    /**
     * Vastaanottaa kertoimen joka vaikuttaa tärinän määrään, asetetaan ohjauslogiikasta panosten määrän perusteella.
     * @param modifier
     */
    public void setPowerModifier(float modifier) {
        powerModifier = modifier;
    }
    
    /**
     * Rajapinta animaattorille, vastaanottaa avaimen ja ohjausarvon jolla avaimen määrittämää objektia animoidaan.
     * @param target avain
     * @param value ohjausarvo
     */
    @Override
    public void drive(String target, double value) {
        if (target.equals("cradle")) {
            setCradle((float) value);
        }
    }
    
    private float getElevationFactor() {
        return (float) Math.cos(Math.PI * (getElevation() + 10) / 180) * powerModifier;
    }
    
    private void setCradle(float t) {
        cradle.setPosition(new Vector3d().lerp(cradleLimits[0], cradleLimits[1], t * getElevationFactor()));
        shakeCoeff = t;
    }
    
    /**
     * Palauttaa tykin korotuskulman.
     * @return asteissa
     */
    public float getElevation() {
        return -(float) gun.localRotation.z;
    }
    
    /**
     * Asettaa ohjausarvon tykin korotuskulman muuttamiseen, PID-säätimellä animoitu.
     * @param r asteissa
     */
    public void setElevationTarget(float r) {
        if (r >= gunLimits[0] && r <= gunLimits[1]) {
            elevationTarget = r;
        } else if (r > gunLimits[1]) {
            elevationTarget = gunLimits[1];
        } else if (r < gunLimits[0]) {
            elevationTarget = gunLimits[0];
        }
        if (!elevationControl.isActive()) {
            elevationControl.activate();            
        }

    }

    /**
     * Asettaa ohjausarvon tykin suunnan muuttamiseen, PID-säätimellä animoitu.
     * @param r asteissa
     */
    public void setTraverseTarget(float r) {
        traverseTarget = r;
        if (!traversalControl.isActive()) {
            traversalControl.activate();            
        }
    }

    private void addTraversal(float r) {
        setTraversal(traversal + r);
    }
    /**
     * Asettaa tykin suunnan suoraan.
     * @param r asteissa
     */
    public void setTraversal(float r) {
        traversal = r;
        traverseTarget = traversal;
        mountGrooves.setTexOffset(getTraversal() / 60, 0);
    }

    private float getLocalTraversal() {
        return traversal;
    }

    /**
     * Palauttaa tykin suunan.
     * @return asteissa
     */
    public float getTraversal() {
        return traversal % 360;
    }

    private void addElevation(float r) {
        setElevation(-elevationWheelRot + r);
    }
    /**
     * Asettaa tykin koron halutun tykin kulman mukaan. Vert. sisäinen metodi setElevation, joka asettaa tykin kulman vaihteiston läpi kerrottuna
     * @param r 
     */
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

    /**
     * Päivitysmetodi, päivittää ajastimen, animoi liikkeen ja päivittää animaattorin.
     */
    @Override
    public void update() {
        dt = this.getDeltatime();
        elevate();
        traverse();
        animator.animate(dt);
    }

    /**
     * Pakotettu päivitys testejä varten, vastanaottaa päivitykseen kuluneen ajan.
     * @param deltatime
     */
    public void forcedUpdate(double deltatime) {
        dt = deltatime;
        elevate();
        traverse();
    }

    /**
     * Summaa tykin korotussäätimen ohjausarvoa.
     * @param f muutos asteissa
     */
    public void addToElevationTarget(float f) {
        setElevationTarget(elevationTarget + f);
    }
    /**
     * Summaa tykin siirtosäätimen ohjausarvoa.
     * @param f muutos asteissa
     */
    public void addToTraverseTarget(float f) {
        setTraverseTarget(traverseTarget + f);
    }
    
    /**
     * Asettaa inklinometrin näkyvyyden.
     * @param state
     */
    public void setInclinometer(boolean state) {
        inclinometer.setVisible(state);
    }
}
