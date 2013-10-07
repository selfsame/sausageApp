package com.sausageApp.Players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
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
    public int sausage_length = 12;

    private Scenario scenario;

    private ShaderProgram sausage_shader;
    private ShaderProgram outline_shader;
    private Mesh mesh;
    private float[] vertices = new float[(sausage_length)*13];
    private int[] indicies = new int[(sausage_length)*13];
    private short[] short_indicies;

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
        DefineShaders(player.color);

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
    }

    public Vec2 VBetween(Body b1,Body b2){
        Vec2 v1 = b1.getPosition();
        Vec2 v2 = b2.getPosition();
        return v1.sub(v2);
    }

    public void PropigateImpulseHead(Vec2 v){
//        head.applyLinearImpulse(v, head.getWorldCenter());
        Vec2 hv = head.getLinearVelocity();
        head.setLinearVelocity(new Vec2((v.x + hv.x) / 2f, (v.y + hv.y) / 2f));
        Body nb1 = sausage_bodies.get(1);
        Body nb2 = sausage_bodies.get(2);
        Body nb3 = sausage_bodies.get(3);

        v = VBetween(head, nb1); // .mul(4f).add(v).mul(.5f);
        Vec2 nb1v = nb1.getLinearVelocity();
        nb1.setLinearVelocity(new Vec2(v.x+nb1v.x, v.y+nb1v.y));
//
//        nb1.applyLinearImpulse(v.mul(.5f), nb1.getWorldCenter());
//
//        v = VBetween(nb1, nb2).mul(4f).add(v).mul(.5f);
//
//        nb2.applyLinearImpulse(v.mul(.2f), nb2.getWorldCenter());
//
//        v = VBetween(nb2, nb3).mul(4f).add(v).mul(.5f);
//
//        nb3.applyLinearImpulse(v.mul(.05f), nb3.getWorldCenter());
    };

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
        UpdateMesh();

        Gdx.gl20.glLineWidth(scenario.P2S(4f));
        outline_shader.begin();
        //mesh.render(sausage_shader, GL20.GL_TRIANGLES);
        short[] outline_indicies = new short[(sausage_length)*13];
        for (int i = 0; i < (sausage_length)*4; i++){
            outline_indicies[i*2] = (short)(i);
            outline_indicies[i*2+1] = (short)(i+2);
        }
        mesh.setIndices(outline_indicies);

        mesh.render(outline_shader, GL20.GL_LINES);
        outline_shader.end();

        Gdx.gl20.glLineWidth(1f);
        sausage_shader.begin();
        mesh.setIndices(short_indicies);
        if (player.debug_draw_sausage_mesh_lines){
            mesh.render(sausage_shader, GL20.GL_LINES);
        } else {
            mesh.render(sausage_shader, GL20.GL_TRIANGLES);
        }

        sausage_shader.end();

        mesh.dispose();

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
        outline_indicies = null;
        short_indicies = null;






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

        Vector2 last_normal = new Vector2(0f,0f);
        for(int i = 0; i < sausage_length; i++) {
            if (true){
                Vector2 v1 = gdx2gl( scenario.B2S(sausage_bodies.get(i).getPosition()));
                Vector2 v2 = gdx2gl( scenario.B2S(sausage_bodies.get(i + 1).getPosition()));
                Vector2 vmid = v2.add(v1).mul(.5f);
                Vector2 vn = Normal(v1, v2);
                vn = vn.nor().mul(scenario.P2S(SRADIUS*2f)).div(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

                Vector2 mn = last_normal.add(vn).mul(.5f);

                vertices[i*12] = mn.x+v1.x;
                vertices[i*12+1] = mn.y+v1.y;
                vertices[i*12+2] = i*.001f;
                vertices[i*12+3] = -mn.x+v1.x;
                vertices[i*12+4] = -mn.y+v1.y;
                vertices[i*12+5] = i*.001f;

                vertices[i*12+6] = vn.x+vmid.x;
                vertices[i*12+7] = vn.y+vmid.y;
                vertices[i*12+8] = i*.001f;
                vertices[i*12+9] = -vn.x+vmid.x;
                vertices[i*12+10] = -vn.y+vmid.y;
                vertices[i*12+11] = i*.001f;

                last_normal = vn;

                indicies[i*12] = i*4;
                indicies[i*12+1] = i*4 + 1;
                indicies[i*12+2] = i*4 + 2;

                indicies[i*12+3] = i*4 + 1;
                indicies[i*12+4] = i*4 + 2;
                indicies[i*12+5] = i*4 + 3;

                indicies[i*12+6] = i*4 + 2;
                indicies[i*12+7] = i*4 + 3;
                indicies[i*12+8] = i*4 + 4;

                indicies[i*12+9] = i*4 + 3;
                indicies[i*12+10] = i*4 + 4;
                indicies[i*12+11] = i*4 + 5;

                if (i == sausage_length-1){
                    Vector2 tv = v1.sub(v2).mul(scenario.P2S(SRADIUS*2f)).div(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                    vertices[i*12+12] = v2.x+vn.x;
                    vertices[i*12+13] = v2.y+vn.y;
                    vertices[i*12+14] = i*.001f;
                    vertices[i*12+15] = v2.x-vn.x;
                    vertices[i*12+16] = v2.y-vn.y;
                    vertices[i*12+17] = i*.001f;
                }



            }
        }

        mesh = new Mesh(true, (sausage_length)*13, (sausage_length*13), VertexAttribute.Position());

        short_indicies = new short[(sausage_length)*13];
        for(int i = 0; i < (sausage_length)*13; i++)
        {
            short_indicies[i] = (short)indicies[i];
        }
        mesh.setVertices(vertices);
        mesh.setIndices(short_indicies);
    }



    private void DefineShaders(Color fragColor){

        String vertexShader = "attribute vec4 a_position;    \n"
                + "void main()                   \n"
                + "{                             \n"
                + "   gl_Position = a_position;  \n"
                + "}                             \n";
        String fragmentShader = "#ifdef GL_ES                \n"
                + "precision mediump float;    \n"
                + "#endif                      \n"
                + "void main()                 \n"
                + "{                           \n"
                + "  gl_FragColor =  vec4("+fragColor.r+","+fragColor.b+","+fragColor.g+","+fragColor.a+");\n"
                + "}";

        String outlineFragShader = "#ifdef GL_ES                \n"
                + "precision mediump float;    \n"
                + "#endif                      \n"
                + "void main()                 \n"
                + "{                           \n"
                + "  gl_FragColor =  vec4("+.0+","+.2+","+.1+","+1.0+");\n"
                + "}";

        sausage_shader = new ShaderProgram(vertexShader, fragmentShader);

        outline_shader = new ShaderProgram(vertexShader, outlineFragShader);

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
