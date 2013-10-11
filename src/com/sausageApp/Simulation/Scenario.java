package com.sausageApp.Simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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

    private ShaderProgram wire_shader;
    private Mesh wire_mesh;

    private ShaderProgram debug_shader;
    private ArrayList<Mesh> debug_mesh = new ArrayList<Mesh>();

    float ticker = 0f;
    public PerspectiveCamera camera;

    public myGame game;
    public GameScreen game_screen;

    public Scenario(myGame _game, GameScreen _game_screen) {
        gravity = new Vec2(.0f, 20.8f);
        world = new World(gravity);
        game = _game;
        game_screen = _game_screen;

        ScenarioData scene = json.fromJson(ScenarioData.class, Gdx.files.internal( "scenarios/level01.json" ));

        camera = new PerspectiveCamera( 46.596f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.translate(scene.camera[0], scene.camera[1], scene.camera[2]);
        //camera.translate( 0f, .01f, 0f);


        LevelMeshCompiler level_geo = new LevelMeshCompiler();
        level_mesh = level_geo.CompileMesh(scene.static_vertices, scene.static_indicies);
        level_shader = level_geo.MakeShader();

        wire_mesh = level_geo.CompileMesh(scene.wire_vertices, scene.wire_indicies);
        wire_shader = level_geo.MakeWireShader();

        for (int i = 0; i < scene.collide_groups.size(); i++) {
            float[] group = scene.collide_groups.get(i);
            Vec2[] pbies = new Vec2[(int)group.length/2];

            float[] phys_debug = new float[((int)group.length/2)*7];
            short[] phys_ind = new short[((int)group.length/2)*2+2];


            for (int j = 0; j < (int)group.length/2; j++) {

                pbies[j] = S2B(gl2S(new Vec2(group[j*2], group[j*2+1])));
                phys_debug[j*7] = group[j*2];
                phys_debug[j*7+1] = group[j*2+1];
                phys_debug[j*7+2] = 0f;
                phys_debug[j*7+3] = 1f;
                phys_debug[j*7+4] = 0f;
                phys_debug[j*7+5] = 0f;
                phys_debug[j*7+6] = 1f;

                phys_ind[j*2] = (short)(j*2);
                phys_ind[j*2+1] = (short)(j*2+1);
            }
            phys_ind[group.length/2] = 0;
            phys_ind[group.length/2+1] = 1;
            debug_mesh.add( level_geo.CompileMesh(phys_debug, phys_ind) );


            createStaticChain( pbies);
        }







    }



    public Vec2 gl2S(Vec2 v){
        float y = (float)(((-1f*v.y)+1f)/2f)*(Gdx.graphics.getHeight()) ;
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


        ticker += .01f;
        Vec2 pp = game.players.get(0).sausage.head.getPosition().mul(.01f);
        Vector2 ppp = new Vector2(pp.x,pp.y);
        Vector2 difff = ppp.sub(new Vector2(camera.position.x, camera.position.y)).mul(1f);
        //camera.near = 2f;
        //camera.far = 200f;

        camera.translate(difff.x, 0f, 0f );
        camera.lookAt(pp.x, pp.y, 0f);
        //camera.rotateAround(ppp, new Vector3(0f,1f,0f), 1f);
        camera.update();



        level_shader.begin();
        //Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        //Gdx.gl.glCullFace(GL20.GL_BACK);
        //Gdx.gl.glClearDepthf(1.0f);
        Gdx.gl.glDepthRangef(0f, 1f);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST) ;
        level_shader.setUniformMatrix("u_worldView", camera.combined);
        level_mesh.render(level_shader, GL20.GL_TRIANGLES);

        level_shader.end();
        ;

        wire_shader.begin();
        Gdx.gl.glEnable(GL20.GL_BLEND) ;
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        Gdx.gl20.glLineWidth(1f);
        Gdx.gl.glPolygonOffset(.75f, .8f);
        Gdx.gl.glDepthRangef(0f, 100f);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST) ;

        wire_shader.setUniformMatrix("u_worldView", camera.combined);
        //Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
//        for (int i = 0; i<debug_mesh.size();i++){
//            level_shader.setUniformMatrix("u_worldView", camera.combined);
//            debug_mesh.get(i).render(level_shader, GL20.GL_LINE_LOOP);
//        }
        wire_mesh.render(wire_shader, GL20.GL_LINES);
        Gdx.gl20.glDisable(GL20.GL_BLEND);
        wire_shader.end();

    }


    public Vec2 GetSpawn(){
        return new Vec2( 1024f *( .8f * (float)Math.random()), 576f * (.8f * (float)Math.random()));
        //return new Vec2(10f,10f);
    }
}
