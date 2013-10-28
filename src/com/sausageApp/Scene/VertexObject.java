package com.sausageApp.Scene;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/14/13
 * Time: 12:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class VertexObject {
    public String name;
    public boolean texture;
    public float[] local_mat4 = new float[]{};
    public float[] position = new float[]{};
    public float[] scale = new float[]{};
    public float[] quaternion = new float[]{};
    public float[] static_vertices = new float[]{};
    public short[] static_indicies = new short[]{};
    public ArrayList<String> classes = new ArrayList<String>();
    public boolean alpha;
    public VertexObject(){};
}
