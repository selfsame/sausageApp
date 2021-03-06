package com.sausageApp.Scene;


import java.util.ArrayList;
import java.util.HashMap;

public class SceneData {
    public float gravity;
    public float[] background = new float[]{};
    public HashMap<String, ArrayList<String>> groups = new HashMap<String, ArrayList<String>>();
    public ArrayList<VertexObject> game_objects = new ArrayList<VertexObject>();

    public ArrayList<InstanceData> instances = new ArrayList<InstanceData>();

    public ArrayList<Collider> collide_groups = new ArrayList<Collider>();
    public ArrayList<SensorData> sensor_groups = new ArrayList<SensorData>();
    public ArrayList<DynamicData> dynamic_objects = new ArrayList<DynamicData>();
    public ArrayList<Locus> locii = new ArrayList<Locus>();

}
