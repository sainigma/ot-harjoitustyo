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
package game.components;

import game.components.animation.Animation;
import game.components.animation.Frame;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Animaattori jolla voi animoida ohjausarvoja ja peliobjekteja. Rajapinta Animation -luokalle.
 * @author Kari Suominen
 */
public class Animator {
    private HashSet<Animation> activeClips;
    private HashMap<String, Animation> animations;
    private HashMap<String, GameObject> bones;
    private HashMap<String, GameObject> drivers;
    private int currentFrame;
    private double startTime;
    
    public Animator() {
        activeClips = new HashSet<>();
        animations = new HashMap<>();
        bones = new HashMap<>();
        drivers = new HashMap<>();
    }
    
    /**
     * Alustaa uuden animaation ja lisää sen animaatioiden listaan.
     * @param path polku animaatiotiedostoon, muotoa objekti/animaatio, ilman tiedostopäätettä
     */
    public void loadAnimation(String path) {
        Animation newAnimation = new Animation(path);
        animations.put(path, newAnimation);
    }
    
    /**
     * Asettaa animaation ajettavaksi avaimen perusteella
     * @param name animaation nimi, muotoa objekti/animaatio
     */
    public void playAnimation(String name) {
        if (!animations.containsKey(name)) {
            return;
        }
        Animation animation = animations.get(name);
        if (activeClips.contains(animation)) {
            animation.play();
        } else {
            activeClips.add(animation);
            animation.play();
        }
    }
    
    /**
     * Palauttaa toden jos jokin animaatio on päällä.
     * @return
     */
    public boolean isPlaying() {
        return activeClips.size() > 0;
    }
    /**
     * Palauttaa toden jos haettu animaatio on päällä.
     * @param name Animaation nimi
     * @return 
     */
    public boolean isPlaying(String name) {
        if (!animations.containsKey(name)) {
            return false;
        }
        Animation animation = animations.get(name);
        return animation.isPlaying();
    }
    /**
     * Asettaa GameObject instanssin siirtymillä animoiduksi.
     * @param name Avain GameObject implementaation animate-metodille
     * @param object Peliobjekti
     */
    public void bindBone(String name, GameObject object) {
        bones.put(name, object);
    }
    
    /**
     * Asettaa GameObject instanssin ohjausarvoilla animoiduksi.
     * @param name Avain GameObject implementaation drive-metodille
     * @param object Peliobjekti
     */
    public void bindDriver(String name, GameObject object) {
        drivers.put(name, object);
    }
  
    private void update(Animation animation, double deltatime) {
        
        for (String name : drivers.keySet()) {
            Frame currentFrame = animation.getDriverFrame(name);
            drivers.get(name).drive(name, currentFrame.value);
        }
        
        if (!animation.isPlaying()) {
            activeClips.remove(animation);
        }
    }
    
    /**
     * Edistää aktiivisia animaatioita
     * @param deltatime Piirtoon kulunut aika sekunneissa
     */
    public void animate(double deltatime) {
        if (!activeClips.isEmpty()) {
            for (Animation animation : activeClips) {
                if (!animation.isPlaying()) {
                    activeClips.remove(animation);
                } else {
                    update(animation, deltatime);
                }
                animation.advance();
            }
        }
    }
}
