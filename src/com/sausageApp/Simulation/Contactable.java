package com.sausageApp.Simulation;

import com.badlogic.gdx.math.Vector2;



/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/26/13
 * Time: 12:18 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Contactable {

    public void beginContact(Vector2 n, Contactable partner);
    public void endContact(Vector2 n, Contactable partner);

}


