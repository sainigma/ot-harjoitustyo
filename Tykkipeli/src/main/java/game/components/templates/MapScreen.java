/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components.templates;

import game.components.GameObject;
import game.graphics.LineDrawer;
import game.logic.controllers.Statistic;
import game.simulations.cases.Ballistics;
import game.utils.Vector3d;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *
 * @author suominka
 */
public class MapScreen extends GameObject {
    Plotter plotter;
    
    private float viewportScale;
    private boolean minimized = true;
    private float mapTexScale = 520f / 10000f;
    Vector3d mapRotation = new Vector3d(0, 0, 0);
    GameObject map;
    GameObject minimap;
    GameObject projectileFront;
    GameObject projectileShadow;
    
    ArrayList<Plotter> plotters;
    ArrayList<Plotter> historicalPlots;
    ArrayList<ProjectileGroup> projectiles;
    
    GameObject cursor;
    GameObject minicursor;
    
    private double traversal;
    
    class Plotter {
        LineDrawer plotter;
        private boolean active;
        public Plotter() {
            plotter = new LineDrawer();
            plotter.setLineStep(100);
            plotter.setScale(mapTexScale);
            plotter.setTransforms(new Vector3d(-256,256,0), new Vector3d(90,0,0), map.getPosition(), map.getRotation());
        }
        public void setPlot(ArrayList<Vector3d> positions) {
            plotter.setPlot(positions);
        }
        public void plot() {
            plotter.setGlobalRotation(map.getRotation());
            plotter.draw();
        }
    }
    
    class ProjectileGroup {
        
        GameObject front;
        GameObject shadow;
        private boolean visible;
        private float power;
        
        public ProjectileGroup() {
            this.front = projectileFront.clone();
            this.shadow = projectileShadow.clone();
            map.append(front);
            map.append(shadow);
            
            setVisible(false);
            power = 0;
        }
        
        private void setVisible(boolean state) {
            visible = state;
            front.setVisible(state);
            shadow.setVisible(state);
        }
        
        public void setPower(float power) {
            this.power = power;
        }
        
        public float getPower() {
            return power;
        }
        
        public void setPosition(Vector3d position) {
            if (position == null) {
                return;
            }
            if (!visible) {
                setVisible(true);
            }
            Vector3d pos = new Vector3d(position.x, position.z, position.y);
            pos.set(pos.scale(520f / 10000f));
            pos.x -= 256f;
            pos.y += 256f;
            pos.z += 0.2f;
            front.setPosition(pos);
            pos.z = 0.2f;
            shadow.setPosition(pos);
            setRotation(map.getRotation());
        }
        public void setRotation(Vector3d rotation) {
            front.setRotation(new Vector3d(0, 0, rotation.z));
        }
        public Vector3d getPosition() {
            return shadow.getPosition();
        }
        public void kill() {
            map.remove(front);
            map.remove(shadow);
        }
    }
    
    public MapScreen(String name, float viewportScale) {
        super(name);
        this.traversal = 0f;
        this.viewportScale = viewportScale;
        projectiles = new ArrayList<>();
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
    }
    
    private void setProjectile(ProjectileGroup projectile, Vector3d position, float power) {
        projectile.setPower(power);
        projectile.setPosition(position);
    }
    
    public void freeProjectiles(int i) {
        if (projectiles.size() <= i) {
            return;
        }
        int j = projectiles.size() - 1;
        while (projectiles.size() > i) {
            ProjectileGroup projectile = projectiles.get(j);
            spawnHitmarker(projectile.getPosition(), projectile.getPower());
            projectiles.get(j).kill();
            projectiles.remove(j);
        }
    }
    
    public void setByHistory(HashMap<Ballistics, Statistic> history) {
        int i = 0;
        for (Ballistics solver : history.keySet()) {
            Statistic stat = history.get(solver);
            if (stat.isActive()) {
                i += 1;
                if (projectiles.size() < i) {
                    spawnProjectile();
                }
                plotter.setPlot(stat.getPositions());
                setProjectile(projectiles.get(i - 1), stat.getLastPosition(), stat.getPower());
            }
        }
        if (projectiles.size() > i) {
            freeProjectiles(i);
        }
    }
    
    private void spawnChildren() {
        projectileFront = new GameObject("mapprojectile", "mapview/projektiili.png", new Vector3d(8, 8), viewportScale) { };;
        projectileShadow = new GameObject("mapprojectile", "mapview/projektiilivarjo.png", new Vector3d(16, 16), viewportScale) { };
        
        map = new GameObject("map3d", "mapview/kartta.png", new Vector3d(512, 512), viewportScale) { };
        minimap = new GameObject("minimap", "mapview/karttamini.png", new Vector3d(128, 128), viewportScale) { };
        
        cursor = new GameObject("mapcursor", "mapview/suunta.png", new Vector3d(5, 7), viewportScale) { };
        minicursor = new GameObject("mapcursor", "mapview/suuntamini.png", new Vector3d(0), viewportScale) { };
    }
    
    private void spawnProjectile() {
        projectiles.add(new ProjectileGroup());
    }
    
    private void spawnHitmarker(Vector3d position, float power) {
        System.out.println("spawning hitmarker at " + position);
    }
    
    private void setChildTransforms() {
        projectileFront.setRotation(new Vector3d(0, 90, 0));
        projectileShadow.setRotation(new Vector3d(90, 0, 0));
        
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
        setChildTransforms();
        
        plotter = new Plotter();
        
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
    
    @Override
    public void update() {
        plotter.plot();
    }
}
