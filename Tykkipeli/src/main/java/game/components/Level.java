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

import game.components.templates.BackgroundCoast;
import game.components.templates.EndScreen;
import game.components.templates.MapScreen;
import game.components.templates.ViewPort;
import game.components.templates.Mortar;
import game.components.templates.ReloadScreen;
/**
 * GameObject luokan implementaatio pelin päänäkymän hallitsemiseen. Sekä kartta- että pelinäkymän juuri.
 * @author Kari Suominen
 */
public class Level extends GameObject {

    /**
     * Tykkinäkymä.
     */
    public ViewPort gameView;

    /**
     * Karttanäkymä.
     */
    public ViewPort mapView;

    /**
     * Tykki.
     */
    public Mortar mortar;

    /**
     * Karttanäkymän käyttöliittymä.
     */
    public MapScreen mapScreen;

    /**
     * Tykkinäkymän tykin latauksen käyttöliittymä.
     */
    public ReloadScreen reloadScreen;

    /**
     * Tykkinäkymän lopetusruudun käyttöliittymä.
     */
    public EndScreen endScreen;
    private GameObject background;
    
    private float viewportScale = 720f / 1080f;    

    /**
     * Rakentaja, alustaa peliobjektin sekä näkymät/käyttöliittymät, tykin ja taustan 
     * @param name
     */
    public Level(String name) {
        super(name);
        init();
    }
 
    private void init() {
        initGameView();
        initMapView();
        
        mortar.setTrueElevation(0f);
        gameView.setVisible(true);
        mapView.setMinimized(true);
    }
    
    private void initGameView() {
        gameView = new ViewPort("game");        
        mortar = new Mortar("mortar");
        background = new BackgroundCoast();
        reloadScreen = new ReloadScreen("reloadScreen");
        endScreen = new EndScreen("endScreen");
        
        endScreen.setVisible(false);
        
        gameView.append(mortar);
        gameView.append(background);
        gameView.append(reloadScreen);
        gameView.append(endScreen);
        append(gameView);
    }
    
    private void initMapView() {
        mapView = new ViewPort("map");
        mapScreen = new MapScreen("mapScreen");
        mapView.append(mapScreen);
        append(mapView);        
    }
    
    /**
     * Poistaa nykyisen karttanäkymän piirtojonosta ja alustaa uuden tilalle.
     * Kenttien vaihtamiseen tarkoitettu metodi.
     */
    public void reset() {
        remove(mapView);
        initMapView();
    }
    
    /**
     * Päivitysmetodi, animoi taustan tykin suunnan perusteella.
     */
    @Override
    public void update() {
        background.setRotation(mortar.getTraversal());
    }
}
