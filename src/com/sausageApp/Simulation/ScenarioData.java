package com.sausageApp.Simulation;

import org.jbox2d.common.Vec2;

import java.util.ArrayList;

public class ScenarioData {
    public float[] camera = new float[]{};
    public float[] static_vertices = new float[]{};
    public short[] static_indicies = new short[]{};
    public ArrayList<float[]> collide_groups = new ArrayList<float[]>();
}
