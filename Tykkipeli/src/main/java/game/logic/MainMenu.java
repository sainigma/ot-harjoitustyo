/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic;

import game.components.Text;
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
    private long timer = 0;
    private boolean initialized = false;
    double deltatimeMillis = 0;
    MainMenuScreen menuScreen;
    Text text;
    
    private int menuIndex = -1;
    private String menuItems[] = {
        " UUSI PELI\nhighscroret\nlopeta",
        "uusi peli\n HIGHSCORET\nlopeta",
        "uusi peli\nhighscroret\n LOPETA"
    };
    
    public MainMenu() {
        menuScreen = new MainMenuScreen("mainmenu");
        menuScreen.setVisible(false);
        text = new Text();
        menuScreen.menuEmpty.append(text);
        text.translate(650, 450);
        text.setContent("Paina ENTER aloittaaksesi");
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

    private boolean intro = false;
    private boolean uiActive = false;
    private void updateGUI() {
        if (!intro && timer > 16 * 3) {
            renderer.setBackground(249f / 255f, 240f / 255f, 223f / 255f);
            intro = true;
            menuScreen.enter();
        }
        if (inputs == null) {
            return;
        }
        if (nextReadyToSpawn) {
            spawnNext();
        }
        if (!uiActive) {
            if (menuScreen.getAnimatedPosition() > 0.85f) {
                uiActive = true;
            } else {
                return;
            }
        }
        if (inputs.keyDownOnce("ok")) {
            if (menuIndex == -1) {
                text.translate(100, 0);
                menuIndex = 0;
                text.setContent(menuItems[0]);
            } else {
                switch(menuIndex) {
                    case 0:
                        next("baseGame");
                        break;
                    case 1:
                        next("highscore");
                        break;
                    case 2:
                       next("close");
                       break;
                }
            }
        }
        if (menuIndex >= 0) {
            boolean hasChanged = false;
            if (inputs.keyDownOnce("up")) {
                menuIndex--;
                hasChanged = true;
            } else if (inputs.keyDownOnce("down")) {
                menuIndex++;
                hasChanged = true;
            }
            if (hasChanged) {
                if (menuIndex >= menuItems.length) {
                    menuIndex = 0;
                } else if (menuIndex < 0) {
                    menuIndex = menuItems.length - 1;
                }
                text.setContent(menuItems[menuIndex]);
            }
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
        timer += deltatimeMillis;
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
                throw new IllegalArgumentException("Level identifier not found");
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
