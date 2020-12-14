/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components.templates;

import game.components.GameObject;
import game.components.Text;
import game.utils.Vector3d;

/**
 *
 * @author suominka
 */
public class EndScreen extends GameObject {
    
    GameObject background;
    Text messenger;
    Text choises;
    String [] winChoises = {"SEURAAVA KENTTÄ   lopeta", "seuraava kenttä   LOPETA"};
    String [] loseChoises = {"UUSI PELI         lopeta", "uusi peli         LOPETA"};
    
    public EndScreen(String name) {
        super(name);
        init();
    }
    
    public void init() {
        background = new GameObject("endCard", "endView/placeholder.png", new Vector3d()) { };
        background.setDepth(40);
        messenger = new Text();
        choises = new Text();
        choises.translate(450, 360);
        choises.setContent(winChoises[0]);
        messenger.translate(490, 168);
        messenger.setContent("PISTEET\nlaivat    000000000\nammukset  000000000\npanokset  000000000\nyhteensä  000000000");
        background.append(choises);
        background.append(messenger);
        append(background);
        background.translate(0, 32 * 2);
    }
}
