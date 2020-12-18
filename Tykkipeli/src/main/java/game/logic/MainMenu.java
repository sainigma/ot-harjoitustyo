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
package game.logic;

import game.components.Text;
import game.components.templates.MainMenuScreen;
import game.graphics.Renderer;
import game.utils.InputManager;
import game.utils.Timing;

/**
 *
 * @author Kari Suominen
 */
public class MainMenu implements LogicInterface {
    Renderer renderer = null;
    InputManager inputs = null;
    private Timing timing;
    
    private LogicInterface parent;
    private boolean nextReadyToSpawn = false;
    private String nextLogicName = "";
    
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
        timing = new Timing();
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
        if (!intro && timing.getTimer() > 16 * 3) {
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
                switch (menuIndex) {
                    case 0:
                        next("baseGame");
                        break;
                    case 1:
                        next("highscores");
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
    
    private void updateLogic() {
        if (!initialized) {
            if (menuScreen.isInitialized()) {
                initialized = true;
                renderer.setLoading(false);
            }
        }
    }
   
    @Override
    public void update() {
        deltatimeMillis = timing.getDeltatimeMillis();
        updateLogic();
        updateGUI();
    }

    @Override
    public void update(double dtMillis) {
        deltatimeMillis = dtMillis;
        updateLogic();
    }
    
    private LogicInterface spawnLogic(String name) {
        LogicInterface newLogic = null;
        switch (name) {
            case "baseGame":
                newLogic = new BaseGame();
                break;
            case "highscores":
                newLogic = new HighScores();
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
