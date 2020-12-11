/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWKeyCallback;

/**
 *
 * @author suominka
 */
public class InputManager {
    long window;
    HashMap<Integer, String[]> keyMap;
    HashMap<String, Boolean> states;
    HashMap<String, Boolean> debounced;
    
    Set<String> keyList;
    public InputManager(long window) {
        this.window = window;
        keyMap = new HashMap<>();
        states = new HashMap<>();
        debounced = new HashMap<>();
        keyList = new HashSet<>();
        setKeys();
        collectKeys();
        glfwSetKeyCallback(window, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (!keyMap.containsKey(key)) {
                    return;
                }
                String[] keys = keyMap.get(key);
                boolean newState = false;
                boolean ok = false;
                if (action == GLFW_PRESS) {
                    newState = true;
                    ok = true;
                } else if (action == GLFW_RELEASE) {
                    ok = true;
                }
                if (!ok) {
                    return;
                }
                for (String k : keys) {
                    //System.out.println(k + " " + newState);
                    states.put(k, newState);
                }
            }
        });
    }
    private void setKeys() {
        //korvaa tiedostonlukijalla
        keyMap.put(GLFW_KEY_UP, new String[]{"up", "elevate"});
        keyMap.put(GLFW_KEY_DOWN, new String[]{"down", "depress"});
        keyMap.put(GLFW_KEY_LEFT, new String[]{"left", "traverse left"});
        keyMap.put(GLFW_KEY_RIGHT, new String[]{"right", "traverse right"});
        
        keyMap.put(GLFW_KEY_SPACE, new String[]{"ok", "fire"});
        keyMap.put(GLFW_KEY_ENTER, new String[]{"ok"});
        keyMap.put(GLFW_KEY_BACKSPACE, new String[]{"previous"});
        
        keyMap.put(GLFW_KEY_R, new String[]{"reload"});
        
        keyMap.put(GLFW_KEY_LEFT_SHIFT, new String[]{"modifier faster"});
        keyMap.put(GLFW_KEY_LEFT_CONTROL, new String[]{"modifier slower"});
        keyMap.put(GLFW_KEY_RIGHT_SHIFT, new String[]{"modifier faster"});
        keyMap.put(GLFW_KEY_RIGHT_CONTROL, new String[]{"modifier slower"});
        
        keyMap.put(GLFW_KEY_M, new String[]{"toggle map"});
        keyMap.put(GLFW_KEY_Q, new String[]{"rotate map left"});
        keyMap.put(GLFW_KEY_E, new String[]{"rotate map right"});
        
        keyMap.put(GLFW_KEY_ESCAPE, new String[]{"quit"});
    }
    
    private void collectKeys() {
        for (int key : keyMap.keySet()) {
            String [] keys = keyMap.get(key);
            for (String trueKey : keys) {
                keyList.add(trueKey);
            }
        }
    }
    
    public boolean keyDown(String action) {
        if (!states.containsKey(action)) {
            return false;
        }
        return states.get(action);
    }
    
    public boolean keyDownOnce(String action) {
        if (!states.containsKey(action)) {
            return false;
        }
        if (!debounced.containsKey(action)) {
            debounced.put(action, false);
            return states.get(action);
        }
        boolean state = states.get(action);
        if (!state && !debounced.get(action)) {
            debounced.put(action, true);
        } else if (state && debounced.get(action)) {
            debounced.put(action, false);
            return true;
        }
        
        return state && debounced.get(action);
    }
    
    public void update() {
        glfwPollEvents();
    }
}
