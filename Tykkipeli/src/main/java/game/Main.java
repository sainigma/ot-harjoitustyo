package game;

import game.components.Level;
import game.logic.BaseGame;
import game.graphics.Renderer;
import game.logic.LogicInterface;
import game.logic.MainMenu;

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
        renderer = new Renderer();
        //renderer.setConfig(conf);
        if (false) {
            LogicInterface logic = new BaseGame();
            renderer.setLogic(logic);
            renderer.setBackground(249f / 255f, 240f / 255f, 223f / 255f);            
        } else {
            LogicInterface logic = new MainMenu();
            renderer.setLogic(logic);
            renderer.setBackground(249f / 255f, 240f / 255f, 223f / 255f);
        }

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
