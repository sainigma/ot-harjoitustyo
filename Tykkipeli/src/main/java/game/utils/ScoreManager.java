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
package game.utils;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.crypto.Cipher;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Pelin pisteiden tallentamisesta ja lataamisesta vastaava luokka. Tallentaa pisteet sekä lokaalisti että lähettää ne pelin backendiin. Backendiin lähetetty data enkryptoidaan X.509 muotoisella julkisella avaimella.
 * @author Kari Suominen
 */
public class ScoreManager {
    private Services services = new Services();
    private String basePath = "tykkipeli/";
    private String key = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJAT56gluyCXd78b3+35xl/nJSE3ryxRAR3a0ECsHSsd+UNKSkRY/qvXiLlNrhxIut45KqFBXQIhjfrcbRnSawcCAwEAAQ==";
    private int statusCode = 0;
    
    private class Score {
        String name;
        String level;
        int score;
        public Score(String level, int score) {
            this.name = "AAA";
            this.level = level;
            this.score = score;
        }
        
        public void setName(String name) {
            this.name = name.toUpperCase().substring(0, 3);
        }
    }
    private Score score;
    
    private String getName() {
        return System.getProperty("user.name");
    }
    /**
     * Luo uuden pisteobjektin, asettaa sille pisteiden määrän ja kentän.
     * @param points
     * @param level 
     */
    public void setScore(int points, String level) {
        score = new Score(level, points);
    }
    /**
     * Asettaa nimen pisteobjektille.
     * @param name 
     */
    public void setName(String name) {
        score.setName(name);
    }
    /**
     * Pyytää pisteobjektin tallennusta paikallisesti sekä pelin backendiin.
     */
    public void saveScore() {
        if (score == null) {
            return;
        }
        saveLocal();
        saveGlobal();
    }
    
    private JSONObject createScores() {
        JSONObject scores = new JSONObject();
        scores.put(score.level, new JSONArray());
        return scores;
    }
    
    private class LevelScoreComparator implements Comparator<JSONArray> {
        public int compare(JSONArray a, JSONArray b) {
            return b.getInt(1) - a.getInt(1);
        }
    }
    
    private JSONArray sortLevelScores(JSONArray levelScores) {
        JSONArray sortedArr = new JSONArray();
        List list = new ArrayList<>();
        for (Object item : levelScores) {
            list.add((JSONArray) item);
        }
        Collections.sort(list, new LevelScoreComparator());
        int i = 0;
        for (Object item : list) {
            if (i < 16) {
                sortedArr.put((JSONArray) item);
            }
            i++;
        }
        return sortedArr;
    }
    
    private void saveLocal() {
        JSONObject scores = getLocalScores();
        if (scores == null) {
            scores = createScores();
        }
        if (!scores.has(score.level)) {
            scores.put(score.level, new JSONArray());
        }
        JSONArray newScore = new JSONArray();
        newScore.put(score.name);
        newScore.put(score.score);
        JSONArray levelScores = scores.getJSONArray(score.level);
        levelScores.put(newScore);
        scores.put(score.level, sortLevelScores(levelScores));
        
        JSONLoader loader = new JSONLoader("");
        loader.save(scores, "scores");
    }
    
    private PublicKey getKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key.getBytes("utf-8"));
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyGenerator = KeyFactory.getInstance("RSA");
            PublicKey key = keyGenerator.generatePublic(spec);
            return key;
        } catch (Exception e) {
            System.out.println("Invalid key!");
            return null;
        }
    }
    
    /**
     * Palauttaa kaikki paikalliset pistetilastot JSON-objektina.
     * @return 
     */
    public JSONObject getLocalScores() {
        JSONLoader loader = new JSONLoader("");
        return loader.read("scores");
    }
    
    /**
     * Palauttaa kaikki globaalit pistetilastot JSON-objektina.
     * @return 
     */
    public JSONObject getGlobalScores() {
        return services.getJSONObject(basePath);
    }
    
    /**
     * Metodi enkryptioavaimen luomisen testaamiseen.
     * @return enkryptioavain
     */
    public PublicKey getKeyTest() {
        return getKey();
    }
    
    /**
     * Metodi tekstin enkryption testaamiseen.
     * @param body enkryptattava teksti
     * @return enkryptattu teksti
     */
    public String encryptTest(String body) {
        return encrypt(body);
    }
    
    private String encrypt(String body) {
        PublicKey key = getKey();
        if (key == null) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBody = cipher.doFinal(body.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encryptedBody);
        } catch (Exception e) {
            System.out.println("Encryption failed!");
        }

        return body;
    }
    
    private void saveGlobal() {
        String message = "{\"name\":\"" + score.name + "\", \"score\":\"" + score.score + "\"}";
        message = encrypt(message);
        if (message == null) {
            statusCode = -1;
            return;
        }
        String body = "{\"raw\":\"" + message + "\"}";
        statusCode = services.post(basePath + score.level, body);
    }
    
    /**
     * Metodi testaamiseen, palauttaa viimesimmästä backend-tapahtumasta tulleen statuskoodin.
     * @return 
     */
    public int getStatusCode() {
        return services.getStatusCode();
    }
    
    /**
     * Asettaa nettirajapinnan juuripolun, testauskäyttöön.
     * @param path 
     */
    public void setServicesPath(String path) {
        services.setPath(path);
    }
    
    /**
     * Asettaa enkryptioavaimen suoraan, testauskäyttöön.
     */
    public void setEncryptionKey(String key) {
        this.key = key;
    }
}
