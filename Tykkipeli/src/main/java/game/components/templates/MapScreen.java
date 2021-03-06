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
package game.components.templates;

import game.components.GameObject;
import game.graphics.primitives.Circle;
import game.graphics.primitives.Lines;
import game.logic.controllers.Statistic;
import game.logic.controllers.TargetLogic;
import game.simulations.cases.Ballistics;
import game.utils.Vector3d;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * Alinäkymä kartan ja minikartan piirtoon.
 * @author Kari Suominen
 */
public class MapScreen extends GameObject {
    private boolean minimized = true;
    private float mapTexScale = 520f / 10000f;
    private Vector3d mapRotation = new Vector3d(0, 0, 0);
    
    /**
     * Juuriobjekti joka näkyy kaikkien näkymien päällä.
     */
    public GameObject overlay;
    
    private GameObject map;
    private GameObject minimap;
    private GameObject projectileFront;
    private GameObject projectileShadow;
    private String [] targetNames = {"windjammer", "frigate", "ironclad", "lineship"};
    private HashMap<String, GameObject> targetIcons;
    private HashMap<TargetLogic, TargetIcon> targets;
    
    private ArrayList<Plotter> plotters;
    private HashMap<Ballistics, Plotter> historicalPlots;
    private ArrayList<ProjectileGroup> projectiles;
    
    private GameObject cursor;
    private GameObject minicursor;
    
    private double traversal;
    
    /**
     * Rakentaja, alustaa luokan ja sen lapset.
     * @param name
     */
    public MapScreen(String name) {
        super(name);
        this.traversal = 0f;
        projectiles = new ArrayList<>();
        historicalPlots = new HashMap<>();
        targetIcons = new HashMap<>();
        targets = new HashMap<>();
        init();
    }
    
    private class Plotter {
        Lines plotter;
        public Plotter() {
            plotter = new Lines();
            plotter.setLineStep(75);
            plotter.setScale(mapTexScale);
            plotter.setTransforms(new Vector3d(-256, 256, 0), new Vector3d(90, 0, 0), map.getPosition(), map.getRotation());
        }
        public void setPlot(ArrayList<Vector3d> positions) {
            plotter.setPlot(positions);
        }
        public void plot() {
            plotter.setGlobalRotation(map.getRotation());
            plotter.draw();
        }
        public void setColor(float r, float b, float g) {
            plotter.setColor(r, b, g);
        }
        public void setLineWidth(float lineWidth) {
            plotter.setLineWidth(lineWidth);
        }
    }
    
    private class RangePlotter {
        Circle plotter;
        Vector3d mapPosition;
        Vector3d localPosition;
        
        public RangePlotter(float range) {
            plotter = new Circle(range, 20);
            plotter.setScale(mapTexScale);
            plotter.setColor(77f / 255f, 64f / 255f, 69f / 255f);
            plotter.setLineWidth(1f);
            localPosition = new Vector3d(-256, 256, 2);
            mapPosition = map.getPosition();
        }
        public void setPosition(Vector3d position) {
            localPosition = position;
            localPosition.z = 2f;
        }
        public void plot() {
            plotter.setTransforms(localPosition, new Vector3d(), mapPosition, map.getRotation());
            plotter.draw();
        }
    }
    
    private class ProjectileGroup {
        
        GameObject front;
        GameObject shadow;
        private boolean visible;
        private float power;
        
        public ProjectileGroup() {
            this.front = projectileFront.clone();
            this.shadow = projectileShadow.clone();
            map.append(front);
            map.append(shadow);
            setVisible(false);
            power = 0;
        }
        
        private void setVisible(boolean state) {
            visible = state;
            front.setVisible(state);
            shadow.setVisible(state);
        }
        
        public void setPower(float power) {
            this.power = power;
        }
        
        public float getPower() {
            return power;
        }
        
        public void setPosition(Vector3d position) {
            if (position == null) {
                return;
            }
            if (!visible) {
                setVisible(true);
            }
            Vector3d pos = new Vector3d(position.x, position.z, position.y);
            pos.set(pos.scale(520f / 10000f));
            pos.x -= 256f;
            pos.y += 256f;
            pos.z += 0.2f;
            front.setPosition(pos);
            pos.z = 0.2f;
            shadow.setPosition(pos);
            setRotation(map.getRotation());
        }
        public void setRotation(Vector3d rotation) {
            front.setRotation(new Vector3d(0, 0, rotation.z));
        }
        public Vector3d getPosition() {
            return shadow.getPosition();
        }
        public void kill() {
            map.remove(front);
            map.remove(shadow);
        }
    }
    
    private class TargetIcon {
        private float range;
        GameObject icon;
        RangePlotter rangePlotter;
        public TargetIcon(String name, Vector3d position, Vector3d rotation, float range) {
            icon = targetIcons.get(name).clone();
            this.range = range;
            setPosition(position);
            setRotation(rotation);
            map.append(icon);
            if (range > 0f) {
                rangePlotter = new RangePlotter(range);
            } else {
                rangePlotter = null;
            }
            
        }

        public void setPosition(Vector3d position) {
            Vector3d pos = position.clone();
            icon.setPosition(new Vector3d(
                    (pos.x - 5000f) * mapTexScale,
                    (pos.y + 5000f) * mapTexScale,
                    pos.z * mapTexScale
            ));
            if (rangePlotter != null) {
                rangePlotter.setPosition(icon.getPosition());
            }
        }
        public void setRotation(Vector3d rotation) {
            icon.setRotation(rotation);
        }
        public void plot() {
            if (rangePlotter == null) {
                return;
            }
            rangePlotter.plot();
        }
        public void kill() {
            map.remove(icon);
        }
    }
    
    /**
     * Vastaanottaa tykin suunnan, vaikuttaa karttakursorin suuntaan.
     * @param rotation
     */
    public void setTraversal(double rotation) {
        double offset = -45f;
        traversal = rotation;
        cursor.setRotation((float) (rotation + offset));
        minicursor.setRotation((float) (rotation + offset));
    }
    
    /**
     * Vastaanottaa kartan kiertymän, pyörittää karttoja ja niiden lapsia.
     * @param rotation
     */
    public void rotateMap(double rotation) {
        map.rotate((float) rotation);
        minimap.rotate((float) rotation);
    }
    
    private void setProjectile(ProjectileGroup projectile, Vector3d position, float power) {
        projectile.setPower(power);
        projectile.setPosition(position);
    }
    
    /**
     * Vapauttaa ylimääräiset projektit piirtojonosta.
     * @param i nykyinen projektiilien määrä
     */
    public void freeProjectiles(int i) {
        if (projectiles.size() <= i) {
            return;
        }
        int j = projectiles.size() - 1;
        while (projectiles.size() > i) {
            ProjectileGroup projectile = projectiles.get(j);
            projectiles.get(j).kill();
            projectiles.remove(j);
            plotters.remove(j);
        }
    }
    
    private Plotter getActivePlotter(int i) {
        Plotter p;
        if (plotters.size() < i) {
            p = new Plotter();
            plotters.add(p);
            p.setColor(77f / 255f, 64f / 255f, 69f / 255f);
        } else {
            p = plotters.get(i - 1);
        }
        return p;
    }
    
    /**
     * Vastaanottaa historiahajautustaulun, päivittää aktiivisten projektiilien sijainnit ja lisää paikalliseen historiaan lisäämättömät deaktivoidut projektiilit.
     * @param history
     */
    public void setByHistory(HashMap<Ballistics, Statistic> history) {
        int i = 0;
        for (Ballistics solver : history.keySet()) {
            Statistic stat = history.get(solver);
            if (stat.isActive()) {
                i++;
                spawnProjectile(i);
                Plotter plotter = getActivePlotter(i);
                plotter.setPlot(stat.getPositions());
                setProjectile(projectiles.get(i - 1), stat.getLastPosition(), stat.getPower());
            } else if (!historicalPlots.containsKey(solver)) {
                Plotter plotter = new Plotter();
                plotter.setPlot(stat.getPositions());
                plotter.setColor(116f / 255f, 103f / 255f, 108f / 255f);
                historicalPlots.put(solver, plotter);
            }
        }
        freeProjectiles(i);
    }
    
    private void iconLoader() {
        for (String name : targetNames) {
            GameObject icon = new GameObject(name + "Icon", "icons/" + name + ".png", new Vector3d(64, 128)) { };
            icon.setRotation(new Vector3d(-90, 0, 0));
            targetIcons.put(name, icon);
        }
    }
    
    private void spawnChildren() {
        projectileFront = new GameObject("mapprojectile", "mapview/projektiili.png", new Vector3d(8, 8)) { };
        projectileShadow = new GameObject("mapprojectile", "mapview/projektiilivarjo.png", new Vector3d(16, 16)) { };
        
        iconLoader();
        
        map = new GameObject("map3d", "mapview/kartta.png", new Vector3d(512, 512)) { };
        minimap = new GameObject("minimap", "mapview/karttamini.png", new Vector3d(128, 128)) { };
        overlay = new GameObject("overlay") { };
        
        cursor = new GameObject("mapcursor", "mapview/suunta.png", new Vector3d(5, 7)) { };
        minicursor = new GameObject("mapcursor", "mapview/suuntamini.png", new Vector3d(0)) { };
    }
    
    /**
     * Alustaa maali-ikonin.
     * @param target
     */
    public void spawnTarget(TargetLogic target) {
        TargetIcon icon = new TargetIcon(target.getName(), target.getPosition(), target.getRotation(), target.getRange());
        targets.put(target, icon);
    }
    
    private void spawnProjectile(int size) {
        if (projectiles.size() < size) {
            projectiles.add(new ProjectileGroup());
        }
    }
    
    private void setChildTransforms() {
        projectileFront.setRotation(new Vector3d(0, 90, 0));
        projectileShadow.setRotation(new Vector3d(90, 0, 0));
        
        map.translate((1280 / 2), (720 / 2));
        map.setRotation(new Vector3d(45, 0, 22.5));
        
        minimap.translate(1280f - 110f, 720f - 90f, 100f);
        minimap.setRotation(new Vector3d(45, 0, 22.5));        
        
        cursor.translate(-256, 256, 1);
        minicursor.translate(-64, 64, 1);
        setTraversal(0);
    }
    private void init() {
        spawnChildren();
        setChildTransforms();
        
        plotters = new ArrayList<>();
        
        map.append(cursor);
        minimap.append(minicursor);

        append(overlay);        
        append(map);
        append(minimap);

    }
    
    /**
     * Minimoi näkymän, minimoituna näkyy minikartta ja overlay, maksimoituna kartta ja overlay.
     * @param minimized
     */
    @Override
    public void setMinimized(boolean minimized) {
        this.minimized = minimized;
        if (minimized) {
            map.setVisible(false);
            minimap.setVisible(true);
        } else {
            map.setVisible(true);
            minimap.setVisible(false);
        }
    }
    
    /**
     * Palauttaa toden jos näkymä on pienennetty.
     * @return
     */
    @Override
    public boolean isMinimized() {
        return minimized;
    }
    
    /**
     * Päivittää maali-ikonin sijainnin ja kiertymän.
     * @param logic
     */
    public void updateTarget(TargetLogic logic) {
        TargetIcon target = targets.get(logic);
        target.setPosition(logic.getPosition());
        target.setRotation(logic.getRotation());
    }
    
    private void plotPlotters() {
        if (plotters.isEmpty()) {
            return;
        }
        for (Plotter plotter : plotters) {
            plotter.plot();
        }
    }
    
    private void plotRanges() {
        for (TargetIcon target : targets.values()) {
            target.plot();
        }
    }
    
    private void plotHistoricalPlots() {
        if (historicalPlots.isEmpty()) {
            return;
        }
        for (Plotter plotter : historicalPlots.values()) {
            plotter.plot();
        }
    }
    
    /**
     * Päivitysmetodi, kutsuu lentoratojen sekä maaliobjektien vastatykistöetäisyyksien piirrot.
     */
    @Override
    public void update() {
        if (!minimized) {
            plotPlotters();
            plotHistoricalPlots();
            plotRanges();
        }
    }
}
