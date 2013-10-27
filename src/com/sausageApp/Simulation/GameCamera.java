package com.sausageApp.Simulation;

import aurelienribon.tweenengine.Tween;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/27/13
 * Time: 1:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class GameCamera extends PerspectiveCamera {

    //public final Vector3 position = new Vector3(0f,0f,0f);

    public GameCamera(float fieldOfView, float viewportWidth, float viewportHeight){
      super(fieldOfView, viewportWidth, viewportHeight);
    }



}
