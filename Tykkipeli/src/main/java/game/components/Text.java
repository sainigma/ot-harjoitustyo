/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components;

import game.graphics.TextureLoader;
import game.graphics.primitives.Letter;
import game.utils.Vector3d;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author suominka
 */
public class Text implements DrawCallInterface{
    private HashMap <Character, Integer> letterMap;
    
    private ArrayList<Letter> letters;
    private TextureLoader texLoader;
    
    public Vector3d localPosition = new Vector3d(0,0,100);
    public Vector3d localRotation = new Vector3d();
    public Vector3d globalPosition = new Vector3d();
    public Vector3d globalRotation = new Vector3d();
    
    public boolean hasUpdated;
    private boolean visible = true;
    private boolean hasText = false;
    private String content;
    
    public Text() {
        letterMap = new HashMap<>();
        letters = new ArrayList<>();
        fillLetterMap();
    }
    
    public void setContent(String content) {
        this.content = content;
        hasText = true;
    }
    
    public void setLetters() {
        if (texLoader == null) {
            return;
        }
        hasText = false;
        int i = 0;
        int spaces = 0;
        float lineChanges = 0;
        for (char c : content.toCharArray()) {
            if (c == '\n') {
                lineChanges += 2f;
                spaces = 0;
            } else {
                if (i + 1 > letters.size()) {
                    spawnLetter();
                }
                Letter letter = letters.get(i);
                letter.reset();
                letter.setIndex(getLetterIndex(c));
                letter.setPosition(spaces, lineChanges);
                spaces++;
                i++;
            }
        }
        while (i < letters.size()) {
            letters.get(i).setIndex(15);
            i++;
        }
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
        }
        return 15;
    }
    
    private void spawnLetter() {
        letters.add(new Letter(texLoader, "default"));
    }
    
    @Override
    public void update() {
        return;
    }

    @Override
    public void setVisible(boolean state) {
        visible = state;
    }

    @Override
    public void translate(float x, float y) {
        translate(x,y, (float) localPosition.z);
    }

    @Override
    public void translate(float x, float y, float z) {
        localPosition = localPosition.add(new Vector3d(x, y, z));
    }

    @Override
    public void load() {
        return;
    }

    @Override
    public void draw() {
        if (hasText) {
            setLetters();
        }
        if (!visible) {
            return;
        }
        for (Letter letter : letters) {
            letter.setGlobalTransforms(localPosition, new Vector3d());
            letter.draw();
        }
    }

    @Override
    public void setPosition(Vector3d position) {
        localPosition = position;
    }
    
    @Override
    public void setRotation(Vector3d rotation) {
        localRotation = rotation;
    }
    
    @Override
    public void setGlobalPosition(Vector3d position) {
        globalPosition = position;
    }

    @Override
    public void setGlobalRotation(Vector3d rotation) {
        globalRotation = rotation;
    }

    @Override
    public void setUpdated(boolean state) {
        hasUpdated = state;
    }

    @Override
    public void setTextureLoader(TextureLoader loader) {
        texLoader = loader;
    }
    
    @Override
    public void setMinimized(boolean state) {
        setVisible(state);
    }

    private void fillLetterMap() {
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
