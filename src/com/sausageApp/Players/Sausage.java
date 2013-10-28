package com.sausageApp.Players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.sausageApp.Game.State;
import com.sausageApp.Game.Units;
import com.sausageApp.screens.WormMesh;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import java.util.ArrayList;



/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/4/13
 * Time: 8:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class Sausage {
    public State state = State.getInstance();
    private Units units = new Units();

    public Player player;
    public ArrayList<Body> sausage_bodies;
    public Body head;
    public Body tail;
    public ArrayList<Link> sausage_links = new ArrayList<Link>();
    public Link head_link;
    public Link tail_link;
    public int sausage_length = 18;
    private float L_DIST = 1.2f;



    private ShaderProgram sausage_shader;

    private Mesh mesh;

    private Mesh end_cap;
    private ShaderProgram cap_shader;

    public float z_depth = 0f;
    public ShapeRenderer shapeRenderer = new ShapeRenderer();

    public FPSLogger FPS = new FPSLogger();

    public float SRADIUS = 7.5f;

    public Sausage(Player _player){

        player = _player;
        player.FORCE = .2f*sausage_length;

        Vec2 spawn = state.box.GetSpawn();
        sausage_bodies = createSausageBodies(spawn.x, spawn.y, SRADIUS, sausage_length);
        head = sausage_bodies.get(0);
        tail = sausage_bodies.get(sausage_length);


        for (int i=0;i<sausage_bodies.size();i++){

            Link new_link = new Link(sausage_bodies.get(i), this);
            sausage_links.add(new_link);
            new_link.body.setUserData(new_link);
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

    public void setCategory(int val){
        for (Body body: sausage_bodies) {
            Fixture fixture = body.m_fixtureList;
            Filter filter = fixture.getFilterData();
            filter.categoryBits = val;
            fixture.setFilterData(filter);
        }
    }

    public void setMask(int val){
        for (Body body: sausage_bodies) {
            Fixture fixture = body.m_fixtureList;
            Filter filter = fixture.getFilterData();
            filter.maskBits = val;
            fixture.setFilterData(filter);
        }
    }

    public void setGroupIndex(int val){
        for (Body body: sausage_bodies) {
            Fixture fixture = body.m_fixtureList;
            Filter filter = fixture.getFilterData();
            filter.groupIndex = val;
            fixture.setFilterData(filter);
        }
    }

    public ArrayList<Body> createSausageBodies(float x, float y, float radius, int link_count)    {
        x = units.P2B(x);
        y = units.P2B(y);
        radius = units.P2B(radius);
        ArrayList<Body> sausage = new ArrayList<Body>();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.1f, 0.8f);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 4.0f;
        fd.friction = 0.2f;

        RevoluteJointDef jd = new RevoluteJointDef();
        jd.collideConnected = false;
        jd.upperAngle = .77079633f;
        jd.lowerAngle = -.77079633f;
        jd.enableLimit = true;
        jd.maxMotorTorque = 20.0f;

        jd.motorSpeed = 0f;

        jd.enableMotor = true;


        //Body first = scenario.createDynamicCircle(x, y, radius*1.00f, 10f);
        Body first = state.box.createDynamicRect(x, y, radius*2.00f, radius*1.00f, 10f, .9f);
        Body prevBody = first;
        prevBody.m_angularDamping = 1.5f;
        sausage.add(prevBody);


        for (int i = 0; i < link_count; ++i) {
            //Body next = scenario.createDynamicCircle(x+(radius*L_DIST)+(i*(radius*L_DIST)), y, radius*1.00f, 10f);
            float friction = .2f;
            if (i >= link_count-1){
                friction = .9f;
            }
            Body next = state.box.createDynamicRect(x+(radius*2f*L_DIST)+(i*(radius*2f*L_DIST)), y, radius*2.00f, radius*1.00f, 10f, friction);
            next.m_angularDamping = .8f;
            next.m_linearDamping = .01f*i;
            Vec2 anchor = new Vec2(x+(i*(radius*2f*L_DIST)), y);
            jd.initialize(prevBody, next, anchor);
            state.box.world.createJoint(jd);
            sausage.add(next);
            prevBody = next;
        }
        return sausage;
    }

    public void render(){

        //probably need to not update links every single frame
        for (int i=0;i<sausage_links.size();i++){

            sausage_links.get(i).Update();
            //Gdx.app.log("HEAD",": "+head_link.curve_potential);
            //Gdx.app.log("TAIL",": "+tail_link.curve_potential);
        }
        float tot = 0f;
        for (int i=sausage_links.size()/2;i>=0;i--){
            tot += sausage_links.get(i).curvature;
            sausage_links.get(i).curve_potential = tot / sausage_links.size() * 20f;
        }
        tot = 0f;
        for (int i=sausage_links.size()/2;i<sausage_links.size();i++){
            tot += sausage_links.get(i).curvature;
            sausage_links.get(i).curve_potential = tot / sausage_links.size() * 20f;
        }

        if (player.debug_draw_sausage_links == false){
            SimpleDraw();
        }


        Gdx.gl20.glLineWidth(1f);
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
        sausage_shader.begin();
        UpdateMesh();
        sausage_shader.setUniformMatrix("u_worldView", state.scene.camera.combined);
        sausage_shader.setUniformf("z_depth", z_depth);
        if (player.debug_draw_sausage_mesh_lines){
            mesh.render(sausage_shader, GL20.GL_LINE_LOOP);
        } else {
            mesh.render(sausage_shader, GL20.GL_TRIANGLES);
        }

        sausage_shader.end();


        Gdx.gl20.glDisable(GL20.GL_BLEND);
        cap_shader.begin();
        cap_shader.setUniformf("z_depth", z_depth);
        cap_shader.setUniformMatrix("u_worldView", state.scene.camera.combined);
        cap_shader.setUniformf("player_color", player.color.r, player.color.g, player.color.b, 1.0f);


        float angle = player.sausage.head_link.NextAngle() * (float)(180f/Math.PI) - 180f;
        Quaternion rot = new Quaternion(new Vector3(0f,0f,1f), angle);
        Matrix4 capmat = new Matrix4(rot);

        Vector2 bv = gdx2gl((units.B2S(player.sausage.head.getPosition()))).mul(2f);
        cap_shader.setUniformf("cap_pos", bv.x,bv.y);
        cap_shader.setUniformMatrix("capmat", capmat);
        end_cap.render(sausage_shader, GL20.GL_TRIANGLES);

        angle = player.sausage.tail_link.PrevAngle() * (float)(180f/Math.PI) - 180f;
        rot = new Quaternion(new Vector3(0f,0f,1f), angle);
        capmat = new Matrix4(rot);

        bv = gdx2gl((units.B2S(player.sausage.tail.getPosition()))).mul(2f);
        cap_shader.setUniformf("cap_pos", bv.x,bv.y);
        cap_shader.setUniformMatrix("capmat", capmat);
        end_cap.render(sausage_shader, GL20.GL_TRIANGLES);

        cap_shader.end();



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


        float[] nodes = new float[(sausage_length+4)*2];

        Vector2 lp = gdx2gl((units.B2S(sausage_bodies.get(0).getPosition())));

        nodes[0] =  lp.x;
        nodes[1] = lp.y;
        for(int i = 1; i <= sausage_length+1; i++) {
            lp = gdx2gl((units.B2S(sausage_bodies.get(i-1).getPosition())));
            nodes[i*2] =  lp.x;
            nodes[i*2+1] =  lp.y;
        }

        lp = gdx2gl((units.B2S(sausage_bodies.get(sausage_bodies.size()-1).getPosition())));

        nodes[(sausage_length+1)*2] =  lp.x;
        nodes[(sausage_length+1)*2+1] = lp.y; // a used node

        sausage_shader.setUniform2fv("nodes", nodes, 0, (sausage_length+2)*2);

    }



    private void DefineShaders(){

        WormMesh worm = new WormMesh(sausage_length, player);
        mesh = worm.CompileMesh();
        sausage_shader = worm.MakeShader();

        end_cap = worm.CapMesh();
        cap_shader = worm.CapShader();

    }

    public void SimpleDraw(){
        //Gdx.gl.glDisable(GL20.GL_DEPTH_TEST) ;
        //Gdx.gl.glEnable(GL20.GL_FRONT_AND_BACK) ;
        shapeRenderer.setProjectionMatrix(state.scene.camera.combined);
        //shapeRenderer.translate(0f,0f,-1f);
        for(int i = 0; i < sausage_links.size(); i++) {



            Vector2 v1 = gdx2gl((units.B2S(sausage_bodies.get(i).getPosition())));

            float p = sausage_links.get(i).potential;
            float c = sausage_links.get(i).curve_potential;
            shapeRenderer.setColor(p, c, 0f, 1f);
            if (sausage_links.get(i).reversing == true){
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.circle(v1.x, v1.y, .012f, 12);
            } else {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.circle(v1.x, v1.y, .012f, 12);
            }

            shapeRenderer.end();
        }
    }


}
