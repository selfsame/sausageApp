package com.sausageApp.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.sausageApp.Game.myGame;
import com.sausageApp.Simulation.Scenario;
import org.jbox2d.common.Vec2;


public class GameScreen
        extends
        AbstractScreen
{
    private Texture sausageSheet = new Texture("sheet.png");
    private TextureRegion sausageLink = new TextureRegion(sausageSheet, 0, 0, 32, 32);
    private TextureRegion stone = new TextureRegion(sausageSheet, 32, 0, 32, 32);
    private Scenario scenario;


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
    }

    @Override
    public void render(
            float delta )
    {
        super.render( delta );

        scenario.step( delta );

        //Gdx.app.log( "BODY", "y:" + scenario.box1.getPosition().y );

        float x =  scenario.box1.getPosition().x ;
        float y =  scenario.convertY( scenario.box1.getPosition().y );

        float fx =  scenario.floor.getPosition().x ;
        float fy =  scenario.convertY( scenario.floor.getPosition().y );
        Vec2 fv =  scenario.getDimensions(scenario.floor);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(sausageLink, x, y);
        batch.draw(stone, fx, fy, fv.x, fv.y);

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