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
public class InstanceData {
    public String name;
    public ArrayList<String> children;
    public float[] position;
    public float[] scale;
    public float[] quaternion;
    public String group;
    public ArrayList<String> classes = new ArrayList<String>();
    public InstanceData(){};
}
