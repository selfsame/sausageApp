package com.sausageApp.Scene;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/28/13
 * Time: 7:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class DynamicData {
    public String type;
    public String name;
    public float radius;
    public float friction;
    public float restitution;
    public float[] position = new float[]{};
    public float mass;
    public ArrayList<String> classes = new ArrayList<String>();
    public DynamicData(){};
}
