/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import java.util.ArrayList;
import utils.Sprite;
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
    float x,y,rotation;
    float[] texOffset = {0,0};
    float[][] vertexOffset = {{0,0},{0,0},{0,0},{0,0}};
    float scale = 1;
    public float parentX,parentY,parentRotation;
    private boolean visible = true;
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
    public GameObject(String name, String path, Vector3d origin, float scale){
        this.origin = origin;
        this.name = name;
        this.path = path;
        this.scale = scale;
        init();
    }
    public boolean isActive(){
        return active;
    }
    public void setActive(boolean newState){
        active = newState;
    }
    public void setVisible(boolean newState){
        visible = newState;
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
    public void setVertexOffset(float[] topLeft, float[] bottomLeft, float[] topRight, float[] bottomRight){
        vertexOffset[0] = topLeft;
        vertexOffset[1] = bottomLeft;
        vertexOffset[2] = topRight;
        vertexOffset[3] = bottomRight;
        if(sprite != null){
            sprite.setVertexOffset(vertexOffset);            
        }
    }
    public void setTextureLoader(TextureLoader loader){
        texLoader = loader;
        for(GameObject child : children){
            child.setTextureLoader(loader);
        }
        load();
    }
    public void setPosition(Vector3d position){
        this.x = (float)position.x;
        this.y = (float)position.y;
        hasUpdated = true;
    }
    public Vector3d getPosition(){
        return new Vector3d(x,y);
    }
    public void translate(float x, float y){
        this.x+=x;
        this.y+=y;
        hasUpdated = true;
    }
    public void translateLocal(int x, int y){
        //ota pyöriminen huomioon
    }
    public void rotate(float rot){
        rotation+=rot;
        hasUpdated = true;
    }
    public void setTexOffset(float x, float y){
        texOffset[0] = x;
        texOffset[1] = y;
        if( sprite != null ){
            sprite.setTexOffset(texOffset);
        }
    }
    public void append(GameObject child){
        children.add(child);
    }
    public void remove(GameObject child){
        children.remove(child);
    }
    public void setZindex(){
        //toteuta
    }
    public void update(){
        //pelilogiikka
    }
    public void propagate(){
        if( !hasUpdated ){
            return;
        }
        for(GameObject child : children){
            child.hasUpdated = true;
            child.parentX = x+parentX;
            child.parentY = y+parentY;
            child.parentRotation = rotation;
        }
        hasUpdated = false;
    }
    private void load(){
        System.out.println("Loading "+name);
        if( path != null ){
            sprite = new Sprite(texLoader, path, origin, scale);
            sprite.setTexOffset(texOffset);
            sprite.setVertexOffset(vertexOffset);
        }
        for(GameObject child : children){
            child.load();
        }
        initialized = true;
    }
    private void _draw(){
        if(!visible){
            return;
        }
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
    @Override
    public GameObject clone(){
        GameObject ret;
        if( path != null ){
            ret = new GameObject(name, path, origin, scale){};
        }else{
            ret = new GameObject(name){};
        }
        ret.setPosition(getPosition());
        return ret;
    }
}
