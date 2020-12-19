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

import java.util.ArrayList;
import game.graphics.primitives.Sprite;
import game.graphics.TextureLoader;
import game.utils.Vector3d;

/**
 * Abstrakti luokka pelikappaleiden manipulointiin ja piirtämiseen.
 * Sisältää vain implementoitujen luokkien jakamat metodit, eli
 * objektin alustuksen, ominaisuuksien propagoinnin sekä kappaleen piirron.
 * 
 * @author Kari Suominen
 */

public abstract class GameObject implements DrawCallInterface {

    /**
     * Peliobjektin lapsiobjektit.
     */
    public ArrayList<DrawCallInterface> children = new ArrayList<>();
    private Sprite sprite;
    
    private boolean initialized = false;
    private boolean visible = true;
    private boolean active = true;
    private boolean hasUpdated = true;
    
    private String path;
    private String name;
    private int[] crop = { -1, -1};
    private float[] texOffset = {0, 0};
    private float[][] vertexOffset = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};
    private float scale = 1;
    private TextureLoader texLoader = null;
    
    private Vector3d origin = new Vector3d(0);

    /**
     * Paikallinen sijainti.
     */
    public Vector3d localPosition = new Vector3d(0);

    /**
     * Paikallinen kiertymä.
     */
    public Vector3d localRotation = new Vector3d(0);

    /**
     * Peritty sijainti.
     */
    public Vector3d globalPosition = new Vector3d(0);

    /**
     * Peritty kiertymä.
     */
    public Vector3d globalRotation = new Vector3d(0);
    
    /**
     * Osittainen konstruktori tyhjälle peliobjektille, eli objektille joka ei itsessään piirrä mitään,
     * mutta voi toimia esimerkiksi keskipisteenä lapsiobjekteille.
     * @param name 
     */
    public GameObject(String name) {
        this.name = name;
        this.path = null;
        this.initialized = true; //empty
    }
    /**
     * Täydellinen konstruktori
     * @param name
     * @param path Polku pelikappaleen tekstuuriin, muotoa peliobjekti/tekstuuri.png
     * @param origin Kappaleen keskipiste, määritellään erikseen jos se on muu kuin tekstuurin vasen ylänurkka
     */
    public GameObject(String name, String path, Vector3d origin) {
        this.origin = origin;
        this.name = name;
        this.path = path;
        this.scale = 720f / 1080f;
        init();
    }
    
    /**
     * Täydellinen konstruktori
     * @param name
     * @param path Polku pelikappaleen tekstuuriin, muotoa peliobjekti/tekstuuri.png
     * @param origin Kappaleen keskipiste, määritellään erikseen jos se on muu kuin tekstuurin vasen ylänurkka
     * @param scale Renderöijän skaala, resoluution vaihtamiseen
     */
    public GameObject(String name, String path, Vector3d origin, float scale) {
        this.origin = origin;
        this.name = name;
        this.path = path;
        this.scale = scale;
        init();
    }
    private void init() {
    }

    /**
     * Palauttaa toden jos objekti on aktivoitu.
     * @return
     */
    public boolean isActive() {
        return active;
    }
    /**
     * Asettaa peliobjektin aktiivisuuden. Epäaktiiviselle peliobjektille ei päivitetä logiikkaa eikä sitä myöskään piirretä.
     * @param newState 
     */
    public void setActive(boolean newState) {
        active = newState;
    }
    /**
     * Asettaa peliobjektin näkyvyyden. Suorittaa pelilogiikkaa näkymättömänäkin.
     * @param newState 
     */
    public void setVisible(boolean newState) {
        visible = newState;
    }
    /**
     * Palauttaa toden jos objekti on näkyvä.
     * @return 
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Kytkee näkyvyyden päälle/pois.
     */
    public void toggleVisible() {
        visible = !visible;
    }

    /**
     * Kytkee kappaleen aktiivisuuden päälle/pois.
     */
    public void toggle() {
        active = !active;
    }
    
    /**
     * Spriten quadin venyttämiseen, normaalisti quad on 1:1 neliö.
     * Tuottaa PSX -tyylisiä ongelmia jos lopullinen quad on puolisuunnikas.
     * @param topLeft
     * @param bottomLeft
     * @param topRight
     * @param bottomRight 
     */
    public void setVertexOffset(float[] topLeft, float[] bottomLeft, float[] topRight, float[] bottomRight) {
        vertexOffset[0] = topLeft;
        vertexOffset[1] = bottomLeft;
        vertexOffset[2] = topRight;
        vertexOffset[3] = bottomRight;
        if (sprite != null) {
            sprite.setVertexOffset(vertexOffset);            
        }
    }
    /**
     * Asettaa peliobjektille tekstuurinlataajan, joka vaatii renderöijän alustumisen.
     * @param loader 
     */
    public void setTextureLoader(TextureLoader loader) {
        texLoader = loader;
        for (DrawCallInterface child : children) {
            child.setTextureLoader(loader);
        }
        load();
    }
    /**
     * Tyhjä metodi implementoivien luokkien käyttöön. Kevyeen animointiin tarkoitettu.
     * @param target ohjausavain
     * @param value ohjausarvo
     */
    public void drive(String target, double value) {
    }
    /**
     * Rajaa spriten pienemmäksi
     * @param x
     * @param y 
     */
    public void setCrop(int x, int y) {
        crop[0] = x;
        crop[1] = y;
        if (sprite != null) {
            sprite.setCrop(crop);
        }
    }

    /**
     * Asettaa päivittyneisyyden tilan, käytetään ominaisuuksien propagointiin lapsiobjekteille.
     * @param newState
     */
    public void setHasUpdated(boolean newState) {
        hasUpdated = newState;
    }
    /**
     * Asettaa paikallisen sijainnin.
     * @param position
     */
    public void setPosition(Vector3d position) {
        localPosition.x = position.x;
        localPosition.y = position.y;
        localPosition.z = position.z;
        hasUpdated = true;
    }
    /**
     * Asettaa paikallisen kiertymän z-akselille, kaksiulotteiseen manipulointiin.
     * @param r asteissa
     */
    public void setRotation(float r) {
        localRotation.z = r;
        hasUpdated = true;
    }
    /**
     * Asettaa paikallisen kiertymän.
     * @param rotation 
     */
    public void setRotation(Vector3d rotation) {
        localRotation.set(rotation);
    }
    /**
     * Siirtää kappaletta z-akselissa. 2D tilassa z-koordinaattia käytetään piirtojärjestyksen määrittelyyn.
     * @param z Suuri arvo tarkoittaa lähellä kameraa, pieni arvo kaukana kamerasta.
     */
    public void setDepth(float z) {
        localPosition.z = z;
    }
    /**
     * Liikuttaa peliobjektia lokaalissa 2d-avaruudessa
     * @param x
     * @param y 
     */
    public void translate(float x, float y) {
        localPosition.x += x;
        localPosition.y += y;
        hasUpdated = true;
    }
    /**
     * Liikuttaa peliobjektia lokaalissa 3d-avaruudessa
     * @param x
     * @param y
     * @param z 
     */
    public void translate(float x, float y, float z) {
        translate(x, y);
        localPosition.z += z;
    }
    
    /**
     * Pyörittää peliobjektia z-akselin ympäri, tarkoitettu 2d-avaruuteen.
     * @param rot 
     */
    public void rotate(float rot) {
        localRotation.z += rot;
        hasUpdated = true;
    }
    
    /**
     * Muokkaa tekstuurin piirron aloituskohtaa, kätevä scrollerien tekemiseen
     * @param x
     * @param y 
     */
    public void setTexOffset(float x, float y) {
        texOffset[0] = x;
        texOffset[1] = y;
        if (sprite != null) {
            sprite.setTexOffset(texOffset);
        }
    }
    /**
     * Lisää kappaleen lapseksi, eli lisää sen GameObjectin piirtojonoon.
     * @param child 
     */
    public void append(DrawCallInterface child) {
        if (texLoader != null) {
            child.setTextureLoader(texLoader);
        }
        children.add(child);
    }

    /**
     *
     * @return
     */
    public TextureLoader getTextureLoader() {
        return texLoader;
    }
    
    /**
     *
     * @param child
     */
    public void append(GameObject child) {
        append((DrawCallInterface) child);
    }
    /**
     * Poistaa kappaleen lapsista, eli poistaa sen GameObjectin piirtojonosta.
     * @param child 
     */
    public void remove(GameObject child) {
        children.remove(child);
    }
    /**
     * Tyhjä metodi implementoivien luokkien käyttöön.
     * Tarkoitettu pelilogiikan päivittämiseen, kutsutaan jokaisen piirron yhteydessä.
     */
    public void update() {
    }
    private long lastTime = System.nanoTime();
    public double getDeltatime() {
        long time = System.nanoTime();
        double dt = (double) (time - lastTime) / 1000000;
        lastTime = time;
        return dt;
    }
    /**
     * Jakaa GameObjectin perityn ja sisäisen muunnoksen yhteenlaskettuna lapsiobjekteille.
     */
    public void propagate() {
        if (!hasUpdated) {
            return;
        }
        for (DrawCallInterface child : children) {
            child.setUpdated(true);
            child.setGlobalPosition(globalPosition.add(localPosition));
            child.setGlobalRotation(globalRotation.add(localRotation));
        }
        hasUpdated = false;
    }
    /**
     * Spriten alustusmetodi. Kutsutaan GameObjectin piirtometodista jos spriteä
     * ei olla alustettu. Ei tee mitään jos GameObjectilta puuttuu sprite.
     */
    public void load() {
        if (path != null) {
            sprite = new Sprite(texLoader, path, origin, scale);
            sprite.setTexOffset(texOffset);
            sprite.setVertexOffset(vertexOffset);
            sprite.setCrop(crop);
        }
        for (DrawCallInterface child : children) {
            child.load();
        }
        initialized = true;
    }
    /**
     * Sisäinen piirtometodi. Kutsuu rekursiivisesti päivityksen lapsille ja n-lapsille, päivittää lopuksi oman spriten muunnoksen ja kutsuu piirron.
     */
    private void drawCall() {
        if (!visible) {
            for (DrawCallInterface child : children) {
                child.update();
            }
            return;
        }
        for (DrawCallInterface child : children) {
            child.draw();
        }
        if (sprite != null && visible) {
            sprite.setTransforms(localPosition, localRotation, globalPosition, globalRotation);
            sprite.draw();
        }
    }
    /**
     * Julkinen piirtometodi. Kutsuu spriten alustuksen, ja pelilogiikan päivityksen, kutsuu sisäisen piirron.
     */
    public void draw() {
        if (!initialized) {
            if (texLoader != null) {
                load();                
            } else {
                update();
                propagate();
            }
        } else if (active) {
            update();
            propagate();
            drawCall();                
        }
    }
    /**
     * Palauttaa uuden GameObjektin jolla on alkuperäisen instanssin alkuparametrit sekä muunnos.
     * @return 
     */
    @Override
    public GameObject clone() {
        GameObject ret;
        if (path != null) {
            ret = new GameObject(name, path, origin, scale) { };
        } else {
            ret = new GameObject(name) { };
        }
        ret.setPosition(getTransform());
        return ret;
    }
    
    /**
     * Palauttaa GameObjectin muunnoksen 2d-avaruudessa
     * @return 
     */
    public Vector3d getTransform() {
        return new Vector3d(localPosition.x, localPosition.y, localRotation.z);
    }

    /**
     * Palauttaa perityn sijainnin.
     * @return
     */
    public Vector3d getGlobalPosition() {
        return globalPosition;
    }

    /**
     * Palauttaa paikallisen sijainnin.
     * @return
     */
    public Vector3d getPosition() {
        return localPosition;
    }

    /**
     * Palauttaa paikallisen kiertymän.
     * @return
     */
    public Vector3d getRotation() {
        return localRotation;
    }

    /**
     * Interfacemetodi, tyhjä.
     */
    public void toggleMinimized() {
    }
    /**
     * Interfacemetodi, tyhjä.
     */    
    public boolean isMinimized() {
        return false;
    }
    /**
     * Palauttaa toden jos objekti on ladattu muistiin.
     * @return 
     */
    public boolean isInitialized() {
        return initialized;
    }
    /**
     * Pienentää peliobjektin, varsinainen toteutus implementoivien luokkien harteilla.
     * @param minimized 
     */
    public void setMinimized(boolean minimized) {
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
     * Asettaa päivittyneisyyden tilan, käytetään ominaisuuksien propagointiin lapsiobjekteihin.
     * @param state
     */
    @Override
    public void setUpdated(boolean state) {
        hasUpdated = state;
    }
}