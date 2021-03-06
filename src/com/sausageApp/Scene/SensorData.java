package com.sausageApp.Scene;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/26/13
 * Time: 8:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class SensorData {
    public String name;
    public ArrayList<String> children = null;
    public String tag;
    public String usage;
    public boolean enter;
    public boolean exit;
    public float fmod;
    public int imod;
    public String istring;
    public float[] loc = new float[]{};
    public float[] verts = new float[]{};
    public SensorData(){}
}
