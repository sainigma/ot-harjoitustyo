/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic;

import game.components.GameObject;
import game.logic.controllers.*;
import game.components.Level;
import game.components.Text;
import game.components.templates.ScreenShaker;
import game.utils.InputManager;
import game.graphics.Renderer;
import game.utils.JSONLoader;
import game.utils.PID;
import game.utils.ScoreManager;
import game.utils.Services;
import game.utils.Timing;
import game.utils.StringTools;
import game.utils.Vector3d;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Pääluokka pelin logiikalle
 * @author suominka
 */
public class BaseGame implements LogicInterface {
    private InputManager inputs = null;
    private Renderer renderer = null;
    private Timing timing;
    LogicInterface parent = null;
    
    ScoreManager scoreManager;
    private String currentLevel;
    private String nextLevel;
    
    private boolean guiInitialized = false;
    public MortarLogic mortarLogic;
    public ReloadLogic reloadLogic;
    public EndLogic endLogic;
    public ArrayList<TargetLogic> targets;
    public Level level = null;
    
    private int targetsLeft = 0;
    
    private String nextLogicName;
    private boolean hasNext = false;
    
    public ScreenShaker screenShaker;
    private boolean gunMovementActive = true;
    
    private boolean initialized = false;
    
    private boolean lost = false;
    
    private double deltatimeMillis;
    
    private boolean instructionsVisible = false;
    private GameObject instructionsBackground;
    private int score = 0;
    private float scoreTarget;
    private float tempScore;
    private Text scoreDisplay;
    private Text windDisplay;
    private Text instructions;
    private PID scorePID = new PID(0.05f, 0, 0.5f, 1f);
    
    private StringTools stringTools = new StringTools();
    private Text messenger;
    
    /**
     * Ilman parametreja konstruktori lataa ensimmäisen kentän.
     */
    public BaseGame() {
        loadLevel("e1m1");
    }
    
    /**
     * Parametrin kanssa konstruktori lataa tietyn kentän.
     * @param level Kentän avain
     */
    public BaseGame(String level) {
        loadLevel(level);
    }
    
    /**
     * Asettaa logiikalle renderöijän, asetuksen yhteydessä Level lisätään renderöijän piirtojonoon.
     * @param renderer 
     */
    @Override
    public void setRenderer(Renderer renderer) {
        if (renderer != null) {
            renderer.appendToRenderQueue(level);
            renderer.setLoading(false);
            this.renderer = renderer;
            guiInitialized = true;
        }
    }
    
    private void loadLevel(String name) {
        timing = new Timing();
        targets = new ArrayList<>();
        currentLevel = name;
        score = 0;
        targetsLeft = 0;
        instructionsBackground = new GameObject("instructionsBackground", "background/instructionBackground.png", new Vector3d(), 1f) { };
        
        spawnLevel();
        spawnMessengers();
        spawnObjects();
        
        setWind();
        loadLevelData();
        level.mortar.setTraversal((float) (Math.random() * 90f));
    }
    
    private void loadLevelData() {
        JSONObject levelData = new JSONLoader("assets/levels/").read(currentLevel);
        JSONObject magazine = levelData.getJSONObject("magazine");
        nextLevel = levelData.has("next") ? levelData.getString("next") : "close";
        
        reloadLogic.setMagazine(
                magazine.getInt("light"),
                magazine.getInt("medium"),
                magazine.getInt("heavy"),
                magazine.getInt("charges")
        );
        spawnTargets(levelData.getJSONArray("ships"));
    }
    
    private void setWind() {
        Services services = new Services();
        JSONObject obj = services.getJSONObject("mainsite/tykkipeli");
        if (obj == null || !obj.has("wind")) {
            windDisplay.setContent("Tuulen hakeminen verkosta epäonnistui");
            return;
        }
        JSONObject wind = obj.getJSONObject("wind");
        double speed = wind.getDouble("speed");
        double direction = wind.getDouble("direction");
        windDisplay.setContent("Tuuli  " + (int) speed + " mps suuntaan " + (int) direction + "°");
        mortarLogic.setWind(speed, direction);
    }
    
    private void toggleInstructions() {
        instructionsVisible = !instructionsVisible;
        instructionsBackground.setVisible(instructionsVisible);
    }
    
    private void spawnLevel() {
        if (level != null) {
            level.reset();
        } else {
            level = new Level("basegame");
        }
    }
    
    private void spawnObjects() {
        screenShaker = new ScreenShaker();
        mortarLogic = new MortarLogic();
        reloadLogic = new ReloadLogic(mortarLogic, level.mortar, level.reloadScreen);
        instructionsBackground.setDepth(100);
        instructionsBackground.translate(850, 8);
        instructionsBackground.setVisible(instructionsVisible);
        level.mapScreen.overlay.append(instructionsBackground);
        reloadLogic.setMessenger(messenger);
        endLogic = new EndLogic(level.endScreen);
        scoreManager = new ScoreManager();
    }
    
    private void spawnMessengers() {
        scoreDisplay = new Text();
        messenger = new Text();
        instructions = new Text();
        windDisplay = new Text();
        windDisplay.translate(8, 720 - 32);
        scoreDisplay.translate(8, 8);
        instructions.translate(8, 8);
        messenger.translate(8, 32 + 8);
        messenger.setContent("Paina H avataksesi ohjeet");
        instructions.setContent("Kontrollit\nYLEISET \n nuolet  valinta\n  ENTER  ok\nTYKKI\n nuolet  liikutus\n  SHIFT  nopeutus\n   CTRL  hidastus\n   VÄLI  laukaisu\n      R  lataus\nMUUT         \n   M  iso karttanäkymä\n Q&E  kartan pyöritys\n   H  näytä/piilota ohje\n F10  luovuta");
        level.mapScreen.overlay.append(windDisplay);
        level.mapScreen.overlay.append(scoreDisplay);
        level.mapScreen.overlay.append(messenger);
        instructionsBackground.append(instructions);
    }

    private void spawnTargets(JSONArray ships) {
        for (Object ship : ships) {
            targetsLeft += 1;
            JSONObject shipObj = (JSONObject) ship;
            TargetLogic target = new TargetLogic(shipObj.getString("name"));
            target.setWaypoints(shipObj.getJSONArray("waypoints"));
            targets.add(target);
            level.mapScreen.spawnTarget(target);
        }
    }
    
    /**
     * Vastaanottaa ja asettaa itselleen ja lapsilogiikoille syötteidenkuuntelijan
     * @param inputs 
     */
    public void setInputManager(InputManager inputs) {
        this.inputs = inputs;
        endLogic.setInputManager(this.inputs);
        reloadLogic.setInputManager(this.inputs);
    }
    
    private int lastSolversActive = 0;
    private void shakeScreen() {
        int solversActive = mortarLogic.activeSolvers.size();
        if (solversActive < lastSolversActive) {
            screenShaker.shake();
        }
        lastSolversActive = solversActive;

        screenShaker.update();
        float shake[] = level.mortar.getShake();
        float altShake = screenShaker.getShakevalue();
        if (shake[1] + altShake > 0.05f) {
            float shakeLevel = 20f * (1.1f - shake[0]) * shake[1] + altShake;
            level.gameView.setScreenShake(shakeLevel);
            level.mapView.setScreenShake(shakeLevel);
        } else if (level.gameView.isShaking()) {
            level.gameView.setScreenShake(0);
            level.mapView.setScreenShake(0);
        }        
    }
    
    private float getSpeedModifier() {
        float framerateCoeff = (float) (16f / deltatimeMillis); //1 when 60fps
        float speedModifier = framerateCoeff;
        if (inputs.keyDown("modifier faster")) {
            speedModifier *= 2f;
        } else if (inputs.keyDown("modifier slower")) {
            speedModifier *= 0.5f;
        }
        return speedModifier;
    }
    
    private void traverse(float speedModifier) {
        if (reloadLogic.isMovementBlocked() && level.mapScreen.isMinimized()) {
            return;
        }
        float traversalSpeed = 0.5f;
        if (inputs.keyDown("traverse right")) {
            level.mortar.addToTraverseTarget(-traversalSpeed * speedModifier);
        } else if (inputs.keyDown("traverse left")) {
            level.mortar.addToTraverseTarget(traversalSpeed * speedModifier);
        }
        level.mapScreen.setTraversal(-level.mortar.getTraversal());
    }
    
    private void gameControls(float speedModifier) {
        if (!gunMovementActive || level.mortar.animator.isPlaying("mortar/firing") || reloadLogic.isMovementBlocked()) {
            return;
        }
        float elevationSpeed = 0.1f;
        if (inputs.keyDown("elevate")) {
            level.mortar.addToElevationTarget(elevationSpeed * speedModifier);
        } else if (inputs.keyDown("depress")) {
            level.mortar.addToElevationTarget(-elevationSpeed * speedModifier);
        }
        traverse(speedModifier);
    }
    
    private void mapControls(float speedModifier) {
        traverse(speedModifier);
        if (inputs.keyDownOnce("reload")) {
            if (reloadLogic.getProjectile() == null && reloadLogic.isReloadFinished()) {
                toggleView();
                reloadLogic.startReload();                
            }
        }
    }
    
    private void sharedControls(float speedModifier) {
        if (inputs.keyDownOnce("fire")) {
            fire();
        }
        if (inputs.keyDownOnce("toggle map") || (inputs.keyDownOnce("cancel") && !level.mapView.isMinimized())) {
            toggleView();
        }
        if (inputs.keyDownOnce("quit")) {
            lost = true;
        }
        if (inputs.keyDownOnce("help")) {
            toggleInstructions();
        }
        rotateMap(speedModifier);
    }
    
    private void rotateMap(float speedModifier) {
        if (!level.mapView.isVisible()) {
            return;
        }
        if (inputs.keyDown("rotate map right")) {
            level.mapScreen.rotateMap(speedModifier);
        } else if (inputs.keyDown("rotate map left")) {
            level.mapScreen.rotateMap(-speedModifier);
        }
    }
    
    private void menuMovement() {
        if (gunMovementActive) {
            return;
        }
    }
    
    /**
     * Tulituslogiikka. Aktivoituu tulitusnappia painettaessa, mutta tulittaa vain jos tulituslogiikka on alustettu.
     */
    public void fire() {
        if (!reloadLogic.isReloadFinished()) {
            return;
        }
        mortarLogic.set(level.mortar.getElevation(), level.mortar.getTraversal());
        if (mortarLogic.fire()) {
            float powerModifier = (reloadLogic.getProjectile().getCartouches() + 4f) / 7f;
            level.mortar.setPowerModifier(powerModifier);
            reloadLogic.resetProjectile();
            level.mortar.animator.playAnimation("mortar/firing");
            setMessage("Tulta!");
        } else {
            setMessage("Tykki ei ole ladattu!\nPaina R ladataksesi");
        }
    }
    
    private void toggleView() {
        level.gameView.toggleVisible();
        level.mapView.toggleMinimized();        
    }
    
    private void gameViewLogic(float speedModifier) {
        if (!level.gameView.isVisible()) {
            return;
        }
        gameControls(speedModifier);
        reloadLogic.reloadControls();
    }
    
    private void mapViewLogic(float speedModifier) {
        if (level.gameView.isVisible()) {
            return;
        }
        mapControls(speedModifier);
    }
    
    private void updateTargets() {
        for (TargetLogic target : targets) {
            target.update(deltatimeMillis);
            level.mapScreen.updateTarget(target);
            if (target.hasWon()) {
                lost = true;
            }
        }
    }
    
    private void getHits() {
        if (mortarLogic.hasHits()) {
            ArrayList<Statistic> hits = new ArrayList<>();
            while (mortarLogic.hasHits()) {
                hits.add(mortarLogic.getHit());
            }
            checkHits(hits);
        }
    }
    
    private void checkHits(ArrayList<Statistic> hits) {
        for (Statistic hit : hits) {
            double maxDamage = hit.getPower() * 150f;
            double maxDistance = 1000f;
            int prevScore = score;
            for (TargetLogic target : targets) {
                damageTarget(target, hit, maxDamage, maxDistance);
            }
            if (prevScore == score) {
                setMessage("Iskulla ei ollut vaikutusta");
            }
        }
    }
    
    private void setScore(int score) {
        tempScore = this.score;
        this.score = score;
        scoreTarget = score;
        scorePID.activate();
    }
    
    private void setScoreDisplay(int score) {
        String message = "Pisteet ";
        message += stringTools.padZeros(score, 9);
        scoreDisplay.setContent(message);
    }
    
    private void addScore(Statistic hit, TargetLogic target, boolean destroyed) {
        float mass = hit.getMass();
        String targetType = target.getName();
        float elevation = 0;
        float baseModifier = destroyed ? 1 : 0.5f;
        
        if (targetType.equals("ironclad")) {
            baseModifier *= 2f;
        } else if (targetType.equals("lineship")) {
            baseModifier *= 1.7f;
        } else if (targetType.equals("windjammer")) {
            baseModifier *= 1.5f;
        }
        
        baseModifier *= (elevation + 40) / 105f;
        baseModifier *= 123f / mass;
        score += (int) (baseModifier * 1000f);
        setScore(score);
    }
    
    private void animateScore() {
        if (scoreTarget < 0f) {
            return;
        }
        float error = scoreTarget - tempScore;
        if (error > 50f) {
            error = 50f;
        }
        float control = (float) scorePID.getControl(error, deltatimeMillis);
        if (control > 100f) {
            control = 100f;
        }
        tempScore += control;
        setScoreDisplay((int) tempScore);
        if (scoreTarget - tempScore < 1f) {
            scorePID.deactivate();
            scoreTarget = -1f;
            setScoreDisplay(score);
        }
    }
    
    private void setMessage(String message) {
        messenger.setContent(message);
    }
    
    private void hitMessage(int damage, String name, boolean sinks) {
        String message = "Osuma! " + damage + " pistettä vahinkoa";
        if (sinks) {
            message += "\n" + name + " uppoaa";
            if (targetsLeft > 0) {
                message += "\nkohteita jäljellä " + Integer.toString(targetsLeft);
            }
        }
        setMessage(message);
    }
    
    private void damageTarget(TargetLogic target, Statistic hit, double maxDamage, double maxDistance) {
        if (target.getHealth() > 0f) {
            Vector3d hitPosition = hit.getLastPosition(), targetPos = target.getPosition();
            double distance = new Vector3d(hitPosition.x - targetPos.x, hitPosition.z - targetPos.y, 0f).magnitude();
            double damageFactor = distance / maxDistance;
            if (damageFactor < 1f) {
                boolean sinks = false;
                double damage = maxDamage * (1 - Math.pow(damageFactor, 2));
                target.reduceHealth((float) damage);
                if (target.isSinking()) {
                    sinks = true;
                    targetsLeft -= 1;
                    addScore(hit, target, true);
                } else {
                    addScore(hit, target, false);
                }
                hitMessage((int) damage, target.getName(), sinks);
            }
        }
    }
    /**
     * Palauttaa jäljelläolevien maalien määrän.
     * @return 
     */
    public int getTargetsLeft() {
        return targetsLeft;
    }
    
    private LogicInterface spawnLogic(String name) {
        LogicInterface newLogic = null;
        if (name.equals("close") || nextLevel.equals("close")) {
            newLogic = new HighScores();
        } else if (name.equals("replay")) {
            newLogic = new BaseGame(currentLevel);
        } else if (name.equals("next")) {
            newLogic = new BaseGame(nextLevel);
        }
        return newLogic;
    }
    
    private void spawnNext() {
        if (!lost) {
            scoreManager.setName(endLogic.getName());
            scoreManager.saveScore();
        }
        renderer.setLogic(spawnLogic(nextLogicName));        
    }
    
    private void next(String name) {
        renderer.setLoading(true);
        renderer.removeFromRenderQueue(level);
        nextLogicName = name;
        hasNext = true;
    }
    
    private void initEndLevelLogic() {
        guiInitialized = false;
        endLogic.setWinState(!lost);
        if (nextLevel.equals("close")) {
            endLogic.finalStageReached();
        }
        level.gameView.setVisible(true);
        level.mapView.setMinimized(true);
        Magazine magazine = reloadLogic.getMagazine();
        int warheadScore = magazine.getWarheadsLeft(0) * 25 + magazine.getWarheadsLeft(1) * 200 + magazine.getWarheadsLeft(2) * 400;
        int chargeScore = magazine.getChargesLeft() * 20;
        endLogic.setScores(score, warheadScore, chargeScore);
        scoreManager.setScore(score + warheadScore + chargeScore, currentLevel);
        scoreDisplay.setVisible(false);
        windDisplay.setVisible(false);
        messenger.translate(0, -32);
    }
    
    private void endLevel() {
        if (endLogic.hasResolution()) {
            if (endLogic.isActive()) {
                endLogic.deactivate();
                next(endLogic.getNext());
            }
            return;
        }
        if (!endLogic.isActive()) {
            initEndLevelLogic();
        }
        endLogic.update(deltatimeMillis);
        shakeScreen();        
        animateScore();
    }
    
    private boolean historyDebouncer;    
    private void linkMapscreenToSolvers() {
        if (mortarLogic.hasActiveSolvers()) {
            level.mapScreen.setByHistory(mortarLogic.history);
            historyDebouncer = true;
        } else {
            if (historyDebouncer) {
                level.mapScreen.setByHistory(mortarLogic.history);
                historyDebouncer = false;
            }
            level.mapScreen.freeProjectiles(0);
        }        
    }
    
    private void updateGUI() {
        if (!guiInitialized) {
            return;
        }
        shakeScreen();        
        animateScore();
        
        float speedModifier = getSpeedModifier();
        sharedControls(speedModifier);
        gameViewLogic(speedModifier);
        mapViewLogic(speedModifier);

        linkMapscreenToSolvers();
    }
    
    private void endConditions() {
        if (targetsLeft <= 0) {
            endLevel();
        } else if (lost) {
            endLevel();
        }
        if (reloadLogic.isEmpty()) {
            if (!notifiedOfEnding) {
                notifiedOfEnding = true;
                setMessage("Ampumatarvikkeet loppu!");
            }
            if (!mortarLogic.hasActiveSolvers() && targetsLeft > 0) {
                lost = true;
            }
        }
    }
    
    private boolean notifiedOfEnding = false;
    private void updateLogic() {
        if (hasNext) {
            hasNext = false;
            spawnNext();
            return;
        }
        
        if (guiInitialized && inputs == null) {
            return;
        }
        
        mortarLogic.solve(deltatimeMillis);
        updateGUI();
        updateTargets();
        getHits();
        endConditions();
    }
    
    /**
     * Logiikan julkinen päivitysmetodi. Hakee piirtoon kuluneen ajan ennen sisäisen päivitysmetodin kutsumista.
     */
    public void update() {
        deltatimeMillis = timing.getDeltatimeMillis();
        updateLogic();
    }
    
    /**
     * Logiikan julkinen päivitysmetodi manuaaliseen kutsuun. Vastaanottaa piirtoon kuluneen ajan, ja on tarkoitettu testeistä ajettavaksi.
     * @param dtMillis Piirtoon kulunut aika millisekunneissa. Yleensä 16 (60fps).
     */
    public void update(double dtMillis) {
        deltatimeMillis = dtMillis;
        updateLogic();
    }

    @Override
    public void setParent(LogicInterface parent) {
        this.parent = parent;
    }
}
