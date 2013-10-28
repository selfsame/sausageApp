package com.sausageApp.Game;

import com.sausageApp.Scene.Scene;
import com.sausageApp.Simulation.Box;
import org.jbox2d.common.Vec2;

/**
The definitive assortment of instances currently running
 */
public class State {
    private static State instance = null;
    public Scene scene = null;
    public Box box = null;
    public myGame game = null;
    //public Scenario scenario = null;

    public static State getInstance(){
        if(instance == null) {
            instance = new State();
        }
        return instance;
    }

    protected State(){}

    public void update(float delta){
        if (scene != null) scene.render(delta);
        if (box != null) box.update(delta);

    }
    public void newBox(Vec2 gravity){
        if (box != null){
            box.dispose();
        }
        box = new Box(gravity);
    }
    public void setScene(String filename){
        scene = new Scene(filename);

        for (int i = 0; i < game.players.size(); i++){
            game.players.get(i).newScene();
        }

    }
}
