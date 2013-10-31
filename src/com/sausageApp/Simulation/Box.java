package com.sausageApp.Simulation;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.sausageApp.Game.State;
import com.sausageApp.Game.Units;


import com.sausageApp.Scene.*;
import com.sausageApp.TweenAccessors.MoveableAccessor;





// handles a Box2d simulation.

public class Box {
    public State state = State.getInstance();
    private Units units = new Units();
    public Box2DDebugRenderer debugRenderer;


    public World world;
    private int spawn_index = 0;







    private SausageContactListener contact_listener = new SausageContactListener();



    public Box(Vector2 gravity) {






        world = new World(gravity, true);
        world.setContactListener(contact_listener);
        if (state.debug){
            debugRenderer = new Box2DDebugRenderer();
            debugRenderer.setDrawJoints(true);
            debugRenderer.setDrawContacts(true);
            debugRenderer.setDrawInactiveBodies(true);
        }





    }

    public void step(float delta) {
        long startTime = System.nanoTime();
        world.step(delta, 20, 40);
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        state.game.profiler.addStat("World Step: (ms) "+(int)(duration*1.0e-6));
    }





    public void update(float delta){

        for (DynamicObject obj: state.scene.dynamics) {
            obj.update();
        }

        step(.018f);

        // The camera stuff can really be in an acestor of all scenarios, in case it needs to get overriden
        int player_count = state.game.players.size();
        Vector2 pp = new Vector2(0f,0f);
        float ubx = 99999f;
        float uby = 99999f;
        float lbx = -99999f;
        float lby = -99999f;
        for (int i=0;i<player_count;i++){
            Vector2 player_p = units.applyAspect(units.S2gl(units.B2S(state.game.players.get(i).sausage.head.getPosition())));
            if (player_p.x < ubx) { ubx = player_p.x;}
            if (player_p.y < uby) { uby = player_p.y;}
            if (player_p.x > lbx) { lbx = player_p.x;}
            if (player_p.y > lby) { lby = player_p.y;}
            pp = pp.add(player_p);
        }
        float dist = new Vector2(ubx,uby*1.4f).dst2(lbx,lby*1.4f);
        pp = pp.scl(1f/(float)player_count);
        Vector2 ppp = new Vector2(pp.x,pp.y);
        Vector2 difff = ppp.sub(new Vector2(state.scene.camera.position.x, state.scene.camera.position.y)).mul(1f);
        float final_dist =  (float)(Math.sqrt((double)dist));
        if (final_dist < state.scene.view_dist){final_dist = state.scene.view_dist;}

        Tween.to(state.scene.camera, MoveableAccessor.POSITION_X, .2f).target(pp.x).start(state.scene.tweenManager);
        Tween.to(state.scene.camera, MoveableAccessor.POSITION_Y, .2f).target(pp.y).start(state.scene.tweenManager);
        Tween.to(state.scene.camera, MoveableAccessor.POSITION_Z, .3f).target(final_dist).start(state.scene.tweenManager);


        if (state.debug){
            Matrix4 ddm = state.scene.camera.combined.cpy().rotate(new Vector3(0f,0f,.5f), (float)180).rotate(new Vector3(0f,.5f,0f), (float)180).scl((1f/40f)*3f).trn(-1.6f,2.2f,0f);
            debugRenderer.render(world, ddm);
        }
    }

    public Vector2 GetSpawn(){
        Locus using = state.scene.spawn_points.get(spawn_index);
        spawn_index += 1;
        return units.unAspect(units.S2P(units.gl2S(new Vector2(using.pos[0], -using.pos[1]))));

        //return new Vec2(10f,10f);
    }






    public Body createDynamicCircle(float x, float y, float radius, float density) {
        return createDynamicCircle(x, y, radius, density, .3f, .8f);
    }

    public Body createDynamicCircle(float x, float y, float radius, float density, float friction, float restitution) {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);

        FixtureDef circleF = new FixtureDef();
        circleF.shape = circleShape;
        circleF.density = 1.0f*density;
        circleF.restitution = restitution;
        circleF.friction = friction;
        circleF.filter.categoryBits = 1;
        circleF.filter.maskBits = 1;
        //circleF.filter.groupIndex = 1;
        BodyDef bodyDef = new BodyDef();

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.allowSleep = false;
        Body body = world.createBody(bodyDef);
        body.setAngularDamping(.01f);
        body.setLinearDamping(.01f);

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
        circleF.restitution = .96f;
        circleF.friction = friction;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.allowSleep = false;

        Body body = world.createBody(bodyDef);
        body.setAngularDamping(.2f);
        body.setLinearDamping(.2f);
        body.createFixture(polygonShape, 5.0f);
        body.createFixture(circleF);

        return body;
    }

    public Body createStatic(float x, float y, float w, float h) {
        x = units.P2B(x); y = units.P2B(y); w = units.P2B(w); h = units.P2B(h);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(w, h);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        bodyDef.allowSleep = false;
        Body body = world.createBody(bodyDef);
        body.createFixture(polygonShape, 5.0f);
        return body;
    }

    public Body createStaticChain(Vector2[] verticies, boolean is_sensor, int mask) {
        ChainShape chainShape = new ChainShape();
        chainShape.createLoop(verticies);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        bodyDef.position.set(0f, 0f);

        FixtureDef circleF = new FixtureDef();
        circleF.shape = chainShape;
        circleF.restitution = .8f;
        circleF.friction = .9f;

        circleF.filter.categoryBits = (short)mask;
        circleF.filter.maskBits = (short)mask;
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