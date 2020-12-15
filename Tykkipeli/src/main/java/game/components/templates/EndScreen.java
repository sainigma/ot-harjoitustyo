/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components.templates;

import game.components.GameObject;
import game.components.Text;
import game.components.animation.PIDAnimator;
import game.utils.PID;
import game.utils.Vector3d;

/**
 *
 * @author suominka
 */
public class EndScreen extends GameObject {
    
    GameObject background;
    Text messenger;
    Text choises;
    int choise = 0;
    boolean winState = true;
    String [] winChoises = {"SEURAAVA KENTTÄ   lopeta", "seuraava kenttä   LOPETA"};
    String [] loseChoises = {"UUSI PELI         lopeta", "uusi peli         LOPETA"};
    
    PIDAnimator animator = new PIDAnimator(0.1f, 0f, 0.2f, 100f);
    
    public EndScreen(String name) {
        super(name);
        init();
    }
    
    public void enter() {
        setVisible(true);
        animator.enter();
        animatePosition(0);
    }
    
    public void exit() {
        animator.exit();
        animatePosition(1f);
    }
    
    private void animatePosition(float t) {
        Vector3d hidden = new Vector3d(0, 1080, 100);
        Vector3d visible = new Vector3d(0, 0, 100);
        setPosition(new Vector3d().lerp(hidden, visible, t));
    }
    
    public boolean animating() {
        return animator.getAnimatedPosition() < 0.5f;
    }
    
    public void animate(double deltatimeMillis) {
        if (!animator.animating()) {
            return;
        }
        animatePosition(animator.animate(deltatimeMillis));
    }
    
    public void init() {
        background = new GameObject("endCard", "endView/placeholder.png", new Vector3d()) { };
        background.setDepth(40);
        messenger = new Text();
        choises = new Text();
        choises.translate(450, 360);
        choises.setVisible(false);
        messenger.translate(490, 168);
        background.append(choises);
        background.append(messenger);
        append(background);
        background.translate(0, 32 * 2);
        background.setDepth(1000);
        setScore(new int[] {100,100});
    }
    
    public void setScore(int[] scores) {
        String titles[] = {"\nlaivat    ", "\nammukset  ", "\npanokset  ", "\nyhteensä  "};
        String message = "PISTEET";
        int i = 0;
        for (int score : scores) {
            if (score >= 0) {
                message += titles[i];
                String scoreString = Integer.toString(score);
                for (int j = scoreString.length(); j < 9; j++) {
                    message += "0";
                }
                message += scoreString;
            }
            i++;
        }
        messenger.setContent(message);
    }
    
    public void enableChoises() {
        choise = 0;
        choises.setVisible(true);
        setChoise();
    }
    
    public void choiseIncrement() {
        choise++;
        setChoise();
    }
    
    public void choiseDecrement() {
        choise--;
        setChoise();
    }
    
    public void setChoise() {
        if (choise > 1) {
            choise = 0;
        } else if (choise < 0) {
            choise = 1;
        }
        if (winState) {
            choises.setContent(winChoises[choise]);
        } else {
            choises.setContent(loseChoises[choise]);
        }
    }
    
    public void setWinState(boolean winState) {
        this.winState = winState;
    }
    
    public int getChoise() {
        return choise;
    }
    
    public void update() {
        animate(getDeltatime());
    }
}
