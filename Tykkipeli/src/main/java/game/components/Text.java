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
package game.components;

import game.graphics.TextureLoader;
import game.graphics.primitives.Letter;
import game.utils.Vector3d;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Tekstiä piirtävä luokka.
 * @author Kari Suominen
 */
public class Text implements DrawCallInterface {
    private HashMap<Character, Integer> letterMap;
    
    private ArrayList<Letter> letters;
    private TextureLoader texLoader;
    
    private Vector3d localPosition = new Vector3d(0, 0, 10);
    private Vector3d localRotation = new Vector3d();
    private Vector3d globalPosition = new Vector3d();
    private Vector3d globalRotation = new Vector3d();
    
    private boolean hasUpdated;
    private boolean visible = true;
    private boolean hasText = false;
    private String content;
    
    /**
     * Alustaa objektin sekä täyttää kirjaintauluun ascii-epäyhteensopivat merkit (skandinaaviset merkit).
     */
    public Text() {
        letterMap = new HashMap<>();
        letters = new ArrayList<>();
        fillLetterMap();
    }
    
    /**
     * Asettaa objektin tekstin sisällön ja tekee päivityspyynnön joka kutsutaan seuraavalla ruudunpäivityksellä.
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
        hasText = true;
    }

    private void setLetter(Letter letter, char character, int spaces, int lineChanges) {
        letter.reset();
        letter.setIndex(getLetterIndex(character));
        letter.setPosition(spaces, lineChanges);        
    }
    
    private void freeLetters(int startIndex) {
        int i = startIndex;
        while (i < letters.size()) {
            letters.get(i).setIndex(15);
            i++;
        }
    }
    
    private void spawnLetter(int currentIndex) {
        if (currentIndex + 1 > letters.size()) {
            letters.add(new Letter(texLoader, "default"));
        }

    }
    
    private void setLetters() {
        if (texLoader == null) {
            return;
        }
        hasText = false;
        int i = 0, spaces = 0, lineChanges = 0;
        for (char c : content.toCharArray()) {
            if (c == '\n') {
                lineChanges += 2;
                spaces = 0;
            } else {
                spawnLetter(i);
                setLetter(letters.get(i), c, spaces, lineChanges);
                spaces++;
                i++;
            }
        }
        freeLetters(i);
    }
    
    private int getLetterIndex(char c) {
        int cValue = (int) c;
        if (cValue >= 48 && cValue <= 57) {
            return cValue - 48;
        } else if (cValue >= 97 && cValue <= 122) {
            return cValue - 97 + 16;
        } else if (cValue >= 65 && cValue <= 90) {
            return cValue - 65 + 48;
        } else if (letterMap.containsKey(c)) {
            return letterMap.get(c);
        } else {
            System.out.println("unmapped character: " + c + ", code: " + cValue);
        }
        return 15;
    }
    
    /**
     * Interfacemetodi joka ei tee mitään.
     */
    @Override
    public void update() {
        return;
    }

    /**
     * Asettaa objektin näkyvyyden.
     * @param state
     */
    @Override
    public void setVisible(boolean state) {
        visible = state;
    }

    /**
     * Siirtää objektia paikallisesti kaksiulotteisesti (lisäävästi).
     * @param x
     * @param y
     */
    @Override
    public void translate(float x, float y) {
        translate(x, y, (float) localPosition.z);
    }

    /**
     * Siirtää objektia paikallisesti kolmiulotteisesti (lisäävästi).
     * @param x
     * @param y
     * @param z
     */
    @Override
    public void translate(float x, float y, float z) {
        localPosition = localPosition.add(new Vector3d(x, y, z));
    }

    /**
     * Interfacemetodi, tyhjä.
     */
    @Override
    public void load() {
        return;
    }

    /**
     * Piirtometodi, kutsuu Letter-objektien piirrot ja päivittää ne tekstin tai sijainninmuutosten perusteella.
     */
    @Override
    public void draw() {
        if (hasText) {
            setLetters();
        }
        if (!visible) {
            return;
        }
        for (Letter letter : letters) {
            letter.setGlobalTransforms(globalPosition.add(localPosition), new Vector3d());
            letter.draw();
        }
    }

    /**
     * Asettaa paikallisen sijainnin suoraan.
     * @param position
     */
    @Override
    public void setPosition(Vector3d position) {
        localPosition = position;
    }
    
    /**
     * Asettaa paikallisen kiertymän suoraan.
     * @param rotation
     */
    @Override
    public void setRotation(Vector3d rotation) {
        localRotation = rotation;
    }
    
    /**
     * Asettaa perityn sijainnin suoraan.
     * @param position
     */
    @Override
    public void setGlobalPosition(Vector3d position) {
        globalPosition = position;
    }

    /**
     * Asettaa perityn kiertymän suoraan.
     * @param rotation
     */
    @Override
    public void setGlobalRotation(Vector3d rotation) {
        globalRotation = rotation;
    }

    /**
     * Asettaa päivittyneisyyden tilan.
     * @param state
     */
    @Override
    public void setUpdated(boolean state) {
        hasUpdated = state;
    }

    /**
     * Vastaanottaa tekstuurinlataajan.
     * @param loader
     */
    @Override
    public void setTextureLoader(TextureLoader loader) {
        texLoader = loader;
    }
    
    /**
     * Uudelleenohjaa pienennyksen näkyvyyden asettamiseen.
     * @param state
     */
    @Override
    public void setMinimized(boolean state) {
        setVisible(state);
    }

    private void fillLetterMap() {
        letterMap.put(' ', 15);
        letterMap.put('ä', 42);
        letterMap.put('ö', 43);
        letterMap.put('Ä', 74);
        letterMap.put('Ö', 75);
        letterMap.put('!', 10);
        letterMap.put('?', 11);
        letterMap.put('#', 12);
        letterMap.put('&', 13);
    }
}
