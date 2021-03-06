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
package game.graphics;

import game.components.GameObject;
import game.graphics.primitives.Sprite;
import game.logic.LogicInterface;
import game.utils.InputManager;
import java.nio.IntBuffer;
import java.util.ArrayList;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_DECORATED;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glAlphaFunc;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Pääluokka piirtämiseen ja pelisilmukan päivitykseen.
 * @author Kari Suominen
 */
public class Renderer {
    private ArrayList<GameObject> objects;
    private LogicInterface logic = null;
    private InputManager inputs;
    private float[] clearColor = new float[]{0, 0, 0, 1}; 
    private TextureLoader texLoader;
    private long window;
    private int resoX, resoY;
    private boolean alive = true;
    private boolean loading = true;
    private boolean windowed = true;
    private boolean fullscreen = false;
    private boolean initialized = false;
    private String windowname;
    private Sprite loadingScreen;
    
    /**
     * Rakentaja.
     */
    public Renderer() {
        resoX = 1280;
        resoY = 720;
        windowname = "Tykkipeli";
        objects = new ArrayList<>();
    }
    
    /**
     * Asettaa ikkunan resoluution, ikkunallisuuden sekä kokoruudullisuuden, kahden jälkimmäisen ollessa päällä sovellus on borderless windowed tilassa.
     * @param width Vaakasuuntainen resoluutio
     * @param height Pystysuuntainen resoluutio
     * @param windowed ikkunallisuus
     * @param fullscreen kokoruudullisuus
     */
    public void setDisplay(int width, int height, boolean windowed, boolean fullscreen) {
        resoX = width;
        resoY = height;
        this.windowed = windowed;
        this.fullscreen = fullscreen;
    }
    
    /**
     * Sitoo LogicInterfacen toteuttavan luokan päivityssilmukkaan.
     * @param logic 
     */
    public void setLogic(LogicInterface logic) {
        if (logic == null) {
            return;
        }
        this.logic = logic;
        logic.setInputManager(inputs);
        logic.setRenderer(this);
        if (texLoader != null) {
            initObjects();
        }
    }
    
    /**
     * Lisää GameObjectin ja sen lapset piirtojonoon.
     * @param object 
     */
    public void appendToRenderQueue(GameObject object) {
        objects.add(object);
    }
    
    /**
     * Poistaa GameObjectin ja sen lapset piirtojonosta.
     * @param object 
     */
    public void removeFromRenderQueue(GameObject object) {
        objects.remove(object);
    }

    /**
     * Käynnistää ja hoitaa luokan elämänsyklin, kutsutaan kun luokan alustus on valmis.
     */
    public void run() {
        init();
        initObjects();
        loop();
        kill();
    }
    
    /**
     * Asettaa näytönpuhdistusvärin, väriulottuvuuksien arvot välillä 0-1f.
     * @param r punaisuus
     * @param g vihreys
     * @param b sinisyys
     */
    public void setBackground(float r, float g, float b) {
        clearColor = new float[]{r, g, b, 1};
        if (initialized) {
            glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
        }
    }
    
    private void initObjects() {
        for (GameObject object : objects) {
            object.setTextureLoader(texLoader);
        }
    }
    
    private void updateObjects() {        
        if (logic != null) {
            logic.update();
        }
        if (!loading) {
            for (GameObject object : objects) {
                object.draw();
            }
            if (inputs != null) {
                inputs.update();
            }            
        } else {
            loadingScreen.draw();
        }
    }
    
    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("oof");
        }
        
        
        if (windowed && fullscreen) {
            glfwWindowHint(GLFW_DECORATED, GL_FALSE);
            GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            resoX = mode.width();
            resoY = mode.height();
            window = glfwCreateWindow(resoX, resoY, windowname, NULL, NULL);
        } else if (fullscreen) {
            window = glfwCreateWindow(resoX, resoY, windowname, glfwGetPrimaryMonitor(), NULL);
        } else {
            window = glfwCreateWindow(resoX, resoY, windowname, NULL, NULL);
        }
        
        if (window == NULL) {
            throw new RuntimeException("tuplaoof");
        }
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
        }
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
        
        GL.createCapabilities();
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_DEPTH_TEST);
        glAlphaFunc(GL_GREATER, 0.5f);
        glMatrixMode(GL_PROJECTION);
        glOrtho(0, 1280, 720, 0, -2000, 2000);
        glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
        
        texLoader = new TextureLoader();
        inputs = new InputManager(window);
        logic.setInputManager(inputs);
        
        loadingScreen = new Sprite(texLoader, "loading/placeholder.png");
        initialized = true;
    }
    
    /**
     * Kytkee latausruudun päälle ja pois.
     * @param state 
     */
    public void setLoading(boolean state) {
        loading = state;
    }
    
    private void loop() {
        glMatrixMode(GL11.GL_MODELVIEW);
        glLoadIdentity();
        while (!glfwWindowShouldClose(window) && alive) {
            draw();
        }
    }
    
    private void draw() {
        while (!glfwWindowShouldClose(window) && alive) {
            glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);    
            
            updateObjects();                
            
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }
    
    private void kill() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);   
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
    
    /**
     * Aloittaa piirtosilmukan sammutuksen.
     */
    public void close() {
        alive = false;
    }
}
