/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.mycompany.unicafe.Kassapaate;
import com.mycompany.unicafe.Maksukortti;
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
public class KassapaateTest {
    Kassapaate paate;
    Maksukortti kortti;
    public KassapaateTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        paate = new Kassapaate();
        kortti = new Maksukortti(1000);
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void alustusToimii(){
        int myyty = paate.edullisiaLounaitaMyyty() + paate.maukkaitaLounaitaMyyty();
        int rahaa = paate.kassassaRahaa();
        assertEquals(0,myyty);
        assertEquals(100000,rahaa);
    }
    @Test
    public void kateisostoEdullisesti(){
        assertEquals(100000,paate.kassassaRahaa());
        assertEquals(230,paate.syoEdullisesti(230));
        assertEquals(0,paate.syoEdullisesti(240));
        assertEquals(10,paate.syoEdullisesti(250));
        assertEquals(2,paate.edullisiaLounaitaMyyty());
        assertEquals(2*240+100000,paate.kassassaRahaa());
    }
    @Test
    public void kateisostoMaukkaasti(){
        assertEquals(390,paate.syoMaukkaasti(390));
        assertEquals(0,paate.syoMaukkaasti(400));
        assertEquals(10,paate.syoMaukkaasti(410));
        assertEquals(2,paate.maukkaitaLounaitaMyyty());
        assertEquals(2*400+100000,paate.kassassaRahaa());
    }
    @Test
    public void korttiostoEdullisesti(){
        Maksukortti kortti2 = new Maksukortti(200);
        assertEquals(true,paate.syoEdullisesti(kortti));
        assertEquals(1000-240,kortti.saldo());
        assertEquals(false,paate.syoEdullisesti(kortti2));
        assertEquals(200,kortti2.saldo());
        assertEquals(1,paate.edullisiaLounaitaMyyty());
        assertEquals(100000,paate.kassassaRahaa());
    }
    @Test
    public void korttiostoMaukkaasti(){
        Maksukortti kortti2 = new Maksukortti(200);
        assertEquals(true,paate.syoMaukkaasti(kortti));
        assertEquals(1000-400,kortti.saldo());
        assertEquals(false,paate.syoMaukkaasti(kortti2));
        assertEquals(200,kortti2.saldo());
        assertEquals(1,paate.maukkaitaLounaitaMyyty());
        assertEquals(100000,paate.kassassaRahaa());
    }
}
