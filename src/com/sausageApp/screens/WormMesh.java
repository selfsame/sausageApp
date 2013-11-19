package com.sausageApp.screens;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.sausageApp.Players.Player;

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

    private Player player;

    public WormMesh(int _count, Player _player){
        segment_count = _count;
        player = _player;
        for (int i=0;i<segment_count;i++){
//            if (i==0){
//                segments.add( new MeshSegment(i, verticies, indicies,true) );
//            } else {
              segments.add( new MeshSegment(i, verticies, indicies, player) );

        }

    }



    public Mesh CompileMesh(){
        Mesh mesh = new Mesh(false, verticies.size(), indicies.size(),
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
        mesh.setVertices( verts, 0, verticies.size() );
        mesh.setIndices( inds );
        return mesh;
    }

    public ShaderProgram MakeShader(){
        String vertexShader =
                "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                + "attribute vec4 a_color ;   \n"
                + "attribute vec4 a_normal ;   \n"
                        + "uniform mat4 u_worldView;"
                + "uniform vec2 nodes[128];                           \n"
                + "uniform float z_depth;  \n"
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
                + "  v_concavity = sign(  distance(next_normal+next, prev_normal+prev) - distance(next, prev));                         \n"


//                + "  float curve_amount = distance(next_normal+next, prev_normal+prev) - distance(next, prev);                                    \n"
//                + "  float prev_curve_amount = distance(node, prev_normal+prev) - distance(node, prev);                                    \n"
//                + "  float next_curve_amount = distance(next_normal+next, node) - distance(next, node);                                    \n"
//                + "  curve_amount =  (v_concavity + abs(v_concavity))*.5 * curve_amount*15.0 ;  "
//                + "  prev_curve_amount =  (sign(prev_curve_amount) + abs(sign(prev_curve_amount)))*.5 * prev_curve_amount*15.0 ;  "
//                        + "  next_curve_amount =  (sign(next_curve_amount) + abs(sign(next_curve_amount)))*.5 * next_curve_amount*15.0 ;  "
                + "  float curve_amount = distance(next_normal+next, prev_normal+prev) - distance(next, prev);                                    \n"
                + "  float prev_curve_amount = distance(node, prev_normal+prev) - distance(node, prev);                                    \n"
                + "  float next_curve_amount = distance(next_normal+next, node) - distance(next, node);                                    \n"
                + "  curve_amount =  (v_concavity ) * curve_amount*17.0 ;  "
                + "  prev_curve_amount =  (prev_curve_amount) * prev_curve_amount*17.0;  "
                + "  next_curve_amount =  (next_curve_amount)  * next_curve_amount*17.0;  "


                +"   vec2 nspread = (next_normal * interpolation_right*(1.0+next_curve_amount) + prev_normal * interpolation_left*(1.0+prev_curve_amount)) + a_position.x*((prev_normal+next_normal)/4.0)*(1.0+curve_amount * (1.0+(v_concavity-abs(v_concavity))*.1) ) ;"
                + "  v_color = a_color;                     \n"
                + "                                      \n"

                //+ "   nspread = nspread - abs(1- v_concavity)*abs(a_position.y+1)*.003*a_position.x;                          \n"

                + "   mod =   vec4(pos.x + nspread.x  , pos.y + nspread.y    ,z_depth+index*.0001,a_position.w);                        \n"
                + "   gl_Position =  u_worldView * mod ;   \n"
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

                + "  if(mask*v_concavity > 0) col = vec4(1.0,0.0,0.0,0.0); \n"
                //+ "  if(mask*v_concavity > 0.0) discard;"
                //+ "  if(mask > 0.0) col = vec4(1.0,0.0,0.0,1.0); "//discard;" //vec4(.1,.2,.2,1.0); "//discard; \n"    (.1f, .2f, .2f, 1f)

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




    public Mesh CapMesh(){
        Mesh mesh = new Mesh(false, 512, 512,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE));
        float[] verts = new float[]{
                0.000000f, -0.030000f, 0.000000f,
                0.058424f, -0.030000f, 0.000000f,
                0.058424f, 0.030000f, 0.000000f,
                -0.029544f, -0.005209f, 0.000000f,
                -0.029544f, 0.005209f, 0.000000f,
                -0.049048f, -0.014930f, 0.000000f,
                -0.025981f, -0.015000f, 0.000000f,
                -0.025981f, 0.015000f, 0.000000f,
                -0.019284f, -0.022981f, 0.000000f,
                -0.019284f, 0.022981f, 0.000000f,
                -0.010261f, -0.028191f, 0.000000f,
                -0.010261f, 0.028191f, 0.000000f,
                0.000000f, -0.030000f, 0.000000f,
                0.000000f, 0.030000f, 0.000000f,
                -0.049048f, 0.014930f, 0.000000f };

        short[] inds = new short[]{0 , 1, 2,
                3 , 4, 5,
                6 , 7, 4,
                8 , 9, 7,
                10,  11, 9,
                12,  13, 11,
                13,  0, 2,
                3 , 6, 4,
                6 , 8, 7,
                8 , 10, 9,
                10,  12, 11,
                4 , 14, 5};

        mesh.setVertices( verts );
        mesh.setIndices( inds );
        return mesh;
    }

    public ShaderProgram CapShader(){
        String vertexShader =
                "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"

                        + "uniform mat4 u_worldView; \n"
                        + "uniform float z_depth;  \n"
                        + "uniform mat4 capmat; \n"
                        + "uniform vec2 cap_pos;                           \n"
                        + "uniform vec4 player_color;                           \n"
                        + "varying vec4 v_color;                        \n"

                        +" vec4 mod; \n"

                        + "void main()                   \n"
                        + "{                             \n"
                        + "   v_color = player_color;                    \n"
                        + "   mod = vec4(cap_pos.x, cap_pos.y, z_depth, 1.0);                    \n"
                        + "   gl_Position =  u_worldView * (a_position * capmat + mod)  ;   \n"
                        + "}                             \n";
        String fragmentShader = "#ifdef GL_ES                \n"
                + "precision mediump float;    \n"

                + "#endif                      \n"
                + "                      \n"
                + "varying vec4 v_color;                       \n"

                + "void main()                 \n"
                + "{                           \n"
                + "  gl_FragColor = v_color;    \n"

                + "}";
        return new ShaderProgram(vertexShader, fragmentShader);
    }








}
