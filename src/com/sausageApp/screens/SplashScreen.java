package com.sausageApp.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.sausageApp.Game.myGame;
import com.sausageApp.Players.Avatar;
import com.sausageApp.Players.Player;
import tv.ouya.console.api.OuyaController;

public class SplashScreen
        extends
        AbstractScreen
{
    private Texture splashTexture;
    private TextureRegion splashTextureRegion;
    private Stage stage;

// shader test

    public ShaderProgram test_shader;
    public Mesh mesh;
    private float time = 0.f;

    private float ticker = .0f;


    public SplashScreen(
            myGame game )
    {
        super( game );




        WormMesh worm = new WormMesh(10);
        mesh = worm.CompileMesh();
        test_shader = worm.MakeDebugShader();




    }

    public void FindControllers(){
        for(Controller controller: Controllers.getControllers()) {
            if (!game.player_map.containsKey(controller)){
                Player new_player = new Player(controller, game.player_colors.get(game.player_count), game.player_count);
                game.players.add( new_player );
                game.player_map.put(controller, new_player);
                game.player_count += 1;
            }
        }
        if (game.player_count == 0){
            Player new_player = new Player(game.player_colors.get(game.player_count), game.player_count);
            game.players.add( new_player );
            game.player_count += 1;
        }
    }

    @Override
    public void show()
    {
        super.show();

        FindControllers();

        stage = new Stage();



        Skin skin = super.getSkin();

        Gdx.input.setInputProcessor(stage);



        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // label "welcome"
        final TextButton button = new TextButton("New Game!", skin);
        button.setPosition(Gdx.graphics.getWidth()*.4f, Gdx.graphics.getHeight()*.15f);
        button.setSize(Gdx.graphics.getWidth()*.2f, Gdx.graphics.getWidth()*.1f);
        stage.addActor( button );

        // load the splash image and create the texture region
        splashTexture = new Texture( "splash.jpg" );
        splashTexture.setFilter( TextureFilter.Linear, TextureFilter.Linear );
        splashTextureRegion = new TextureRegion( splashTexture, 0, 0, 512, 512 );

        button.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen( game.getGameScreen() );
                return false;
            }
        });



    }

    @Override
    public void render(
            float delta )
    {
        float gamew = Gdx.graphics.getWidth();
        float gameh = Gdx.graphics.getHeight();
        super.render( delta );
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );
        batch.begin();

        for (int i = 0; i < game.players.size(); i++){
            game.players.get(i).avatar.drawPortrait(batch, gamew*.1f+i*gamew*.25f, gameh*.6f, Gdx.graphics.getWidth()*.2f, Gdx.graphics.getWidth()*.2f);
            game.players.get(i).handleInput();
        }

        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        for(Controller controller: Controllers.getControllers()) {
            boolean BUTTON_O = controller.getButton(OuyaController.BUTTON_O);
            if (BUTTON_O) {
                game.setScreen( game.getGameScreen() );
            }
            boolean BUTTON_A = controller.getButton(OuyaController.BUTTON_A);
            if (BUTTON_A) {
                Gdx.app.exit();
            }

        }


        Gdx.gl20.glLineWidth(2f);
        test_shader.begin();
        ticker += .01f;
        //float ts = (float)Math.sin(ticker)*.1f;
        float ts = 0f;
//        test_shader.setUniform2fv("nodes", new float[]{
//                .01f,.9f, //repeated first entry
//                .05f,.8f,
//                -.05f,.7f-ts,
//                .1f,.6f+ts,
//                -.1f,.5f-ts,
//                .05f,.4f+ts,
//                .0f,-.06f-ts,
//                .1f,.05f+ts,
//                .2f,.1f-ts,
//                .3f,.0f+ts,
//                .4f,-.1f-ts,
//                .5f,-.2f, // repeated last entry
//                    }, 0, 24);
        test_shader.setUniform2fv("nodes", new float[]{
                .0f,.0f, //repeated first entry
                .0f,.0f,
                .1f,.0f,
                .2f,-.2f,
                .3f,.0f,
                .4f,.0f,
                .5f,-.1f,
                .6f,.0f,
                .7f,.1f,
                .8f,.0f,
                .9f,.0f,
                .9f,.0f, // repeated last entry
        }, 0, 24);
//        test_shader.setUniform1fv("concavity", new float[]{
//                1f, //repeated first entry
//                1f,
//                1f,
//                1f,
//                1f,
//                1f,
//                1f,
//                1f,
//                1f,
//                1f,
//                1f,
//                1f, // repeated last entry
//        }, 0, 12);
        mesh.render(test_shader, GL20.GL_LINES);
        test_shader.end();

    }

    @Override
    public void resize( int width, int height) {
        super.resize( width, height );
        stage.setViewport(width, height, true);


    }

    @Override
    public void dispose()
    {

        splashTexture.dispose();
        stage.dispose();
        super.dispose();
    }
}