package com.sausageApp.Scene;


import java.util.ArrayList;
import java.util.HashMap;

public class SceneData {
    public float gravity;
    public float[] background = new float[]{};
    public HashMap<String, ArrayList<String>> groups = new HashMap<String, ArrayList<String>>();
    public ArrayList<VertexObject> vertex_objects = new ArrayList<VertexObject>();
    public ArrayList<VertexObject> texture_objects = new ArrayList<VertexObject>();
    public ArrayList<InstanceData> instances = new ArrayList<InstanceData>();
    public float[] static_tex_vertices;
    public short[] static_tex_indicies;
    public float[] wire_vertices;
    public short[] wire_indicies;
    public ArrayList<Collider> collide_groups = new ArrayList<Collider>();
    public ArrayList<SensorData> sensor_groups = new ArrayList<SensorData>();
    public ArrayList<DynamicData> dynamic_objects = new ArrayList<DynamicData>();
    public ArrayList<Locus> locii = new ArrayList<Locus>();

}
