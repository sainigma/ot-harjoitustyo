/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.utils;

import java.security.PublicKey;
import org.json.JSONArray;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author suominka
 */
public class ScoreManagerTest {
    
    ScoreManager scoreManager;
    
    @Before
    public void setUp() {
        scoreManager = new ScoreManager();
    }
    
    @Test
    public void PublicKeyLoadsCorrectlyTest() {
        PublicKey result = scoreManager.getKeyTest();
        assertTrue(result != null);
    }
    
    @Test
    public void EncryptionWorksTest() {
        String result = scoreManager.encryptTest("testi");
        assertTrue(!result.equals("testi"));
    }
    
    @Test
    public void ScoreSaveTest() {
        scoreManager.setScore(123213, "e1m1");
        scoreManager.saveScore();
        int status = scoreManager.getStatusCode();
        assertTrue(status == -1 || status == 200);
    }
    
    @Test
    public void SendScoreWithInvalidLevel() {
        scoreManager.setScore(32423, "map32");
        scoreManager.saveScore();
        int status = scoreManager.getStatusCode();
        assertTrue(status == -1 || status == 400);
    }
    
    @Test
    public void GetScoresForLevelTest() {
        JSONArray scores = scoreManager.getScores("e1m1");
        assertTrue(true);
    }
}
