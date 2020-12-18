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
package game.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWKeyCallback;

/**
 * Rajapinta käyttäjän syötteiden lukemiseen. Lukee syötteitä ruudunpiirron yhteydessä GLFW:n määrittämällä tavalla, tallentaa syötteiden tilan ihmisystävälliseen hajautustauluun.
 * @author Kari Suominen
 */
public class InputManager {
    private long window;
    private HashMap<Integer, String[]> keyMap;
    private HashMap<String, Boolean> states;
    private HashMap<String, Boolean> debounced;
    
    private Set<String> keyList;
    /**
     * Rakentaja näppäimistönkuuntelijalle.
     * @param window GLFW osoitin aktiiviselle ikkunalle
     */
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
                    states.put(k, newState);
                }
            }
        });
    }
    private void setKeys() {
        keyMap.put(GLFW_KEY_UP, new String[]{"up", "elevate"});
        keyMap.put(GLFW_KEY_DOWN, new String[]{"down", "depress"});
        keyMap.put(GLFW_KEY_LEFT, new String[]{"left", "traverse left"});
        keyMap.put(GLFW_KEY_RIGHT, new String[]{"right", "traverse right"});
        
        keyMap.put(GLFW_KEY_SPACE, new String[]{"ok", "fire"});
        keyMap.put(GLFW_KEY_ENTER, new String[]{"ok"});
        keyMap.put(GLFW_KEY_BACKSPACE, new String[]{"previous"});
        keyMap.put(GLFW_KEY_ESCAPE, new String[]{"previous"});
        
        keyMap.put(GLFW_KEY_F10, new String[]{"quit"});
        
        keyMap.put(GLFW_KEY_R, new String[]{"reload"});
        
        keyMap.put(GLFW_KEY_H, new String[]{"help"});
        
        keyMap.put(GLFW_KEY_LEFT_SHIFT, new String[]{"modifier faster"});
        keyMap.put(GLFW_KEY_LEFT_CONTROL, new String[]{"modifier slower"});
        keyMap.put(GLFW_KEY_RIGHT_SHIFT, new String[]{"modifier faster"});
        keyMap.put(GLFW_KEY_RIGHT_CONTROL, new String[]{"modifier slower"});
        
        keyMap.put(GLFW_KEY_M, new String[]{"toggle map"});
        keyMap.put(GLFW_KEY_Q, new String[]{"rotate map left"});
        keyMap.put(GLFW_KEY_E, new String[]{"rotate map right"});
    }
    
    private void collectKeys() {
        for (int key : keyMap.keySet()) {
            String [] keys = keyMap.get(key);
            for (String trueKey : keys) {
                keyList.add(trueKey);
            }
        }
    }
    /**
     * Palauttaa toiminnan määräämän napin tilan. Jatkuviin toimintoihin, esim liikkumiseen.
     * @param action toiminta, avain hajautustaululle
     * @return 
     */
    public boolean keyDown(String action) {
        if (!states.containsKey(action)) {
            return false;
        }
        return states.get(action);
    }
    /**
     * Palauttaa toiminnan määräämän napin tilan, jos se on muuttunut. Kertatoimintoihin, esim menuissa navigointiin.
     * @param action toiminta, avain hajautustaululle.
     * @return 
     */
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
    /**
     * Päivitysmetodi, kutsuu rakentajassa määrätyn glfwSetKeyCallbackin invoke -metodin joka päivittää yksittäisen napin tilan hajautustauluun.
     */
    public void update() {
        glfwPollEvents();
    }
}
