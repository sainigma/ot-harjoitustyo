/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.graphics;

import game.utils.Vector3d;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author suominka
 */
public abstract class ImmediateDrawer {
    private float scale = 1f;
    public Vector3d localPosition = new Vector3d(0);
    private Vector3d localRotation = new Vector3d(0);
    private Vector3d globalPosition = new Vector3d(0);
    private Vector3d globalRotation = new Vector3d(0);
    
    public void setScale(float scale) {
        this.scale = scale;
    }
    
    public float getScale() {
        return scale;
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
    
    private void transform() {
        translate(globalPosition);
        rotate(globalRotation);
        translate(localPosition);
        rotate(localRotation);
    }
    
    public void _draw() {
    }
    
    public void draw() {
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);
        transform();
        _draw();
        GL11.glPopMatrix();
    }
    
    public void setPosition(Vector3d position) {
        localPosition.set(position);
    }
    
    public void setGlobalRotation(Vector3d rotation) {
        this.globalRotation.set(rotation);
    }
    
    public void setGlobalPosition(Vector3d position) {
        this.globalPosition.set(position);
    }
    
    public void setTransforms(Vector3d localPosition, Vector3d localRotation, Vector3d globalPosition, Vector3d globalRotation){
        this.localPosition.set(localPosition);
        this.localRotation.set(localRotation);
        this.globalPosition.set(globalPosition);
        this.globalRotation.set(globalRotation);
    }
    
    public void setGlobalTransforms(Vector3d globalPosition, Vector3d globalRotation) {
        this.globalPosition.set(globalPosition);
        this.globalRotation.set(globalRotation);
    }
}
