package com.sausageApp.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.sausageApp.Game.myGame;
import com.sausageApp.Players.Player;
import com.sausageApp.Simulation.Scenario;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.CatmullRomSpline;






public class GameScreen
        extends
        AbstractScreen
{
    private Texture sausageSheet = new Texture("sheet.png");
    private TextureRegion sausageLink = new TextureRegion(sausageSheet, 0, 0, 32, 32);
    private TextureRegion sausageMiddle = new TextureRegion(sausageSheet, 0, 32, 32, 32);
    private TextureRegion block = new TextureRegion(sausageSheet, 32, 32, 32, 32);
    private TextureRegion stone = new TextureRegion(sausageSheet, 32, 0, 32, 32);
    private Scenario scenario;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    private float last_x = 0f;
    private float last_y = 0f;
    private float r = 0f;

    public ArrayList<Player> players = new ArrayList<Player>();
    public int player_count = 0;

    public ArrayList<Vec2> spawn_points = new ArrayList<Vec2>();
    public ArrayList<Color> colors = new ArrayList<Color>();


    public GameScreen(
            myGame game )
    {
        super( game );
    }


    @Override
    public void show()
    {
        super.show();
        scenario = new Scenario();

        spawn_points.add(new Vec2(100f,100f));
        spawn_points.add(new Vec2(300f,150f));
        colors.add(new Color(1.0f, .6f, .6f, 1.0f));
        colors.add(new Color(.4f, .6f, 1f, 1.0f));


        for(Controller controller: Controllers.getControllers()) {
            Player new_player = new Player(scenario, controller,  spawn_points.get(player_count), colors.get(player_count));
            players.add( new_player );
            player_count += 1;
        }

        if (player_count == 0){
            Player new_player = new Player(scenario,  spawn_points.get(player_count), colors.get(player_count));
            players.add( new_player );
            player_count += 1;
        }

    }



    public void drawLineSausage(ArrayList<Body> sausage){
        Gdx.gl20.glLineWidth(1.0f);


        shapeRenderer.begin(ShapeRenderer.ShapeType.FilledCircle);

        for(int i = 0; i < 20; i++) {
            float x =  sausage.get(i).getPosition().x ;
            float y =  scenario.convertY( sausage.get(i).getPosition().y );
            shapeRenderer.filledCircle(x, y, 16f);

        }
        shapeRenderer.end();



        shapeRenderer.setColor(new Color(1f, 1f, 1f, 1f));

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        last_x = sausage.get(0).getPosition().x ;
        last_y = scenario.convertY( sausage.get(0).getPosition().y ) ;
        r =  sausage.get(0).getAngle()+1.57079633f;
        Vec2 last_g = new Vec2((float)  Math.cos(-r)*16, (float) Math.sin(-r)*16);

        for(int i = 1; i < 13; i++) {
            r += 0.26179;
            Vec2 g = new Vec2((float)  Math.cos(-r)*16, (float) Math.sin(-r)*16);
            shapeRenderer.line(last_x+last_g.x, last_y+last_g.y, last_x+g.x, last_y+g.y);
            last_g = g;
        }
        r =  sausage.get(0).getAngle()+1.57079633f;
        last_g = new Vec2((float)  Math.cos(-r)*16, (float) Math.sin(-r)*16);

        for(int i = 1; i < 20; i++) {
            float x =  sausage.get(i).getPosition().x ;
            float y =  scenario.convertY( sausage.get(i).getPosition().y );


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



        shapeRenderer.begin(ShapeRenderer.ShapeType.Curve);


        Gdx.gl20.glLineWidth(3.0f);

        for(int i = 0; i < 20; i++) {
            float x =  sausage.get(i).getPosition().x ;
            float y =  scenario.convertY( sausage.get(i).getPosition().y );




            if ( i < 19 && i % 2 == 0){
                float r1 =  sausage.get(i).getAngle()+1.57079633f;
                Vec2 g1 = new Vec2((float)  Math.cos(-r1)*16, (float) Math.sin(-r1)*16);
                float r2 =  sausage.get(i+1).getAngle()+1.57079633f;
                Vec2 g2 = new Vec2((float)  Math.cos(-r2)*16, (float) Math.sin(-r2)*16);
                float r3 =  sausage.get(i+2).getAngle()+1.57079633f;
                Vec2 g3 = new Vec2((float)  Math.cos(-r3)*16, (float) Math.sin(-r3)*16);

                float x2 =  sausage.get(i+1).getPosition().x ;
                float y2 =  scenario.convertY( sausage.get(i+1).getPosition().y );
                float x3 =  sausage.get(i+2).getPosition().x ;
                float y3 =  scenario.convertY( sausage.get(i+2).getPosition().y );
                float cx1 = (x2+g2.x + x+g1.x) / 2;
                float cy1 = (y2+g2.y + y+g1.y) / 2;
                float cx2 = (x2+g2.x + x3+g3.x) / 2;
                float cy2 = (y2+g2.y + y3+g3.y) / 2;
                shapeRenderer.setColor(new Color(1f, 0f, 1f, 1f));
                shapeRenderer.curve(x+g1.x, y+g1.y, cx1, cy1, cx2, cy2, x3+g3.x, y3+g3.y);

                cx1 = (x2-g2.x + x-g1.x) / 2;
                cy1 = (y2-g2.y + y-g1.y) / 2;
                cx2 = (x2-g2.x + x3-g3.x) / 2;
                cy2 = (y2-g2.y + y3-g3.y) / 2;
                shapeRenderer.setColor(new Color(0f, 1f, 1f, 1f));
                shapeRenderer.curve(x-g1.x, y-g1.y, cx1, cy1, cx2, cy2, x3-g3.x, y3-g3.y);

            }
        }




        shapeRenderer.end();



    }

    @Override
    public void render(
            float delta )
    {
        super.render( delta );

        scenario.step( delta );

        //Gdx.app.log( "BODY", "y:" + scenario.box1.getPosition().y );





        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        for(int i = 0; i < player_count; i++) {
            Player player = players.get(i);
            player.handleInput();
            shapeRenderer.setColor(player.color);
            drawLineSausage(player.sausage);
        }

        batch.begin();

        for(int i = 0; i < 4; i++) {
            float fx =  scenario.statics.get(i).getPosition().x ;
            float fy =  scenario.convertY( scenario.statics.get(i).getPosition().y );
            Vec2 fv =  scenario.getDimensions(scenario.statics.get(i));
            batch.draw(stone, fx-(fv.x/2f), fy-(fv.y/2f), fv.x, fv.y);
        }

        for(int i = 0; i < 2; i++) {
            float fx =  scenario.dynamics.get(i).getPosition().x ;
            float fy =  scenario.convertY( scenario.dynamics.get(i).getPosition().y );
            r =  scenario.dynamics.get(i).getAngle()* MathUtils.radiansToDegrees;
            Vec2 fv =  scenario.dynamic_sizes.get(i);

            Sprite dyn = new Sprite(block);
            dyn.setBounds(fx, fy, fv.x, fv.y);
            dyn.setRotation(-r);
            dyn.setOrigin(fv.x / 2, fv.y / 2);
            dyn.draw(batch);

        }

        batch.end();




    }

    @Override
    public void resize( int width, int height) {
        super.resize( width, height );


    }

    @Override
    public void dispose()
    {
        sausageSheet.dispose();
        super.dispose();
    }
}


//for(int i = 0; i < 20; i++) {
//        float fx =  scenario.sausages.get(i).getPosition().x ;
//float fy =  scenario.convertY( scenario.sausages.get(i).getPosition().y );
//r =  scenario.sausages.get(i).getAngle()* MathUtils.radiansToDegrees;
//Vec2 fv =  scenario.getDimensions(scenario.sausages.get(i));
//Sprite dyn = new Sprite();
//if (i == 0 || i == 19){
//        dyn.setRegion(sausageLink);
//} else {
//        dyn.setRegion(sausageMiddle);
//}
//
//
//        dyn.setBounds(fx, fy, fv.x, fv.y);
//dyn.setRotation(-r);
//dyn.setOrigin(fv.x/2,fv.y/2);
////dyn.draw(batch);
//}