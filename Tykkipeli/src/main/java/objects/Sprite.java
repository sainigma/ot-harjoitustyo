/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

import org.lwjgl.opengl.GL11;
import utils.Texture;
import utils.TextureLoader;
import utils.Vector3d;

/**
 *
 * @author suominka
 */
public class Sprite {
    Texture texture;
    int width,height;
    Vector3d origin;
    
    private void _load(TextureLoader loader, String path, Vector3d origin){
        texture = loader.loadTexture("./assets/"+path);
        width = texture.getImageWidth();
        height = texture.getImageHeight();
        this.origin = origin;
    }
    
    public Sprite(TextureLoader loader, String path, Vector3d origin){
        _load(loader,path,origin);
    }

    public Sprite(TextureLoader loader, String path){
        _load(loader,path,new Vector3d(0,0,0));
    }
    
    private void _draw(int x, int y, float r){
        float xOffset = (float)origin.x;
        float yOffset = (float)origin.y;
        float[][] vertices = {{0,0},{0,height},{width,height},{width,0}};

        GL11.glPushMatrix();
        texture.bind();
        GL11.glTranslatef(x, y, 0);
        GL11.glRotatef(r, 0, 0, 1f);
        GL11.glColor3f(1, 1, 1);
        GL11.glBegin(GL11.GL_QUADS);
        
        for(float[] vertex : vertices){
            float texW = vertex[0] > 0 ? texture.getWidth() : 0;
            float texH = vertex[1] > 0 ? texture.getHeight() : 0;
            GL11.glTexCoord2f(texW-xOffset, texH-yOffset);
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
