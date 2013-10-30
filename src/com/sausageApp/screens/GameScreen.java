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
import com.sausageApp.Scenario.DefaultScenario;
import com.sausageApp.Scenario.Scenario;


public class GameScreen
        extends
        AbstractScreen
{

    public State state = State.getInstance();

    private Stage stage;
    private SpriteBatch facebatch = new SpriteBatch();
    public Touchpad touchpad_left;
    public Touchpad touchpad_right;
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
        state.setScene("scenarios/level01.json");
        state.setScenario(new DefaultScenario());

        SetupTouchpads();
    }



    public void SetupTouchpads(){
        touchpadSkin = new Skin();
        touchpadSkin.add("touchBackground", new Texture("data/touchBackground.png"));
        touchpadSkin.add("touchKnob", new Texture("data/touchKnob.png"));
        touchpadStyle = new Touchpad.TouchpadStyle();
        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        touchpad_left = new Touchpad(units.P2S(10f), touchpadStyle);
        touchpad_left.setBounds(10f, 10f, w/4f, w/4f);

        touchpad_right = new Touchpad(units.P2S(10f), touchpadStyle);
        touchpad_right.setBounds(w*.75f-10f, 10f, w/4f, w/4f);

        //Create a Stage and add TouchPad
        stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, batch);
        stage.addActor(touchpad_left);
        stage.addActor(touchpad_right);
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
        //Gdx.gl.glDisable(GL20.GL_DEPTH_TEST) ;
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

