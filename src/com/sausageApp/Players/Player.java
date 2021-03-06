package com.sausageApp.Players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.math.Vector2;
import com.sausageApp.Game.State;
import com.sausageApp.Game.Units;
import com.sausageApp.Scene.SensorObject;
import tv.ouya.console.api.OuyaController;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/4/13
 * Time: 3:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class Player {
    public State state = State.getInstance();
    private Units units = new Units();

    public int uid;
    public boolean desktop = false;
    public Controller controller;
    public Color color;

    public Avatar avatar = new Avatar(this);
    public Sausage sausage;

    public boolean in_lobby = true;

    public float FORCE = 80f;
    public int propigation = 10;

    public int BID;

    public ArrayList<Sausage> party = new ArrayList<Sausage>();

    public boolean debug_draw_sausage_links = false;
    public boolean debug_draw_sausage_mesh_lines = false;
    public boolean debug_draw_sausage_force = false;
    public boolean debug_draw_static_chains = false;
    public boolean debug_draw_level = true;

    public int LEFT_AXIS_X = 0;
    public int LEFT_AXIS_Y = 1;
    public int RIGHT_AXIS_X = 2;
    public int RIGHT_AXIS_Y = 3;

    public Player( Controller cont, Color col, int _uid){
        uid = _uid;
        controller = cont;
        color = col;
        Init();
    }

    public Player(Color col, int _uid){
        uid = _uid;
        desktop = true;
        color = col;
        Init();
    }

    private void Init(){
        avatar.background_color = color;
        AddControllerListener();
        BID = 256 + uid;
    }

    public void render(){
        if (!in_lobby){

            long startTime = System.nanoTime();
            sausage.render();
            long endTime = System.nanoTime();

            long duration = endTime - startTime;
            state.game.profiler.addStat("\nSausage "+uid+": (ms) "+(int)(duration*1.0e-6));

            for (int i = 0; i < party.size(); i++){
                party.get(i).render();
            }
        }
    }

    private void AddControllerListener(){
        if (!desktop){
            controller.addListener(new ControllerAdapter() {
                @Override
                public void connected(Controller controller) {
                }
                @Override
                public void disconnected(Controller controller) {
                }
                @Override
                public boolean buttonDown (Controller controller, int buttonCode){
                  ButtonDown(buttonCode);
                  return true;
                }
                @Override
                public boolean buttonUp (Controller controller, int buttonCode){
                    return true;
                }
            });
        }
    }



    public void newScene(){
        in_lobby = false;
        sausage = new Sausage(this);
        propigation = sausage.sausage_length/2;
        //state.log("PLAYER "+uid+":"+controller.getName());


    }

    public void EndScenario(){
        in_lobby = true;
    }

    public void ButtonDown(int code){
         //Gdx.app.log("CODES",":"+code+":"+OuyaController.BUTTON_DPAD_LEFT);
         if (in_lobby == true){
             if (code == OuyaController.BUTTON_DPAD_LEFT){
                 avatar.changeMouth(-1);
             }
             if (code == OuyaController.BUTTON_DPAD_RIGHT){
                 avatar.changeMouth(1);
             }
             if (code == OuyaController.BUTTON_DPAD_UP){
                 avatar.changeEyes(-1);
             }
             if (code == OuyaController.BUTTON_DPAD_DOWN){
                 avatar.changeEyes(1);
             }
         }
    }



    public void handleInput(){
        int _propigation = propigation;
        float _FORCE = FORCE;
//        if (state.scene != null) if (Math.abs(state.scene.gravity.y) < 3f){
//            _propigation = 1;
//            _FORCE = FORCE*.1f;
//
//        }
        if (in_lobby){
            return;
        }
        Vector2 right_linV = sausage.tail.getLinearVelocity();
        Vector2 left_linV = sausage.head.getLinearVelocity();
        if (desktop) {
            if(Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT)){
                sausage.tail_link.applyLinearImpulse(new Vector2(-_FORCE, 0f), _propigation, true, true, .75f);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)){
                sausage.tail_link.applyLinearImpulse(new Vector2(_FORCE, 0f), _propigation, true, true, .75f);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.DPAD_UP)){
                sausage.tail_link.applyLinearImpulse(new Vector2(0, -_FORCE), _propigation, true, true, .75f);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN)){
                sausage.tail_link.applyLinearImpulse(new Vector2(0, _FORCE), _propigation, true, true, .75f);
            }

            if(Gdx.input.isKeyPressed(Input.Keys.A)){
                sausage.head_link.applyLinearImpulse(new Vector2(-_FORCE, 0f), _propigation, false, true, .75f);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.D)){
                sausage.head_link.applyLinearImpulse(new Vector2(_FORCE, 0f), _propigation, false, true, .75f);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.W)){
                sausage.head_link.applyLinearImpulse(new Vector2(0, -_FORCE), _propigation, false, true, .75f);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.S)){
                sausage.head_link.applyLinearImpulse(new Vector2(0, _FORCE), _propigation, false, true, .75f);
            }

            //debug options
            if(Gdx.input.isKeyPressed(Input.Keys.NUM_1)){
                debug_draw_sausage_links = !debug_draw_sausage_links;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)){
                debug_draw_sausage_mesh_lines = !debug_draw_sausage_mesh_lines;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.NUM_3)){
                debug_draw_sausage_force = !debug_draw_sausage_force;
            }

            if(Gdx.input.isKeyPressed(Input.Keys.NUM_4)){
                debug_draw_level = !debug_draw_level;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.NUM_5)){
                debug_draw_static_chains = !debug_draw_static_chains;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.NUM_6)){
                state.debug = !state.debug;
            }

            if(Gdx.input.isKeyPressed(Input.Keys.O) ){
                state.scenario.playerInput(this, Input.Keys.O);

            }


        } else {

            float leftXAxis = controller.getAxis(LEFT_AXIS_Y);
            float leftYAxis = controller.getAxis(LEFT_AXIS_X);
            float rightXAxis = controller.getAxis(RIGHT_AXIS_Y);
            float rightYAxis = controller.getAxis(RIGHT_AXIS_X);
            if (uid == 1) leftYAxis = -1.0f;
            if (leftXAxis > 0.0 || leftXAxis < 0.0){
                sausage.tail_link.applyLinearImpulse(new Vector2(leftXAxis*(_FORCE)*1.3f, 0f), _propigation, true, true, .75f);
            }
            if (leftYAxis > 0.0 || leftYAxis < 0.0){
                sausage.tail_link.applyLinearImpulse(new Vector2(0f, leftYAxis*(_FORCE)*1.3f), _propigation, true, true, .75f);
            }
            if (rightXAxis > 0.0 || rightXAxis < 0.0){
                sausage.head_link.applyLinearImpulse(new Vector2(rightXAxis*_FORCE*1.3f, 0f), _propigation, false, true, .75f);
            }
            if (rightYAxis > 0.0 || rightYAxis < 0.0){
                sausage.head_link.applyLinearImpulse(new Vector2(0f, rightYAxis*_FORCE*1.3f), _propigation, false, true, .75f);
            }

            if(controller.getButton(OuyaController.BUTTON_O)){
                state.scenario.playerInput(this, OuyaController.BUTTON_O);
            }

        }



        // Needs to be some sort of Interface Solution

        float touch_left_x = -state.game.game_screen.touchpad_left.getKnobPercentX();
        float touch_left_y = -state.game.game_screen.touchpad_left.getKnobPercentY();
        sausage.head_link.applyLinearImpulse(new Vector2(-touch_left_x*FORCE*1.3f, 0f), propigation, false, true, .75f);
        sausage.head_link.applyLinearImpulse(new Vector2(0f, touch_left_y *FORCE*1.3f), propigation, false, true, .75f);

        float touch_right_x = -state.game.game_screen.touchpad_right.getKnobPercentX();
        float touch_right_y = -state.game.game_screen.touchpad_right.getKnobPercentY();
        sausage.tail_link.applyLinearImpulse(new Vector2(-touch_right_x*FORCE*1.3f, 0f), propigation, true, true, .75f);
        sausage.tail_link.applyLinearImpulse(new Vector2(0f, touch_right_y *FORCE*1.3f), propigation, true, true, .75f);


    }



}



