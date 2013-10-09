package com.sausageApp.Simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.sausageApp.Game.myGame;
import com.sausageApp.screens.GameScreen;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import java.util.ArrayList;


public class Scenario {
    public Vec2 gravity;
    public World world;
    public ArrayList<StaticObject> statics = new ArrayList<StaticObject>();
    public ArrayList<Body> dynamics = new ArrayList<Body>();
    public ArrayList<Vec2> dynamic_sizes = new ArrayList<Vec2>();

    private JsonReader JSON = new JsonReader();
    private Json json = new Json();

    private ShaderProgram level_shader;

    private Mesh level_mesh;

    public myGame game;
    public GameScreen game_screen;

    public Scenario(myGame _game, GameScreen _game_screen) {
        gravity = new Vec2(.0f, 20.8f);
        world = new World(gravity);
        game = _game;
        game_screen = _game_screen;

        LevelMeshCompiler level_geo = new LevelMeshCompiler();
        level_mesh = level_geo.CompileMesh();
        level_shader = level_geo.MakeShader();

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


        Vec2[] pbies = new Vec2[(int)scene.physics_bodies.length/2];
        for (int i = 0; i < (int)scene.physics_bodies.length/2; i++) {
            pbies[i] = S2B(gl2S(new Vec2(scene.physics_bodies[i*2], scene.physics_bodies[i*2+1])));

        }
       createStaticChain( pbies);
        //statics.add(new StaticObject(this, "stone", 0f, 576f, 1024f, 16f));
        statics.add(new StaticObject(this, "stone", 0f, 576f, 16f, 576f));
        statics.add(new StaticObject(this, "stone", 0f, 16f, 1008f, 16f));
        statics.add(new StaticObject(this, "stone", 1008f, 576f, 16f, 576f));

        createStaticChain(new Vec2[]{P2B(new Vec2(0f,576f)), P2B(new Vec2(1024f, 576f)), P2B(new Vec2(1024f, 560f)), P2B(new Vec2(0f, 560f)) });


        createStaticChain(new Vec2[]{P2B(new Vec2(40f,40f)), P2B(new Vec2(80f, 80f)), P2B(new Vec2(120f, 80f)), P2B(new Vec2(80f, 0f)) });


    }



    public Vec2 gl2S(Vec2 v){
        float y = (float)((v.y+1f)/2f)*(Gdx.graphics.getHeight()) ;
        float x = (float)((v.x+1f)/2f)*(Gdx.graphics.getWidth()) ;
        return new Vec2( x, y );
    };


    // 1024 576
    private float PHYSICS_SCALE = 40f;

    // Pixel scale to Box2d scale
    public float P2B(float a){
        return a/PHYSICS_SCALE;
    }
    public Vec2 P2B(Vec2 v){
        return new Vec2(P2B(v.x), P2B(v.y));
    }

    //Pixel scale to screen scale
    public float P2S(float a){
        return a / (1024f/Gdx.graphics.getWidth()) ;
    }
    public Vec2 P2S(Vec2 v){
        return new Vec2(P2S(v.x), P2S(v.y));
    }


    //Box2d scale to Pixel scale
    public float B2P(float a){
        return a*PHYSICS_SCALE;
    }
    public Vec2 B2P(Vec2 v){
        return new Vec2(B2P(v.x), B2P(v.y));
    }

    //Box2d scale to Screen scale
    public float B2S(float a){
        return P2S(B2P(a));
    }
    public Vec2 B2S(Vec2 v){
        return new Vec2(B2S(v.x), B2S(v.y));
    }


    //Screen scale to Pixel scale
    public float S2P(float a){
        return (1024f/Gdx.graphics.getWidth()) * a ;
    }
    public Vec2 S2P(Vec2 v){
        return new Vec2(S2P(v.x), S2P(v.y));
    }

    //Screen scale to Box2d scale
    public float S2B(float a){
        return P2B(S2P(a));
    }
    public Vec2 S2B(Vec2 v){
        return new Vec2(S2B(v.x), S2B(v.y));
    }

    //Flip y axis for screen draws
    public float SFlip(float a){
        return Gdx.graphics.getHeight() - a;
    }
    public Vec2 SFlip(Vec2 v){
        return new Vec2(v.x, SFlip(v.y));
    }



    public Body createDynamicCircle(float x, float y, float radius, float density) {
        CircleShape circleShape = new CircleShape();
        circleShape.m_radius = radius;
        FixtureDef circleF = new FixtureDef();
        circleF.shape = circleShape;
        circleF.density = 1.0f*density;
        circleF.restitution = .8f;
        circleF.friction = .9f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position.set(x, y);

        //bodyDef.angle = (float) (Math.PI / 4 * i);
        bodyDef.allowSleep = false;
        Body body = world.createBody(bodyDef);
        body.m_angularDamping = .8f;
        body.m_linearDamping = .8f;
        body.createFixture(circleShape, 5.0f);
        body.createFixture(circleF);

        return body;
    }

    public Body createStatic(float x, float y, float w, float h) {
        x = P2B(x); y = P2B(y); w = P2B(w); h = P2B(h);
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

    public Body createStaticChain(Vec2[] verticies) {

        ChainShape chainShape = new ChainShape();
        //PolygonShape chainShape = new PolygonShape();
        chainShape.createChain(verticies, verticies.length);
        //chainShape.set(verticies, verticies.length);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;



        bodyDef.position.set(0f, 0f);

        Body body = world.createBody(bodyDef);
        body.createFixture(chainShape, 5.0f);
        return body;
    }



    public void step(float delta) {
        long startTime = System.nanoTime();
        world.step(delta, 8, 6);
        long endTime = System.nanoTime();

        long duration = endTime - startTime;
        game.profiler.addStat("World Step: (ms) "+(int)(duration*1.0e-6));




    }

    public void render(){
        level_shader.begin();
        level_mesh.render(level_shader, GL20.GL_TRIANGLES);
        level_shader.end();
    }


    public Vec2 GetSpawn(){
        return new Vec2( 1024f *( .8f * (float)Math.random()), 576f * (.8f * (float)Math.random()));
        //return new Vec2(10f,10f);
    }
}
