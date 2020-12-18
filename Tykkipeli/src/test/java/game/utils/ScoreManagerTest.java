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
import org.json.JSONArray;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kari Suominen
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
    
    /*
    @Test
    public void GetScoresForLevelTest() {
        //JSONArray scores = scoreManager.getScores("e1m1");
        assertTrue(true);
    }
    */
}
