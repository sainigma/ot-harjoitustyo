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

import java.security.PublicKey;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kari Suominen
 */
public class ScoreManagerTest {
    
    ScoreManager scoreManager;
    Services services;
    
    @Before
    public void setUp() {
        scoreManager = new ScoreManager();
    }
    
    @Test
    public void PublicKeyLoadsCorrectlyTest() {
        System.out.println(" Testing that public key is generated");
        PublicKey result = scoreManager.getKeyTest();
        assertTrue(result != null);
    }
    
    @Test
    public void EncryptionWorksTest() {
        System.out.println(" Testing that encryption works");
        String result = scoreManager.encryptTest("testi");
        assertTrue(!result.equals("testi"));
    }
    
    @Test
    public void ScoreSaveTest() {
        System.out.println(" Testing that saving scores works");
        scoreManager.setScore(123213, "e1m1");
        scoreManager.setName("TST");
        scoreManager.saveScore();
        int status = scoreManager.getStatusCode();
        if (status == 400) {
            System.out.println("  Warning, key mismatch with server.");            
        }
        assertTrue(status == -1 || status == 200 || status == 400);
    }
    
    @Test
    public void SendScoreWithInvalidLevel() {
        System.out.println(" Testing that saving scores fails with incorrect level identifier");
        scoreManager.setScore(32423, "map32");
        scoreManager.setName("TST");
        scoreManager.saveScore();
        int status = scoreManager.getStatusCode();
        assertTrue(status == -1 || status == 400);
    }
    
    @Test
    public void GetGlobalScores() {
        System.out.println(" Testing the fetching of global scores");
        JSONObject scores = scoreManager.getGlobalScores();
        int statusCode = scoreManager.getStatusCode();
        boolean success = false;
        if (statusCode == -1 && scores == null) {
            System.out.println("Failed to connect to server, test exited safely");
            success = true;
        } else if (statusCode == 200 && !scores.keySet().isEmpty()) {
            success = true;
        }
        assertTrue(success);
    }
    
    @Test
    public void GetScoreFailTest() {
        System.out.println(" Testing that the client survives timeout");
        scoreManager.setServicesPath("http://todennakoisestiolematonpolku.car/kissa/api/");
        JSONObject scores = scoreManager.getGlobalScores();
        int statusCode = scoreManager.getStatusCode();
        boolean success = false;
        if (statusCode == -1 && scores == null) {
            success = true;
        }
        assertTrue(success);
    }
    
    @Test
    public void SavingKeyMismatchWithValidKeyTest() {
        System.out.println(" Testing that both the game and the backend survives a POST with incorrect key.");
        scoreManager.setEncryptionKey("MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAN86q9yggLOEnVu79fFPwnCgxQIkUubvXiKBIbJ0tB9XERledQEXByBYXY/wvYITiX2cg1mjqkn/yPUrFYCn+j8CAwEAAQ==");
        PublicKey result = scoreManager.getKeyTest();
        assertTrue(result != null);
        scoreManager.setScore(123213, "e1m1");
        scoreManager.setName("TST");
        scoreManager.saveScore();
        int status = scoreManager.getStatusCode();
        assertTrue(status == 400);        
    }
    
    @Test
    public void SavingKeyMismatchWithGarbledKeyTest() {
        System.out.println(" Testing that both the game and the backend survives a POST with X.509 incompatible key.");
        scoreManager.setEncryptionKey("kissakissakissaahaanytsäästänrahaa");
        PublicKey result = scoreManager.getKeyTest();
        assertTrue(result == null);
        scoreManager.setScore(123213, "e1m1");
        scoreManager.setName("TST");
        scoreManager.saveScore();
        int status = scoreManager.getStatusCode();
        assertTrue(status == 0);                
    }
}
