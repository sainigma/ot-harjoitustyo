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
package game.logic.controllers;

import game.components.animation.PIDAnimator;
import game.components.templates.EndScreen;
import game.utils.InputManager;

/**
 * Logiikka ja käyttöliittymäohjaaja pelin lopetukselle, kontrollit seuraavaan logiikkaan siirtymiselle ja käyttäjän nimimerkin vastaanotolle.
 * @author Kari Suominen
 */
public class EndLogic {
    private boolean active = false;

    private EndScreen endScreen;
    private InputManager inputs;
    
    private boolean winState;
    
    private boolean nameEntry = false;
    private boolean confirmed = false;
    private boolean animateScores = false;
    private boolean resolution = false;
    private String next = "";
    
    private String name = "";
    private int letterSelected = (int) 'A';
    
    private int scoreIndex = -1;
    private int scoreTarget;
    private int[] scores = {-1, -1, -1, -1, -1};
    private int[] displayScores = {-1, -1, -1, -1, -1};
    
    private boolean scoresFinished = false;
    
    private PIDAnimator scoreAnimator = new PIDAnimator(0.05f, 0, 0.5f, 1f);
    /**
     * Rakentaja, alustaa lopetusruudun käyttöliittymän.
     * @param endScreen 
     */
    public EndLogic(EndScreen endScreen) {
        this.endScreen = endScreen;
    }
    /**
     * Asettaa logiikalle näppäinkuuntelijan.
     * @param inputs 
     */
    public void setInputManager(InputManager inputs) {
        this.inputs = inputs;
    }
    /**
     * Palauttaa logiikan päälläolotilan.
     * @return 
     */
    public boolean isActive() {
        return active;
    }
    /**
     * Aktivoi logiikan ja sen käyttöliittymän.
     */
    public void activate() {
        if (active) {
            return;
        }
        active = true;
        endScreen.enter();
    }
    /**
     * Sammuttaa logiikan.
     */
    public void deactivate() {
        active = false;
    }
    /**
     * Palauttaa käyttäjän nimimerkin.
     * @return 
     */
    public String getName() {
        return name;
    }
    /**
     * Asettaa lopulliset pisteet kentälle.
     * @param shipScore
     * @param warheadScore
     * @param chargeScore 
     */
    public void setScores(int shipScore, int warheadScore, int chargeScore) {
        scores[0] = shipScore + 10000;
        scores[1] = warheadScore;
        scores[2] = chargeScore;
        scores[3] = scores[0] + scores[1] + scores[2];
        scores[4] = 1000;
    }
    /**
     * Asettaa pelin voittotilan, vaikuttaa logiikan etenemiseen ja vaihtoehtoihin.
     * @param winState 
     */
    public void setWinState(boolean winState) {
        this.winState = winState;
        endScreen.setWinState(winState);
    }
    /**
     * Kutsuttuna lähettää graafiselle käyttöliittymälle tiedon että kyseinen kenttä on viimeinen.
     */
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
        switch (endScreen.getChoise()) {
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
    /**
     * Palauttaa seuraavan määränpään.
     * @return 
     */
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
    /**
     * Palauttaa toden jos käyttäjä on määrittänyt seuraavan määränpään käyttöliittymässä.
     * @return 
     */
    public boolean hasResolution() {
        return resolution;
    }
    /**
     * Päivitysmetodi, käyttää päälogiikan ajastusta tahdistukseen.
     * @param deltatimeMillis 
     */
    public void update(double deltatimeMillis) {
        activate();
        animateScores(deltatimeMillis);
        endControls();
    }
}
