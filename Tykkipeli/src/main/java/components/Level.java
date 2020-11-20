/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import utils.Vector3d;

/**
 *
 * @author suominka
 */
public class Level extends GameObject{
    long start = System.currentTimeMillis();
    ViewPort gameView;
    ViewPort mapView;
    private boolean isFinished = false;
    
    public Level(String name){
        super(name);
        init();
    }
    
    public boolean isFinished(){
        return isFinished;
    }
    
    GameObject mortarTube;
    private void init(){
        gameView = new ViewPort("game");        
        float viewPortScale = 720f/1080f;
        
        //toteuta lokaalit avaruudet ni esim skaalan saa propogoitua suoraan viewportista alas
        
        mortarTube = new GameObject("mortartube","mortar/tykki.png",new Vector3d(512,512,1), viewPortScale){};
        GameObject mortarCar = new GameObject("mortarcar","mortar/karry.png",new Vector3d(398,147,-1), viewPortScale){};
        GameObject mortarStand = new GameObject("mortarstand","mortar/jalusta.png",new Vector3d(0), viewPortScale){};
        mortarCar.append(mortarTube);
        mortarCar.translate((int)(1065*viewPortScale), (int)(355*viewPortScale));
        mortarStand.append(mortarCar);
        gameView.append(mortarStand);
       
        /*
        mapView = new ViewPort("map");
        mapView.toggle();
        GameObject updateTest = new ScrollingObject("testiobjekti","test.png",new Vector3d(128));
        GameObject transparencyTest = new GameObject("transtest","testisprite_trans.png"){};
        
        GameObject empty = new ScrollingObject("empty");
        GameObject childTest1 = new GameObject("childtest1","testisprite.png"){};
        GameObject childTest2 = new GameObject("childtest2","testisprite.png"){};
        GameObject childTest3 = new GameObject("childtest3","testisprite.png"){};
        childTest2.rotate(120);
        childTest3.rotate(240);
        empty.append(childTest1);
        empty.append(childTest2);
        empty.append(childTest3);
        empty.translate(684, 432);
       
        gameView.append(updateTest);
        gameView.append(transparencyTest);
        gameView.append(empty);
        
        transparencyTest.translate(1280/2-128, 720/2-100);
        
        GameObject backgroundTest = new GameObject("background","tausta.png"){};
        mapView.append(backgroundTest);
        
        
        append(mapView);
        System.currentTimeMillis();
        */
        append(gameView);
    }
    private boolean removeMe = true;
    @Override
    public void update(){
        mortarTube.rotate(1);
        
        /*
        if( System.currentTimeMillis()-start < 1000*2 ){
            return;
        }
        double toggler = Math.sin((System.currentTimeMillis()-start)*0.005);
        if( toggler > -0.5 && !gameView.isActive()){
            gameView.setActive(true);
            mapView.setActive(false);
            if( removeMe ){
                gameView.setScreenShake(20);
                removeMe = false;
            }else{
                gameView.setScreenShake(0);
                removeMe = true;
            }
        }else if( toggler < -0.5 && !mapView.isActive()){
            mapView.setActive(true);
            gameView.setActive(false);
        }*/
    }
}
