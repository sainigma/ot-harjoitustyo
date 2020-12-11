/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic;

import game.components.templates.MainMenuScreen;
import game.graphics.Renderer;
import game.utils.InputManager;

/**
 *
 * @author suominka
 */
public class MainMenu implements LogicInterface {
    Renderer renderer = null;
    InputManager inputs = null;
    
    private LogicInterface parent;
    private boolean nextReadyToSpawn = false;
    private String nextLogicName = "";
    
    private long lastTime;
    private boolean initialized = false;
    double deltatimeMillis = 0;
    MainMenuScreen menuScreen;
    
    public MainMenu() {
        menuScreen = new MainMenuScreen("mainmenu");
    }
    
    @Override
    public void setInputManager(InputManager inputs) {
        this.inputs = inputs;
    }

    @Override
    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
        renderer.appendToRenderQueue(menuScreen);
    }
    
    private void updateGUI() {
        if (inputs == null) {
            return;
        }
        if (nextReadyToSpawn) {
            spawnNext();
        }
        if (inputs.keyDownOnce("ok")) {
            next("baseGame");
        }
        if (inputs.keyDownOnce("previous")) {
            next("close");
        }
    }
    
    private void getDeltatimeMillis() {
        long time = System.nanoTime() / 1000000;
        if (lastTime > 0) {
            deltatimeMillis = (double) (time - lastTime);
        }
        lastTime = time;
    }
    
    private void _update() {
        if (!initialized) {
            if (menuScreen.isInitialized()) {
                initialized = true;
                renderer.setLoading(false);
            }
        }
    }
    
    @Override
    public void update() {
        getDeltatimeMillis();
        _update();
        updateGUI();
    }

    @Override
    public void update(double dtMillis) {
        deltatimeMillis = dtMillis;
        _update();
    }
    
    private LogicInterface spawnLogic(String name) {
        LogicInterface newLogic = null;
        switch (name) {
            case "baseGame":
                newLogic = new BaseGame();
                break;
            case "close":
                renderer.close();
                break;
            default:
                break;
        }
        return newLogic;
    }
    
    private void spawnNext() {
        nextReadyToSpawn = false;
        LogicInterface logic = spawnLogic(nextLogicName);
        renderer.setLogic(logic);        
    }
    
    private void next(String name) {
        nextLogicName = name;
        renderer.setLoading(true);
        renderer.removeFromRenderQueue(menuScreen);
        nextReadyToSpawn = true;
    }
    
    public void setParent(LogicInterface parent) {
        this.parent = parent;
    }
}
