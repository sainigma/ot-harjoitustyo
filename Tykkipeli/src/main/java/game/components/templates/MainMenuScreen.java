/*
 * Copyright (C) 2020 Kari Suominen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package game.components.templates;

import game.components.GameObject;
import game.components.animation.PIDAnimator;
import game.utils.Vector3d;

/**
 *
 * @author Kari Suominen
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
