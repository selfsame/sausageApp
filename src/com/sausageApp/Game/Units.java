package com.sausageApp.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

/**
 * Created with IntelliJ IDEA.
 * User: jparker
 * Date: 10/28/13
 * Time: 1:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class Units {
    public float RATIO = 1.0f ;

    public Units(){
        RATIO = (float)Gdx.graphics.getHeight()/(float)Gdx.graphics.getWidth() ;
    }

    public Vector2 applyAspect(Vector2 v){
        return new Vector2(v.x, (v.y)*RATIO + (1f-RATIO));
    }

    public Vector2 unAspect(Vector2 v){
        return new Vector2(v.x, (v.y)/RATIO);
    }

    public Vector2 S2gl(Vector2 v){
        float y = (float) (Gdx.graphics.getHeight() - v.y)/Gdx.graphics.getHeight()*2f-1f ;
        float x = (float) v.x/Gdx.graphics.getWidth()*2f-1f ;
        return new Vector2( x, y );
    }

    public Vector2 gl2S(Vector2 v){
        float y = (float)(((-1f*v.y+1f))/2f)*(Gdx.graphics.getHeight()) ;
        float x = (float)((v.x+1f)/2f)*(Gdx.graphics.getWidth()) ;
        return new Vector2( x, y );
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

    public Vector2 P2B(Vector2 v){
        return new Vector2(P2B(v.x), P2B(v.y));
    }
    //Pixel scale to screen scale
    public float P2S(float a){
        return a / (1024f/Gdx.graphics.getWidth()) ;
    }

    public Vector2 P2S(Vector2 v){
        return new Vector2(P2S(v.x), P2S(v.y));
    }
    //Box2d scale to Pixel scale
    public float B2P(float a){
        return a*PHYSICS_SCALE;
    }

    public Vector2 B2P(Vector2 v){
        return new Vector2(B2P(v.x), B2P(v.y));
    }
    //Box2d scale to Screen scale
    public float B2S(float a){
        return P2S(B2P(a));
    }

    public Vector2 B2S(Vector2 v){
        return new Vector2(B2S(v.x), B2S(v.y));
    }
    //Screen scale to Pixel scale
    public float S2P(float a){
        return (1024f/Gdx.graphics.getWidth()) * a ;
    }

    public Vector2 S2P(Vector2 v){
        return new Vector2(S2P(v.x), S2P(v.y));
    }
    //Screen scale to Box2d scale
    public float S2B(float a){
        return P2B(S2P(a));
    }

    public Vector2 S2B(Vector2 v){
        return new Vector2(S2B(v.x), S2B(v.y));
    }
    //Flip y axis for screen draws
    public float SFlip(float a){
        return Gdx.graphics.getHeight() - a;
    }

    public Vector2 SFlip(Vector2 v){
        return new Vector2(v.x, SFlip(v.y));
    }
}
