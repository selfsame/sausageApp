package com.sausageApp.Simulation;

import com.badlogic.gdx.utils.Array;
import org.jbox2d.common.Vec2;


import java.util.ArrayList;

public class ScenarioData {
    public float[] camera = new float[]{};
    public ArrayList<VertexObject> vertex_objects = new ArrayList<VertexObject>();
    public ArrayList<VertexObject> texture_objects = new ArrayList<VertexObject>();
    public float[] static_tex_vertices = new float[]{};
    public short[] static_tex_indicies = new short[]{};
    public float[] wire_vertices = new float[]{};
    public short[] wire_indicies = new short[]{};
    public ArrayList<Collider> collide_groups = new ArrayList<Collider>();
    public ArrayList<SensorData> sensor_groups = new ArrayList<SensorData>();
    public ArrayList<Locus> locii = new ArrayList<Locus>();

}
