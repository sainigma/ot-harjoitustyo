/*
 * Copyright (C) 2020 Kari Suominen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package game.logic.controllers;

import game.utils.JSONLoader;
import game.utils.Vector3d;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
/**
 * Luokka maaliobjektien hallintaan.
 * @author Kari Suominen
 */
public class TargetLogic {
    private ArrayList<Vector3d> waypoints;
    
    private String name;
    private float health;
    private float speedMs;
    private float range;
    
    private Vector3d position;
    private Vector3d direction;
    
    private boolean active;
    private boolean initialized = false;
    
    private boolean sinking = false;
    
    private double timeToSpawn = 0;
    private double timeAlive = 0;
    
    private double sinkAngle = 0f;
    
    private boolean victory = false;
    
    /**
     * Rakentaja luokalle, nimi määrittää mistä tiedostosta parametrit luetaan.
     * @param name
     */
    public TargetLogic(String name) {
        this.name = name;
        this.waypoints = new ArrayList<>();
        load();
    }
    
    /**
     * Vahingoitaa maalia, upottaa maalin jos health putoaa nollan toiselle puolelle.
     * @param damage
     */
    public void reduceHealth(float damage) {
        health -= damage;
        if (health <= 0f) {
            sinking = true;
        }
    }
    
    /**
     * Palauttaa healthin.
     * @return
     */
    public float getHealth() {
        return health;
    }
    
    /**
     * Palauttaa tykkien kantaman maalille, peli päättyy jos pelaaja tulee kantaman sisälle.
     * @return
     */
    public float getRange() {
        return range;
    }
    
    /**
     * Onko uppoaminen käynnissä.
     * @return
     */
    public boolean isSinking() {
        return sinking;
    }
    
    private void load() {
        JSONObject obj = new JSONLoader("assets/levels/ships/").read(name);
        float speedKmh = obj.getFloat("speedKmh");
        this.speedMs = speedKmh / 3.6f;
        this.health = obj.getFloat("health");
        this.name = obj.getString("icon");
        this.range = obj.getFloat("rangeMeters");
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
        if (!sinking) {
            if (health <= 0f) {
                sinking = true;
            }
            double deltaSpace = factor * speedMs * 16f / 1000f;
            position.x += deltaSpace * direction.x;
            position.y += deltaSpace * direction.y;
            position.z += deltaSpace * direction.z;
            
            if (position.magnitude() < range) {
                victory = true;
            }
        } else if (position.z > -2000f) {
            position.z -= 0.2f;
        } else {
            active = false;
        }
    }
    
    /**
     * Palauttaa toden jos pelaaja on maalin kantaman sisällä, voitto maalille tarkoittaa häviötä pelaajalle.
     * @return
     */
    public boolean hasWon() {
        return victory;
    }
    
    /**
     * Päivitysmetodi luokalle, varsinainen päivitys kutsutussa moveship metodissa.
     * @param deltatimeMillis
     */
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
    
    private float getWaypoint(Object waypoint) {
        try {
            if (waypoint instanceof Integer) {
                return ((Integer) waypoint).floatValue();
            }
            return ((BigDecimal) waypoint).floatValue();
        } catch (ClassCastException e) {
            throw new ClassCastException("level data malformed, check ship waypoints.");
        }
    }
    
    /**
     * Vastaanottaa kenttädatasta maalille määrätyn liikeradan.
     * @param arr
     */
    public void setWaypoints(JSONArray arr) {
        for (Object it : arr) {
            JSONArray subarr = (JSONArray) it;
            float x = getWaypoint(subarr.get(0));
            float y = getWaypoint(subarr.get(1));

            addWaypoint(x, y);
        }
        loadWaypoint(0);
    }
    
    private void addWaypoint(float x, float y) {
        waypoints.add(new Vector3d(x, y, 0));
    }
    
    private void loadWaypoint(int index) {
        position = waypoints.get(index);
        direction = waypoints.get(index + 1).diff(position);
        direction.normalize();
        initialized = true;
    }
    
    /**
     * Palauttaa maalin sijainnin.
     * @return
     */
    public Vector3d getPosition() {
        return position;
    }
    
    /**
     * Palauttaa maalin suunnan.
     * @return
     */
    public Vector3d getDirection() {
        return direction;
    }
    
    /**
     * Palauttaa maalin suunnan asteissa.
     * @return
     */
    public Vector3d getRotation() {
        if (sinking && sinkAngle < 20f) {
            sinkAngle += 0.04f;
        }
        double rotation = -180f * Math.atan2(direction.y, direction.x) / Math.PI;
        return new Vector3d(-90, rotation, sinkAngle);
    }
    
    /**
     * Palauttaa maalin nimen, eli tyypin.
     * @return
     */
    public String getName() {
        return name;
    }
}
