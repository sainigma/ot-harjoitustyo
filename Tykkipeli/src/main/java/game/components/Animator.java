/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components;

import java.util.HashMap;
import java.util.HashSet;

/**
 *
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
    
    public void loadAnimation(String path) {
        Animation newAnimation = new Animation(path);
        animations.put(path, newAnimation);
    }
    
    public void playAnimation(String name) {
        if (!animations.containsKey(name)) {
            return;
        }
        Animation animation = animations.get(name);
        if (activeClips.contains(animation)) {
            animation.reset();
            animation.play();
        } else {
            activeClips.add(animation);
            animation.play();
        }

    }
    
    public void bindBone(String name, GameObject object) {
        bones.put(name, object);
    }
    
    public void bindDriver(String name, GameObject object) {
        drivers.put(name, object);
    }
    
    private void _animate(Animation animation, double deltatime) {
        
        for (String name : drivers.keySet()) {
            Frame currentFrame = animation.getDriverFrame(name);
            drivers.get(name).drive(name, currentFrame.value);
        }
        
        if (!animation.isPlaying()) {
            activeClips.remove(animation);
        }
    }
    
    public void animate(double deltatime) {
        if (!activeClips.isEmpty()) {
            for (Animation animation : activeClips) {
                if (!animation.isPlaying()) {
                    activeClips.remove(animation);
                } else {
                    _animate(animation, deltatime);
                }
                animation.advance();
            }
        }
    }
}
