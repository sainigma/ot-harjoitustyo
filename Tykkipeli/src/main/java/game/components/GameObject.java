/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components;

import java.util.ArrayList;
import game.utils.Sprite;
import game.utils.TextureLoader;
import game.utils.Vector3d;

/**
 *
 * @author suominka
 */
public abstract class GameObject {
    public ArrayList<GameObject> children = new ArrayList<>();
    private Sprite sprite;
    
    private boolean initialized = false;
    private boolean visible = true;
    private boolean active = true;
    public boolean hasUpdated = true;
    
    private String path;
    private String name;
    private int[] crop = { -1, -1};
    private float[] texOffset = {0, 0};
    private float[][] vertexOffset = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};
    private float scale = 1;
    private TextureLoader texLoader = null;
    
    private Vector3d origin = new Vector3d(0);
    public Vector3d localPosition = new Vector3d(0);
    public Vector3d localRotation = new Vector3d(0);
    public Vector3d globalPosition = new Vector3d(0);
    public Vector3d globalRotation = new Vector3d(0);
    
    public GameObject(String name) {
        this.name = name;
        this.path = null;
        this.initialized = true; //empty
    }
    public GameObject(String name, String path) {
        this.name = name;
        this.path = path;
        init();
    }
    public GameObject(String name, String path, Vector3d origin) {
        this.origin = origin;
        this.name = name;
        this.path = path;
        init();
    }
    public GameObject(String name, String path, Vector3d origin, float scale) {
        this.origin = origin;
        this.name = name;
        this.path = path;
        this.scale = scale;
        init();
    }
    private void init() {
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean newState) {
        active = newState;
    }
    public void setVisible(boolean newState) {
        visible = newState;
    }
    public boolean isVisible() {
        return visible;
    }
    public void toggleVisible() {
        visible = !visible;
    }
    public void toggle() {
        active = !active;
    }
    
    public void setVertexOffset(float[] topLeft, float[] bottomLeft, float[] topRight, float[] bottomRight) {
        vertexOffset[0] = topLeft;
        vertexOffset[1] = bottomLeft;
        vertexOffset[2] = topRight;
        vertexOffset[3] = bottomRight;
        if (sprite != null) {
            sprite.setVertexOffset(vertexOffset);            
        }
    }
    public void setTextureLoader(TextureLoader loader) {
        texLoader = loader;
        for (GameObject child : children) {
            child.setTextureLoader(loader);
        }
        load();
    }
    public void drive(String target, double value) {
    }
    public void setCrop(int x, int y) {
        crop[0] = x;
        crop[1] = y;
        if (sprite != null) {
            sprite.setCrop(crop);
        }
    }
    public void setHasUpdated(boolean newState) {
        hasUpdated = newState;
    }
    
    public void setPosition(Vector3d position) {
        localPosition.x = position.x;
        localPosition.y = position.y;
        localPosition.z = position.z;
        hasUpdated = true;
    }
    public void setRotation(float r) {
        localRotation.z = r;
        hasUpdated = true;
    }
    public void setRotation(Vector3d rotation) {
        localRotation.set(rotation);
    }
    public void setDepth(float z) {
        localPosition.z = z;
    }
    public void translate(float x, float y) {
        localPosition.x += x;
        localPosition.y += y;
        hasUpdated = true;
    }
    public void translate(float x, float y, float z) {
        translate(x, y);
        localPosition.z += z;
    }
    
    public void rotate(float rot) {
        localRotation.z += rot;
        hasUpdated = true;
    }
    
    public void setTexOffset(float x, float y) {
        texOffset[0] = x;
        texOffset[1] = y;
        if (sprite != null) {
            sprite.setTexOffset(texOffset);
        }
    }
    public void append(GameObject child) {
        children.add(child);
    }
    public void remove(GameObject child) {
        children.remove(child);
    }
    public void update() {
        //pelilogiikka
    }
    public void propagate() {
        if (!hasUpdated) {
            return;
        }
        for (GameObject child : children) {
            child.hasUpdated = true;
            child.globalPosition.set(globalPosition.add(localPosition));
            child.globalRotation.set(globalRotation.add(localRotation));
        }
        hasUpdated = false;
    }
    private void load() {
        System.out.println("Loading " + name);
        if (path != null) {
            sprite = new Sprite(texLoader, path, origin, scale);
            sprite.setTexOffset(texOffset);
            sprite.setVertexOffset(vertexOffset);
            sprite.setCrop(crop);
        }
        for (GameObject child : children) {
            child.load();
        }
        initialized = true;
    }
    private void _draw() {
        if (!visible) {
            for (GameObject child : children) {
                child.update();
            }
            return;
        }
        for (GameObject child : children) {
            child.draw();
        }
        if (sprite != null && visible) {
            sprite.setTransforms(localPosition, localRotation, globalPosition, globalRotation);
            sprite.draw();
        }
    }
    public void draw() {
        if (!initialized) {
            if (texLoader != null) {
                load();                
            } else {
                update();
                propagate();
            }
        } else if (active) {
            update();
            propagate();
            _draw();                
        }
    }
    @Override
    public GameObject clone() {
        GameObject ret;
        if (path != null) {
            ret = new GameObject(name, path, origin, scale) { };
        } else {
            ret = new GameObject(name) { };
        }
        ret.setPosition(getTransform());
        return ret;
    }
    
    public Vector3d getTransform() {
        return new Vector3d(localPosition.x, localPosition.y, localRotation.z);
    }
    public Vector3d getPosition() {
        return localPosition;
    }
    public Vector3d getRotation() {
        return localRotation;
    }
    public void toggleMinimized() {
    }
    
    public boolean isMinimized() {
        return false;
    }

    public void setMinimized(boolean minimized) {
    }
}