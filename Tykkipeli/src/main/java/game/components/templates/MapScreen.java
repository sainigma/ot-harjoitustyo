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
    private boolean minimized = true;
    Vector3d mapRotation = new Vector3d(0, 0, 0);
    GameObject map;
    GameObject minimap;
    ProjectileGroup projectileIcon;
    
    GameObject cursor;
    GameObject minicursor;
    
    private double traversal;
    
    class ProjectileGroup {
        
        GameObject front;
        GameObject shadow;
        
        public ProjectileGroup(GameObject front, GameObject shadow) {
            this.front = front;
            this.shadow = shadow;
            
            this.front.setRotation(new Vector3d(0, 90, 0));
            this.front.setRotation(new Vector3d(90, 0, 0));
            
            map.append(front);
            map.append(shadow);
        }
        
        public void setPosition(Vector3d position) {
            if (position == null) {
                return;
            }
            Vector3d pos = new Vector3d(position.x, position.z, position.y);
            pos.set(pos.scale(520f / 10000f));
            pos.x -= 256f;
            pos.y += 256f;
            pos.z += 2f;
            front.setPosition(pos);
            pos.z = 2f;
            shadow.setPosition(pos);
        }
        public void setRotation(Vector3d rotation) {
            front.setRotation(new Vector3d(0, 0, rotation.z));
        }
    }
    
    public MapScreen(String name, float viewportScale) {
        super(name);
        this.traversal = 0f;
        this.viewportScale = viewportScale;
        init();
    }
    
    public void setTraversal(double rotation) {
        double offset = -45f;
        traversal = rotation;
        cursor.setRotation((float) (rotation + offset));
        minicursor.setRotation((float) (rotation + offset));
    }
    
    public void rotateMap(double rotation) {
        map.rotate((float) rotation);
        minimap.rotate((float) rotation);
        projectileIcon.setRotation(map.getRotation());
    }
    
    public void setProjectile(Vector3d position) {
        projectileIcon.setPosition(position);
    }
    
    public void setByStatistic(Statistic stat) {
        if (stat == null) {
            return;
        }
        setProjectile(stat.getLastPosition());
    }
    
    private void spawnChildren() {
        map = new GameObject("map3d", "mapview/kartta.png", new Vector3d(512, 512), viewportScale) { };
        minimap = new GameObject("minimap", "mapview/karttamini.png", new Vector3d(128, 128), viewportScale) { };
        
        cursor = new GameObject("mapcursor", "mapview/suunta.png", new Vector3d(5, 7), viewportScale) { };
        minicursor = new GameObject("mapcursor", "mapview/suuntamini.png", new Vector3d(0), viewportScale) { };
    }
    
    private void spawnProjectile() {
        GameObject projectileFront = new GameObject("mapprojectile", "mapview/projektiili.png", new Vector3d(8, 8), viewportScale) { };
        GameObject projectileShadow = new GameObject("mapprojectile", "mapview/projektiilivarjo.png", new Vector3d(16, 16), viewportScale) { };        
        projectileIcon = new ProjectileGroup(projectileFront, projectileShadow);        
    }
    
    private void setChildTransforms() {
        map.translate((1280 / 2), (720 / 2));
        map.setRotation(new Vector3d(45, 0, 22.5));
        
        minimap.translate(1280f - 110f, 720f - 90f, 100f);
        minimap.setRotation(new Vector3d(45, 0, 22.5));        
        
        cursor.translate(-256, 256, 1);
        minicursor.translate(-64, 64, 1);
        setTraversal(0);
    }
    
    private void init() {
        spawnChildren();
        spawnProjectile();
        setChildTransforms();

        map.append(cursor);
        minimap.append(minicursor);
        
        append(map);
        append(minimap);
    }
    
    @Override
    public void setMinimized(boolean minimized) {
        if (minimized) {
            map.setVisible(false);
            minimap.setVisible(true);
        } else {
            map.setVisible(true);
            minimap.setVisible(false);
        }
    }
}
