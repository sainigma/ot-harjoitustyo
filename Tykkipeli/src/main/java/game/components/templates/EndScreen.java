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
    Text title;
    Text nameEntry;
    int choise = 0;
    boolean winState = true;
    String [] winChoises = {"SEURAAVA KENTTÄ   lopeta", "seuraava kenttä   LOPETA"};
    String [] loseChoises = {"YRITÄ UUDESTAAN   lopeta", "yritä uudestaan   LOPETA"};
    
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
    
    public void finalStageReached() {
        winChoises = new String [] {"                  LOPETA", "                  LOPETA"};
    }
    
    private void animatePosition(float t) {
        Vector3d hidden = new Vector3d(0, 1080, 100);
        Vector3d visible = new Vector3d(0, 0, 100);
        setPosition(new Vector3d().lerp(hidden, visible, t));
    }
    
    public void animate(double deltatimeMillis) {
        if (!animator.animating()) {
            return;
        }
        animatePosition(animator.animate(deltatimeMillis));
    }
    
    private void spawnObjects() {
        background = new GameObject("endCard", "endView/placeholder.png", new Vector3d()) { };
        messenger = new Text();
        choises = new Text();
        title = new Text();
        nameEntry = new Text();
    }
    
    public void init() {
        spawnObjects();

        background.setDepth(40);
        title.translate(576, 280);
        nameEntry.translate(576 + 48, 280 + 32);
        nameEntry.setVisible(false);
        choises.translate(450, 360);
        choises.setVisible(false);
        messenger.translate(490, 168);
        background.append(title);
        background.append(nameEntry);
        background.append(choises);
        background.append(messenger);
        append(background);
        background.translate(0, 32 * 2);
        background.setDepth(1000);
    }
    
    public void setScore(int[] scores) {
        String titles[] = {"\nlaivat    ", "\nammukset  ", "\npanokset  ", "\nyhteensä  "};
        String message = "PISTEET";
        int i = 0;
        for (int score : scores) {
            if (score >= 0) {
                if (i < titles.length) {
                    message += titles[i];
                    String scoreString = Integer.toString(score);
                    for (int j = scoreString.length(); j < 9; j++) {
                        message += "0";
                    }
                    message += scoreString;
                }
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
    
    public void disableChoises() {
        choises.setVisible(false);
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
    
    public void setNameEntryVisibility(boolean state) {
        title.setVisible(state);
        nameEntry.setVisible(state);
        if (state) {
            messenger.setVisible(false);
            title.setContent("Syötä nimi");            
            nameEntry.setContent("A");
        } else {
            messenger.setVisible(true);
        }
    }
    
    public void setNameEntry(String content) {
        nameEntry.setContent(content);
    }
    
    public void hideTitle() {
        title.setVisible(false);
    }
    
    public void setWinState(boolean winState) {
        this.winState = winState;
        System.out.println("asd");
        if (winState) {
            title.setContent(" VOITTO!  ");
        } else {
            title.setContent(" HÄVIÖ!!  ");
        }
    }
    
    public int getChoise() {
        return choise;
    }
    
    public void update() {
        animate(getDeltatime());
    }
}
