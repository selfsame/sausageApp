package com.sausageApp.Simulation;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.sausageApp.Game.State;
import com.sausageApp.Game.Units;
import com.sausageApp.Game.myGame;

import com.sausageApp.Scene.*;
import com.sausageApp.TweenAccessors.GameObjectAccessor;
import com.sausageApp.TweenAccessors.MoveableAccessor;
import com.sausageApp.screens.GameScreen;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

// handles a Box2d simulation.

public class Box {
    public State state = State.getInstance();
    private Units units = new Units();

    public final TweenManager tweenManager = new TweenManager();

    public World world;

    private int spawn_index = 0;







    private SausageContactListener contact_listener = new SausageContactListener();



    public Box(Vec2 gravity) {

        Tween.registerAccessor(GameCamera.class, new MoveableAccessor());
        Tween.registerAccessor(GameObject.class, new GameObjectAccessor());




        world = new World(gravity);
        world.setContactListener(contact_listener);



//        Tween.to(scene.object_map.get("Lamp"), GameObjectAccessor.POSITION_Y, .3f).targetRelative(.1f).repeatYoyo(100, 0f).start(tweenManager);
//        Tween.to(scene.object_map.get("Lamp"), GameObjectAccessor.SCALE_XYZ, .1f).delay(.3f).target(1f,1f,.5f).repeatYoyo(100, .2f).start(tweenManager);
//        Tween.to(scene.object_map.get("Cloud"), GameObjectAccessor.POSITION_X, 40f).targetRelative(-50f).repeatYoyo(10, .2f).start(tweenManager);
//        Tween.to(scene.object_map.get("Cloud.001"), GameObjectAccessor.POSITION_X, 40f).targetRelative(-50f).repeatYoyo(10, .2f).start(tweenManager);
//        Tween.to(scene.object_map.get("Cloud.002"), GameObjectAccessor.POSITION_X, 50f).targetRelative(-30f).repeatYoyo(10, .2f).start(tweenManager);
//        Tween.to(scene.object_map.get("Cloud.003"), GameObjectAccessor.POSITION_X, 30f).targetRelative(-10f).repeatYoyo(10, .2f).start(tweenManager);
//        Tween.to(scene.object_map.get("Cloud.004"), GameObjectAccessor.POSITION_X, 30f).targetRelative(10f).repeatYoyo(10, .2f).start(tweenManager);
    }

    public void step(float delta) {
        long startTime = System.nanoTime();
        world.step(delta, 20, 40);
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        //game.profiler.addStat("World Step: (ms) "+(int)(duration*1.0e-6));
    }





    public void update(float delta){
        tweenManager.update(Gdx.graphics.getDeltaTime());
        for (DynamicObject obj: state.scene.dynamics) {
            obj.update();
        }

        step(.018f);

        // The camera stuff can really be in an acestor of all scenarios, in case it needs to get overriden
        int player_count = state.game.players.size();
        Vec2 pp = new Vec2(0f,0f);
        float ubx = 99999f;
        float uby = 99999f;
        float lbx = -99999f;
        float lby = -99999f;
        for (int i=0;i<player_count;i++){
            Vec2 player_p = units.S2gl(units.B2S(state.game.players.get(i).sausage.head.getPosition()));
            if (player_p.x < ubx) { ubx = player_p.x;}
            if (player_p.y < uby) { uby = player_p.y;}
            if (player_p.x > lbx) { lbx = player_p.x;}
            if (player_p.y > lby) { lby = player_p.y;}
            pp = pp.add(player_p);
        }
        float dist = new Vector2(ubx,uby*1.4f).dst2(lbx,lby*1.4f);
        pp = pp.mul(1f/(float)player_count);
        Vector2 ppp = new Vector2(pp.x,pp.y);
        Vector2 difff = ppp.sub(new Vector2(state.scene.camera.position.x, state.scene.camera.position.y)).mul(1f);
        float final_dist =  (float)(Math.sqrt((double)dist));
        if (final_dist < state.scene.view_dist){final_dist = state.scene.view_dist;}

        Tween.to(state.scene.camera, MoveableAccessor.POSITION_X, .2f).target(pp.x).start(tweenManager);
        Tween.to(state.scene.camera, MoveableAccessor.POSITION_Y, .2f).target(pp.y+1.5f).start(tweenManager);
        Tween.to(state.scene.camera, MoveableAccessor.POSITION_Z, .3f).target(final_dist).start(tweenManager);
    }

    public Vec2 GetSpawn(){
        Locus using = state.scene.spawn_points.get(spawn_index);
        spawn_index += 1;
        return units.S2P(units.gl2S(new Vec2(using.pos[0], -using.pos[1])));

        //return new Vec2(10f,10f);
    }






    public Body createDynamicCircle(float x, float y, float radius, float density) {
        CircleShape circleShape = new CircleShape();
        circleShape.m_radius = radius;
        FixtureDef circleF = new FixtureDef();
        circleF.shape = circleShape;
        circleF.density = 1.0f*density;
        circleF.restitution = .8f;
        circleF.friction = .3f;
        circleF.filter.categoryBits = 1;
        circleF.filter.maskBits = 1;
        //circleF.filter.groupIndex = 1;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position.set(x, y);
        bodyDef.allowSleep = false;
        Body body = world.createBody(bodyDef);
        body.m_angularDamping = .8f;
        body.m_linearDamping = .8f;
        body.createFixture(circleShape, 5.0f);
        body.createFixture(circleF);
        return body;
    }

    public Body createDynamicRect(float x, float y, float width, float height, float density, float friction) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width, height);

        FixtureDef circleF = new FixtureDef();
        circleF.shape = polygonShape;
        circleF.density = 1.0f*density;
        circleF.restitution = .8f;
        circleF.friction = friction;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position.set(x, y);
        bodyDef.allowSleep = false;
        Body body = world.createBody(bodyDef);
        body.m_angularDamping = .8f;
        body.m_linearDamping = .8f;
        body.createFixture(polygonShape, 5.0f);
        body.createFixture(circleF);
        return body;
    }

    public Body createStatic(float x, float y, float w, float h) {
        x = units.P2B(x); y = units.P2B(y); w = units.P2B(w); h = units.P2B(h);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(w, h);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;
        bodyDef.position.set(x, y);
        bodyDef.allowSleep = false;
        Body body = world.createBody(bodyDef);
        body.createFixture(polygonShape, 5.0f);
        return body;
    }

    public Body createStaticChain(Vec2[] verticies, boolean is_sensor, int mask) {
        ChainShape chainShape = new ChainShape();
        chainShape.createLoop(verticies, verticies.length);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;

        bodyDef.position.set(0f, 0f);

        FixtureDef circleF = new FixtureDef();
        circleF.shape = chainShape;
        circleF.restitution = .8f;
        circleF.friction = .9f;

        circleF.filter.categoryBits = (short)mask;
        circleF.filter.maskBits = mask;
        //circleF.filter.groupIndex = mask;

        //Gdx.app.log("MASK:", ":"+circleF.filter.categoryBits);

        circleF.isSensor = is_sensor;

        Body body = world.createBody(bodyDef);
        body.createFixture(circleF);
        return body;
    }

    public void dispose(){

    }
}