package com.sausageApp.Simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.mappings.Ouya;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import com.sausageApp.Game.myGame;

import java.util.ArrayList;


public class Scenario {
    private Vec2 gravity;
    private World world;
    public ArrayList<Body> statics = new ArrayList<Body>();
    public ArrayList<Body> dynamics = new ArrayList<Body>();
    public ArrayList<Vec2> dynamic_sizes = new ArrayList<Vec2>();




    public Scenario() {
        gravity = new Vec2(.0f, 16.0f);
        world = new World(gravity);
        statics.add(createStatic(0f, Gdx.graphics.getHeight()-16f, Gdx.graphics.getWidth(), 8f));
        statics.add(createStatic(5f, Gdx.graphics.getHeight(), 10f, Gdx.graphics.getHeight()));

        dynamics.add(createStatic(60f, 160f, 84f, 10f));
        dynamics.add(createStatic(120f, 40f, 100f, 10f));
        dynamic_sizes.add(new Vec2(84f,10f) );
        dynamic_sizes.add(new Vec2(100f,10f) );

    }




    public float convertX(float x){
        return x;
    }
    public float convertY(float y){
        return Gdx.graphics.getHeight() - (y);
    }

    public Vec2 getDimensions(Body b){
        Vec2 flb = b.getFixtureList().getAABB(0).lowerBound ;
        Vec2 fub = b.getFixtureList().getAABB(0).upperBound ;
        return(new Vec2( fub.x -flb.x, fub.y -flb.y )    );
    }

    public Body createDynamicBox(float x, float y, float w, float h, float r) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(w, h);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position.set(x, y);
        //bodyDef.angle = (float) (Math.PI / 4 * i);
        bodyDef.allowSleep = false;
        Body body = world.createBody(bodyDef);
        body.createFixture(polygonShape, 5.0f);

        return body;
    }

    public Body createDynamicCircle(float x, float y, float radius) {
        CircleShape circleShape = new CircleShape();
        circleShape.m_radius = radius;
        FixtureDef circleF = new FixtureDef();
        circleF.shape = circleShape;
        circleF.restitution = .9f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position.set(x, y);

        //bodyDef.angle = (float) (Math.PI / 4 * i);
        bodyDef.allowSleep = false;
        Body body = world.createBody(bodyDef);
        body.createFixture(circleShape, 5.0f);
        body.createFixture(circleF);

        return body;
    }

    public Body createStatic(float x, float y, float w, float h) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(w, h);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;
        bodyDef.position.set(x, y);
        //bodyDef.angle = (float) (Math.PI / 4 * i);
        bodyDef.allowSleep = false;
        Body body = world.createBody(bodyDef);
        body.createFixture(polygonShape, 5.0f);
        return body;
    }

    public ArrayList<Body> createSausage(float x, float y, float radius, int link_count)    {
        ArrayList<Body> sausage = new ArrayList<Body>();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.1f, 0.6f);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 20.0f;
        fd.friction = 0.2f;

        RevoluteJointDef jd = new RevoluteJointDef();
        jd.collideConnected = false;
        jd.upperAngle = 1.17079633f;
        jd.lowerAngle = -1.17079633f;
        jd.enableLimit = true;

        Body first = createDynamicCircle(x, y, radius*.9f);
        Body prevBody = first;
        sausage.add(first);

        for (int i = 0; i < link_count; ++i) {
            Body next = createDynamicCircle(x+radius+(i*radius), y, radius*.9f);

            Vec2 anchor = new Vec2(x+(i*radius), y);
            jd.initialize(prevBody, next, anchor);
            world.createJoint(jd);
            sausage.add(next);
            prevBody = next;
        }
        return sausage;
    }

    public void step(float delta) {
          world.step(delta, 1, 1);
    }
}
