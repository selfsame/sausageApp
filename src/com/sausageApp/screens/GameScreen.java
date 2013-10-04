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
import java.util.Map;

import com.badlogic.gdx.math.CatmullRomSpline;






public class GameScreen
        extends
        AbstractScreen
{
    private Texture sausageSheet = new Texture("sheet.png");
    private TextureRegion sausageLink = new TextureRegion(sausageSheet, 0, 0, 32, 32);
    private TextureRegion sausageMiddle = new TextureRegion(sausageSheet, 0, 32, 32, 32);

    private Scenario scenario;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    private float last_x = 0f;
    private float last_y = 0f;
    private float r = 0f;



    public ArrayList<Vec2> spawn_points = new ArrayList<Vec2>();



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

        for (int i = 0; i < game.players.size(); i++){
            game.players.get(i).SetScenario(scenario,spawn_points.get(game.players.get(i).uid));

        }

    }




    @Override
    public void render(
            float delta )
    {
        super.render( delta );
        scenario.step( delta );

        Gdx.gl.glClearColor(.1f, .2f, .2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        for(int i = 0; i < game.player_count; i++) {
            Player player = game.players.get(i);
            player.handleInput();
            player.render();
        }

        batch.begin();
        for (int i = 0; i < scenario.statics.size(); i++) scenario.statics.get(i).render(batch);
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

