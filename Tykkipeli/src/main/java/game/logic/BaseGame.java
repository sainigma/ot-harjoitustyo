/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic;

import game.logic.controllers.*;
import game.components.Level;
import game.components.Text;
import game.components.templates.ScreenShaker;
import game.utils.InputManager;
import game.graphics.Renderer;
import game.utils.JSONLoader;
import game.utils.PID;
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
    LogicInterface parent = null;
    
    private boolean guiInitialized = false;
    public MortarLogic mortarLogic;
    public ReloadLogic reloadLogic;
    public ArrayList<TargetLogic> targets;
    public Level level = null;
    
    private int targetsLeft = 0;
    
    public ScreenShaker screenShaker;
    private boolean gunMovementActive = true;
    
    private boolean initialized = false;
    
    private long lastTime;
    private double deltatimeMillis;
    
    private int score = 0;
    private float scoreTarget;
    private float tempScore;
    private Text scoreDisplay;
    private PID scorePID = new PID(0.05f, 0, 0.5f, 1f);
    
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
    
    /**
     * Luokan alustusmetodi joka kutsutaan konstruktorista.
     * Kutsuu lapsiobjektien luonnin, hakee kentän parametrit tiedostosta, sekä asettaa em. parametrit lapsiobjekteihin.
     * Lisäksi kutsuu maaliobjektien spawnaamisen kenttäparametrien perusteella.
     * @param name 
     */
    private void loadLevel(String name) {
        score = 0;
        targetsLeft = 0;
        targets = new ArrayList<>();
        spawnLevel();
        spawnMessengers();
        spawnObjects();
        JSONObject levelData = new JSONLoader("assets/levels/").read(name);
        JSONObject magazine = levelData.getJSONObject("magazine");
        reloadLogic.setMagazine(
                magazine.getInt("light"),
                magazine.getInt("medium"),
                magazine.getInt("heavy"),
                magazine.getInt("charges")
        );
        spawnTargets(levelData.getJSONArray("ships"));
        lastTime = System.nanoTime() / 1000000;
    }
    
    private void spawnMessengers() {
        scoreDisplay = new Text();
        messenger = new Text();
        messenger.translate(0, 32);
        level.gameView.append(scoreDisplay);
        level.gameView.append(messenger);
        level.mapScreen.map.append(scoreDisplay);
        level.mapScreen.map.append(messenger);
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
        reloadLogic.setMessenger(messenger);
    }
    
    /**
     * Luo maaliobjektit kenttäparametreista saadun listan perusteella.
     * @param ships Kenttäparametrien ships-avaimella määritelty lista
     */
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
     * Vastaanottaa ja asettaa itselleen syötteidenkuuntelijan
     * @param inputs 
     */
    public void setInputManager(InputManager inputs) {
        this.inputs = inputs;
        reloadLogic.setInputManager(this.inputs);
    }
    
    /**
     * Ruuduntärisytysmetodi. Hakee tärisytyskertoimen tykin animaatioista, sekä aktivoi itse tärinän jos aktiivisten solvereiden määrä on laskenut.
     */
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
    
    /**
     * Nopeuttaa/hidastaa asioita ruudunpäivitysnopeuden sekä nopeutus/hidastusnappien perusteella.
     * @return 
     */
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
    
    /**
     * Tykin liikutuskontrollit vaakasuunnassa, aina voimassa.
     * @param speedModifier 
     */
    private void traverse(float speedModifier) {
        if (reloadLogic.isMovementBlocked() && level.mapScreen.isMinimized()) {
            return;
        }
        float traversalSpeed = 0.05f;
        if (inputs.keyDown("traverse right")) {
            level.mortar.addToTraverseTarget(-traversalSpeed * speedModifier);
        } else if (inputs.keyDown("traverse left")) {
            level.mortar.addToTraverseTarget(traversalSpeed * speedModifier);
        }
        level.mapScreen.setTraversal(-level.mortar.getTraversal());
    }
    
    /**
     * Pelin kontrollit päänäkymän ollessa aktiivisena. Nostaa/siirtää tykkiä jos tulitusanimaatio ei ole voimassa.
     * @param speedModifier ruudunpäivitysnopeudesta ja nopeutusnapeista riippuva kerroin
     */
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
    
    /**
     * Pelin kontrollit karttanäkymän ollessa aktiivinen.
     * @param speedModifier 
     */
    private void mapControls(float speedModifier) {
        traverse(speedModifier);
        if (inputs.keyDownOnce("reload")) {
            if (reloadLogic.getProjectile() == null) {
                toggleView();
                reloadLogic.startReload();                
            }
        }
    }
    
    /**
     * Pelin jaetut kontrollit, eli kontrollit jotka ovat voimassa näkymästä riippumatta.
     * @param speedModifier 
     */
    private void sharedControls(float speedModifier) {
        if (inputs.keyDownOnce("fire")) {
            fire();
        }
        if (inputs.keyDownOnce("toggle map") || (inputs.keyDownOnce("cancel") && !level.mapView.isMinimized())) {
            toggleView();
        }
        if (inputs.keyDownOnce("quit")) {
            renderer.close();
        }
        rotateMap(speedModifier);
    }
    
    /**
     * Kontrollit mini- ja pääkartan pyörittämiseen.
     * @param speedModifier 
     */
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
    
    private void getDeltatimeMillis() {
        long time = System.nanoTime() / 1000000;
        deltatimeMillis = (double) (time - lastTime);
        lastTime = time;
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
    
    /**
     * Päivittää maaliobjektien sijainnin ja jakaa sen karttanäkymälle.
     */
    private void updateTargets() {
        for (TargetLogic target : targets) {
            target.update(deltatimeMillis);
            level.mapScreen.updateTarget(target);
        }
    }
    
    /**
     * Tarkistaa onko tykkilogiikalla osumia valmiina. Jos on, vastaanottaa ne ja lähettää ne eteenpäin tarkastettaviksi. Osuman vastaanottaminen poistaa osuman tykkilogiikasta.
     */
    private void getHits() {
        if (mortarLogic.hasHits()) {
            ArrayList<Statistic> hits = new ArrayList<>();
            while (mortarLogic.hasHits()) {
                hits.add(mortarLogic.getHit());
            }
            checkHits(hits);
        }
    }
    /**
     * Vertailee jokaista vastaanotettua osumaa jokaiselle maalille.
     * Maksimivahinko tarkastetaan projektiilin painon perusteella, minimissään se on 150.
     * For-for, mutta käytännössä O(n) koska osumia kerrallaan 1-2. Jatkototeutuksessa olisi hyvä korvata esim. sweep&prune algoritmilla.
     * @param hits 
     */
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
        String scoreString = Integer.toString(score);
        for (int i = scoreString.length(); i < 9; i++) {
            message += "0";
        }
        message += scoreString;
        scoreDisplay.setContent(message);
    }
    
    private void addScore(Statistic hit, TargetLogic target, boolean destroyed) {
        float mass = hit.getMass();
        String targetType = target.getName();
        float elevation = 0;
        float baseModifier = destroyed ? 1 : 0.5f;
        switch (targetType) {
            case "ironclad":
                baseModifier *= 2f;
                break;
            case "lineship":
                baseModifier *= 1.7f;
                break;
            case "windjammer":
                baseModifier *= 1.5f;
                break;
            default:
                break;
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
        setScoreDisplay((int)tempScore);
        if (scoreTarget - tempScore < 1f) {
            scorePID.deactivate();
            scoreTarget = -1f;
            setScoreDisplay(score);
        }
    }
    
    private void setMessage(String message) {
        messenger.setContent(message);
    }
    
    /**
     * Osuman tarkastelu yksittäiselle maalille. 
     * Tarkastaa etäisyyden osumasta maaliin, ja tarjoilee mallille vahinkoa jos osuma on maksimietäisyyttä pienempi.
     * Vahingon määrä laskee etäisyyden neliönä, maksimietäisyydessä sen ollessa 0.
     * Tarkastus pysäytetään jos maali ei ole aktiivinen.
     * @param target
     * @param hitPosition
     * @param maxDamage
     * @param maxDistance 
     */
    private void damageTarget(TargetLogic target, Statistic hit, double maxDamage, double maxDistance) {
        if (target.getHealth() < 0f) {
            return;
        }
        Vector3d hitPosition = hit.getLastPosition();
        Vector3d targetPos = target.getPosition();
        double distance = new Vector3d(hitPosition.x - targetPos.x, hitPosition.z - targetPos.y, 0f).magnitude();
        double damageFactor = distance / maxDistance;
        if (damageFactor < 1f) {
            double damage = maxDamage * (1 - Math.pow(damageFactor, 2));
            target.reduceHealth((float) damage);
            String message = "Osuma! " + (int) damage + " pistettä vahinkoa";
            if (target.isSinking()) {
                targetsLeft -= 1;
                addScore(hit, target, true);
                message += "\n" + target.getName() + " uppoaa";
                if (targetsLeft > 0) {
                    message += "\nkohteita jäljellä " + Integer.toString(targetsLeft);
                }
            } else {
                addScore(hit, target, false);
            }
            setMessage(message);
        }
    }
    /**
     * Palauttaa jäljelläolevien maalien määrän.
     * @return 
     */
    public int getTargetsLeft() {
        return targetsLeft;
    }
    
    /**
     * Keskeneräinen metodi, lopullisessa versiossa tarjoilee käyttäjälle käyttöliittymän seuraavaan kenttään siirtymiseen tai pelin lopettamiseen.
     */
    private void endLevel() {
        //System.out.println("No targets left, ending game");

    }
    
    /**
     * Päivittää solverien muutokset karttanäkymälle.
     */
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
    
    /**
     * Käyttöliittymästä riippuva päivityslogiikan osa.
     */
    private void updateGUI() {
        if (!guiInitialized) {
            return;
        }
        float speedModifier = getSpeedModifier();
        sharedControls(speedModifier);
        gameViewLogic(speedModifier);
        mapViewLogic(speedModifier);

        shakeScreen();        
        animateScore();
        linkMapscreenToSolvers();
    }
    
    /**
     * Logiikan sisäinen pääpäivitysmetodi. Kutsutaan julkisista update-metodeista.
     */
    private void _update() {
        if (guiInitialized && inputs == null) {
            return;
        }
        
        mortarLogic.solve(deltatimeMillis);
        updateGUI();
        updateTargets();
        getHits();
        if (targetsLeft <= 0) {
            endLevel();
        }
    }
    
    /**
     * Logiikan julkinen päivitysmetodi. Hakee piirtoon kuluneen ajan ennen sisäisen päivitysmetodin kutsumista.
     */
    public void update() {
        getDeltatimeMillis();
        _update();
    }
    
    /**
     * Logiikan julkinen päivitysmetodi manuaaliseen kutsuun. Vastaanottaa piirtoon kuluneen ajan, ja on tarkoitettu testeistä ajettavaksi.
     * @param dtMillis Piirtoon kulunut aika millisekunneissa. Yleensä 16 (60fps).
     */
    public void update(double dtMillis) {
        deltatimeMillis = dtMillis;
        _update();
    }

    @Override
    public void setParent(LogicInterface parent) {
        this.parent = parent;
    }
}
