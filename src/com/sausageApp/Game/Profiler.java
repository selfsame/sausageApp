package com.sausageApp.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/5/13
 * Time: 7:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class Profiler {
    private Units units = new Units();
    private SpriteBatch spritebatch;
    private BitmapFont font;

    private String stats = "\n";

    public Profiler(){
        spritebatch = new SpriteBatch();
        font = new BitmapFont();
    }
    public void addStat(String stat){
        stats += stat;
    }
    public void render(){

        spritebatch.begin();
        font.drawMultiLine(spritebatch, "FPS: "+((int)(1f/ Gdx.graphics.getDeltaTime()))+"\nRATIO:"+units.RATIO+stats, 20f, Gdx.graphics.getHeight()-10f);
        spritebatch.end();

        stats = "\n";
    }
}

