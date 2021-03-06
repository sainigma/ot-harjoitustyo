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

import game.components.GameObject;
import game.components.Text;
import game.components.animation.PIDAnimator;
import game.components.templates.ScreenShaker;
import game.graphics.Renderer;
import game.utils.InputManager;
import game.utils.ScoreManager;
import game.utils.StringTools;
import game.utils.Timing;
import game.utils.Vector3d;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Ohjauslogiikka pistenäkymälle.
 * @author Kari Suominen
 */
public class HighScores implements LogicInterface {
    private Renderer renderer = null;
    private InputManager inputs = null;
    private Timing timing;
    
    private boolean nextReadyToSpawn = false;
    
    private JSONObject localScores;
    private JSONObject globalScores;
    private ArrayList<String> levelList;
 
    private StringTools stringTools = new StringTools();
    
    private PIDAnimator animator = new PIDAnimator(0.015f, 0f, 0.4f, 100f);
    private Vector3d scoreRootStartPosition;
    private Vector3d scoreRootTargetPosition = new Vector3d();
    
    private double deltatimeMillis;
    private boolean initialized = false;
    
    private ScreenShaker root;
    private GameObject scoreRoot;
    private Text title;
    
    /**
     * Rakentaja, alustaa käyttöliittymän ja hakee nykyiset pistetilastot.
     */
    public HighScores() {
        timing = new Timing();
        root = new ScreenShaker();
        scoreRoot = new GameObject("scoreRoot") { };
        title = new Text();
        root.append(title);
        root.append(scoreRoot);
        scoreRoot.translate(1000, 0);
        scoreRootStartPosition = scoreRoot.getPosition();
        title.setContent("PISTEET");
        fetchScores();
    }
    
    private void initLevelList() {
        levelList = new ArrayList<>();
        for (String filename : new File("./assets/levels/").list()) {
            if (Pattern.matches("e.m..json", filename)) {
                levelList.add(filename.substring(0, 4));                
            }
        }
        Collections.sort(levelList, String.CASE_INSENSITIVE_ORDER);
    }

    private void setScoreList(String title, String level, Text scoreList, JSONObject scores) {
        String content = title;
        if (scores == null || !scores.has(level) || scores.getJSONArray(level).length() == 0) {
            content += "\n ei pisteitä";
        } else {
            for (Object singleScore : scores.getJSONArray(level)) {
                JSONArray singleScoreArr = (JSONArray) singleScore;
                try {
                    String name = singleScoreArr.getString(0);
                    content += "\n" + name + "  " + stringTools.padZeros(singleScoreArr.getInt(1), 9);
                } catch (Exception e) {
                    throw new ClassCastException("Malformed score!");
                }

            }
        }
        scoreList.setContent(content);
    }
    
    private void createViews() {
        int offset = 0;
        for (String level : levelList) {
            Text title = new Text();
            title.translate(offset + 32, 48);
            title.setContent(level.toUpperCase());
            Text localScoreList = new Text();
            Text globalScoreList = new Text();
            scoreRoot.append(title);
            scoreRoot.append(localScoreList);
            scoreRoot.append(globalScoreList);
            localScoreList.translate(offset + 32, 80);
            globalScoreList.translate(offset + 320, 80);
            setScoreList("Lokaali", level, localScoreList, localScores);
            setScoreList("Globaali", level, globalScoreList, globalScores);
            offset += 640;
        }
    }
    
    private void fetchScores() {
        ScoreManager scoreManager = new ScoreManager();
        localScores = scoreManager.getLocalScores();
        globalScores = scoreManager.getGlobalScores();
        initLevelList();
        createViews();
    }
    
    /**
     * Asettaa näppäimistökuuntelijan.
     * @param inputs
     */
    @Override
    public void setInputManager(InputManager inputs) {
        this.inputs = inputs;
    }
    
    /**
     * Asettaa renderöijän logiikalle.
     * @param renderer
     */
    @Override
    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
        renderer.appendToRenderQueue(root);
    }
    
    private void updateLogic() {
    }
    
    private void animateScoreRoot(float t) {
        scoreRoot.setPosition(new Vector3d().lerp(
                scoreRootStartPosition,
                scoreRootTargetPosition,
                t
        ));
    }
    
    private void animate() {
        if (!animator.animating()) {
            return;
        }
        float control = animator.animate(deltatimeMillis);
        animateScoreRoot(control);
    }
    
    private void shiftView(float value) {
        scoreRootStartPosition.set(scoreRoot.getPosition());
        scoreRootTargetPosition.x += value;
        animator.enter();
    }
    
    private void updateControls() {
        if (inputs == null) {
            return;
        }
        if (inputs.keyDownOnce("right")) {
            shiftView(-640f);
        } else if (inputs.keyDownOnce("left")) {
            shiftView(640f);
        }
        if (inputs.keyDownOnce("previous") || inputs.keyDownOnce("quit") || inputs.keyDownOnce("ok")) {
            exit();
        }
    }
    
    private void spawnNext() {
        nextReadyToSpawn = false;
        renderer.setLogic(new MainMenu());
    }
    
    private void exit() {
        renderer.setLoading(true);
        renderer.removeFromRenderQueue(root);
        nextReadyToSpawn = true;
    }
    
    /**
     * Logiikan päivitysmetodi, alustaa käyttöliittymän, päivittää ajastimen, logiikan ja käyttöliittymän sekä tarvittaessa kutsuu seuraavan logiikan spawnauksen.
     */
    @Override
    public void update() {
        if (nextReadyToSpawn) {
            spawnNext();
        }
        if (!initialized) {
            initialized = true;
            renderer.setLoading(false);
            animator.enter();
        }
        deltatimeMillis = timing.getDeltatimeMillis();
        updateLogic();
        updateControls();
        animate();
    }
}
