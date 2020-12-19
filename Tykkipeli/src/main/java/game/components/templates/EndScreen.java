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
package game.components.templates;

import game.components.GameObject;
import game.components.Text;
import game.components.animation.PIDAnimator;
import game.utils.Vector3d;

/**
 * Käyttöliittymä lopetusruudulle. <a href="./../../logic/controllers/EndLogic.html">EndLogic</a> luokan ohjaama.
 * @author Kari Suominen
 */
public class EndScreen extends GameObject {
    
    private GameObject background;
    private Text messenger;
    private Text choises;
    private Text title;
    private Text nameEntry;
    private int choise = 0;
    private boolean winState = true;
    private String [] winChoises = {"SEURAAVA KENTTÄ   lopeta", "seuraava kenttä   LOPETA"};
    private String [] loseChoises = {"YRITÄ UUDESTAAN   lopeta", "yritä uudestaan   LOPETA"};
    
    private PIDAnimator animator = new PIDAnimator(0.1f, 0f, 0.2f, 100f);
    
    public EndScreen(String name) {
        super(name);
        init();
    }
    
    /**
     * Aktivoi sisääntuloanimaation.
     */
    public void enter() {
        setVisible(true);
        animator.enter();
        animatePosition(0);
    }
    
    /**
     * Aktivoi ulostuloanimaation.
     */
    public void exit() {
        animator.exit();
        animatePosition(1f);
    }
    
    /**
     * Kutsutaan jos nykyinen kenttä on viimeinen, uudelleenasettaa käyttöliittymän vaihtoehdot.
     */
    public void finalStageReached() {
        winChoises = new String [] {"                  LOPETA", "                  LOPETA"};
    }
    
    private void animatePosition(float t) {
        Vector3d hidden = new Vector3d(0, 1080, 100);
        Vector3d visible = new Vector3d(0, 0, 100);
        setPosition(new Vector3d().lerp(hidden, visible, t));
    }
    
    /**
     * Päivitysmetodi ruudun animaattorille, liikuttaa käyttöliittymää pystysuunnassa.
     * @param deltatimeMillis
     */
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
    
    /**
     * Alustaa käyttöliittymän käyttämän objektit sekä asettaa niiden sijainnit.
     */
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
    
    /**
     * Kutsutaan kun käyttöliittymän pistelista halutaan päivittää, animoitu EndLogic luokassa.
     * @param scores eri asioita saadut pisteet eriteltynä
     */
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
    /**
     * Tekee käyttäjän vaihtoehdoista näkyviä.
     */
    public void enableChoises() {
        choise = 0;
        choises.setVisible(true);
        setChoise();
    }
    /**
     * Piilottaa käyttäjän vaihtoehdot.
     */
    public void disableChoises() {
        choises.setVisible(false);
    }
    /**
     * Valitsee seuraavan vaihtoehdon.
     */
    public void choiseIncrement() {
        choise++;
        setChoise();
    }
    /**
     * Valitsee edellisen vaihtoehdon.
     */
    public void choiseDecrement() {
        choise--;
        setChoise();
    }
    private void setChoise() {
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
    /**
     * Asettaa nimisyötteen käyttöliittymän näkyvyyden.
     * @param state 
     */
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
    /**
     * Asettaa nimisyötteen arvon.
     * @param content 
     */
    public void setNameEntry(String content) {
        nameEntry.setContent(content);
    }
    /**
     * Piilottaa otsikon, voittostatuksen joka näkyy käyttöliittymän käynnistyessä.
     */
    public void hideTitle() {
        title.setVisible(false);
    }
    
    /**
     * Asettaa voittotilan luokalle, vaikuttaa viesteihin joita käyttöliittymä näyttää.
     * @param winState
     */
    public void setWinState(boolean winState) {
        this.winState = winState;
        System.out.println("asd");
        if (winState) {
            title.setContent(" VOITTO!  ");
        } else {
            title.setContent(" HÄVIÖ!!  ");
        }
    }
    
    /**
     * Palauttaa nykyisen valinnan.
     * @return
     */
    public int getChoise() {
        return choise;
    }
    
    /**
     * Päivitysmetodi, päivittää animointimetodia.
     */
    @Override
    public void update() {
        animate(getDeltatime());
    }
}
