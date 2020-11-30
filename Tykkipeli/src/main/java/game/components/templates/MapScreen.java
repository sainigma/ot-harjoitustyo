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
    GameObject cursor;
    GameObject projectilePlane;
    
    public MapScreen(String name, float viewportScale) {
        super(name);
        this.viewportScale = viewportScale;
        init();
    }
    
    public void setTraversal(double rotation) {
        double offset = -45f;
        cursor.setRotation((float) (rotation + offset));
    }
    
    public void rotateMap(double rotation) {
        map.rotate((float) rotation);
    }
    
    private void init() {
        map = new GameObject("map3d", "mapview/kartta.png", new Vector3d(512,512,0), viewportScale) { };
        cursor = new GameObject("mapcursor", "mapview/suunta.png", new Vector3d(5,7,0), viewportScale) { };
        map.translate(viewportScale*600, viewportScale*(1080/2));
        map.setRotation(new Vector3d(45,0,22.5));
        cursor.translate(-256, 256,1);
        setTraversal(0);
        map.append(cursor);
        append(map);
    }
    
    @Override
    public void update() {
    }
}
