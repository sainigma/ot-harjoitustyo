/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components.templates;

import game.components.GameObject;
import game.utils.Vector3d;

/**
 *
 * @author suominka
 */
public class Mortar extends GameObject{
    private float viewportScale;
    long start = System.currentTimeMillis();
    
    private float elevationTarget = 0;
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
    
    public Mortar(String name, float viewportScale){
        super(name);
        this.viewportScale = viewportScale;
        init();
    }
    
    private void init(){
        
        mount = new GameObject("mortarstand","mortar/jalusta.png",new Vector3d(0), viewportScale){};
        mountGrooves = new GameObject("mortargrooves","mortar/urat.png",new Vector3d(512,512,0), viewportScale*0.58f){};
        gun = new GameObject("mortartube","mortar/tykki.png",new Vector3d(512,512,1), viewportScale){};
        cradle = new GameObject("mortarcar","mortar/karry.png",new Vector3d(398,147,-1), viewportScale){};
        elevationWheel = new GameObject("mortarwheel","mortar/ruori.png",new Vector3d(128,128,-3), viewportScale){};
        elevationGear = new GameObject("mortargear","mortar/ratas.png",new Vector3d(64,64,-2), viewportScale){};
        craneWheel = elevationWheel.clone();
        craneGear = elevationGear.clone();
        
        cradle.append(gun);
        cradle.append(elevationGear);
        cradle.append(elevationWheel);
        mount.append(cradle);
        mount.append(craneWheel);
        mount.append(craneGear);
        mount.append(mountGrooves);
        
        elevationWheel.translate(20, 106);
        elevationGear.translate(50,90);
        cradle.translate(1065f*viewportScale, 355f*viewportScale);
        
        craneWheel.translate(545, 385);
        craneGear.translate(495, 398);
        
        cradle.translate(-210, -0.15f*210);

        mountGrooves.translate(997*viewportScale, (730)*viewportScale);
        mountGrooves.setVertexOffset(
                new float[]{240,-30},
                new float[]{-220,80},
                new float[]{220,80},
                new float[]{-240,-30}
        );
        append(mount);

        setElevationTarget(60);
    }
    
    public float getElevationFactor(){
        return (float)Math.cos(Math.PI*(getElevation()-10)/180);
    }
    
    public void setCradle(float t){
        cradle.setPosition(new Vector3d().lerp(cradleLimits[0], cradleLimits[1], t*getElevationFactor()));
    }
    public float getElevation(){
        return -gun.rotation;
    }
    public void setElevationTarget(float r){
        elevationTarget = r;
    }
    private float wheelRot = 0;
    public void addElevation(float r){
        setElevation(-wheelRot+r);
    }
    private void setElevation(float r){
        //22min -70max
        wheelRot = -r;
        float gearRot = -wheelRot*(12f/60f);
        float gunRot = -gearRot*(12f/142f);
        if( getElevation() > gunLimits[0] && getElevation() < gunLimits[1] ){
            elevationWheel.setRotation(wheelRot);
            elevationGear.setRotation(gearRot);
            gun.setRotation(gunRot+22f);            
        }
    }
    private float errorPrev = 0;
    private float errorSum = 0;
    private void elevate(){
        float maxSpeed = 10f;
        float error =  elevationTarget - getElevation();
        if( error > 0.1f || error < -0.1f ){
            float errorSlope = (error - errorPrev)/dt;
            errorPrev = error;
            float control = error + 2f*errorSlope;
            if( control > maxSpeed ){
                control = maxSpeed;
            }else if( control < -maxSpeed ){
                control = -maxSpeed;
            }
            addElevation(control);
        }
    }
    
    float r2 = 0;
    float t2 = 0f;
    private float dt = 0;
    private long lastTime = System.nanoTime();
    public void update(){
        long time = System.nanoTime()/1000000;
        dt = (float)(time - lastTime);
        float t = (float)(Math.cos((System.currentTimeMillis()-start)*0.001)*0.5f+0.5f);
        r2 += 10;
        setCradle(t);
        elevate();
        t2+=0.005f;
        mountGrooves.setTexOffset(t2, 0);
        lastTime = time;
    }
}
