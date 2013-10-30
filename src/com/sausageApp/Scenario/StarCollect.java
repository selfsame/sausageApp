package com.sausageApp.Scenario;

import com.badlogic.gdx.Input;
import com.sausageApp.Players.Player;
import com.sausageApp.Scene.SensorObject;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/29/13
 * Time: 9:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class StarCollect extends DefaultScenario implements Scenario {
    public String title = "Star Collect";

    public float timer = 100f;

    public StarCollect(){
        state.ui.game_title = title;
    }

    public void update(float delta){
        timer -= delta;
        state.ui.game_title = title+"  "+(int)timer;
    }

    public void playerInput(Player player, int input){
        if (input == Input.Keys.O){
        }
    }
}
