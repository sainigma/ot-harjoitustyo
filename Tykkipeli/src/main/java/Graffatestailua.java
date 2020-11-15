/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author suominka
 */
import java.util.Random;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import objects.Sprite;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import utils.Vector3d;

public class Graffatestailua {
    private long window;
    
    public void run(){
        init();
        loop();
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
    
    private void init(){
        GLFWErrorCallback.createPrint(System.err).set();
        if(!glfwInit()){
            throw new IllegalStateException("oof");
        }
        window = glfwCreateWindow(1280,720,"Graffatestailua",NULL,NULL);
        if(window == NULL){
            throw new RuntimeException("tuplaoof");
        }
        try( MemoryStack stack = stackPush() ){
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
        }
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); //vsync
        glfwShowWindow(window);
    }
    
    private void loop(){
        int x = 0;
        int y = 0;
        float r = 0;
        Random rand = new Random();
        Sprite testi = new Sprite(null, new Vector3d(50,50,0));
        GL.createCapabilities();
        glEnable(GL11.GL_TEXTURE_2D);
        glDisable(GL11.GL_DEPTH_TEST);
        glMatrixMode(GL11.GL_PROJECTION);
        glOrtho(0, 1280, 720, 0, -1, 1);
        glClearColor(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 0.0f);
        while( !glfwWindowShouldClose(window) ){
            x+=25;
            r+=15;
            if( x > 1280-100 ){
                x = 0;
                y += 100;
                if( y > 620 ){
                    y = 0;
                }
            }
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            testi.draw(x,y,r);
            //draw(x,y,r);
            glfwSwapBuffers(window);
            glfwPollEvents();
            //glClearColor(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 0.0f);
        }
    }
    
    public static void main(String[] args){
        new Graffatestailua().run();
    }
}
