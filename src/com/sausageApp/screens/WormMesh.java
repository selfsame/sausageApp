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

    public ArrayList<MeshSegment> segments;
    public int segment_count = 10;

    public WormMesh(){
        for (int i=0;i<segment_count;i++){
            segments.add( new MeshSegment(i), verticies );
        }

    }



    public Mesh CompileMesh(){
        Mesh mesh = new Mesh(false, 512, 0,
        new VertexAttribute(VertexAttributes.Usage.Position, 4, ShaderProgram.POSITION_ATTRIBUTE),
        new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoords"));

        mesh.setVertices( verticies );
        return mesh;
    }
}
