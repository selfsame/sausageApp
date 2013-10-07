package com.sausageApp.screens;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/6/13
 * Time: 9:50 PM
 * To change this template use File | Settings | File Templates.
 */


public class WormMesh {
    public ArrayList<Float> verticies = new ArrayList<Float>();
    public ArrayList<Short> indicies = new ArrayList<Short>();

    public ArrayList<MeshSegment> segments = new ArrayList<MeshSegment>();
    public int segment_count = 9;

    public WormMesh(){
        for (int i=0;i<segment_count;i++){
            segments.add( new MeshSegment(i, verticies, indicies) );
        }

    }



    public Mesh CompileMesh(){
        Mesh mesh = new Mesh(false, 512, 512,
        new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
        new VertexAttribute(VertexAttributes.Usage.Color, 4, "a_color"));
        float[] verts = new float[verticies.size()];
        for (int i=0;i<verticies.size();i++){
            verts[i] = verticies.get(i);
        }
        short[] inds = new short[indicies.size()];
        for (int i=0;i<indicies.size();i++){
            inds[i] = indicies.get(i);
        }
        mesh.setVertices( verts );
        mesh.setIndices( inds );
        return mesh;
    }
}
