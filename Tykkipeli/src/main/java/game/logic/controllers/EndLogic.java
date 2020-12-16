/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic.controllers;

import game.components.animation.PIDAnimator;
import game.components.templates.EndScreen;
import game.utils.InputManager;

/**
 *
 * @author suominka
 */
public class EndLogic {
    private boolean active = false;

    EndScreen endScreen;
    InputManager inputs;
    
    private boolean winState;
    
    private boolean nameEntry = false;
    private boolean confirmed = false;
    private boolean animateScores = false;
    private boolean resolution = false;
    private String next = "";
    
    private String name = "";
    private int letterSelected = (int)'A';
    
    private int scoreIndex = -1;
    private int scoreTarget;
    private int[] scores = {-1, -1, -1, -1, -1};
    private int[] displayScores = {-1, -1, -1, -1, -1};
    
    private boolean scoresFinished = false;
    
    PIDAnimator scoreAnimator = new PIDAnimator(0.05f, 0, 0.5f, 1f);
    
    public EndLogic(EndScreen endScreen) {
        this.endScreen = endScreen;
    }
    
    public void setInputManager(InputManager inputs) {
        this.inputs = inputs;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void activate() {
        if (active) {
            return;
        }
        active = true;
        endScreen.enter();
    }
    
    public void deactivate() {
        active = false;
    }
    
    public String getName() {
        return name;
    }
    
    public void setScores(int shipScore, int warheadScore, int chargeScore) {
        scores[0] = shipScore + 10000;
        scores[1] = warheadScore;
        scores[2] = chargeScore;
        scores[3] = scores[0] + scores[1] + scores[2];
        scores[4] = 1000;
    }
    
    public void setWinState(boolean winState) {
        this.winState = winState;
        endScreen.setWinState(winState);
    }
    
    public void finalStageReached() {
        endScreen.finalStageReached();
    }
    
    private void acceptLetter() {
        name += (char) letterSelected;
        letterSelected = (int) 'A';
        endScreen.setNameEntry(name + (char) letterSelected);
        if (name.length() == 3) {
            nameEntry = false;
            endScreen.setNameEntryVisibility(false);
            endScreen.enableChoises();
        }
    }
    
    private void processOK() {
        if (!confirmed) {
            confirmed = true;
            endScreen.hideTitle();
            if (winState) {
                animateScores = true;
            } else {
                endScreen.enableChoises();
                scoresFinished = true;
            }
            return;
        }
        if (!scoresFinished) {
            nextScore();                
        } else if (nameEntry) {
            acceptLetter();
        } else {
            setNext();
        }
    }
    
    private void processHorizontal(int direction) {
        if (nameEntry) {
            letterSelected += direction;
            if (letterSelected < (int) 'A') {
                letterSelected = (int) 'Z';
            } else if (letterSelected > (int) 'Z') {
                letterSelected = (int) 'A';
            }
            endScreen.setNameEntry(name + (char) letterSelected);
            return;
        }
        if (scoresFinished && !resolution) {
            if (direction > 0) {
                endScreen.choiseIncrement();
            } else {
                endScreen.choiseDecrement();
            }
        }
    }
    
    private void endControls() {
        if (!active) {
            return;
        }
        if (inputs.keyDownOnce("ok")) {
            processOK();
        }
        boolean left = inputs.keyDownOnce("left");
        boolean right = inputs.keyDownOnce("right");
        if (left || right) {
            processHorizontal(left ? -1 : 1);
        }
    }
    
    private void setNext() {
        switch(endScreen.getChoise()) {
            case 0:
                if (winState) {
                    next = "next";
                } else {
                    next = "replay";
                }
                break;
            case 1:
                next = "close";
                break;
        }
        endScreen.disableChoises();
        resolution = true;
    }
    
    public String getNext() {
        return next;
    }
    
    private void nextScore() {
        if (scoreIndex >= scores.length) {
            return;
        }
        if (scoreIndex >= 0) {
           displayScores[scoreIndex] = scores[scoreIndex];            
        }
        scoreIndex++;
        if (scoreIndex < scores.length) {
            scoreTarget = scores[scoreIndex];
            scoreAnimator.enter();
        } else {
            scoresFinished = true;
            endScreen.setNameEntryVisibility(true);
            nameEntry = true;
        }
    }
    
    private void animateScores(double deltatimeMillis) {
        if (scoresFinished || !animateScores) {
            return;
        }
        if (!scoreAnimator.animating()) {
            nextScore();
            return;
        }
        float t = scoreAnimator.animate(deltatimeMillis);
        int score = (int) (scoreTarget * t);
        displayScores[scoreIndex] = score;
        endScreen.setScore(displayScores);
    }
    
    public boolean hasResolution() {
        return resolution;
    }
    public void update(double deltatimeMillis) {
        activate();
        animateScores(deltatimeMillis);
        endControls();
    }
}
