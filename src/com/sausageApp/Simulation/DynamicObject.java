package com.sausageApp.Simulation;

import com.badlogic.gdx.math.Vector3;
import com.sausageApp.Simulation.DynamicData;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import java.util.ArrayList;

public class DynamicObject extends DynamicData{
    public Scenario scene;
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

    public DynamicObject(Scenario _scene, DynamicData data){
        scene = _scene;
        type = data.type;
        name = data.name;
        radius = data.radius;
        position = new Vector3(data.position);
        friction = data.friction;
        restitution = data.restitution;
        mass = data.mass;
        classes = data.classes;
        body = scene.createDynamicCircle(position.x, position.y, radius, mass);

        if (scene.object_map.containsKey(name)){
           visuals.add(scene.object_map.get(name));
        }

    }

    public void update(){
        Vec2 p = body.getPosition();
        for (GameObject g : visuals){
            g.setPosition(new Vector3(p.x,p.y,position.z));
        }

    }
}
