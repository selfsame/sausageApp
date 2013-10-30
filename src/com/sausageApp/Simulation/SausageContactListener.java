package com.sausageApp.Simulation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.sausageApp.Players.Link;
import com.sausageApp.Scene.SensorObject;




/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/25/13
 * Time: 11:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class SausageContactListener implements com.badlogic.gdx.physics.box2d.ContactListener {

    public SausageContactListener(){}

    public Contactable castUserData(Object obj){
        Contactable o = null;
        if (obj != null || !(obj instanceof String)) o = new AnonContact();
        if (obj instanceof Link) o = (Link) obj;
        if (obj instanceof SensorObject) o = (SensorObject) obj;
        return o;
    }

    public void beginContact(Contact contact){
        WorldManifold manifold = contact.getWorldManifold();

        Vector2 n = manifold.getNormal();
        Object a_data = contact.getFixtureA().getBody().getUserData();
        Object b_data = contact.getFixtureB().getBody().getUserData();
        Contactable a = castUserData(a_data);
        Contactable b = castUserData(b_data);
        a.beginContact(n, b);
        b.beginContact(n, a);
    }

    public void endContact(Contact contact){
        WorldManifold manifold = contact.getWorldManifold();
        Vector2 n = manifold.getNormal();
        Object a_data = contact.getFixtureA().getBody().getUserData();
        Object b_data = contact.getFixtureB().getBody().getUserData();
        Contactable a = castUserData(a_data);
        Contactable b = castUserData(b_data);
        a.endContact(n, b);
        b.endContact(n, a);

    }


    public void preSolve(Contact contact, Manifold oldManifold){

    }

    public void postSolve(Contact contact, ContactImpulse impulse){

    }
}
