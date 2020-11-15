/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

import java.awt.Image;
import org.lwjgl.opengl.GL11;
import utils.Vector3d;

/**
 *
 * @author suominka
 */
public class Sprite {
    private Image image;
    int width,height;
    Vector3d origin;
    
    public Sprite(){
        origin = new Vector3d(0,0,0);
    }
    
    public Sprite(Image image){
        this.image = image;
        origin = new Vector3d(0,0,0);
    }
    public Sprite(Image image, Vector3d origin){
        this.image = image;
        this.origin = origin;
        width = 100;
        height = 100;
    }
    private void _draw(int x, int y, float r){
        float xOffset = (float)origin.x;
        float yOffset = (float)origin.y;
        float[][] vertices = {{0,0},{0,height},{width,height},{width,0}}; 
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        GL11.glRotatef(r, 0, 0, 1f);
        GL11.glColor3f(1, 1, 1);
        GL11.glBegin(GL11.GL_QUADS);
        for(float[] vertex : vertices){
            GL11.glTexCoord2f(vertex[0]-xOffset, vertex[1]-yOffset);
            GL11.glVertex2f(vertex[0]-xOffset, vertex[1]-yOffset);
        }
        GL11.glEnd();
        GL11.glPopMatrix();
    }
    public void draw(int x, int y){
        _draw(x,y,0);
    }
    public void draw(int x, int y, float r){
        _draw(x,y,r);
    }
    
}
