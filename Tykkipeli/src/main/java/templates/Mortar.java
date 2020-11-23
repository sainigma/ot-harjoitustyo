/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package templates;

import components.GameObject;
import utils.Vector3d;

/**
 *
 * @author suominka
 */
public class Mortar extends GameObject{
    private float viewportScale;
    long start = System.currentTimeMillis();
    
    private float[] gunLimits = {0, 90};
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
    }
    
    public void setCradle(float t){
        cradle.setPosition(new Vector3d().lerp(cradleLimits[0], cradleLimits[1], t));
    }
    float t2 = 0f;
    public void update(){
        float t = (float)(Math.cos((System.currentTimeMillis()-start)*0.001)*0.5f+0.5f);
        setCradle(t);
        t2+=0.005f;
        mountGrooves.setTexOffset(t2, 0);
    }
}
