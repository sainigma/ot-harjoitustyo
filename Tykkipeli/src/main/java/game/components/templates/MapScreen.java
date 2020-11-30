/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components.templates;

import game.components.GameObject;
import game.logic.controllers.Statistic;
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
    GameObject projectile;
    GameObject projectileShadow;
    
    private double traversal = 0f;
    
    public MapScreen(String name, float viewportScale) {
        super(name);
        this.viewportScale = viewportScale;
        init();
    }
    
    public void setTraversal(double rotation) {
        double offset = -45f;
        traversal = rotation;
        cursor.setRotation((float) (rotation + offset));
    }
    
    public void rotateMap(double rotation) {
        map.rotate((float) rotation);
    }
    
    private void resetProjectile() {
        projectile.setPosition(new Vector3d(0));
        projectile.translate(-256, 256+5, 2);
    }
    
    public void setProjectile(Vector3d position) {
        if (position == null) {
            return;
        }
        System.out.println(position);
        Vector3d pos = new Vector3d(position.x, position.z, position.y);
        pos.set(pos.scale(520f/10000f));
        pos.x -= 256f;
        pos.y += 256f;
        pos.z += 2f;
        projectile.setPosition(pos);
        pos.z = 2f;
        projectileShadow.setPosition(pos);
    }
    
    public void setByStatistic(Statistic stat) {
        if (stat == null) {
            return;
        }
        setProjectile(stat.getLastPosition());
    }
    
    private void init() {
        map = new GameObject("map3d", "mapview/kartta.png", new Vector3d(512,512), viewportScale) { };
        cursor = new GameObject("mapcursor", "mapview/suunta.png", new Vector3d(5,7), viewportScale) { };
        projectile = new GameObject("mapprojectile", "mapview/projektiili.png", new Vector3d(8,8), viewportScale) { };
        projectileShadow = new GameObject("mapprojectile", "mapview/projektiilivarjo.png", new Vector3d(16,16), viewportScale) { };
        
        map.translate(viewportScale*600, viewportScale*(1080/2));
        map.setRotation(new Vector3d(45,0,22.5));
        cursor.translate(-256, 256,1);
        setTraversal(0);
        map.append(cursor);
        map.append(projectile);
        map.append(projectileShadow);
        append(map);
    }
    
    @Override
    public void update() {
        //cursor.rotate(0.5f);
    }
}
