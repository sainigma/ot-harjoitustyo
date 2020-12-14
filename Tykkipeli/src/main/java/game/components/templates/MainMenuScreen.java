/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components.templates;

import game.components.GameObject;
import game.utils.PID;
import game.utils.Vector3d;

/**
 *
 * @author suominka
 */
public class MainMenuScreen extends GameObject {
    private GameObject backgroundStatic;
    private GameObject backgroundFar;
    private GameObject backgroundNear;
    public GameObject title;
    public GameObject menuEmpty;
    
    float animatedPosition = 0f;
    float animatedTarget = 0f;
    boolean animating = false;
    PID animator = new PID(0.015f, 0f, 0.4f, 100f);
    
    @Override
    public boolean isInitialized() {
        return (backgroundStatic.isInitialized() && backgroundFar.isInitialized() && backgroundNear.isInitialized() && title.isInitialized());
    }
    
    public MainMenuScreen(String name) {
        super(name);
        init();
    }
    
    public void init() {
        backgroundStatic = new GameObject("taustatausta", "menu/taustatausta.png", new Vector3d()) { };
        backgroundFar = new GameObject("takatausta", "menu/takatausta.png", new Vector3d()) { };
        backgroundNear = new GameObject("etutausta", "menu/etutausta.png", new Vector3d()) { };
        title = new GameObject("otsikko", "menu/otsikko.png", new Vector3d()) { };
        menuEmpty = new GameObject("menuempty") { };
        /*
        EndScreen poista = new EndScreen("");
        append(poista);
        */

        backgroundStatic.setDepth(0);
        backgroundFar.setDepth(1);
        backgroundNear.setDepth(2);
        title.setDepth(3);
        
        append(backgroundStatic);
        append(backgroundFar);
        append(backgroundNear);
        append(title);
        append(menuEmpty);
        
        //enter();
    }
    
    public void enter() {
        setVisible(true);
        animating = true;
        animatedPosition = 0f;
        animatePosition(animatedPosition);
        animatedTarget = 1f;
        animator.activate();
    }
    
    public void exit() {
        animating = true;
        animatedPosition = 1f;
        animatePosition(animatedPosition);
        animatedTarget = 0f;
        animator.activate();
    }
    
    public float getAnimatedPosition() {
        return animatedPosition;
    }
    
    private void animatePosition(float t) {
        title.setPosition(new Vector3d().lerp(
                new Vector3d(2048, 0, 3),
                new Vector3d(0, 0, 3), t)
        );
        menuEmpty.setPosition(new Vector3d().lerp(
                new Vector3d(3048, 0, 3),
                new Vector3d(0, 0, 3), t)
        );
        backgroundFar.setPosition(new Vector3d().lerp(
                new Vector3d(-100, 0, 1),
                new Vector3d(0, 0, 1), t)
        );
        backgroundNear.setPosition(new Vector3d().lerp(
                new Vector3d(-300, 0, 2),
                new Vector3d(0, 0, 2), t)
        );
    }
    
    private void animate(double deltatimeMillis) {
        if (!animating) {
            return;
        }
        float error = animatedTarget - animatedPosition;
        float control = (float) animator.getControl(error, deltatimeMillis);
        animatedPosition += control;
        if (Math.abs(error) < 0.00001f) {
            animating = false;
            animatedPosition = animatedTarget;
        }
        animatePosition(animatedPosition);
    }
    
    public void update() {
        animate(getDeltatime());
    }
}
