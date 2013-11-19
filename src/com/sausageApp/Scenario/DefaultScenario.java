package com.sausageApp.Scenario;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.sausageApp.Game.State;
import com.sausageApp.Game.Units;
import com.sausageApp.Players.Player;
import com.sausageApp.Scene.SensorObject;
import tv.ouya.console.api.OuyaController;


/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/29/13
 * Time: 6:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultScenario implements Scenario{
    public State state = State.getInstance();
    private Units units = new Units();

    public String title = "Free Roam";

    public DefaultScenario(){
        state.ui.game_title = title;
    }

    public void start(){

    }

    public void update(float delta){
        for (SensorObject sensor: state.scene.sensors) {
            if (sensor.active){
                if (sensor.usage.equals("PORTAL")) state.ui.portals.add(sensor);
                if (sensor.usage.equals("GAME")) state.ui.game_portals.add(sensor);
            }
        }
    }

    public void finish(){
        state.log("Default finish");
    }

    public void playerInput(Player player, int input){
        if (input == Input.Keys.O || input == OuyaController.BUTTON_O){
            for (SensorObject sensor: state.scene.sensors) {
                if (sensor.active) if (sensor.player_contacts.get(player) > 0) {

                    if (sensor.usage.equals("PORTAL")) state.setScene("scenarios/"+sensor.tag);

                    if (sensor.usage.equals("GAME")){

                        setScenarioByString(sensor.tag);
                    }

                }
            }
        }
    }

    public boolean setScenarioByString(String _scenario){
        try {
            state.setScenarioByString(_scenario);
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); return false;
        } catch (IllegalAccessException e) {
            e.printStackTrace(); return false;
        } catch (InstantiationException e) {
            e.printStackTrace(); return false;
        }
        return true;
    }

    public void enterSensor(SensorObject sensor, Player player){
        if (sensor.usage.equals("ZOOM")){
            state.scene.view_dist += sensor.fmod;
        }
        if (sensor.usage.equals("GRAVITY")){
            state.box.world.setGravity( new Vector2(0f,sensor.fmod) );
        }
        enterSpecialSensor(sensor, player);
    }
    public void enterSpecialSensor(SensorObject sensor, Player player){}

    public void exitSensor(SensorObject sensor, Player player){
        if (sensor.usage.equals("ZOOM")){
            state.scene.view_dist -= sensor.fmod;
        }
        if (sensor.usage.equals("GRAVITY")){
            state.box.world.setGravity( new Vector2(0f,sensor.fmod) );
            state.scene.gravity.y = sensor.fmod;
        }
        exitSpecialSensor(sensor, player);
    }
    public void exitSpecialSensor(SensorObject sensor, Player player){}

}
