/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.utils;

import org.lwjgl.opengl.GL11;

/**
 *
 * @author suominka
 */
public class Sprite {
    Texture texture;
    int width=-1,height=-1;
    float scale = 1;
    float[] texOffset = {0,0};
    float[][] vertexOffset = {{0,0},{0,0},{0,0},{0,0}};
    Vector3d origin;
    private float[][] vertices;
    private float[][] uvmap;
    
    private Vector3d localPosition = new Vector3d(0);
    private Vector3d localRotation = new Vector3d(0);
    private Vector3d globalPosition = new Vector3d(0);
    private Vector3d globalRotation = new Vector3d(0);
    
    
    private void _load(TextureLoader loader, String path, Vector3d origin){
        texture = loader.loadTexture("./assets/textures/"+path);
        if( width == -1 ){
            width = texture.getImageWidth();
            height = texture.getImageHeight();
        }
        float [][] v = {{0,0},{0,height},{width,height},{width,0}};
        float [][] u = {{0,0},{0,1},{1,1},{1,0}};
        vertices = v;
        uvmap = u;
        this.origin = origin;
    }
    
    public void setCrop(int[] arr){
        if( arr[0] > 0 && arr[1] > 0 ){
            width = arr[0];
            height = arr[1];            
        }
    }
    
    public Sprite(TextureLoader loader, String path, Vector3d origin, float scale){
        this.scale = scale;
        _load(loader,path,origin);
    }

    public Sprite(TextureLoader loader, String path){
        _load(loader,path,new Vector3d(0,0,0));
    }
    
    private void translate(Vector3d position) {
        float inverseScale = (float)Math.pow(scale, -1);
        GL11.glTranslated(position.x*inverseScale, position.y*inverseScale, position.z*inverseScale);
    }
    
    private void rotate(Vector3d rotation) {
        GL11.glRotated(rotation.x, 1f, 0, 0);
        GL11.glRotated(rotation.y, 0, 1f, 0);
        GL11.glRotated(rotation.z, 0, 0, 1f);        
    }
    
    private void _draw(){
        int i = 0;
        float xOffset = (float)origin.x;
        float yOffset = (float)origin.y;
        
        GL11.glPushMatrix();
        texture.bind();
        GL11.glScalef(scale, scale, scale);
        
        translate(globalPosition);
        rotate(globalRotation);
        translate(localPosition);
        rotate(localRotation);
        
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
    public void draw(){
        _draw();
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
    public void setTransforms(Vector3d localPosition, Vector3d localRotation, Vector3d globalPosition, Vector3d globalRotation){
        this.localPosition.set(localPosition);
        this.localRotation.set(localRotation);
        this.globalPosition.set(globalPosition);
        this.globalRotation.set(globalRotation);
    }
}
