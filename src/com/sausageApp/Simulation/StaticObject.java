package com.sausageApp.Simulation;

import com.badlogic.gdx.graphics.Texture;
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
    private TextureRegion block = new TextureRegion(sausageSheet, 32, 32, 32, 32);
    private TextureRegion stone = new TextureRegion(sausageSheet, 32, 0, 32, 32);

    public StaticObject(Scenario _scenario, String _type, float _x, float _y, float _w, float _h){
        scenario = _scenario;
        type = _type;
        w = scenario.unconvertX(_w);
        h = scenario.unconvertX(_h);
        x = scenario.unconvertX(_x);
        y = scenario.unconvertX(_y);
        body = scenario.createStatic(x+(w/2f), y, w/2f, h);
        sprite = new Sprite(stone);
        sprite.setBounds(x , scenario.flipY(y) ,w,h);
        sprite.setOrigin(w/2f,h/2f);
    }

    public void render(SpriteBatch batch){
        sprite.draw(batch);

    }

}
