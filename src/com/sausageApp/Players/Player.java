package com.sausageApp.Players;

import com.badlogic.gdx.controllers.mappings.Ouya;
import com.sausageApp.Simulation.Scenario;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.Color;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/4/13
 * Time: 3:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class Player {

    public Controller controller;
    public ArrayList<Body> sausage;
    public Body right_link;
    public Body left_link;
    public Color color;

    public Player(Scenario scenario, Controller cont, Vec2 spawn){
        controller = cont;
        sausage = scenario.createSausage(spawn.x, spawn.y, 16f, 20);
        left_link = sausage.get(0);
        right_link = sausage.get(20);

    }

    public void handleInput(){
        Vec2 right_linV = right_link.getLinearVelocity();
        Vec2 left_linV = left_link.getLinearVelocity();
        float leftXAxis = controller.getAxis(Ouya.AXIS_LEFT_X);
        float leftYAxis = controller.getAxis(Ouya.AXIS_LEFT_Y);
        float rightXAxis = controller.getAxis(Ouya.AXIS_RIGHT_X);
        float rightYAxis = controller.getAxis(Ouya.AXIS_RIGHT_Y);
        left_link.setLinearVelocity(new Vec2(left_linV.x+leftXAxis*50f, left_linV.y+leftYAxis*50f));
        right_link.setLinearVelocity(new Vec2(right_linV.x+rightXAxis*50f, right_linV.y+rightYAxis*50f));
    }
}



//        if(Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT)){
//            right_link.setLinearVelocity(new Vec2(-120f, right_linV.y));
//        }
//        if(Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)){
//            right_link.setLinearVelocity(new Vec2(120f, right_linV.y));
//        }
//        if(Gdx.input.isKeyPressed(Input.Keys.DPAD_UP)){
//            right_link.setLinearVelocity(new Vec2(right_linV.x, -120f));
//        }
//        if(Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN)){
//            right_link.setLinearVelocity(new Vec2(right_linV.x, 120f));
//        }
//
//        if(Gdx.input.isKeyPressed(Input.Keys.A)){
//            left_link.setLinearVelocity(new Vec2(-120f, left_linV.y));
//        }
//        if(Gdx.input.isKeyPressed(Input.Keys.D)){
//            left_link.setLinearVelocity(new Vec2(120f, left_linV.y));
//        }
//        if(Gdx.input.isKeyPressed(Input.Keys.W)){
//            left_link.setLinearVelocity(new Vec2(left_linV.x, -120f));
//        }
//        if(Gdx.input.isKeyPressed(Input.Keys.S)){
//            left_link.setLinearVelocity(new Vec2(left_linV.x, 120f));
//        }