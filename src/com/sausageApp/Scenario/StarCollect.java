package com.sausageApp.Scenario;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.sausageApp.Players.Player;
import com.sausageApp.Scene.GameInstance;
import com.sausageApp.Scene.SensorObject;
import com.sausageApp.TweenAccessors.GameInstanceAccessor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/29/13
 * Time: 9:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class StarCollect extends DefaultScenario implements Scenario {
    public String title = "Star Collect";

    public float timer = 15f;

    private HashMap<SensorObject, GameInstance> starmap = new HashMap<SensorObject, GameInstance>();
    private ArrayList<GameInstance> stars = new ArrayList<GameInstance>();

    public StarCollect(){
        state.ui.game_title = title;
    }

    public void start(){

      for (SensorObject sensor: state.scene.sensors){
          if (sensor.usage.equals("GENERIC")){
              GameInstance star = new GameInstance("star"+sensor.loc[0], "star", new Vector3(sensor.loc[0], -sensor.loc[1], 0f), new Vector3(1f,1f,1f), new Quaternion() );
              stars.add(star);
              starmap.put(sensor, star);
              state.scene.instances.add(star);
              Tween.to(star, GameInstanceAccessor.POSITION_Y, .5f).targetRelative(-.05f).repeatYoyo(10000, 0f).start(state.scene.tweenManager);
          }

      }
    }

    public void finish(){
        for (GameInstance star: stars){
            state.scene.instances.remove(star);
        }
        state.log("StarCollect finish");
        state.scenario = null;
        state.setScenario(new DefaultScenario());
    }

    public void update(float delta){
        timer -= delta;
        state.ui.game_title = title+"  "+(int)timer;
        if ((int)timer < 0){
            finish();
        }
    }

    public void playerInput(Player player, int input){
        if (input == Input.Keys.O){
        }
    }

    public void enterSpecialSensor(SensorObject sensor, Player player){
        if (sensor.usage.equals("GENERIC")){
            state.scene.view_dist += sensor.fmod;

            if (starmap.containsKey(sensor) ){
                GameInstance star = starmap.get(sensor);
                //state.scene.instances.remove(star);
                Tween.to(star, GameInstanceAccessor.SCALE_XYZ, .2f).target(1.5f, 1.5f, 1.5f).start(state.scene.tweenManager);
                Tween.to(star, GameInstanceAccessor.SCALE_XYZ, .3f).delay(.2f).target(0.3f, .3f, .3f).start(state.scene.tweenManager).setCallback(new gotStar(star)).setCallbackTriggers(0x04);

                starmap.remove(sensor);
            }

        }
    }

    private class gotStar implements TweenCallback {
        private GameInstance star = null;
        public gotStar(GameInstance s){
            star = s;
        }
        public static final int BEGIN = 0x01;
        public static final int END = 0x04;
        public static final int ANY = 0xFF;

        public void onEvent(int type, BaseTween<?> source){
            if (type == END){
                state.log("tween end!!");
                state.scene.instances.remove(star);
            }
        }
    }


}
