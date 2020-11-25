package game;


import game.components.Level;
import game.logic.BaseGame;
import game.utils.Renderer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author suominka
 */

public class Main {
    Renderer renderer;
    public void init() {
        Level level = new Level("testlevel");
        BaseGame logic = new BaseGame(level);
        renderer = new Renderer(logic);
        renderer.appendToRenderQueue(level);
        renderer.setBackground(0f / 255f, 240f / 255f, 223f / 255f);
        run();
    }
    public void update() {
        System.out.println("update");
    }
    public void run() {
        renderer.run();
    }
    public static void main(String args[]) {
        System.out.println("Init");
        new Main().init();
    }    
}
