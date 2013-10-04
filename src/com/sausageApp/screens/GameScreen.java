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
            new_player.render_mode = player_count;

            player_count += 1;

        }

        if (player_count == 0){
            Player new_player = new Player(scenario,  spawn_points.get(player_count), colors.get(player_count));
            players.add( new_player );
            player_count += 1;
        }

    }




    @Override
    public void render(
            float delta )
    {
        super.render( delta );
        scenario.step( delta );

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        for(int i = 0; i < player_count; i++) {
            Player player = players.get(i);
            player.handleInput();
            player.render();

        }

        batch.begin();

        for(int i = 0; i < scenario.statics.size(); i++) {
            float fx =  scenario.statics.get(i).getPosition().x ;
            float fy =  scenario.flipY(scenario.statics.get(i).getPosition().y );
            Vec2 fv =  scenario.getDimensions(scenario.statics.get(i));
            //fv = new Vec2(scenario.unconvertX(fv.x), scenario.unconvertY(fv.y));
            batch.draw(stone, fx, fy, fv.x/2f, fv.y/2f);
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

