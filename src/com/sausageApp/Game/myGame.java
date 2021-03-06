package com.sausageApp.Game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.Color;
import com.sausageApp.Players.Player;
import com.sausageApp.screens.SplashScreen;
import com.sausageApp.screens.GameScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import org.jbox2d.dynamics.Body;


/**
 * Created with IntelliJ IDEA.
 * User: jparker
 * Date: 10/3/13
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class myGame extends Game{

    public State state = State.getInstance();

    public ArrayList<Player> players = new ArrayList<Player>();
    public Map<Controller, Player> player_map = new HashMap<Controller, Player>();
    public int player_count = 0;
    public ArrayList<Color> player_colors = new ArrayList<Color>();

    public Profiler profiler;

    public Screen current_screen;
    public GameScreen game_screen = null;

    public SplashScreen getSplashScreen()
    {
        return new SplashScreen( this );
    }



    public GameScreen getGameScreen()
    {
        game_screen = new GameScreen( this );
        return game_screen;
    }

    @Override
    public void create() {
        profiler = new Profiler();
        state.game = this;
        player_colors.add(new Color(.8f,.5f, .5f, 1f));
        player_colors.add(new Color(.8f, .8f, .2f, 1f));

        setScreen(getSplashScreen());
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

        profiler.render();
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
        current_screen = screen;
        Gdx.app.log( "APP", "Setting screen: " + screen.getClass().getSimpleName() );
    }

    @Override
    public void dispose()
    {

        Gdx.app.log( "HELP", "Disposing game" );
        super.dispose();
    }
}
