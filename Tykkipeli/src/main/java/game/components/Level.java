/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components;

import game.components.templates.BackgroundCoast;
import game.components.templates.ViewPort;
import game.components.templates.Mortar;

/**
 *
 * @author suominka
 */
public class Level extends GameObject {
    long start = System.currentTimeMillis();
    
    public ViewPort gameView;
    public ViewPort mapView;
    public Mortar mortar;
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
        
        mortar = new Mortar("mortar", viewportScale);
        background = new BackgroundCoast("coast", 1);
        gameView.append(mortar);
        gameView.append(background);
        append(gameView);
        
        //gameView.setScreenShake(2);
        mortar.setTrueElevation(0f);
        //mortar.setElevationTarget(55f);
        //mortar.setTraverseTarget(120f);
    }
    
    @Override
    public void update() {
        background.setRotation(mortar.getTraversal());
        System.out.println(mortar.getLocalTraversal());
    }
}
