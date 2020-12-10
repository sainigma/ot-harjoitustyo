/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic;

import game.logic.controllers.*;
import game.components.Level;
import game.components.templates.ScreenShaker;
import game.utils.InputManager;
import game.graphics.Renderer;
import game.utils.JSONLoader;
import game.utils.Vector3d;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Pääluokka pelin logiikalle
 * @author suominka
 */
public class BaseGame {
    private InputManager inputs = null;
    private Renderer renderer = null;
    
    private boolean guiInitialized = false;
    public MortarLogic mortarLogic;
    public ReloadLogic reloadLogic;
    public ArrayList<TargetLogic> targets;
    public Level level = null;
    
    private int targetsLeft = 0;
    
    public ScreenShaker screenShaker;
    private boolean gunMovementActive = true;
    
    private long lastTime;
    private double deltatimeMillis;
    
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
    public void setRenderer(Renderer renderer) {
        if (renderer != null) {
            renderer.appendToRenderQueue(level);
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
        targetsLeft = 0;
        targets = new ArrayList<>();
        spawnLevel();
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
        if (!gunMovementActive || level.mortar.animator.isPlaying("mortar/firing")) {
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
            Vector3d hitPosition = hit.getLastPosition();
            
            for (TargetLogic target : targets) {
                damageTarget(target, hitPosition, maxDamage, maxDistance);
            }
        }
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
    private void damageTarget(TargetLogic target, Vector3d hitPosition, double maxDamage, double maxDistance) {
        if (target.getHealth() < 0f) {
            return;
        }
        Vector3d targetPos = target.getPosition();
        double distance = new Vector3d(hitPosition.x - targetPos.x, hitPosition.z - targetPos.y, 0f).magnitude();
        double damageFactor = distance / maxDistance;
        if (damageFactor < 1f) {
            double damage = maxDamage * (1 - Math.pow(damageFactor, 2));
            target.reduceHealth((float) damage);
            System.out.println("Target hit, inflicted " + damage + " damage");
            if (target.isSinking()) {
                targetsLeft -= 1;
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
}
