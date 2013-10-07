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




    public SplashScreen(
            myGame game )
    {
        super( game );


        Color fragColor = new Color(1.0f,0f,0f,1f);

        String vertexShader =
                "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                + "uniform vec2 nodes[22];                           \n"
                + "varying vec4 frag_pos;                        \n"
                + "vec4 mod;                            \n"
                + "int index;                           \n"
                + "vec2 node; "
                + "vec2 prev; "
                + "vec2 next; "
                + "vec2 next_normal; float next_nrad; float av_nrad;"
                + "vec2 prev_normal; float prev_nrad; "
                + "void main()                   \n"
                + "{                             \n"
                + "   index = int(a_position.z)+1;             \n"
                + "   node = nodes[index]; prev = nodes[index-1]; next = nodes[index+1];              \n"
                +"   next_nrad = atan( (next.y-node.y), next.x-node.x); "
                +"   prev_nrad = atan( (node.y-prev.y), node.x-prev.x); "
                +"   av_nrad = (next_nrad + prev_nrad) / 2.0;"
                + "   next_normal = vec2( cos(av_nrad)*a_position.x - sin(av_nrad)*a_position.y, cos(av_nrad)*a_position.y + sin(av_nrad)*a_position.x );                      \n"
                + "                         \n"
                + "                       \n"
                + "                           \n"
                + "                           \n"
                + "   mod =   vec4(node.x + next_normal.x , node.y + next_normal.y     ,0.0,a_position.w);                        \n"
                + "   gl_Position =   mod;  frag_pos = mod;  \n"
                + "}                             \n";
        String fragmentShader = "#ifdef GL_ES                \n"
                + "precision mediump float;    \n"
                + "varying vec4 frag_pos;                       \n"
                + "#endif                      \n"
                + "void main()                 \n"
                + "{                           \n"
                + "  gl_FragColor = vec4(abs(frag_pos.x),0.0,0.0,1.0);    \n"
                + "}";
        test_shader = new ShaderProgram(vertexShader, fragmentShader);
        mesh = new Mesh(false, 256, 0,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE));
        float[] verts = new float[6*10];

        for (int i=0;i<10;i++){
            //for (int j=0;j<3;j++){
            verts[i*6] = 0.0f;
            verts[i*6+1] = 0.1f;
            verts[i*6+2] = (float)i; //index for node in uniform


            verts[i*6+3] = 0.0f;
            verts[i*6+4] = -0.1f;
            verts[i*6+5] = (float)i;


        }


        short[] indic = new short[]{0, 2,4,1,3,5,4,6,8,5,7,9};

        mesh.setVertices( verts );
        //mesh.setIndices( indic );



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

        test_shader.setUniform2fv("nodes", new float[]{
                .0f,.0f, //repeated first entry
                .0f,.0f,
                .1f,.1f,
                .2f,-.1f,
                .3f,.1f,
                .4f,-.1f,
                .5f,-.1f,
                .6f,-.2f,
                .7f,-.1f,
                .8f,.0f,
                .9f,.1f,
                .9f,.1f // repeated last entry
                    }, 0, 22);
        mesh.render(test_shader, GL20.GL_TRIANGLES);
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