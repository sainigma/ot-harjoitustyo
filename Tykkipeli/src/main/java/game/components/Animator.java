/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components;

import game.components.animation.Animation;
import game.components.animation.Frame;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Animaattori jolla voi animoida sekä ohjausarvoja että peliobjekteja.
 * @author suominka
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
    public boolean isPlaying() {
        return activeClips.size() > 0;
    }
    public boolean isPlaying(String name) {
        if (!animations.containsKey(name)) {
            return false;
        }
        Animation animation = animations.get(name);
        return animation.isPlaying();
    }
    
    public void bindBone(String name, GameObject object) {
        bones.put(name, object);
    }
    
    /**
     * Asettaa GameObject instanssin ajettavaksi.
     * @param name Avain GameObject implementaation drive-metodille
     * @param object Peliobjekti
     */
    public void bindDriver(String name, GameObject object) {
        drivers.put(name, object);
    }
    
    /**
     * Hakee animaatiolle nykyisen ruudun piirtoon kuluneen ajan perusteella.
     * Ruudun arvo luovutetaan avaimen osoittaman GameObject instanssin drive -funktiolle
     * 
     * Tästä metodista puuttuu vielä animointi GameObjectia suoraan pyörittämällä.
     * @param animation
     * @param deltatime 
     */
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
