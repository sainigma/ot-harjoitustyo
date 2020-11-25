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
    
    Set<String> keyList;
    
    public InputManager(long window) {
        this.window = window;
        keyMap = new HashMap<>();
        states = new HashMap<>();
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
                    System.out.println(k + " " + newState);
                    states.put(k, newState);
                }
            }
        });
    }
    private void setKeys() {
        //korvaa tiedostonlukijalla
        keyMap.put(GLFW_KEY_UP, new String[]{"up", "gunUp"});
        keyMap.put(GLFW_KEY_DOWN, new String[]{"down", "gunDown"});
        keyMap.put(GLFW_KEY_LEFT, new String[]{"left", "gunLeft"});
        keyMap.put(GLFW_KEY_RIGHT, new String[]{"right", "gunRight"});
        
        keyMap.put(GLFW_KEY_SPACE, new String[]{"ok", "fire"});
        keyMap.put(GLFW_KEY_ENTER, new String[]{"ok", "fire"});
        keyMap.put(GLFW_KEY_BACKSPACE, new String[]{"cancel"});
        
        keyMap.put(GLFW_KEY_R, new String[]{"reload"});
        
        keyMap.put(GLFW_KEY_LEFT_SHIFT, new String[]{"hasten"});
        keyMap.put(GLFW_KEY_LEFT_CONTROL, new String[]{"dawdle"});
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
    
    public void update() {
        glfwPollEvents();
    }
}
