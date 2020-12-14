/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components;

import game.graphics.TextureLoader;
import game.utils.Vector3d;

/**
 *
 * @author suominka
 */
public interface DrawCallInterface {
    public void update();
    public void load();
    public void draw();
    public void setPosition(Vector3d position);
    public void setRotation(Vector3d rotation);
    public void setGlobalPosition(Vector3d position);
    public void setGlobalRotation(Vector3d rotation);
    public void setUpdated(boolean state);
    public void setVisible(boolean state);
    public void setMinimized(boolean minimized);
    public void translate(float x, float y);
    public void translate(float x, float y, float z);
    public void setTextureLoader(TextureLoader loader);
}
