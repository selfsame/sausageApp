package com.sausageApp.Game;

import com.badlogic.gdx.Gdx;
import org.jbox2d.common.Vec2;

/**
 * Created with IntelliJ IDEA.
 * User: jparker
 * Date: 10/28/13
 * Time: 1:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class Units {
    public float RATIO = (float)Gdx.graphics.getHeight()/(float)Gdx.graphics.getWidth() ;

    public Units(){
        RATIO = (float)Gdx.graphics.getHeight()/(float)Gdx.graphics.getWidth() ;
    }
    public Vec2 S2gl(Vec2 v){
        float SCALE = 4f;
        float y = (float)( (Gdx.graphics.getHeight()-(v.y*SCALE))  /(Gdx.graphics.getHeight()*2f)-1f) ;
        float x = (float)((v.x*SCALE)/(Gdx.graphics.getWidth()*2f)-1f) ;
        return new Vec2( x, y );
    }

    public Vec2 gl2S(Vec2 v){
        float SCALE = 4f;
        float y = (float)(((-1f*v.y)+1f)/2f)*(Gdx.graphics.getHeight()) ;
        float x = (float)((v.x+1f)/2f)*(Gdx.graphics.getWidth()) ;
        return new Vec2( x, y );
    }

    public float S2gl(float f){
        float SCALE = 4f;
        f = (float)((f*SCALE)/(Gdx.graphics.getWidth()*2f)-1f) ;
        return f;
    }

    public float gl2S(float f){

        float x = (float)((f+1f)/2f)*(Gdx.graphics.getWidth()) ;
        return f;
    }

    // 1024 576
    private float PHYSICS_SCALE = 40f;

    // Pixel scale to Box2d scale
    public float P2B(float a){
        return a/PHYSICS_SCALE;
    }
    public Vec2 P2B(Vec2 v){
        return new Vec2(P2B(v.x), P2B(v.y));
    }
    //Pixel scale to screen scale
    public float P2S(float a){
        return a / (1024f/Gdx.graphics.getWidth()) ;
    }
    public Vec2 P2S(Vec2 v){
        return new Vec2(P2S(v.x), P2S(v.y));
    }
    //Box2d scale to Pixel scale
    public float B2P(float a){
        return a*PHYSICS_SCALE;
    }
    public Vec2 B2P(Vec2 v){
        return new Vec2(B2P(v.x), B2P(v.y));
    }
    //Box2d scale to Screen scale
    public float B2S(float a){
        return P2S(B2P(a));
    }
    public Vec2 B2S(Vec2 v){
        return new Vec2(B2S(v.x), B2S(v.y));
    }
    //Screen scale to Pixel scale
    public float S2P(float a){
        return (1024f/Gdx.graphics.getWidth()) * a ;
    }
    public Vec2 S2P(Vec2 v){
        return new Vec2(S2P(v.x), S2P(v.y));
    }
    //Screen scale to Box2d scale
    public float S2B(float a){
        return P2B(S2P(a));
    }
    public Vec2 S2B(Vec2 v){
        return new Vec2(S2B(v.x), S2B(v.y));
    }
    //Flip y axis for screen draws
    public float SFlip(float a){
        return Gdx.graphics.getHeight() - a;
    }
    public Vec2 SFlip(Vec2 v){
        return new Vec2(v.x, SFlip(v.y));
    }
}
