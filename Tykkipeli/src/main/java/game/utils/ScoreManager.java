/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author suominka
 */
public class ScoreManager {
    Services services = new Services();
    String basePath = "tykkipeli/";
    String key = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJAT56gluyCXd78b3+35xl/nJSE3ryxRAR3a0ECsHSsd+UNKSkRY/qvXiLlNrhxIut45KqFBXQIhjfrcbRnSawcCAwEAAQ==";
    private int statusCode = 0;
    
    class Score {
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
    Score score;
    public ScoreManager() {
    }
    
    private String getName() {
        return System.getProperty("user.name");
    }
    
    public void setScore(int points, String level) {
        score = new Score(level, points);
    }
    
    public void setName(String name) {
        score.setName(name);
    }
    
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
    
    class levelScoreComparator implements Comparator <JSONArray> {
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
        Collections.sort(list, new levelScoreComparator());
        for (Object item : list) {
            sortedArr.put((JSONArray) item);
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
    
    public JSONObject getLocalScores() {
        JSONLoader loader = new JSONLoader("");
        return loader.read("scores");
    }
    
    public JSONObject getGlobalScores() {
        return services.getJSONObject(basePath);
    }
    
    public JSONArray getScores(String level) {
        return fetchScore(level);
    }
    
    public PublicKey getKeyTest() {
        return getKey();
    }
    
    public String encryptTest(String body) {
        return encrypt(body);
    }
    
    private String encrypt(String body) {
        PublicKey key = getKey();
        if (key == null) {
            return body;
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
        String body = "{\"raw\":\"" + message + "\"}";
        statusCode = services.post(basePath + score.level, body);
    }
    
    private JSONArray fetchScore(String level) {
        return services.getJSONArray(basePath + level);
    }
    
    public int getStatusCode() {
        return statusCode;
    }
}
