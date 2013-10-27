package com.sausageApp.Simulation;

import com.badlogic.gdx.Gdx;
import com.sausageApp.Players.Link;
import com.sausageApp.Players.Player;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/26/13
 * Time: 12:42 PM
 * To change this template use File | Settings | File Templates.
 */



public class AnonContact implements Contactable{


    public AnonContact(){}
    public void beginContact(Vec2 n, Contactable partner){}
    public void endContact(Vec2 n, Contactable partner){}

}
