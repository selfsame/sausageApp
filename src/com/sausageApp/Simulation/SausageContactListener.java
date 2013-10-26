package com.sausageApp.Simulation;

import com.badlogic.gdx.Gdx;
import com.sausageApp.Players.Link;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
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

    public void beginContact(Contact contact){
       WorldManifold manifold = new WorldManifold();
       contact.getWorldManifold(manifold);
       Vec2 n = manifold.normal;
       Object a_data = contact.getFixtureA().getBody().getUserData();
       Object b_data = contact.getFixtureB().getBody().getUserData();

       if (a_data != null){
          Link link = (Link)a_data;

           if (b_data != null){
               Link partner = (Link)b_data;
               link.beginContact(n, partner);
           } else {
               link.beginContact(n);
           }
       }
        if (b_data != null){
            Link link = (Link)b_data;

            if (a_data != null){
                Link partner = (Link)a_data;
                link.beginContact(n, partner);
            } else {
                link.beginContact(n);
            }
        }
    }

    public void endContact(Contact contact){
        WorldManifold manifold = new WorldManifold();
        contact.getWorldManifold(manifold);
        Vec2 n = manifold.normal;
        Object a_data = contact.getFixtureA().getBody().getUserData();
        Object b_data = contact.getFixtureB().getBody().getUserData();

        if (a_data != null){
            Link link = (Link)a_data;

            if (b_data != null){
                Link partner = (Link)b_data;
                link.endContact(n, partner);
            } else {
                link.endContact(n);
            }
        }
        if (b_data != null){
            Link link = (Link)b_data;

            if (a_data != null){
                Link partner = (Link)a_data;
                link.endContact(n, partner);
            } else {
                link.endContact(n);
            }
        }
    }

    public void preSolve(Contact contact, Manifold oldManifold){

    }

    public void postSolve(Contact contact, ContactImpulse impulse){

    }
}
