/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components.templates;

import game.utils.Vector3d;
import game.components.GameObject;
import game.components.GameObject;
import java.util.Random;

/**
 *
 * @author suominka
 */
public class ViewPort extends GameObject {
    private boolean screenShake = false;
    private float screenShakeIntensity = 1;
    Random rand = new Random();
    
    public ViewPort(String name) {
        super(name);
    }
    
    @Override
    public void update() {
        if (screenShake) {
            setPosition(new Vector3d(
                    (rand.nextFloat() * 4 * screenShakeIntensity),
                    (rand.nextFloat() * 2 * screenShakeIntensity)
            ));
        }
    }
    public void setScreenShake(float intensity) {
        screenShakeIntensity = intensity;
        if (intensity < 0.1) {
            screenShake = false;
            setPosition(new Vector3d());
        } else {
            screenShake = true;
        }
    }
    public boolean isShaking() {
        return screenShake;
    }
}
