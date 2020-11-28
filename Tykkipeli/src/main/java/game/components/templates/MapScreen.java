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
public class MapScreen extends GameObject {
    private float viewportScale;
    Vector3d mapRotation = new Vector3d(0,0,0);
    GameObject map;
    public MapScreen(String name, float viewportScale) {
        super(name);
        this.viewportScale = viewportScale;
        init();
    }
    
    private void init() {
        map = new GameObject("mortarstand", "mapview/kartta.png", new Vector3d(512,512,20), viewportScale) { };
        map.translate(viewportScale*600, viewportScale*(1080/2));
        map.setTrueRotation(new Vector3d(45,0,22.5));
        System.out.println("moi");
        append(map);
    }
    
    float a = 0;
    @Override
    public void update() {
        a+=0.5;
        map.setTrueRotation(new Vector3d(45,0,22.5+a));
    }
}
