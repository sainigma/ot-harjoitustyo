/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components;

import game.components.templates.BackgroundCoast;
import game.components.templates.MapScreen;
import game.components.templates.ViewPort;
import game.components.templates.Mortar;
import game.utils.Vector3d;

/**
 *
 * @author suominka
 */
public class Level extends GameObject {
    long start = System.currentTimeMillis();
    
    public ViewPort gameView;
    public ViewPort mapView;
    public Mortar mortar;
    public MapScreen mapScreen;
    public GameObject background;
    
    private boolean isFinished = false;
    private float viewportScale = 720f / 1080f;    
    public Level(String name) {
        super(name);
        init();
    }
    
    public boolean isFinished() {
        return isFinished;
    }
    
    private void init() {
        gameView = new ViewPort("game");        
        mapView = new ViewPort("map");
        
        mortar = new Mortar("mortar", viewportScale);
        background = new BackgroundCoast("coast", 1);
        gameView.append(mortar);
        gameView.append(background);
        
        mapScreen = new MapScreen("mapScreen", viewportScale);
        mapView.append(mapScreen);
        
        append(gameView);
        append(mapView);
        
        //gameView.setVisible(false);
        mapView.setVisible(false);
        mortar.setTrueElevation(0f);
    }
    
    @Override
    public void update() {
        background.setRotation(mortar.getTraversal());
        //System.out.println(mortar.getLocalTraversal());
    }
}
