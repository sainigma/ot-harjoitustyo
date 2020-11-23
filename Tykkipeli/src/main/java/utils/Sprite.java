/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

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
    float scale = 1;
    float[] texOffset = {0,0};
    float[][] vertexOffset = {{0,0},{0,0},{0,0},{0,0}};
    Vector3d origin;
    private float[][] vertices;
    private float[][] uvmap;
    
    private void _load(TextureLoader loader, String path, Vector3d origin){
        texture = loader.loadTexture("./assets/"+path);
        width = texture.getImageWidth();
        height = texture.getImageHeight();
        
        //float texW = texture.getWidth();
        //float texH = texture.getHeight();
        //float [][] u = {{0,0},{0,texH},{texW,texH},{texW,0}};
        float [][] v = {{0,0},{0,height},{width,height},{width,0}};
        float [][] u = {{0,0},{0,1},{1,1},{1,0}};
        vertices = v;
        uvmap = u;
        this.origin = origin;
    }
    
    public Sprite(TextureLoader loader, String path, Vector3d origin, float scale){
        this.scale = scale;
        _load(loader,path,origin);
    }

    public Sprite(TextureLoader loader, String path){
        _load(loader,path,new Vector3d(0,0,0));
    }
    
    private void _draw(int x, int y, float r){
        int i = 0;
        float xOffset = (float)origin.x;
        float yOffset = (float)origin.y;
        float inverseScale = (float)Math.pow(scale, -1);
        
        
        GL11.glPushMatrix();
        texture.bind();
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(x*inverseScale, y*inverseScale, (float)origin.z);
        GL11.glRotatef(r, 0, 0, 1f);
        GL11.glColor3f(1, 1, 1);
        
        GL11.glBegin(GL11.GL_QUADS);
        for(float[] vertex : vertices){
            float point[] = uvmap[i];
            float vOffset[] = vertexOffset[i];
            GL11.glTexCoord2f(point[0]-texOffset[0], point[1]-texOffset[1]);
            GL11.glVertex2f(vertex[0]-xOffset+vOffset[0], vertex[1]-yOffset+vOffset[1]);
            i+=1;
        }
                
        GL11.glEnd();
        GL11.glPopMatrix();
    }
    
    public void draw(float x, float y){
        _draw((int)x,(int)y,0);
    }
    public void draw(float x, float y, float r){
        _draw((int)x,(int)y,r);
    }
    public void setScale(float scale){
        this.scale = scale;
    }
    public void setTexOffset(float[] v){
        texOffset[0] = v[0];
        texOffset[1] = v[1];
    }
    public void setVertexOffset(float[][] offset){
        vertexOffset = offset;
    } 
}
