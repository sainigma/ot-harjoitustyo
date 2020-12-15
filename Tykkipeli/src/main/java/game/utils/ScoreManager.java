/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;
import javax.crypto.Cipher;
import org.json.JSONArray;

/**
 *
 * @author suominka
 */
public class ScoreManager {
    String basePath = "http://192.168.0.100/api/tykkipeli/";
    String key = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJAT56gluyCXd78b3+35xl/nJSE3ryxRAR3a0ECsHSsd+UNKSkRY/qvXiLlNrhxIut45KqFBXQIhjfrcbRnSawcCAwEAAQ==";
    private int statusCode = 0;
    
    class Score {
        String name;
        String level;
        int score;
        public Score(String name, String level, int score) {
            this.name = name.toUpperCase().substring(0, 3);
            this.level = level;
            this.score = score;
        }
    }
    Score score;
    public ScoreManager() {
    }
    
    private String getName() {
        return System.getProperty("user.name");
    }
    
    public void setScore(int points, String level) {
        score = new Score(getName(), level, points);
    }
    
    public void saveScore() {
        if (score == null) {
            return;
        }
        saveLocal();
        saveGlobal();
    }
    
    private void saveLocal() {
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
        HttpClient client = HttpClient.newHttpClient();
        URI serverURI = URI.create(basePath + score.level);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(serverURI)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(Duration.ofMillis(300))
                .build();
        try {
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            statusCode = res.statusCode();
        } catch (IOException | InterruptedException ex) {
            System.out.println("Connection to server failed: timeout");
            statusCode = -1;
        }
    }
    
    private JSONArray fetchScore(String level) {
        HttpClient client = HttpClient.newHttpClient();
        URI serverURI = URI.create(basePath + level);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(serverURI)
                .header("Accept", "application/json")
                .GET()
                .timeout(Duration.ofMillis(300))
                .build();
        try {
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            JSONArray scores = new JSONArray(res.body());
            return scores;
        } catch (IOException | InterruptedException ex) {
            System.out.println("Connection to server failed: timeout");            
        }
        return null;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
}
