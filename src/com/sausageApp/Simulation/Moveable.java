package com.sausageApp.Simulation;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/27/13
 * Time: 3:13 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Moveable {
    public Vector3 scale = null;
    public Quaternion getRotation();
    public void setRotation(Quaternion q);
    public Vector3 getPosition();
    public void setPosition(Vector3 p);
    public void setScale(Vector3 p);
}
