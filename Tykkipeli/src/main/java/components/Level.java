/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import templates.Mortar;
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
    GameObject mortarGear;
    GameObject mortarWheel;
    GameObject mortarCar;
    
    private void init(){
        gameView = new ViewPort("game");        
        float viewportScale = 720f/1080f;
        /*
        
        //toteuta lokaalit avaruudet ni esim skaalan saa propogoitua suoraan viewportista alas
        GameObject mortarStand = new GameObject("mortarstand","mortar/jalusta.png",new Vector3d(0), viewPortScale){};
        mortarTube = new GameObject("mortartube","mortar/tykki.png",new Vector3d(512,512,1), viewPortScale){};
        mortarCar = new GameObject("mortarcar","mortar/karry.png",new Vector3d(398,147,-1), viewPortScale){};
        mortarWheel = new GameObject("mortarwheel","mortar/ruori.png",new Vector3d(128,128,-3), viewPortScale){};
        mortarGear = new GameObject("mortargear","mortar/ratas.png",new Vector3d(64,64,-2), viewPortScale){};
        mortarCar.append(mortarTube);
        mortarCar.append(mortarGear);
        mortarCar.append(mortarWheel);
        mortarWheel.translate(20, 106);
        mortarGear.translate(50,90);
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
        GameObject mortar = new Mortar("mortar",viewportScale);
        gameView.append(mortar);
        //gameView.setScreenShake(2);
        append(gameView);
    }
    boolean asd = true;
    @Override
    public void update(){
        /*
        float initialRot = -10f;
        mortarWheel.rotate(initialRot);
        mortarGear.rotate(-initialRot*0.2f);
        mortarTube.rotate(initialRot*0.2f*0.00845f);
        if( asd ){
            mortarCar.translate(-1*5, -0.15f*5);            
        }else{
            mortarCar.translate(1*5, 0.15f*5);
        }
        if( mortarCar.x > 710 ){
            asd = true;
        }else if( mortarCar.x < 500 ){
            asd = false;
        }
        System.out.println(mortarCar.x);

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
