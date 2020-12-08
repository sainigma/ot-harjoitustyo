/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic.controllers;

import game.utils.JSONLoader;
import game.utils.Vector3d;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
/**
 *
 * @author suominka
 */
public class TargetLogic {
    ArrayList<Vector3d> waypoints;
    
    private String name;
    private float health;
    private float speedMs;
    
    private Vector3d position;
    private Vector3d direction;
    
    private boolean active;
    private boolean initialized = false;
    
    private boolean sinking = false;
    
    private double timeToSpawn = 0;
    private double timeAlive = 0;
    
    private double sinkAngle = 0f;
    
    public TargetLogic(String name) {
        this.name = name;
        this.waypoints = new ArrayList<>();
        load();
    }
    
    public void reduceHealth(float damage) {
        health -= damage;
        if (health <= 0f) {
            sinking = true;
        }
    }
    
    public float getHealth() {
        return health;
    }
    
    public boolean isSinking() {
        return sinking;
    }
    
    private void load() {
        JSONObject obj = new JSONLoader("assets/levels/ships/").read(name);
        float speedKmh = obj.getFloat("speedKmh");
        this.speedMs = speedKmh / 3.6f;
        this.health = obj.getFloat("health");
        this.name = obj.getString("icon");
    }
    
    private boolean sleeping() {
        if (active) {
            return !active;
        }
        if (health > 0 && timeToSpawn < timeAlive) {
            active = true;
        }
        return !active;
    }
    
    private void moveShip(double deltatimeMillis) {
        double factor = deltatimeMillis / 16f;
        if (factor < 5f) {
            if (!sinking) {
                if (health <= 0f) {
                    sinking = true;
                }
                double deltaSpace = factor * speedMs * 16f / 1000f;
                position.x += deltaSpace * direction.x;
                position.y += deltaSpace * direction.y;
                position.z += deltaSpace * direction.z;
            } else if (position.z > -200f) {
                position.z -= 0.2f;
            } else {
                active = false;
            }
        }
    }
    
    public void update(double deltatimeMillis) {
        if (!initialized) {
            return;
        }
        timeAlive += (double) deltatimeMillis;
        if (sleeping()) {
            return;
        }
        moveShip(deltatimeMillis);
    }
    
    public void setWaypoints(JSONArray arr) {
        for (Object it : arr) {
            JSONArray subarr = (JSONArray) it;
            float x = ((BigDecimal) subarr.get(0)).floatValue();
            float y = ((BigDecimal) subarr.get(1)).floatValue();
            addWaypoint(x, y);
        }
        loadWaypoint(0);
    }
    
    public void addWaypoint(float x, float y) {
        waypoints.add(new Vector3d(x, y, 0));
    }
    
    private void loadWaypoint(int index) {
        position = waypoints.get(index);
        direction = waypoints.get(index + 1).diff(position);
        direction.normalize();
        initialized = true;
    }
    
    public Vector3d getPosition() {
        return position;
    }
    
    public Vector3d getDirection() {
        return direction;
    }
    
    public Vector3d getRotation() {
        if (sinking && sinkAngle < 20f) {
            sinkAngle += 0.04f;
        }
        double rotation = -180f * Math.atan2(direction.y, direction.x) / Math.PI;
        return new Vector3d(-90, rotation, sinkAngle);
    }
    
    public String getName() {
        return name;
    }
}
