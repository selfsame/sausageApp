package com.sausageApp.Players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.gdx.graphics.*;
import com.sausageApp.Simulation.Scenario;
import com.badlogic.gdx.controllers.Controller;
import org.jbox2d.common.Vec2;
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

    public int uid;
    public boolean desktop = false;
    public Controller controller;
    public Color color;

    public Avatar avatar = new Avatar(this);
    public Sausage sausage;

    public boolean in_lobby = true;
    public Scenario scenario;

    public float FORCE = 4f;
    public int propigation = 10;

    public int BID;

    public ArrayList<Sausage> party = new ArrayList<Sausage>();

    public boolean debug_draw_sausage_links = false;
    public boolean debug_draw_sausage_mesh_lines = false;
    public boolean debug_draw_sausage_force = false;

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
            scenario.game.profiler.addStat("\nSausage "+uid+": (ms) "+(int)(duration*1.0e-6));

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



    public void SetScenario(Scenario scen, Vec2 spawn){
        in_lobby = false;
        scenario = scen;
        sausage = new Sausage(this, scenario);
        propigation = sausage.sausage_length/2;

//        party.add(new Sausage(this, scenario));
//        party.add(new Sausage(this, scenario));
//        party.add(new Sausage(this, scenario));
//        party.add(new Sausage(this, scenario));


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

        if (in_lobby){
            return;
        }
        Vec2 right_linV = sausage.tail.getLinearVelocity();
        Vec2 left_linV = sausage.head.getLinearVelocity();
        if (desktop) {
            if(Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT)){
                sausage.tail_link.applyLinearImpulse(new Vec2(-FORCE, 0f), propigation, true, true, .7f);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)){
                sausage.tail_link.applyLinearImpulse(new Vec2(FORCE, 0f), propigation, true, true, .7f);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.DPAD_UP)){
                sausage.tail_link.applyLinearImpulse(new Vec2(0, -FORCE), propigation, true, true, .7f);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN)){
                sausage.tail_link.applyLinearImpulse(new Vec2(0, FORCE), propigation, true, true, .7f);
            }

            if(Gdx.input.isKeyPressed(Input.Keys.A)){
                sausage.head_link.applyLinearImpulse(new Vec2(-FORCE, 0f), propigation, false, true, .7f);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.D)){
                sausage.head_link.applyLinearImpulse(new Vec2(FORCE, 0f), propigation, false, true, .7f);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.W)){
                sausage.head_link.applyLinearImpulse(new Vec2(0, -FORCE), propigation, false, true, .7f);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.S)){
                sausage.head_link.applyLinearImpulse(new Vec2(0, FORCE), propigation, false, true, .7f);
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


        } else {
            float leftXAxis = controller.getAxis(Ouya.AXIS_LEFT_X);
            float leftYAxis = controller.getAxis(Ouya.AXIS_LEFT_Y);
            float rightXAxis = controller.getAxis(Ouya.AXIS_RIGHT_X);
            float rightYAxis = controller.getAxis(Ouya.AXIS_RIGHT_Y);
            if (leftXAxis > 0.0 || leftXAxis < 0.0){
                sausage.tail_link.applyLinearImpulse(new Vec2(leftXAxis*(FORCE), 0f), propigation, true, true, .7f);
            }
            if (leftYAxis > 0.0 || leftYAxis < 0.0){
                sausage.tail_link.applyLinearImpulse(new Vec2(0f, leftYAxis*(FORCE)), propigation, true, true, .7f);
            }
            if (rightXAxis > 0.0 || rightXAxis < 0.0){
                sausage.head_link.applyLinearImpulse(new Vec2(rightXAxis*FORCE, 0f), propigation, false, true, .7f);
            }
            if (rightYAxis > 0.0 || rightYAxis < 0.0){
                sausage.head_link.applyLinearImpulse(new Vec2(0f, rightYAxis*FORCE), propigation, false, true, .7f);
            }


        }
        float touch_left_x = scenario.game_screen.touchpad.getKnobPercentX();
        float touch_left_y = scenario.game_screen.touchpad.getKnobPercentY();
        //sausage.head_link.applyLinearImpulse(new Vec2(-touch_left_x*FORCE*1.5f, 0f), 4, false, true, .7f);
        //sausage.head_link.applyLinearImpulse(new Vec2(0f, touch_left_y *FORCE*1.5f), 4, false, true, .7f);


    }

}



