package com.sausageApp.Simulation;

import com.sausageApp.Players.Link;
import com.sausageApp.Players.Sausage;
import org.jbox2d.common.Vec2;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/26/13
 * Time: 12:18 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Contactable {

    public void beginContact(Vec2 n, Contactable partner);
    public void endContact(Vec2 n, Contactable partner);

}


