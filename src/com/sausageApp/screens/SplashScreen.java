package com.sausageApp.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.sausageApp.Game.myGame;

public class SplashScreen
        extends
        AbstractScreen
{
    private Texture splashTexture;
    private TextureRegion splashTextureRegion;
    private Stage stage;

    public SplashScreen(
            myGame game )
    {
        super( game );
    }


    @Override
    public void show()
    {
        super.show();

        stage = new Stage();
        Skin skin = super.getSkin();

        Gdx.input.setInputProcessor(stage);
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // label "welcome"
        final TextButton button = new TextButton("New Game!", skin);
        button.setPosition(Gdx.graphics.getWidth()*.6f+8, Gdx.graphics.getHeight()-160);
        button.setSize(Gdx.graphics.getWidth()*.2f, 80);
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
        super.render( delta );
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );
        batch.begin();
        batch.draw( splashTextureRegion, 0, Gdx.graphics.getHeight()-Gdx.graphics.getWidth()*.6f, Gdx.graphics.getWidth()*.6f, Gdx.graphics.getWidth()*.6f );
        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
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