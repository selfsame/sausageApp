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
    public int segment_count;

    public WormMesh(int _count){
        segment_count = _count;
        for (int i=0;i<segment_count;i++){
//            if (i==0){
//                segments.add( new MeshSegment(i, verticies, indicies,true) );
//            } else {
              segments.add( new MeshSegment(i, verticies, indicies) );

        }

    }



    public Mesh CompileMesh(){
        Mesh mesh = new Mesh(false, 512, 512,
        new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
        new VertexAttribute(VertexAttributes.Usage.Color, 4, "a_color"),
        new VertexAttribute(VertexAttributes.Usage.Normal, 4, "a_normal"));
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
                + "attribute vec4 a_normal ;   \n"
                + "uniform vec2 nodes[128];                           \n"
                //+ "uniform float concavity[48];                           \n"
                + "                       \n"
                + "varying vec4 v_color;                        \n"
                + "varying vec4 v_normal;   \n"
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
                + "                       \n"
                + "                       \n"
                + "   index = int(a_position.z)+1;             \n"
                + "   interpolation_left = a_color.z;             \n"
                + "   interpolation_right = a_color.w;             \n"
                + "   node = nodes[index]; prev = nodes[index-1]; next = nodes[index+1];              \n"

                + "   vec2 pos = node + ((next - node ) * interpolation_right + ((prev - node ) * interpolation_left ) )   ;                      \n"

                + " v_normal = a_normal;"
                + " next_normal = vec2(a_position.y * -1.0*(next.y-node.y)*1.0 , a_position.y * (next.x-node.x)*1.0  );                      \n"
                + " prev_normal = vec2(a_position.y * -1.0*(node.y-prev.y)*1.0 , a_position.y * (node.x-prev.x)*1.0 );                      \n"
                + " next_normal = next_normal*.03/length(next_normal);"
                + " prev_normal = prev_normal*.03/length(prev_normal);"
                +"   vec2 nspread = (next_normal * interpolation_right + prev_normal * interpolation_left) + a_position.x*(normalize((prev_normal+next_normal)/2.0)*.015) ;"
                + "  v_color = a_color;                     \n"
                + "                                      \n"
                + "  v_concavity = sign(  distance(next_normal+next, prev_normal+prev) - distance(next, prev));                         \n"
                //+ "   nspread = nspread - abs(1- v_concavity)*abs(a_position.y+1)*.003*a_position.x;                          \n"

                + "   mod =   vec4(pos.x + nspread.x  , pos.y + nspread.y    ,0.0,a_position.w);                        \n"
                + "   gl_Position =   mod;   \n"
                + "}                             \n";
        String fragmentShader = "#ifdef GL_ES                \n"
                + "precision mediump float;    \n"

                + "#endif                      \n"
                + "                      \n"
                + "varying vec4 v_normal ;   \n"
                + "varying vec4 v_color;                       \n"
                + "varying float v_concavity;                        \n"
                + "float mask;"
                + "void main()                 \n"
                + "{                           \n"
                + "float thresh = 1.0;"
                + "vec4 col = v_normal; "
                + "mask = (v_color.x * v_color.x) - ( v_color.y) ;"
                //+ "  if(mask*v_concavity > -0.1) col = vec4(1.0,.5,.5,1.0); \n"
                + "  if(mask*v_concavity > 0.1) col = vec4(.1,.2,.2,1.0); "//discard; \n"    (.1f, .2f, .2f, 1f)

                + "  gl_FragColor = col;    \n"

                + "}";
        return new ShaderProgram(vertexShader, fragmentShader);
    }

    public ShaderProgram MakeDebugShader(){
        String vertexShader =
                "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                        + "attribute vec4 a_color ;   \n"
                        + "uniform vec2 nodes[128];                           \n"
                        //+ "uniform float concavity[48];                           \n"
                        + "                       \n"
                        + "varying vec4 v_color;                        \n"
                        //+ "varying float v_concavity;                        \n"
                        + "vec4 mod;                            \n"
                        + "int index;                           \n"
                        + "float interpolation_left;                           \n"
                        + "float interpolation_right;                           \n"
                        + "vec2 node; "
                        + "vec2 prev; "
                        + "vec2 next; "

                        + "vec2 next_normal; float next_nrad; float av_nrad;"
                        + "vec2 prev_normal; float prev_nrad; "
                        + "void main()                   \n"
                        + "{                             \n"
                        + "                       \n"
                        + "                       \n"
                        + "   index = int(a_position.z)+1;             \n"
                        + "   interpolation_left = a_color.z;             \n"
                        + "   interpolation_right = a_color.w;             \n"
                        + "   node = nodes[index]; prev = nodes[index-1]; next = nodes[index+1];              \n"

                        + "   vec2 pos = node + ((next - node ) * interpolation_right + ((prev - node ) * interpolation_left ) )   ;                      \n"
//
//
                        + " next_normal = vec2(a_position.y * -1.0 * (next.y-node.y)*1.0 , a_position.y * (next.x-node.x)*1.0  );                      \n"
                        + " prev_normal = vec2(a_position.y * -1.0 * (node.y-prev.y)*1.0 , a_position.y * (node.x-prev.x)*1.0 );                      \n"
                        + " next_normal = next_normal*.03/length(next_normal);"
                        + " prev_normal = prev_normal*.03/length(prev_normal);"
                        +"   vec2 nspread = (next_normal * interpolation_right + prev_normal * interpolation_left) + a_position.x*((prev_normal+next_normal)/4.0) ;"
                        + "  v_color = a_color;                     \n"
//                        + "                                        \n"
                        //+ "  v_concavity = concavity[index];                         \n"
                        + "                           \n"

                        + "   mod =   vec4(pos.x + nspread.x  , pos.y + nspread.y    ,0.0,a_position.w);                        \n"
                        + "   gl_Position =   mod;   \n"
                        //+ "   gl_Position =   vec4(node.x, node.y, 0.0, a_position.w);   \n"
                        + "}                             \n";
        String fragmentShader = "#ifdef GL_ES                \n"
                + "precision mediump float;    \n"

                + "#endif                      \n"
                + "                      \n"
                + "varying vec4 v_color;                       \n"
                //+ "varying float v_concavity;                        \n"
                //+ "float mask; vec4 col;"
                + "void main()                 \n"
                + "{                           \n"
                //+ "float thresh = 1.0;"
                //+ "col = vec4(1.0,.5,.5,1.0); "
                //+ "mask = (v_color.x * v_color.x) - ( v_color.y) ;"
                //+ "  if(mask*v_concavity > -0.1) col = vec4(1.0,.5,.5,1.0); \n"
                //+ "  if(mask*v_concavity > 0.0) discard; \n"

                + "  gl_FragColor = vec4(1.0,0.0,0.0,1.0);    \n"

                + "}";
        return new ShaderProgram(vertexShader, fragmentShader);
    }

}
