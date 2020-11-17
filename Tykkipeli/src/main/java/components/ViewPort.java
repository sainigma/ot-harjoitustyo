/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import java.util.Random;

/**
 *
 * @author suominka
 */
public class ViewPort extends GameObject{
    private boolean screenShake = false;
    private float screenShakeIntensity = 1;
    Random rand = new Random();
    
    public ViewPort(String name){
        super(name);
    }
    
    @Override
    public void update(){
        if(screenShake){
            hasUpdated = true;
            x=(int)(rand.nextFloat()*4*screenShakeIntensity);
            y=(int)(rand.nextFloat()*2*screenShakeIntensity);
        }else if(x!=0 || y!=0 || rotation!=0){
            x=0;
            y=0;
            rotation=0;
        }
    }
    public void setScreenShake(float intensity){
        screenShakeIntensity = intensity;
        if( intensity < 0.1 ){
            screenShake = false;
        }else{
            screenShake = true;
        }
    }
}
