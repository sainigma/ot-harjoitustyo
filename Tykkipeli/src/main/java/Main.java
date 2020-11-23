
import components.Level;
import java.util.Random;
import utils.Renderer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author suominka
 */

public class Main {
    Renderer renderer;
    public void init(){
        Random rand = new Random();
        renderer = new Renderer();
        Level level = new Level("testlevel");        
        renderer.appendToRenderQueue(level);
        renderer.setBackground(1, 1, 1);
        //renderer.setBackground(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        run();
    }
    public void update(){
        System.out.println("update");
    }
    public void run(){
        renderer.run();
    }
    public static void main(String args[]){
        System.out.println("Init");
        new Main().init();
    }    
}
