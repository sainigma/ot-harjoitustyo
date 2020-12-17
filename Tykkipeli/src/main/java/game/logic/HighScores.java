/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author suominka
 */
public class HighScores implements LogicInterface {
    private Renderer renderer = null;
    private InputManager inputs = null;
    private Timing timing;
    
    private boolean nextReadyToSpawn = false;
    
    JSONObject localScores;
    JSONObject globalScores;
    ArrayList<String> levelList;
 
    private StringTools stringTools = new StringTools();
    
    private PIDAnimator animator = new PIDAnimator(0.015f, 0f, 0.4f, 100f);
    private Vector3d scoreRootStartPosition;
    private Vector3d scoreRootTargetPosition = new Vector3d();
    
    private double deltatimeMillis;
    private boolean initialized = false;
    
    private ScreenShaker root;
    private GameObject scoreRoot;
    private Text title;
    
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
                    content += "\n" + stringTools.padZeros(singleScoreArr.getInt(1), 9);
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
    
    public void setInputManager(InputManager inputs) {
        this.inputs = inputs;
    }

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

    @Override
    public void update(double dtMillis) {
        deltatimeMillis = dtMillis;
        updateLogic();
    }

    @Override
    public void setParent(LogicInterface parent) {
    }
}
