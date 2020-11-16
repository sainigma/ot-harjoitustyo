/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import components.GameObject;
import java.nio.IntBuffer;
import objects.Sprite;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
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
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
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
    private TextureLoader texLoader;
    private long window;
    private int resoX,resoY;
    String windowname;
    public Renderer(){
        resoX = 1280;
        resoY = 720;
        windowname = "Tykkipeli";
        //init renderqueue
    }
    
    public void appendToRenderQueue(GameObject object){
        
    }
    
    public void removeFromRenderQueue(GameObject object){
        
    }
    
    public void run(){
        init();
        loop();
        kill();
    }
    
    private void init(){
        GLFWErrorCallback.createPrint(System.err).set();
        if(!glfwInit()){
            throw new IllegalStateException("oof");
        }
        window = glfwCreateWindow(resoX,resoY,windowname,NULL,NULL);
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
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glMatrixMode(GL_PROJECTION);
        glOrtho(0, resoX, resoY, 0, -1, 1);
        glClearColor(0,0,0,1.0f);
        texLoader = new TextureLoader();
    }
    
    private void loop(){
        while( !glfwWindowShouldClose(window) ){
            glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            glMatrixMode(GL11.GL_MODELVIEW);
            glLoadIdentity();
            //Spritet/pelilogiikka tähän
            glfwSwapBuffers(window);
            glfwPollEvents();
            //glClearColor(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 0.0f);
        }        
    }
    
    private void kill(){
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);   
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
