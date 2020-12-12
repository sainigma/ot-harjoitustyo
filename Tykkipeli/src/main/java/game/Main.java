package game;

import game.logic.BaseGame;
import game.graphics.Renderer;
import game.logic.LogicInterface;
import game.logic.MainMenu;
import game.utils.JSONLoader;
import org.json.JSONArray;
import org.json.JSONObject;

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
    
    private void setDisplaySettings(JSONObject display) {
        JSONArray resolution = display.getJSONArray("resolution");        
        renderer.setDisplay(resolution.getInt(0), resolution.getInt(1), display.getBoolean("windowed"), display.getBoolean("fullscreen"));        
    }
    
    private void loadSettings() {
        JSONLoader loader = new JSONLoader("config/");
        JSONObject generalConfig = loader.read("settings");
        JSONObject keyConfig = loader.read("keyconfig");
        
        setDisplaySettings(generalConfig.getJSONObject("display"));
    }
    
    public void init() {
        renderer = new Renderer();
        loadSettings();

        LogicInterface logic = new MainMenu();
        renderer.setLogic(logic);
        renderer.setBackground(249f / 255f, 240f / 255f, 223f / 255f);

        run();
    }
    
    public void run() {
        renderer.run();
    }
    
    public static void main(String args[]) {
        System.out.println("Init");
        new Main().init();
    }    
}
