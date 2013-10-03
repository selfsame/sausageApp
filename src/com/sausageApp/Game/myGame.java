package com.sausageApp.Game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.sausageApp.screens.SplashScreen;
import com.sausageApp.screens.GameScreen;

//import org.jbox2d.dynamics.Body;


/**
 * Created with IntelliJ IDEA.
 * User: jparker
 * Date: 10/3/13
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class myGame extends Game{

    private Texture splashTexture;
    private TextureRegion splashTextureRegion;
    private SpriteBatch batch;

    public SplashScreen getSplashScreen()
    {
        return new SplashScreen( this );
    }

    public GameScreen getGameScreen()
    {
        return new GameScreen( this );
    }

    @Override
    public void create() {
        setScreen( getSplashScreen() );
    }

    @Override
    public void resize(

            int width,
            int height )
    {
        super.resize(width, height);
        Gdx.app.log( "HELP", "Resizing game to: " + width + " x " + height );
    }

    @Override
    public void render( )
    {
        super.render();

    }
    @Override
    public void pause()
    {
        super.pause();
        Gdx.app.log( "HELP", "Pausing game" );
    }

    @Override
    public void resume()
    {
        super.resume();
        Gdx.app.log( "HELP", "Resuming game" );
    }

    @Override
    public void setScreen(
            Screen screen )
    {
        super.setScreen( screen );
        Gdx.app.log( "APP", "Setting screen: " + screen.getClass().getSimpleName() );
    }

    @Override
    public void dispose()
    {

        Gdx.app.log( "HELP", "Disposing game" );
        super.dispose();
    }
}
