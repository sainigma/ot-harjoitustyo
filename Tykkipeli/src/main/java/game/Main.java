package game;

import game.logic.BaseGame;
import game.graphics.Renderer;
import game.logic.LogicInterface;
import game.logic.MainMenu;
import game.utils.JSONLoader;
import org.json.JSONArray;
import org.json.JSONObject;

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

/**
 *
 * @author Kari Suominen
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
        renderer.setBackground(0, 0, 0);

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
