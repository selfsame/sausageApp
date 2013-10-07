package com.sausageApp.Simulation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/4/13
 * Time: 7:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class StaticObject {
    public String type;
    public float w;
    public float h;
    public float x;
    public float y;
    public Sprite sprite;
    public Body body;

    private Scenario scenario;
    private Texture sausageSheet = new Texture("sheet.png");

    private NinePatch chalkbox;
    private TextureRegion block = new TextureRegion(sausageSheet, 32, 32, 32, 32);
    private TextureRegion stone = new TextureRegion(sausageSheet, 32, 0, 32, 32);

    public StaticObject(Scenario _scenario, String _type, float _x, float _y, float _w, float _h){

        Texture chalk = new Texture("chalk_ninepatch.png");
        //chalk.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        chalkbox = new NinePatch(chalk, 4, 12, 8, 12);

        scenario = _scenario;
        type = _type;
        w = scenario.P2S(_w);
        h = scenario.P2S(_h);
        x = scenario.P2S(_x);
        y = scenario.P2S(_y);
        body = scenario.createStatic(_x+(_w/2f), _y-(_h/2f), _w/2f, _h/2f);
        sprite = new Sprite(stone);
        sprite.setBounds(x , scenario.SFlip(y) ,w,h);
        sprite.setOrigin(w/2f,h/2f);


    }

    public void render(SpriteBatch batch){

        chalkbox.draw(batch,sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());



    }

}
