/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import org.json.*;

/**
 *
 * @author suominka
 */
public class Animation {
    private class Frame {
        public int frame = 0;
        public float position[] = {0, 0};
        public double value = 0;
        
        public Frame(int frame, double value) {
            this.frame = frame;
            this.value = value;
        }
        
        public Frame(int frame, float position[]) {
            this.frame = frame;
            this.position[0] = position[0];
            this.position[1] = position[1];
        }
    }
    
    private class Clip {
        private int index = 0;
        private int current = 0;
        private int next = 0;
        private ArrayList<Frame> frames;
        
        public Frame get(int frame) {
            return null;
        }
        public void addFrame(Frame frame) {
            frames.add(frame);
        }
    }
    
    private double framerate = 24;
    private int frames = 0;
    private boolean linearInterpolation = false;
    private int currentIndex = 0;
    private boolean playing = false;
    private boolean loop = false;
    private HashMap<String, Clip> driverClips;
    
    public Animation(String path) {
        loadAnimation(path);
    }
    
    private void loadClips(HashMap clips, JSONObject data) {
        for (String key : data.keySet()) {
            System.out.println(key);
            Clip clip = new Clip();
            JSONArray frames = data.getJSONArray(key);
            for (Object frame : frames) {
                JSONArray framearr = (JSONArray) frame;
                if (framearr.length() == 2) {
                    Frame newFrame = new Frame(framearr.getInt(0), framearr.getDouble(1));
                } else if (framearr.length() == 3) {
                    float values[] = {framearr.getFloat(1), framearr.getFloat(2)};
                    Frame newFrame = new Frame(framearr.getInt(0), values);
                }
            }
        }
    }
    
    private void loadAnimation(String path) {
        try {
            String raw = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
            JSONObject obj = new JSONObject(raw);
            System.out.println(obj.keySet());
            
            framerate = obj.getInt("framerate");
            frames = obj.getInt("length");
            
            JSONObject drivers = obj.getJSONObject("drivers");
            
            loadClips(driverClips, drivers);
            
        } catch (Exception e) {
        }

    }
    
    public void play() {
        playing = true;
    }
    
    public void pause() {
        playing = false;
    }
    
    public void stop() {
        playing = false;
        currentIndex = 0;
    }
    
    public void reset() {
        currentIndex = 0;
    }
    
    public boolean isPlaying() {
        return playing;
    }
}
