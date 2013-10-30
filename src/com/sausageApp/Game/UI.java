package com.sausageApp.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.sausageApp.Scene.SensorObject;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/29/13
 * Time: 7:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class UI {
    private State state = State.getInstance();
    private Units units = null;
    private SpriteBatch spritebatch = null;
    private BitmapFont title_font = null;
    private BitmapFont small_font = null;
    private OrthographicCamera camera = null;

    private Texture interface_sheet = new Texture("interface.png");
    private TextureRegion O_button = new TextureRegion(interface_sheet, 0, 0, 16, 16);
    private TextureRegion banner_1 = new TextureRegion(interface_sheet, 0, 32, 64, 16);
    private TextureRegion banner_blue = new TextureRegion(interface_sheet, 0, 48, 64, 16);

    private float timer = 0;

    public ArrayList<SensorObject> portals = new ArrayList<SensorObject>();
    public ArrayList<SensorObject> game_portals = new ArrayList<SensorObject>();

    public String game_title = "Sausage Party!";

    public UI(){
        units = new Units();
        spritebatch = new SpriteBatch();
        title_font = new BitmapFont(Gdx.files.internal("data/sausage.fnt"), Gdx.files.internal("data/sausage.png"), false);
        small_font = new BitmapFont();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    }

    public void render(float delta){
        timer += delta;
        spritebatch.setProjectionMatrix(camera.combined);
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST) ;
        spritebatch.begin();
        BitmapFont.TextBounds bounds = title_font.getBounds(game_title);
        title_font.draw(spritebatch, game_title, -bounds.width/2f, Gdx.graphics.getHeight()/2.05f); //Gdx.graphics.getWidth()/2f




        spritebatch.setProjectionMatrix(state.scene.camera.combined);
        if (portals.size() > 0){
            for (SensorObject portal: portals) {
                spritebatch.draw(banner_1, portal.loc[0]-.12f , -portal.loc[1] + ( (float)Math.sin((timer+.02f)*5f))*.03f, .24f,.06f);
                spritebatch.draw(O_button, portal.loc[0]-.03f , -portal.loc[1] +.02f + ( (float)Math.sin(timer*5f))*.04f, .06f,.06f);
            }
            portals.clear();
        }
        if (game_portals.size() > 0){
            for (SensorObject portal: game_portals) {
                spritebatch.draw(banner_blue, portal.loc[0]-.12f , -portal.loc[1] + ( (float)Math.sin((timer+.02f)*5f))*.03f, .24f,.06f);
                spritebatch.draw(O_button, portal.loc[0]-.03f , -portal.loc[1] +.02f + ( (float)Math.sin(timer*5f))*.04f, .06f,.06f);
            }
            game_portals.clear();
        }
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST) ;
        spritebatch.end();
    }
}
