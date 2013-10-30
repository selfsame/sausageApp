package com.sausageApp.Scene;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.sausageApp.Game.State;
import com.sausageApp.Game.Units;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import java.util.ArrayList;

public class DynamicObject extends DynamicData{
    public State state = State.getInstance();
    private Units units = new Units();

    public String type;
    public String name;
    public float radius;
    public float friction;
    public float restitution;
    public Vector3 position;
    public float mass;
    public ArrayList<String> classes = new ArrayList<String>();

    public ArrayList<GameObject> visuals = new ArrayList<GameObject>();

    public Body body;

    public DynamicObject(Scene scene, DynamicData data){
        type = data.type;
        name = data.name;
        radius = data.radius;
        position = new Vector3(data.position);
        friction = data.friction;
        restitution = data.restitution;
        mass = data.mass;
        classes = data.classes;
        Vec2 m = units.unAspect(units.S2B(units.gl2S(new Vec2(position.x, -position.y))));
        float h =  units.S2B(units.gl2S(radius));
        body = state.box.createDynamicCircle(m.x,m.y, 2f*(radius/.12f), mass, friction, restitution);

        if (scene.object_map.containsKey(name)){
           visuals.add(scene.object_map.get(name));
        }

    }

    public void update(){
        Vec2 p = body.getPosition();
        float r = body.getAngle();
        for (GameObject g : visuals){
            Vec2 m = units.applyAspect(units.S2gl(units.B2S(p)));
            //float h = units.S2gl( units.B2S(position.z));
            g.setRotation(new Quaternion(0f, 0f, 1f, r) );
            g.setPosition(new Vector3(m.x,m.y,0f));

        }

    }
}
