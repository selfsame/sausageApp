package com.sausageApp.Players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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

    public boolean desktop = false;
    public Controller controller;
    public ArrayList<Body> sausage;
    public Body right_link;
    public Body left_link;
    public Color color;
    public int render_mode = 0;
    public int sausage_length = 10;

    private Scenario scenario;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    public Player(Scenario scen, Controller cont, Vec2 spawn, Color col){
        scenario = scen;
        controller = cont;
        sausage = scenario.createSausage(spawn.x, spawn.y, 16f, sausage_length);
        color = col;
        left_link = sausage.get(0);
        right_link = sausage.get(sausage_length);

    }

    public Player(Scenario scen, Vec2 spawn, Color col){
        desktop = true;
        scenario = scen;
        sausage = scenario.createSausage(spawn.x, spawn.y, 16f, sausage_length);
        color = col;
        left_link = sausage.get(0);
        right_link = sausage.get(sausage_length);

    }

    public void handleInput(){
        Vec2 right_linV = right_link.getLinearVelocity();
        Vec2 left_linV = left_link.getLinearVelocity();
        if (desktop) {
            if(Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT)){
                right_link.setLinearVelocity(new Vec2(-120f, right_linV.y));
            }
            if(Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)){
                right_link.setLinearVelocity(new Vec2(120f, right_linV.y));
            }
            if(Gdx.input.isKeyPressed(Input.Keys.DPAD_UP)){
                right_link.setLinearVelocity(new Vec2(right_linV.x, -120f));
            }
            if(Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN)){
                right_link.setLinearVelocity(new Vec2(right_linV.x, 120f));
            }

            if(Gdx.input.isKeyPressed(Input.Keys.A)){
                left_link.setLinearVelocity(new Vec2(-120f, left_linV.y));
            }
            if(Gdx.input.isKeyPressed(Input.Keys.D)){
                left_link.setLinearVelocity(new Vec2(120f, left_linV.y));
            }
            if(Gdx.input.isKeyPressed(Input.Keys.W)){
                left_link.setLinearVelocity(new Vec2(left_linV.x, -120f));
            }
            if(Gdx.input.isKeyPressed(Input.Keys.S)){
                left_link.setLinearVelocity(new Vec2(left_linV.x, 120f));
            }
        } else {
            float leftXAxis = controller.getAxis(Ouya.AXIS_LEFT_X);
            float leftYAxis = controller.getAxis(Ouya.AXIS_LEFT_Y);
            float rightXAxis = controller.getAxis(Ouya.AXIS_RIGHT_X);
            float rightYAxis = controller.getAxis(Ouya.AXIS_RIGHT_Y);
            left_link.setLinearVelocity(new Vec2(left_linV.x+leftXAxis*50f, left_linV.y+leftYAxis*50f));
            right_link.setLinearVelocity(new Vec2(right_linV.x+rightXAxis*50f, right_linV.y+rightYAxis*50f));
        }
    }

    public void render(){
        shapeRenderer.setColor(color);
        if (render_mode == 0){
            drawLineSausage();
        } else if (render_mode == 1){
            drawCrayonSausage();
        } else if (render_mode == 2){
            drawSolidSausage();
        }
    }

    public void drawSolidSausage(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.FilledCircle);
        for(int i = 0; i < sausage_length; i++) {
            float x =  scenario.convertX(sausage.get(i).getPosition().x) ;
            float y =  scenario.flipY(scenario.convertY( sausage.get(i).getPosition().y ));
            shapeRenderer.filledCircle(x, y, 16f);
        }
        shapeRenderer.end();
    }

    public void drawLineSausage(){
        Gdx.gl20.glLineWidth(2.0f);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Vec2 pos = scenario.convertVec2(sausage.get(0).getPosition());
        float last_x = pos.x ;
        float last_y = scenario.flipY( pos.y ) ;
        float r =  sausage.get(0).getAngle()+1.57079633f;
        Vec2 last_g = new Vec2((float)  Math.cos(-r)*16, (float) Math.sin(-r)*16);

        for(int i = 1; i < 13; i++) {
            r += 0.26179;
            Vec2 g = new Vec2((float)  Math.cos(-r)*16, (float) Math.sin(-r)*16);
            shapeRenderer.line(last_x+last_g.x, last_y+last_g.y, last_x+g.x, last_y+g.y);
            last_g = g;
        }
        r =  sausage.get(0).getAngle()+1.57079633f;
        last_g = new Vec2((float)  Math.cos(-r)*16, (float) Math.sin(-r)*16);

        for(int i = 1; i < sausage_length; i++) {
            pos = scenario.convertVec2(sausage.get(i).getPosition());
            float x =  pos.x ;
            float y =  scenario.flipY( pos.y );
            r =  sausage.get(i).getAngle()+1.57079633f;
            Vec2 g = new Vec2((float)  Math.cos(-r)*16, (float) Math.sin(-r)*16);
            shapeRenderer.line(last_x+last_g.x, last_y+last_g.y, x+g.x, y+g.y);
            shapeRenderer.line(last_x-last_g.x, last_y-last_g.y, x-g.x, y-g.y);
            last_x = x;
            last_y = y;
            last_g = g;
        }

        for(int i = 1; i < 13; i++) {
            r -= 0.26179;
            Vec2 g = new Vec2((float)  Math.cos(-r)*16, (float) Math.sin(-r)*16);
            shapeRenderer.line(last_x+last_g.x, last_y+last_g.y, last_x+g.x, last_y+g.y);
            last_g = g;
        }
        shapeRenderer.end();
    }

    public float subDif(float v1, float v2){
        return v1+(v1-v2);
    }

    public void drawCrayonSausage(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Curve);
        Gdx.gl20.glLineWidth(3.0f);
        for(int i = 0; i < sausage_length; i++) {

            if ( i > 0 && i < sausage_length-2 ){
                float x1 =  sausage.get(i-1).getPosition().x ;
                float y1 =  scenario.convertY( sausage.get(i-1).getPosition().y );
                float r1 =  sausage.get(i-1).getAngle()+1.57079633f;
                Vec2 g1 = new Vec2((float)  Math.cos(-r1)*16, (float) Math.sin(-r1)*16);

                float x2 =  sausage.get(i).getPosition().x ;
                float y2 =  scenario.convertY( sausage.get(i).getPosition().y );
                float r2 =  sausage.get(i).getAngle()+1.57079633f;
                Vec2 g2 = new Vec2((float)  Math.cos(-r2)*16, (float) Math.sin(-r2)*16);

                float x3 =  sausage.get(i+1).getPosition().x ;
                float y3 =  scenario.convertY( sausage.get(i+1).getPosition().y );
                float r3 =  sausage.get(i+1).getAngle()+1.57079633f;
                Vec2 g3 = new Vec2((float)  Math.cos(-r3)*16, (float) Math.sin(-r3)*16);

                float x4 =  sausage.get(i+2).getPosition().x ;
                float y4 =  scenario.convertY( sausage.get(i+2).getPosition().y );
                float r4 =  sausage.get(i+2).getAngle()+1.57079633f;
                Vec2 g4 = new Vec2((float)  Math.cos(-r3)*16, (float) Math.sin(-r3)*16);

                shapeRenderer.curve(x2+g2.x, y2+g2.y, subDif(x2+g2.x, x1+g1.x), subDif(y2+g2.y,y1+g1.y), subDif(x3+g3.x, x4+g4.x), subDif(y3+g3.y,y4+g4.y),  x3+g3.x, y3+g3.y);
                shapeRenderer.curve(x2-g2.x, y2-g2.y, subDif(x2-g2.x, x1-g1.x), subDif(y2-g2.y,y1-g1.y), subDif(x3-g3.x, x4-g4.x), subDif(y3-g3.y,y4-g4.y),  x3-g3.x, y3-g3.y);

            }
        }

        shapeRenderer.end();

    }

}



