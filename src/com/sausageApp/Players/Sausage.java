package com.sausageApp.Players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.sausageApp.screens.WormMesh;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import java.util.ArrayList;

import com.sausageApp.Simulation.Scenario;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/4/13
 * Time: 8:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class Sausage {

    public Player player;
    public ArrayList<Body> sausage_bodies;
    public Body head;
    public Body tail;
    public ArrayList<Link> sausage_links = new ArrayList<Link>();
    public Link head_link;
    public Link tail_link;
    public int sausage_length = 10;

    private Scenario scenario;

    private ShaderProgram sausage_shader;

    private Mesh mesh;

    public ShapeRenderer shapeRenderer = new ShapeRenderer();

    public FPSLogger FPS = new FPSLogger();

    public float SRADIUS = 8f;

    public Sausage(Player _player, Scenario _scenario){
        player = _player;
        scenario = _scenario;
        Vec2 spawn = scenario.GetSpawn();
        sausage_bodies = createSausageBodies(spawn.x, spawn.y, SRADIUS, sausage_length);
        head = sausage_bodies.get(0);
        tail = sausage_bodies.get(sausage_length);


        for (int i=0;i<sausage_bodies.size();i++){

            Link new_link = new Link(sausage_bodies.get(i), this);
            sausage_links.add(new_link);
            if (i > 0) {
               new_link.prev = sausage_links.get(i-1);
               sausage_links.get(i-1).next = new_link;
            }
        }
        head_link = sausage_links.get(0);
        tail_link = sausage_links.get(sausage_links.size()-1);

        DefineShaders();
    }

    public Vec2 VBetween(Body b1,Body b2){
        Vec2 v1 = b1.getPosition();
        Vec2 v2 = b2.getPosition();
        return v1.sub(v2);
    }



    public ArrayList<Body> createSausageBodies(float x, float y, float radius, int link_count)    {
        x = scenario.P2B(x);
        y = scenario.P2B(y);
        radius = scenario.P2B(radius);
        ArrayList<Body> sausage = new ArrayList<Body>();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.1f, 0.8f);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 46.0f;
        fd.friction = 0.1f;

        RevoluteJointDef jd = new RevoluteJointDef();
        jd.collideConnected = false;
        jd.upperAngle = .97079633f;
        jd.lowerAngle = -.97079633f;
        jd.enableLimit = true;


        Body first = scenario.createDynamicCircle(x, y, radius*1.05f);
        Body prevBody = first;
        sausage.add(first);


        for (int i = 0; i < link_count; ++i) {
            Body next = scenario.createDynamicCircle(x+(radius*2f)+(i*(radius*2f))*1.06f, y, radius*1.05f);
            next.m_angularDamping = .8f;
            Vec2 anchor = new Vec2(x+(i*(radius*2f)), y);
            jd.initialize(prevBody, next, anchor);
            scenario.world.createJoint(jd);
            sausage.add(next);
            prevBody = next;
        }
        return sausage;
    }

    public void render(){






        // hack for head

        Vec2 v1 = scenario.SFlip(scenario.B2S(sausage_bodies.get(sausage_bodies.size()-1).getPosition()));

        shapeRenderer.begin(ShapeRenderer.ShapeType.FilledCircle);

        Gdx.gl20.glLineWidth(0f);
        shapeRenderer.setColor(player.color);
        if (!player.debug_draw_sausage_mesh_lines){
            shapeRenderer.filledCircle(v1.x, v1.y, scenario.P2S(SRADIUS));
        }
        shapeRenderer.end();


        if (player.debug_draw_sausage_links){
            SimpleDraw();
        }


        Gdx.gl20.glLineWidth(2f);
        sausage_shader.begin();
        UpdateMesh();
        if (player.debug_draw_sausage_mesh_lines){
            mesh.render(sausage_shader, GL20.GL_LINES);
        } else {
            mesh.render(sausage_shader, GL20.GL_TRIANGLES);
        }

        sausage_shader.end();





    }

    public Vector2 gdx2gl(Vec2 v){
        float y = (float) (Gdx.graphics.getHeight()-v.y)/Gdx.graphics.getHeight()*2f-1f ;
        float x = (float) v.x/Gdx.graphics.getWidth()*2f-1f ;
        return new Vector2( x, y );
    };

    public Vector2 Normal(Vector2 v1, Vector2 v2){
        return new Vector2( -(v2.y-v1.y), v2.x-v1.x );
    }

    public void UpdateMesh(){


        float[] nodes = new float[24];
        Vector2 lp = gdx2gl((scenario.B2S(sausage_bodies.get(0).getPosition())));
        nodes[0] =  lp.x;
        nodes[1] = lp.y;
        for(int i = 1; i < sausage_length+1; i++) {
            lp = gdx2gl((scenario.B2S(sausage_bodies.get(i).getPosition())));
            nodes[i*2] =  lp.x;
            nodes[i*2+1] =  lp.y;
        }

        sausage_shader.setUniform2fv("nodes", nodes, 0, 24);
//        sausage_shader.setUniform2fv("nodes", new float[]{
//                .01f,.0f, //repeated first entry
//                .05f,.0f,
//                .1f,.2f,
//                .1f,.0f,
//                .3f,.25f,
//                .4f,.05f,
//                .5f,.25f,
//                .6f,.05f,
//                .7f,.25f,
//                .8f,.05f,
//                .9f,.25f,
//                .9f,.25f, // repeated last entry
//        }, 0, 24);
        sausage_shader.setUniform1fv("concavity", new float[]{
                1f, //repeated first entry
                -1f,
                1f,
                -1f,
                1f,
                -1f,
                1f,
                -1f,
                1f,
                -1f,
                1f,
                -1f, // repeated last entry
        }, 0, 12);


    }



    private void DefineShaders(){

        WormMesh worm = new WormMesh();
        mesh = worm.CompileMesh();
        sausage_shader = worm.MakeShader();

    }

    public void SimpleDraw(){
        for(int i = 0; i < sausage_links.size(); i++) {
            Vec2 v1 = sausage_bodies.get(i).getPosition();
            v1 = scenario.SFlip(scenario.B2S(v1));
            shapeRenderer.begin(ShapeRenderer.ShapeType.Circle);

            shapeRenderer.setColor(0f, 1f, 0f, 1f);
            shapeRenderer.circle(v1.x, v1.y, scenario.P2S(SRADIUS));
            shapeRenderer.end();
        }
    }


}
