package com.sausageApp.Simulation;

import com.sausageApp.Players.Link;
import com.sausageApp.Scene.SensorObject;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;



/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/25/13
 * Time: 11:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class SausageContactListener implements org.jbox2d.callbacks.ContactListener {

    public SausageContactListener(){}

    public Contactable castUserData(Object obj){
        Contactable o = null;
        if (obj != null || !(obj instanceof String)) o = new AnonContact();
        if (obj instanceof Link) o = (Link) obj;
        if (obj instanceof SensorObject) o = (SensorObject) obj;
        return o;
    }

    public void beginContact(Contact contact){
        WorldManifold manifold = new WorldManifold();
        contact.getWorldManifold(manifold);
        Vec2 n = manifold.normal;
        Object a_data = contact.getFixtureA().getBody().getUserData();
        Object b_data = contact.getFixtureB().getBody().getUserData();
        Contactable a = castUserData(a_data);
        Contactable b = castUserData(b_data);
        a.beginContact(n, b);
        b.beginContact(n, a);
    }

    public void endContact(Contact contact){
        WorldManifold manifold = new WorldManifold();
        contact.getWorldManifold(manifold);
        Vec2 n = manifold.normal;
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
