package com.sausageApp.Simulation;

import org.jbox2d.common.Vec2;

import java.util.ArrayList;

public class ScenarioData {
    public ArrayList<StaticData> static_objects = new ArrayList<StaticData>();
    public float[] static_geo_verticies = new float[]{};
    public short[] static_geo_indicies = new short[]{};
    public float[] physics_bodies = new float[]{};
}
