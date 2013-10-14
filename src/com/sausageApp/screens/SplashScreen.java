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
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.sausageApp.Game.myGame;
import com.sausageApp.Players.Avatar;
import com.sausageApp.Players.Player;
import com.sausageApp.Simulation.LevelMeshCompiler;
import com.sausageApp.Simulation.ScenarioData;
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

    private JsonReader JSON = new JsonReader();
    private Json json = new Json();
    private float ticker = .0f;

    ScenarioData scene = json.fromJson(ScenarioData.class, Gdx.files.internal( "scenarios/level01.json" ));
    private Mesh level_mesh;

    public SplashScreen(
            myGame game )
    {
        super( game );







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


        ticker += .01f;
        float ts = 0f;




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