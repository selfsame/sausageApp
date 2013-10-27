package com.sausageApp.Simulation;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
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

    public final TweenManager tweenManager = new TweenManager();

    public Vec2 gravity;
    public World world;
    public ArrayList<StaticObject> statics = new ArrayList<StaticObject>();
    public ArrayList<Body> dynamics = new ArrayList<Body>();
    public ArrayList<Vec2> dynamic_sizes = new ArrayList<Vec2>();

    private JsonReader JSON = new JsonReader();
    private Json json = new Json();

    private ShaderProgram level_shader;
    private Mesh level_mesh;

    private ShaderProgram tex_shader;
    private ArrayList<Mesh> texture_meshes = new ArrayList<Mesh>();
    private ArrayList<Mesh> texture_meshes_alpha = new ArrayList<Mesh>();

    private ShaderProgram wire_shader;
    private Mesh wire_mesh;

    private ShaderProgram debug_shader;
    private ArrayList<Mesh> debug_mesh = new ArrayList<Mesh>();

    private ArrayList<Mesh> vertex_meshes = new ArrayList<Mesh>();
    private ArrayList<Mesh> vertex_meshes_alpha = new ArrayList<Mesh>();

    float ticker = 0f;
    public GameCamera camera;
    public float default_view_dist = 3f;
    public float view_dist = 3f;

    public myGame game;
    public GameScreen game_screen;

    private ArrayList<Locus> spawn_points = new ArrayList<Locus>();
    private int spawn_index = 0;

    private ScenarioData scene;

    private Texture tex;

    private Texture interface_sheet = new Texture("interface.png");
    private TextureRegion O_button = new TextureRegion(interface_sheet, 0, 0, 16, 16);
    private TextureRegion banner_1 = new TextureRegion(interface_sheet, 0, 32, 64, 16);
    private SpriteBatch batch = new SpriteBatch();
    private BitmapFont font = new BitmapFont();

    public ArrayList<SensorObject> sensors = new ArrayList<SensorObject>();

    private SausageContactListener contact_listener = new SausageContactListener();

    public float RATIO = Gdx.graphics.getHeight()/Gdx.graphics.getWidth() ;

    public Scenario(myGame _game, GameScreen _game_screen, String filename) {

        Tween.registerAccessor(GameCamera.class, new MoveableAccessor());

        gravity = new Vec2(.0f, 20.8f);
        world = new World(gravity);
        world.setContactListener(contact_listener);
        game = _game;
        game_screen = _game_screen;
        //Gdx.gl.glViewport(0,0,(int)Gdx.graphics.getWidth(), (int)Gdx.graphics.getHeight());
        scene = json.fromJson(ScenarioData.class, Gdx.files.internal( filename ));
        tex = new Texture(Gdx.files.internal("house_tex.png"));
        camera = new GameCamera(46.596f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 0f, 60f);
        //Gdx.graphics.setDisplayMode((int)Gdx.graphics.getWidth()/2,(int)Gdx.graphics.getHeight()/2, false);




        ArrayList<VertexObject> vertex_objects = scene.vertex_objects;
        ArrayList<VertexObject> texture_objects = scene.texture_objects;
        LevelMeshCompiler level_geo = new LevelMeshCompiler();

        for (int i=0;i<vertex_objects.size();i++){
            if (vertex_objects.get(i).alpha == false){
                vertex_meshes.add(level_geo.CompileMesh(vertex_objects.get(i).static_vertices, vertex_objects.get(i).static_indicies));
            } else {
                vertex_meshes_alpha.add(level_geo.CompileMesh(vertex_objects.get(i).static_vertices, vertex_objects.get(i).static_indicies));
            }
        }
        level_shader = level_geo.MakeShader();



        for (int i=0;i<texture_objects.size();i++){
            if (texture_objects.get(i).alpha == false){
                texture_meshes.add(level_geo.CompileTexMesh(texture_objects.get(i).static_vertices, texture_objects.get(i).static_indicies));
            } else {
                texture_meshes_alpha.add(level_geo.CompileTexMesh(texture_objects.get(i).static_vertices, texture_objects.get(i).static_indicies));
            }
        }
        tex_shader = level_geo.MakeTexShader();

        wire_mesh = level_geo.CompileMesh(scene.wire_vertices, scene.wire_indicies);
        wire_shader = level_geo.MakeWireShader();

        for (Locus locus: scene.locii) {
            if (locus.usage.equals("PLAYER_SPAWN")){
                spawn_points.add(locus);
            }

        }

        for (Collider group: scene.collide_groups) {
            float[] verts = group.verts;
            Vec2[] pbies = new Vec2[(int)verts.length/2];
            float[] phys_debug = new float[((int)verts.length/2)*7];
            short[] phys_ind = new short[((int)verts.length)+2];
            for (int j = 0; j < (int)verts.length/2; j++) {
                pbies[j] = S2B(gl2S(new Vec2(verts[j*2],verts[j*2+1])));
                phys_debug[j*7] = verts[j*2];
                phys_debug[j*7+1] = verts[j*2+1];
                phys_debug[j*7+2] = 0f;
                phys_debug[j*7+3] = 1f;
                phys_debug[j*7+4] = 0f;
                phys_debug[j*7+5] = 0f;
                phys_debug[j*7+6] = 1f;
                phys_ind[j*2] = (short)(j);
                phys_ind[j*2+1] = (short)(j+1);
            }
            phys_ind[verts.length] = (short)((verts.length/2) - 1);
            phys_ind[(verts.length)+1] = (short)0;
            debug_mesh.add( level_geo.CompileMesh(phys_debug, phys_ind) );
            createStaticChain( pbies, false, group.mask);
        }

        for (SensorData group: scene.sensor_groups) {
            sensors.add(new SensorObject(this, group));
        }

    }

    public void step(float delta) {
        long startTime = System.nanoTime();
        world.step(delta, 5, 3);
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        game.profiler.addStat("World Step: (ms) "+(int)(duration*1.0e-6));
    }



    public Vec2 S2gl(Vec2 v){
        float SCALE = 4f;
        float y = (float)( (Gdx.graphics.getHeight()-(v.y*SCALE))  /(Gdx.graphics.getHeight()*2f)-1f) ;
        float x = (float)((v.x*SCALE)/(Gdx.graphics.getWidth()*2f)-1f) ;
        return new Vec2( x, y );
    }

    public Vec2 gl2S(Vec2 v){
        float y = (float)(((-1f*v.y)+1f)/2f)*(Gdx.graphics.getHeight()) ;
        float x = (float)((v.x+1f)/2f)*(Gdx.graphics.getWidth()) ;
        return new Vec2( x, y );
    }



    public void render(){

        tweenManager.update(Gdx.graphics.getDeltaTime());


        ticker += .01f;
        int player_count = game.players.size();
        Vec2 pp = new Vec2(0f,0f);
        float ubx = 99999f;
        float uby = 99999f;
        float lbx = -99999f;
        float lby = -99999f;
        for (int i=0;i<player_count;i++){
           Vec2 player_p = S2gl(B2S(game.players.get(i).sausage.head.getPosition()));
           if (player_p.x < ubx) { ubx = player_p.x;}
            if (player_p.y < uby) { uby = player_p.y;}
            if (player_p.x > lbx) { lbx = player_p.x;}
            if (player_p.y > lby) { lby = player_p.y;}
           pp = pp.add(player_p);
        }


        float dist = new Vector2(ubx,uby*1.4f).dst2(lbx,lby*1.4f);
        pp = pp.mul(1f/(float)player_count);
        Vector2 ppp = new Vector2(pp.x,pp.y);
        Vector2 difff = ppp.sub(new Vector2(camera.position.x, camera.position.y)).mul(1f);




        float final_dist =  (float)(Math.sqrt((double)dist));
        if (final_dist < view_dist){final_dist = view_dist;}
        //camera.position.set(finalx, finaly, final_dist);
        Tween.to(camera, MoveableAccessor.POSITION_X, .2f).target(pp.x).start(tweenManager);
        Tween.to(camera, MoveableAccessor.POSITION_Y, .2f).target(pp.y+1.5f).start(tweenManager);
        Tween.to(camera, MoveableAccessor.POSITION_Z, .3f).target(final_dist).start(tweenManager);

        camera.update();

        if (game.players.get(0).debug_draw_level == true){

            Gdx.gl.glDisable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            tex_shader.begin();
            Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
            tex.bind();
            Gdx.gl.glDepthRangef(0f, 1f);
            Gdx.gl.glDepthFunc(GL20.GL_LESS);
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST) ;
            tex_shader.setUniformMatrix("u_viewProj", camera.combined);
            for (int i = 0; i<texture_meshes.size();i++){
                texture_meshes.get(i).render(tex_shader, GL20.GL_TRIANGLES);
            }
            tex_shader.end();




            Gdx.gl.glDisable(GL20.GL_BLEND);
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST) ;

            //Gdx.gl20.glDisable(GL20.GL_BLEND);
            level_shader.begin();
            Gdx.gl.glDepthRangef(0f, 1f);
            Gdx.gl.glDepthFunc(GL20.GL_LESS);
            level_shader.setUniformMatrix("u_viewProj", camera.combined);
            for (int i = 0; i<vertex_meshes.size();i++){
                vertex_meshes.get(i).render(level_shader, GL20.GL_TRIANGLES);
            }
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
            for (int i = 0; i<vertex_meshes_alpha.size();i++){
                vertex_meshes_alpha.get(i).render(level_shader, GL20.GL_TRIANGLES);
            }
            level_shader.end();

            tex_shader.begin();
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            for (int i = 0; i<texture_meshes_alpha.size();i++){
                texture_meshes_alpha.get(i).render(tex_shader, GL20.GL_TRIANGLES);
            }
            tex_shader.end();


            wire_shader.begin();
            //Gdx.gl.glEnable(GL20.GL_BLEND) ;
            //Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            Gdx.gl20.glLineWidth(1f);
            Gdx.gl.glPolygonOffset(1f, 2f);
            Gdx.gl.glDepthRangef(0f, 100f);
            Gdx.gl.glDepthFunc(GL20.GL_LESS);
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST) ;
            Gdx.gl20.glDisable(GL20.GL_BLEND);
            wire_shader.setUniformMatrix("u_viewProj", camera.combined);
            wire_mesh.render(wire_shader, GL20.GL_LINES);
            Gdx.gl20.glDisable(GL20.GL_BLEND);
            wire_shader.end();
        }

        if (game.players.get(0).debug_draw_static_chains == true){
            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
            level_shader.begin();
            for (int i = 0; i<debug_mesh.size();i++){
                level_shader.setUniformMatrix("u_viewProj", camera.combined);
                debug_mesh.get(i).render(level_shader, GL20.GL_LINES);
            }
            level_shader.end();
        }


        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST) ;

        batch.begin();

        batch.setProjectionMatrix(camera.combined);
        for (SensorObject sensor: sensors) {
            if (sensor.active){

                batch.draw(banner_1, sensor.loc[0]-.12f , -sensor.loc[1] + ( (float)Math.sin((ticker+.02f)*5f))*.03f, .24f,.06f);
                batch.draw(O_button, sensor.loc[0]-.03f , -sensor.loc[1] + ( (float)Math.sin(ticker*5f))*.04f, .06f,.06f);


            }

        }

        batch.end();
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST) ;


    }


    public Vec2 GetSpawn(){
        Locus using = spawn_points.get(spawn_index);
        spawn_index += 1;
        return S2P(gl2S(new Vec2( using.pos[0], -using.pos[1])));

        //return new Vec2(10f,10f);
    }


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
        x = P2B(x); y = P2B(y); w = P2B(w); h = P2B(h);
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
}