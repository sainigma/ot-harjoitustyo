/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import utils.Vector3d;

/**
 *
 * @author suominka
 */
public class ScrollingObject extends GameObject{
    public ScrollingObject(String name){
        super(name);
    }
    public ScrollingObject(String name, String path){
        super(name,path);
    }
    public ScrollingObject(String name, String path, Vector3d origin){
        super(name,path,origin);
    }
    @Override
    public void update(){
        hasUpdated = true;
        x+=15;
        rotation+=15;
        if( x > 1280 ){
            x = 0;
            y += 100;
            if( y > 720 ){
                y = 0;
            }
        }
    }
}
