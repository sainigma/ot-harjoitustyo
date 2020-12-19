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

import game.components.Text;
import game.utils.InputManager;
import game.components.templates.Mortar;
import game.components.templates.ReloadScreen;

/**
 * Logiikka ja käyttöliittymäohjaaja tykin lataamiselle lopetukselle, käyttöliittymäsekvenssin seurauksena tykin logiikka vastaanottaa ammuksen.
 * @author Kari Suominen
 */
public class ReloadLogic {
    private Magazine magazine;
    private String[] warheads = {"light", "medium", "heavy"};
    private int[] cartouches = {3, 2, 1};
    
    private int reloadIndex = 0;
    private int prevReloadIndex = 0;
    private boolean allowRoll = true;
    private int prevWarhead = 0;
    private int prevCartouche = 2;
    private boolean firstSelected = false;
    
    private boolean reloadUpdate = true;
    private boolean reloadFinished = true;
    private boolean blockMovement = false;
    
    private Text messenger;
    
    private Projectile currentProjectile;
    private InputManager inputs = null;
    private MortarLogic mortarLogic;
    private ReloadScreen reloadScreen;
    private Mortar mortar;
    
    /**
     * Rakentaja, vastaanottaa ampumisen logiikan, tykkipeliobjektin ja latausruudun käyttöliittymän.
     * @param mortarLogic
     * @param mortar
     * @param reloadScreen
     */
    public ReloadLogic(MortarLogic mortarLogic, Mortar mortar, ReloadScreen reloadScreen) {
        this.mortarLogic = mortarLogic;
        this.reloadScreen = reloadScreen;
        this.mortar = mortar;
        this.magazine = new Magazine(12, 6, 3, 54);
    }
    /**
     * Vastaanottaa tekstiobjektin johon lähettää viestejä.
     * @param messenger 
     */
    public void setMessenger(Text messenger) {
        this.messenger = messenger;
    }
    
    private void setMessage(String message) {
        if (messenger == null) {
            return;
        }
        messenger.setContent(message);
    }
    
    private void displayGrenadeStatus() {
        setMessage("Valitse kranaatti\n" + magazine.warHeadStatus());
    }
    
    private void displayChargeStatus() {
        setMessage("Valitse latauspanos\n" + magazine.chargeStatus());
    }
    /**
     * Läpikulkumetodi makasiinin ammustilanteen asettamiseen.
     * @param light
     * @param medium
     * @param heavy
     * @param charges 
     */
    public void setMagazine(int light, int medium, int heavy, int charges) {
        this.magazine = new Magazine(light, medium, heavy, charges);
    }
    /**
     * Palauttaa ammusvaraston.
     * @return 
     */
    public Magazine getMagazine() {
        return magazine;
    }
    private int selectorDirection;
    private void reloadSelector(int length) {
        if (inputs.keyDownOnce("left")) {
            reloadIndex -= 1;
            reloadUpdate = true;
            selectorDirection = -1;
        } else if (inputs.keyDownOnce("right")) {
            reloadIndex += 1;
            reloadUpdate = true;
            selectorDirection = 1;
        }
        if (allowRoll) {
            rollReloadIndex(length);
        } else {
            if (reloadIndex >= length) {
                reloadIndex = length - 1;
            } else if (reloadIndex < 0) {
                reloadIndex = 0;
            }
        }
    }
    
    private void rollReloadIndex(int length) {
        if (reloadIndex >= length) {
            reloadIndex = 0;
        } else if (reloadIndex < 0) {
            reloadIndex = length - 1;
        }                    
    }
    
    private void activateSelector() {
        if (!firstSelected && (inputs.keyDownOnce("left") || inputs.keyDownOnce("right"))) {
            firstSelected = true;
            reloadScreen.setWarhead(reloadIndex);
        }
    }
    
    private void chooseProjectile() {
        if (reloadUpdate) {
            while (!magazine.warheadAvailable(reloadIndex)) {
                reloadIndex += selectorDirection;
                rollReloadIndex(warheads.length);
            }
            reloadUpdate = false;
            reloadScreen.setWarhead(reloadIndex);
        }
        activateSelector();
        reloadSelector(warheads.length);
        if (inputs.keyDownOnce("ok") && firstSelected) {
            confirm(false);
        }
        if (inputs.keyDownOnce("previous")) {
            cancel(true);
        }
    }
    
    private void confirm(boolean finishReload) {
        if (!finishReload) {
            currentProjectile = new Projectile(magazine.getWarhead(reloadIndex), 0);
            if (!currentProjectile.initOk()) {
                currentProjectile = null;
            }
            prevWarhead = reloadIndex;
            reloadIndex = prevCartouche;
            allowRoll = false;
            reloadUpdate = true;
        } else {
            currentProjectile.addCartouches(magazine.getCartouche(cartouches[reloadIndex]));
            reloadScreen.exit();
            prevCartouche = reloadIndex;
            reload();
        }
    }
    
    private void cancel(boolean cancelReload) {
        if (cancelReload) {
            setMessage("");
            reset();
        } else {
            prevCartouche = reloadIndex;
            magazine.addWarhead(prevWarhead);
            reloadScreen.setCharges(0);
            currentProjectile = null;
            allowRoll = true;
            displayGrenadeStatus();            
        }
    }
    
    private void chooseCartouches() {
        if (reloadUpdate) {
            while (cartouches[reloadIndex] > magazine.cartouchesLeft() && reloadIndex < 3) {
                reloadIndex++;
            }
            reloadUpdate = false;
            reloadScreen.setCharges(3 - reloadIndex);
            displayChargeStatus();       
        }
        reloadSelector(cartouches.length);
        
        if (inputs.keyDownOnce("ok")) {
            confirm(true);
        }
        if (inputs.keyDownOnce("previous")) {
            cancel(false);
        }
    }
    
    private void reload() {
        if (mortarLogic.addProjectile(currentProjectile)) {
            setMessage("Tulivalmis!");
            System.out.println(magazine);
            reloadFinished = true;
            blockMovement = false;
            mortar.setInclinometer(true);
        }
    }
    /**
     * Palauttaa toden jos lataussekvenssi on päättynyt.
     * @return 
     */
    public boolean isReloadFinished() {
        return reloadFinished;
    }
    /**
     * Nollaa aktiivisen projektiilin.
     */
    public void resetProjectile() {
        currentProjectile = null;
    }
    /**
     * Palauttaa aktiivisen projektiilin.
     * @return 
     */
    public Projectile getProjectile() {
        return currentProjectile;
    }
    /**
     * Asettaa näppäimistökuuntelijan luokalle.
     * @param inputs 
     */
    public void setInputManager(InputManager inputs) {
        this.inputs = inputs;
    }
    /**
     * Palauttaa toden jos logiikan halutaan estävän syötteet muissa logiikoissa.
     * @return 
     */
    public boolean isMovementBlocked() {
        return blockMovement;
    }
    /**
     * Palauttaa toden jos makasiini on tyhjä ammuksista tai panoksista.
     * @return 
     */
    public boolean isEmpty() {
        return magazine.isEmpty();
    }
    private boolean clearForReload() {
        if (currentProjectile != null && reloadFinished) {
            setMessage("Tykki on jo ladattu!");
            return false;
        } else if (!reloadFinished) {
            return false;
        }
        if (magazine.isEmpty()) {
            blockMovement = false;
            return false;
        }
        return true;
    }
    /**
     * Initioi lataussekvenssin ja aktivoi latauksen käyttöliittymän.
     */
    public void startReload() {
        if (clearForReload()) {
            reloadScreen.enter();
            reloadScreen.setCharges(0);
            reloadScreen.setWarhead(3);

            firstSelected = false;
            allowRoll = true;
            displayGrenadeStatus();
            reloadUpdate = false;
            blockMovement = true;
            reloadFinished = false;
            mortar.setElevationTarget(0f);
            mortar.setInclinometer(false);

            reloadIndex = prevWarhead;   
        }
    }
    /**
     * Pääpäivitysmetodi luokalle, aktivoi luokkaa näppäimistökuuntelijan tilan perusteella.
     */
    public void reloadControls() {
        if (inputs.keyDownOnce("reload")) {
            startReload();
        } else if (!blockMovement) {
            return;
        }
        if (currentProjectile == null) {
            chooseProjectile();
        } else if (!reloadFinished) {
            chooseCartouches();
        }
    }
    /**
     * Nollaa lataussekvenssin ja sammuttaa käyttöliittymän.
     */
    public void reset() {
        blockMovement = false;
        currentProjectile = null;
        reloadFinished = true;
        reloadScreen.exit();
    }
    /**
     * Asettaa logiikalle projektiilin makasiinista.
     * @param warhead
     * @param cartouches 
     */
    public void setProjectile(int warhead, int cartouches) {
        float weight = magazine.getWarhead(warhead);
        cartouches = magazine.getCartouche(cartouches);
        if (weight > 0 && cartouches > 0) {
            currentProjectile = new Projectile(weight, cartouches);            
        }
    }
}
