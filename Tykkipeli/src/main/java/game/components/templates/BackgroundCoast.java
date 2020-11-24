/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components.templates;

import game.components.GameObject;
import game.utils.Vector3d;

/**
 *
 * @author suominka
 */
public class BackgroundCoast extends GameObject {
    private float viewportScale;
    private float rotation = 0;
    GameObject background;
    public BackgroundCoast(String name, float viewportScale) {
        super(name);
        this.viewportScale = viewportScale;
        init();
    }
    private void init() {
        background = new GameObject("bg_foreground", "background/taustaluonnos.png", new Vector3d(0, 0, 1)) { };
        append(background);
        setRotation(0);
    }
    @Override
    public void setRotation(float r) {
        float factor = (r % 360) / 360;
        background.setTexOffset(factor + 0.465f, 0);
    }
    @Override
    public void update() {
    }
}
