/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.utils;

import java.net.http.HttpClient;

/**
 *
 * @author suominka
 */
public class ScoreManager {
    String URI = "http://192.168.0.100/api/tykkipeli/";
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
    
    public ScoreManager() {
        
    }
    
    private String getName() {
        return System.getProperty("user.name");
    }
    
    public void saveScore(int points, String level) {
        Score score = new Score(getName(), level, points);
        saveLocal(score);
        saveGlobal(score);
    }
    
    private void saveLocal(Score score) {
        
    }
    
    private void saveGlobal(Score score) {
        HttpClient client = HttpClient.newHttpClient();
    }
}
