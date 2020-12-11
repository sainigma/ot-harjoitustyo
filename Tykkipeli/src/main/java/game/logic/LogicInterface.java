/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic;

import game.graphics.Renderer;
import game.utils.InputManager;

/**
 *
 * @author suominka
 */
public interface LogicInterface {
    public void setInputManager(InputManager inputs);
    public void setRenderer(Renderer renderer);
    public void update();
    public void update(double dtMillis);
    public void setParent(LogicInterface parent);
}
