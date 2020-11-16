/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import java.util.ArrayList;
import objects.Sprite;
import utils.TextureLoader;

/**
 *
 * @author suominka
 */
public abstract class GameObject {
    private ArrayList<GameObject> children;
    private Sprite sprite;
    private String path;
    private String name;
    private int x,y,rotation;
    private boolean initialized = false;
    private boolean active = true;
    private TextureLoader texLoader;
    
    public GameObject(String name){
        this.name = name;
        this.initialized = true; //empty
    }
    public GameObject(String name, String path){
        this.name = name;
        this.path = path;
    }
    public void setTextureLoader(TextureLoader loader){
        texLoader = loader;
    }
    public void update(){
        //pelilogiikka
    }
    private void load(){
        sprite = new Sprite(texLoader, path);
    }
    private void _draw(){
        //piirrä ensin lapset rekursiivisesti
        //sitten piirrä itse peliobjekti
        if( sprite != null ){
           sprite.draw(x, y, rotation);            
        }
    }
    public void draw(){
        if( !initialized ){
            load();
        }else if( active ){
            update();
            _draw();
        }
    }
}
