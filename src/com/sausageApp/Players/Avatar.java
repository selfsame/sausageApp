package com.sausageApp.Players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/4/13
 * Time: 12:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class Avatar {

    private Texture backdrop_texture = new Texture("portrait_back.png");
    private Texture avatar_texture = new Texture("avatar.png");
    private Texture arrows = new Texture("arrows.png");
    private TextureRegion arrow_active_left = new TextureRegion(arrows, 0, 0, 32, 32);
    private TextureRegion arrow_active_right = new TextureRegion(arrows, 32, 0, 32, 32);
    private TextureRegion arrow_inactive_left = new TextureRegion(arrows, 0, 32, 32, 32);
    private TextureRegion arrow_inactive_right = new TextureRegion(arrows, 32, 32, 32, 32);

    private TextureRegion backdrop = new TextureRegion(backdrop_texture, 0, 0, 256, 256);
    private Sprite backdrop_sprite;
    private float bgr = 0f;

    private Sprite mouth_sprite;
    private Sprite eyes_sprite;

    private TextureRegion head = new TextureRegion(avatar_texture, 0, 128, 128, 128);

    private ArrayList<TextureRegion> eyes = new ArrayList<TextureRegion>();
    private ArrayList<TextureRegion> mouths = new ArrayList<TextureRegion>();

    public int current_eyes = 1;
    public int current_mouth = 1;
    public Color background_color = new Color(1f,.2f,.2f,1f);

    public Player player;

    public Avatar(Player _player){
        player = _player;
        backdrop_sprite = new Sprite(backdrop);




       eyes.add( new TextureRegion(avatar_texture, 128, 0, 128, 32) );
        eyes.add( new TextureRegion(avatar_texture, 128, 32, 128, 32) );
        eyes.add( new TextureRegion(avatar_texture, 128, 64, 128, 32) );

        mouths.add( new TextureRegion(avatar_texture, 0, 0, 128, 32) );
        mouths.add( new TextureRegion(avatar_texture, 0, 32, 128, 32) );
        mouths.add( new TextureRegion(avatar_texture, 0, 64, 128, 32) );
        mouths.add( new TextureRegion(avatar_texture, 0, 96, 128, 32) );

        mouth_sprite = new Sprite(mouths.get(current_mouth));
        eyes_sprite = new Sprite(eyes.get(current_eyes));
    }

    public void changeEyes(int inc){
        if (current_eyes+inc >= eyes.size()) {
            current_eyes = 0;
        } else if (current_eyes+inc < 0) {
            current_eyes = eyes.size() - 1;
        } else {
            current_eyes += inc;
        }
        eyes_sprite = new Sprite(eyes.get(current_eyes));
    }
    public void changeMouth(int inc){
        if (current_mouth+inc >= mouths.size()) {
            current_mouth = 0;
        } else if (current_mouth+inc < 0){
             current_mouth = mouths.size()-1;
        }  else {
            current_mouth += inc;
        }
        mouth_sprite = new Sprite(mouths.get(current_mouth));
    }

    public void drawPortrait(SpriteBatch batch, float x, float y, float w, float h){
        bgr += .6f;
        backdrop_sprite.setBounds(x , y ,w,h);
        backdrop_sprite.setOrigin(w / 2f, h / 2f);
        backdrop_sprite.setRotation(bgr);
        backdrop_sprite.setColor(background_color);
        backdrop_sprite.draw(batch);



        batch.draw( head, x+w*.24f, y+w*.2f, w*.5f, w*.5f );
        batch.draw( eyes.get(current_eyes), x+w*.24f, y+w*.5f, w*.5f, w*.125f );
        batch.draw( mouths.get(current_mouth), x+w*.24f, y+w*.4f, w*.5f, w*.125f );

        batch.draw( arrow_active_left, x+w*.1f, y+w*.55f, w*.06f, w*.06f);
        batch.draw( arrow_active_right, x+w*.8f, y+w*.55f, w*.06f, w*.06f);

        batch.draw( arrow_inactive_left, x+w*.1f, y+w*.4f, w*.06f, w*.06f);
        batch.draw( arrow_inactive_right, x+w*.8f, y+w*.4f, w*.06f, w*.06f);
    }

    public float un(float a){
        return player.scenario.P2S(a);
    }

    public void drawFace(SpriteBatch batch, Body body){

        Vec2 bv = player.scenario.B2S(body.getPosition());
        float br = body.getAngle();
        //Vec2 g = new Vec2((float)  Math.cos(-r)*16, (float) Math.sin(-r)*16);
        eyes_sprite.setBounds(bv.x - un(12f) , player.scenario.SFlip(bv.y) ,un(24f),un(8f));
        eyes_sprite.setOrigin(un(12f), 0f);
        eyes_sprite.setRotation((float) (Math.toDegrees(br)+90f)*-1f);
        eyes_sprite.draw(batch);

        mouth_sprite.setBounds(bv.x-un(12f) , player.scenario.SFlip(bv.y+un(8f)) ,un(24f),un(8f));
        mouth_sprite.setOrigin(un(12f), un(8f));
        mouth_sprite.setRotation((float) (Math.toDegrees(br)+90f)*-1f);
        mouth_sprite.draw(batch);
    }
}
