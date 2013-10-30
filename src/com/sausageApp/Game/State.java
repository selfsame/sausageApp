package com.sausageApp.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.sausageApp.Scenario.Scenario;
import com.sausageApp.Scene.Scene;
import com.sausageApp.Simulation.Box;


/**
The definitive assortment of instances currently running
 */
public class State {
    private static State instance = null;
    public Scene scene = null;
    public Box box = null;
    public Scenario scenario = null;
    public myGame game = null;
    public UI ui = null;
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
        if (scenario != null) scenario.update(delta);

    }
    public void newBox(Vector2 gravity){
        if (box != null){
            box.dispose();
        }
        box = new Box(gravity);
    }
    public void setScene(String filename){
        if (ui == null) ui = new UI();
        if (scenario != null) scenario.finish();
        scene = new Scene(filename);

        for (int i = 0; i < game.players.size(); i++){
            game.players.get(i).newScene();
        }

    }
    public void setScenario(Scenario _scenario){
        if (scenario != null) scenario.finish();
        scenario = _scenario;
        scenario.start();
    }

    public void setScenarioByString(String _scenario) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class sclass = Class.forName("com.sausageApp.Scenario."+_scenario);
        Scenario scenario = (Scenario) sclass.newInstance();
        setScenario(scenario);
    }

    public void log(String s){
        Gdx.app.log("STATE", s);
    }



}
