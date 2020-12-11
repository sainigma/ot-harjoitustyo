/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.graphics;

import game.components.GameObject;
import game.graphics.primitives.Sprite;
import game.logic.LogicInterface;
import game.utils.InputManager;
import game.utils.Vector3d;
import java.nio.IntBuffer;
import java.util.ArrayList;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_DECORATED;
import static org.lwjgl.glfw.GLFW.GLFW_FOCUS_ON_SHOW;
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
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 *
 * @author suominka
 */
public class Renderer {
    ArrayList<GameObject> objects;
    LogicInterface logic = null;
    InputManager inputs;
    float[] clearColor = new float[]{0,0,0,1}; 
    private TextureLoader texLoader;
    private long window;
    private int resoX,resoY;
    private boolean alive = true;
    private boolean loading = true;
    private boolean windowed = true;
    private boolean fullscreen = true;
    String windowname;
    Sprite loadingScreen;
    
    public Renderer(){
        resoX = 1280;
        resoY = 720;
        windowname = "Tykkipeli";
        objects = new ArrayList<>();
    }
    
    public Renderer(int resolutionX, int resolutionY, String title){
        resoX = resolutionX;
        resoY = resolutionY;
        windowname = title;
        objects = new ArrayList<>();
    }
    
    public void setLogic(LogicInterface logic){
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
    
    public void appendToRenderQueue(GameObject object){
        objects.add(object);
    }
    
    public void removeFromRenderQueue(GameObject object){
        objects.remove(object);
    }
    
    public void setBackground(float r, float g, float b){
        clearColor = new float[]{r,g,b,1};
    }
    
    public void run(){
        init();
        initObjects();
        loop();
        kill();
    }
    
    private void initObjects(){
        for(GameObject object : objects){
            object.setTextureLoader(texLoader);
        }
    }
    
    private void updateObjects(){        
        if (logic != null) {
            logic.update();
        }
        if (!loading) {
            for(GameObject object : objects){
                object.draw();
            }
            if (inputs != null) {
                inputs.update();
            }            
        } else {
            loadingScreen.draw();
        }
    }
    
    private void init(){
        GLFWErrorCallback.createPrint(System.err).set();
        if(!glfwInit()){
            throw new IllegalStateException("oof");
        }
        
        
        if (windowed && fullscreen) {
            glfwWindowHint(GLFW_DECORATED, GL_FALSE);
            GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            resoX = mode.width();
            resoY = mode.height();
            window = glfwCreateWindow(resoX,resoY,windowname,NULL,NULL);
        } else if (fullscreen) {
            window = glfwCreateWindow(resoX,resoY,windowname,glfwGetPrimaryMonitor(),NULL);
        } else {
            window = glfwCreateWindow(resoX,resoY,windowname,NULL,NULL);
        }
        
        if(window == NULL){
            throw new RuntimeException("tuplaoof");
        }
        try( MemoryStack stack = stackPush() ){
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
        glClearColor(clearColor[0],clearColor[1],clearColor[2],clearColor[3]);
        
        texLoader = new TextureLoader();
        inputs = new InputManager(window);
        logic.setInputManager(inputs);
        
        loadingScreen = new Sprite(texLoader, "loading/placeholder.png");
    }
    
    public void setLoading(boolean state) {
        loading = state;
    }
    
    private void loop(){
        glMatrixMode(GL11.GL_MODELVIEW);
        glLoadIdentity();
        while( !glfwWindowShouldClose(window) && alive ){
            draw();
        }
    }
    
    private void draw() {
        while( !glfwWindowShouldClose(window) && alive ){
            glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);    
            
            updateObjects();                
            
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }
    
    private void kill(){
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);   
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
    public void close() {
        alive = false;
    }
}
