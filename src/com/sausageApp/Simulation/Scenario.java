package com.sausageApp.Simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.gdx.utils.Json;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import com.badlogic.gdx.utils.JsonReader;

import com.sausageApp.Game.myGame;

import java.util.ArrayList;


public class Scenario {
    private Vec2 gravity;
    private World world;
    public ArrayList<StaticObject> statics = new ArrayList<StaticObject>();
    public ArrayList<Body> dynamics = new ArrayList<Body>();
    public ArrayList<Vec2> dynamic_sizes = new ArrayList<Vec2>();

    private JsonReader JSON = new JsonReader();
    private Json json = new Json();





    public Scenario() {
        gravity = new Vec2(.0f, 100.0f);
        world = new World(gravity);


        ScenarioData scene = json.fromJson(ScenarioData.class, Gdx.files.internal( "scenarios/level01.json" ));


        for (int i = 0; i < scene.static_objects.size(); i++) {
            String type = scene.static_objects.get(i).type;
            float x = scene.static_objects.get(i).x;
            float y = scene.static_objects.get(i).y;
            float w = scene.static_objects.get(i).w;
            float h = scene.static_objects.get(i).h;

            StaticObject new_static = new StaticObject(this, type, x, y, w, h);
            statics.add(new_static);
        }



        statics.add(new StaticObject(this, "stone", 0f, convertX(Gdx.graphics.getHeight()-16f), convertX(Gdx.graphics.getWidth()), 8f));
        statics.add(new StaticObject(this, "stone", 5f, convertX(Gdx.graphics.getHeight()), 10f, convertX(Gdx.graphics.getHeight())));
        statics.add(new StaticObject(this, "stone", 0f, 16f, convertX(Gdx.graphics.getWidth()), 8f));
        statics.add(new StaticObject(this, "stone", convertX(Gdx.graphics.getWidth()-16f), convertX(Gdx.graphics.getHeight()), 10f, convertX(Gdx.graphics.getHeight())));


    }



    public Vec2 convertVec2(Vec2 v){
        return new Vec2(convertX(v.x), convertY(v.y));
    }

    public float convertX(float x){
        return (1024f/Gdx.graphics.getWidth()) * x  ;
    }
    public float convertY(float y){
        return (1024f/Gdx.graphics.getWidth()) * y;
    }
    public float unconvertX(float x){
        return x / (1024f/Gdx.graphics.getWidth()) ;
    }
    public float unconvertY(float y){
        return y / (1024f/Gdx.graphics.getWidth());
    }
    public float flipY(float y){
        return Gdx.graphics.getHeight() -  y;
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
          world.step(delta, 5, 5);
    }
}
