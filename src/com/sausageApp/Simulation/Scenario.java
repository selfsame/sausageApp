package com.sausageApp.Simulation;

import com.badlogic.gdx.Gdx;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import com.sausageApp.Game.myGame;




public class Scenario {
    private Vec2 gravity;
    private World world;
    public Body box1;
    public Body floor;

    public Scenario() {
        gravity = new Vec2(.0f, 9.0f);
        world = new World(gravity);
        box1 = createDynamic(128, 32f, 32f, 32f);
        floor = createStatic(0f, 400f, 512f, 16f);
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

    public Body createDynamic(float x, float y, float w, float h) {
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

    public void step(float delta) {
          world.step(delta, 1, 1);
    }
}
