package com.sausageApp.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.sausageApp.Game.State;
import com.sausageApp.Game.Units;
import com.sausageApp.Game.myGame;
import com.sausageApp.Players.Player;







public class GameScreen
        extends
        AbstractScreen
{

    public State state = State.getInstance();

    private Stage stage;
    private SpriteBatch facebatch = new SpriteBatch();
    public Touchpad touchpad;
    private Touchpad.TouchpadStyle touchpadStyle;
    private Skin touchpadSkin;
    private Drawable touchBackground;
    private Drawable touchKnob;
    private Units units = new Units();





    public GameScreen(
            myGame game )
    {
        super( game );
    }


    @Override
    public void show()
    {
        super.show();
        state.setScene("scenarios/video_games.json");

        SetupTouchpads();
    }



    public void SetupTouchpads(){
        //Create a touchpad skin
        touchpadSkin = new Skin();
        //Set background image
        touchpadSkin.add("touchBackground", new Texture("data/touchBackground.png"));
        //Set knob image
        touchpadSkin.add("touchKnob", new Texture("data/touchKnob.png"));
        //Create TouchPad Style
        touchpadStyle = new Touchpad.TouchpadStyle();
        //Create Drawable's from TouchPad skin
        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");
        //Apply the Drawables to the TouchPad Style
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;
        //Create new TouchPad with the created style
        touchpad = new Touchpad(units.P2S(10f), touchpadStyle);
        //setBounds(x,y,width,height)
        touchpad.setBounds(units.P2S(15f), units.P2S(15f), units.P2S(128f), units.P2S(128f));

        //Create a Stage and add TouchPad
        stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, batch);
        stage.addActor(touchpad);
        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(
            float delta )
    {
        long startTime = System.nanoTime();
        super.render( delta );

        Gdx.gl.glClearColor(.1f, .2f, .2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        long endTime = System.nanoTime();

        long duration = endTime - startTime;
        game.profiler.addStat("\nGameScreen.render(): (ms) "+(int)(duration*1.0e-6));



        state.update(delta);

        for(int i = 0; i < game.player_count; i++) {
            Player player = game.players.get(i);
            player.handleInput();
            player.render();
        }

        facebatch.begin();
        for(int i = 0; i < game.player_count; i++) {
            Player player = game.players.get(i);
            player.avatar.drawFace(facebatch,player.sausage.sausage_bodies.get(player.sausage.sausage_bodies.size()-1));
        }



        facebatch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST) ;
        stage.draw();
    }

    @Override
    public void resize( int width, int height) {
        super.resize( width, height );


    }

    @Override
    public void dispose()
    {
        super.dispose();
        batch.dispose();
        facebatch.dispose();
    }
}

