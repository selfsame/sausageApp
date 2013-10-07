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

    public ShaderProgram MakeShader(){
        String vertexShader =
                "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                + "attribute vec4 a_color ;   \n"
                + "uniform vec2 nodes[24];                           \n"
                + "uniform float concavity[12];                           \n"
                + "                       \n"
                + "varying vec2 v_color;                        \n"
                + "varying float v_concavity;                        \n"
                + "vec4 mod;                            \n"
                + "int index;                           \n"
                + "float interpolation_left;                           \n"
                + "float interpolation_right;                           \n"
                + "vec2 node; "
                + "vec2 prev; "
                + "vec2 next; "
                + "mat4 rot; "
                + "vec2 next_normal; float next_nrad; float av_nrad;"
                + "vec2 prev_normal; float prev_nrad; "
                + "void main()                   \n"
                + "{                             \n"
                + "  rot[0] = 0.0;rot[1] = 0.0; rot[2] = 0.0;rot[3]=0.0;                     \n"
                + "  rot[2][2] = 1.0; rot[3][3] = 1.0;                     \n"
                + "                       \n"
                + "                       \n"
                + "   index = int(a_position.z)+1;             \n"
                + "   interpolation_left = a_color.z;             \n"
                + "   interpolation_right = a_color.w;             \n"
                + "   node = nodes[index]; prev = nodes[index-1]; next = nodes[index+1];              \n"
                +"   next_nrad = atan( (next.y-node.y), next.x-node.x); "
                +"   prev_nrad = atan( (node.y-prev.y), node.x-prev.x); "
                        +"   av_nrad = (next_nrad* interpolation_right + prev_nrad* interpolation_left) + a_position.x*((prev_nrad+next_nrad))*.5/2 ;"
                        + "    rot[0][0] = cos(av_nrad);   rot[0][1] = -sin(av_nrad);               \n"
                        + "    rot[1][0] = sin(av_nrad);   rot[1][1] = cos(av_nrad);                   \n"
                        + "                       \n"
                        + "                       \n"
                + "   vec2 pos = node + ((next - node ) * interpolation_right + ((prev - node ) * interpolation_left ) )   ;                      \n"

                //+ "   next_normal = vec2( cos(av_nrad)*.0 - sin(av_nrad)*(a_position.y*.05), cos(av_nrad)*(a_position.y*.05) + sin(av_nrad)*.0 );                      \n"

               // + " next_normal = vec4(0.0, (a_position.y*.05), 0.0, 0.0) * rot;                      \n"
                        + " next_normal = vec2(a_position.y * -(next.y-node.y) * .5, a_position.y * (next.x-node.x) * .5 );                      \n"
                + "  v_color = a_color;                     \n"
                + "                           \n"
                + "  v_concavity = concavity[index]*a_position.y;                         \n"
                + "                           \n"
                //+ "   mod =   vec4(pos.x + next_normal.x , pos.y + next_normal.y     ,0.0,a_position.w);                        \n"
                + "   mod =   vec4(pos.x + next_normal.x  , pos.y + next_normal.y    ,0.0,a_position.w);                        \n"
                + "   gl_Position =   mod;   \n"
                + "}                             \n";
        String fragmentShader = "#ifdef GL_ES                \n"
                + "precision mediump float;    \n"

                + "#endif                      \n"
                + "                      \n"
                + "varying vec4 v_color;                       \n"
                + "varying float v_concavity;                        \n"
                + "float mask;"
                + "void main()                 \n"
                + "{                           \n"
                + "float thresh = 1.0;"
                + "mask = (v_color.x * v_color.x) - ( v_color.y) ;"
                + "  if(mask*v_concavity > 0.0) thresh = 0.0; \n"
                + "  gl_FragColor = vec4(thresh,.5,.5,1.0);    \n"

                + "}";
        return new ShaderProgram(vertexShader, fragmentShader);
    }

}
