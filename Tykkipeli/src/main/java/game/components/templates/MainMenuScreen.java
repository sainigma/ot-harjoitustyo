/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components.templates;

import game.components.GameObject;
import game.components.animation.PIDAnimator;
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
    
    PIDAnimator animator = new PIDAnimator(0.015f, 0f, 0.4f, 100f);
    
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

        backgroundStatic.setDepth(0);
        backgroundFar.setDepth(1);
        backgroundNear.setDepth(2);
        title.setDepth(3);
        
        append(backgroundStatic);
        append(backgroundFar);
        append(backgroundNear);
        append(title);
        append(menuEmpty);
    }
    
    public void enter() {
        setVisible(true);
        animator.enter();
        animatePosition(0);
    }
    
    public void exit() {
        animator.exit();
        animatePosition(1);
    }
    
    public float getAnimatedPosition() {
        return animator.getAnimatedPosition();
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
        if (!animator.animating()) {
            return;
        }
        animatePosition(animator.animate(deltatimeMillis));
    }
    
    public void update() {
        animate(getDeltatime());
    }
}
