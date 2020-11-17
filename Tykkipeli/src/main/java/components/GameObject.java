/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import java.util.ArrayList;
import objects.Sprite;
import utils.TextureLoader;
import utils.Vector3d;

/**
 *
 * @author suominka
 */
public abstract class GameObject {
    private ArrayList<GameObject> children = new ArrayList<>();
    private Sprite sprite;
    private String path;
    private String name;
    int x,y,rotation;
    int parentX,parentY,parentRotation;
    private boolean initialized = false;
    private boolean active = true;
    boolean hasUpdated = true;
    private TextureLoader texLoader;
    private Vector3d origin = new Vector3d(0);
    
    public GameObject(String name){
        this.name = name;
        this.path = null;
        this.initialized = true; //empty
    }
    public GameObject(String name, String path){
        this.name = name;
        this.path = path;
        init();
    }
    public GameObject(String name, String path, Vector3d origin){
        this.origin = origin;
        this.name = name;
        this.path = path;
        init();
    }
    public boolean isActive(){
        return active;
    }
    public void setActive(boolean newState){
        active = newState;
    }
    public void toggle(){
        active=!active;
    }
    private void init(){
        x=0;
        y=0;
        rotation=0;
        parentX=0;
        parentY=0;
        parentRotation=0;
    }
    public void setTextureLoader(TextureLoader loader){
        texLoader = loader;
        for(GameObject child : children){
            child.setTextureLoader(loader);
        }
        load();
    }
    public void translate(int x, int y){
        this.x+=x;
        this.y+=y;
        hasUpdated = true;
    }
    public void rotate(int rot){
        rotation+=rot;
        hasUpdated = true;
    }
    public void append(GameObject child){
        children.add(child);
    }
    public void remove(GameObject child){
        children.remove(child);
    }
    public void update(){
        //pelilogiikka
    }
    public void propagate(){
        if( !hasUpdated ){
            return;
        }
        for(GameObject child : children){
            child.parentX = x;
            child.parentY = y;
            child.parentRotation = rotation;
        }
        hasUpdated = false;
    }
    private void load(){
        System.out.println("Loading "+name);
        if( path != null ){
            sprite = new Sprite(texLoader, path, origin);
        }
        for(GameObject child : children){
            child.load();
        }
        initialized = true;
    }
    private void _draw(){
        for(GameObject child : children){
            child.draw();
        }
        if( sprite != null ){
           sprite.draw(x+parentX, y+parentY, rotation+parentRotation);            
        }
    }
    public void draw(){
        if( !initialized ){
            load();
        }else if( active ){
            update();
            propagate();
            _draw();                
        }
    }
}
