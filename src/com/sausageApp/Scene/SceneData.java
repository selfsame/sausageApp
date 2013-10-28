package com.sausageApp.Scene;


import java.util.ArrayList;

public class SceneData {
    public float gravity;
    public float[] background = new float[]{};
    public ArrayList<VertexObject> vertex_objects = new ArrayList<VertexObject>();
    public ArrayList<VertexObject> texture_objects = new ArrayList<VertexObject>();
    public float[] static_tex_vertices = new float[]{};
    public short[] static_tex_indicies = new short[]{};
    public float[] wire_vertices = new float[]{};
    public short[] wire_indicies = new short[]{};
    public ArrayList<Collider> collide_groups = new ArrayList<Collider>();
    public ArrayList<SensorData> sensor_groups = new ArrayList<SensorData>();
    public ArrayList<DynamicData> dynamic_objects = new ArrayList<DynamicData>();
    public ArrayList<Locus> locii = new ArrayList<Locus>();

}
