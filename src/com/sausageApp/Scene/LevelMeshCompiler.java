package com.sausageApp.Scene;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created with IntelliJ IDEA.
 * User: jparker
 * Date: 10/9/13
 * Time: 1:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class LevelMeshCompiler {

    private float[] verticies;
    private short[] indicies;

    public LevelMeshCompiler(){

    }

    public Mesh CompileMesh(float[] _v, short[] _i){
        verticies = _v;
        indicies = _i;
        SetData();
        Mesh mesh = new Mesh(false, verticies.length*7,indicies.length,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.Color, 4, "a_color"));

        mesh.setVertices( verticies, 0, verticies.length*7 );
        mesh.setIndices( indicies );
        return mesh;
    }

    public Mesh CompileTexMesh(float[] _v, short[] _i){
        verticies = _v;
        indicies = _i;
        SetData();
        Mesh mesh = new Mesh(false, verticies.length*9,indicies.length,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoords"),
                new VertexAttribute(VertexAttributes.Usage.Color, 4, "a_color"));
        mesh.setVertices( verticies, 0, verticies.length*7 );
        mesh.setIndices( indicies );
        return mesh;
    }


    public ShaderProgram MakeShader(){
        String vertexShader =
                "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                        + "attribute vec4 a_color ;   \n"
                        + "varying vec4 v_color ;   \n"
                        + "uniform mat4 u_viewProj;"
                        + "varying mat4 v_viewProj;"
                        + "uniform vec2 nodes[128];                           \n"
                        + "varying vec2 v_nodes[128];                           \n"
                        + "varying float distToCamera;                           \n"
                        //+ "uniform vec4 u_scale;"
                        //+ "uniform vec4 u_position;"
                        + "uniform mat4 u_obj_mat4; "
                        + "void main()                   \n"
                        + "{  v_color = a_color; v_viewProj = u_viewProj;                           \n"
                        + "   v_nodes = nodes;"
                        + "      vec4 mvp = ( u_obj_mat4  * a_position);"
                        + " distToCamera = -mvp.z;"
                        + "    gl_Position =   u_viewProj *   mvp      ;   \n"
                        + "}                             \n";
        String fragmentShader = "#ifdef GL_ES                \n"
                + "precision mediump float;    \n"
                + "#endif                      \n"
                + "varying vec4 v_color;                       \n"
                + "varying mat4 v_viewProj;"
                + "varying float distToCamera;                           \n"
                + "varying vec2 v_nodes[128];                           \n"
                +"vec4 fogcolor; vec3 dmod;  float occ = 1.0;"
                + "void occlude(in float x, in float y, inout float o){"
                + "vec4 cpos = v_viewProj * vec4(x,y,0.0, 1.0);"
                + "o = o * max(.6, min( distance(vec3(gl_FragCoord.x, gl_FragCoord.y, (.5+distToCamera)*10.0), vec3(x,y,5.0) )*.05, 1.0 )) ;"
                + "o = max(.6,o);"
                + "}"
                + "void main()                 \n"
                + "{                           \n"

//                + "occlude(v_nodes[0].x,v_nodes[0].y, occ);"
//                + "occlude(v_nodes[1].x,v_nodes[1].y, occ);"
//                + "occlude(v_nodes[2].x,v_nodes[2].y, occ);"
//                + "occlude(v_nodes[3].x,v_nodes[3].y, occ);"
//                + "occlude(v_nodes[4].x,v_nodes[4].y, occ);"
//                + "occlude(v_nodes[5].x,v_nodes[5].y, occ);"
//                + "occlude(v_nodes[6].x,v_nodes[6].y, occ);"
//                + "occlude(v_nodes[7].x,v_nodes[7].y, occ);"
//                + "occlude(v_nodes[8].x,v_nodes[8].y, occ);"
//                + "occlude(v_nodes[9].x,v_nodes[9].y, occ);"
//                + "occlude(v_nodes[10].x,v_nodes[10].y, occ);"



                + "  gl_FragColor = v_color    * occ ;    \n"
                + "}";
        return new ShaderProgram(vertexShader, fragmentShader);
    }

    public ShaderProgram MakeTexShader(){
        String vertexShader =
                "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                        + "attribute vec4 a_color ;   \n"
                        + "attribute vec2 a_texCoords ;   \n"
                        + "varying vec4 v_color ;   \n"
                        + "varying vec2 v_texCoords ;   \n"
                        //+ "uniform vec4 u_scale;"
                        //+ "uniform vec4 u_position;"
                        + "uniform mat4 u_obj_mat4;"
                        + "uniform mat4 u_viewProj;"
                        + "void main()                   \n"
                        + "{  v_color = a_color;                           \n"
                        + "   v_texCoords = a_texCoords;  \n"
                        + "   gl_Position =   u_viewProj *   ( u_obj_mat4  * a_position)    ;   \n"
                        + "}                             \n";
        String fragmentShader = "#ifdef GL_ES                \n"
                + "precision mediump float;    \n"
                + "#endif                      \n"
                + "varying vec4 v_color;                       \n"
                + "varying vec2 v_texCoords ;   \n"
                + "uniform sampler2D u_texture;\n"
                + "void main()                 \n"
                + "{                           \n"
                + "  gl_FragColor =  (texture2D(u_texture, v_texCoords) * (v_color  )  )  ;    \n"
                + "}";
        return new ShaderProgram(vertexShader, fragmentShader);
    }

    public ShaderProgram MakeWireShader(){
        String vertexShader =
                "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                        + "attribute vec4 a_color ;   \n"
                        + "varying vec4 v_color ;   \n"
                        + "uniform mat4 u_viewProj;"
                        + "uniform mat4 u_obj_mat4;"
                        + "void main()                   \n"
                        + "{  v_color = a_color;                           \n"

                        + "   gl_Position =   u_viewProj *   ( u_obj_mat4  * a_position ) + vec4(0.0, 0.0, -0.002, 0.0)    ;   \n"
                        + "}                             \n";
        String fragmentShader = "#ifdef GL_ES                \n"
                + "precision mediump float;    \n"

                + "#endif                      \n"

                + "varying vec4 v_color;                       \n"


                + "void main()                 \n"
                + "{                           \n"

                + "  gl_FragColor = v_color * vec4(.8,.8,.8,1.0);    \n"

                + "}";
        return new ShaderProgram(vertexShader, fragmentShader);
    }


    public void SetData(){
        //verticies = new float[]{0.242f, -0.005f, 0.000f, 0.443f, 0.341f, 0.231f, 1.0f, 0.228f, -0.034f, 0.000f, 0.357f, 0.278f, 0.188f, 1.0f, 0.242f, -0.020f, 0.000f, 0.424f, 0.325f, 0.224f, 1.0f, -0.038f, -0.023f, 0.000f, 0.392f, 0.306f, 0.208f, 1.0f, -0.036f, 0.036f, 0.000f, 0.643f, 0.533f, 0.369f, 1.0f, -0.056f, 0.003f, 0.000f, 0.463f, 0.353f, 0.243f, 1.0f, -0.053f, 0.021f, 0.000f, 0.545f, 0.431f, 0.294f, 1.0f, -0.005f, -0.030f, 0.000f, 0.345f, 0.271f, 0.184f, 1.0f, 0.200f, -0.045f, 0.000f, 0.278f, 0.227f, 0.153f, 1.0f, 0.036f, 0.065f, 0.000f, 0.690f, 0.580f, 0.396f, 1.0f, -0.005f, 0.054f, 0.000f, 0.690f, 0.580f, 0.396f, 1.0f, 0.025f, -0.027f, 0.000f, 0.298f, 0.239f, 0.161f, 1.0f, 0.069f, -0.038f, 0.000f, 0.251f, 0.208f, 0.141f, 1.0f, 0.140f, 0.066f, 0.000f, 0.690f, 0.580f, 0.396f, 1.0f, 0.085f, 0.066f, 0.000f, 0.690f, 0.580f, 0.396f, 1.0f, 0.137f, -0.046f, 0.000f, 0.239f, 0.200f, 0.137f, 1.0f, 0.214f, 0.046f, 0.000f, 0.675f, 0.565f, 0.384f, 1.0f, 0.181f, 0.060f, 0.000f, 0.690f, 0.580f, 0.396f, 1.0f, 0.234f, 0.024f, 0.000f, 0.545f, 0.431f, 0.290f, 1.0f, 0.447f, 0.061f, 0.000f, 0.294f, 0.239f, 0.161f, 1.0f, 0.499f, 0.169f, 0.001f, 0.690f, 0.580f, 0.396f, 1.0f, 0.436f, 0.097f, 0.000f, 0.435f, 0.337f, 0.227f, 1.0f, 0.447f, 0.061f, 0.000f, 0.294f, 0.239f, 0.161f, 1.0f, 0.470f, 0.032f, -0.000f, 0.263f, 0.216f, 0.149f, 1.0f, 0.499f, 0.169f, 0.001f, 0.690f, 0.580f, 0.396f, 1.0f, 0.536f, 0.162f, 0.001f, 0.690f, 0.580f, 0.396f, 1.0f, 0.501f, 0.009f, -0.000f, 0.259f, 0.212f, 0.145f, 1.0f, 0.533f, -0.024f, -0.000f, 0.243f, 0.200f, 0.137f, 1.0f, 0.536f, 0.162f, 0.001f, 0.690f, 0.580f, 0.396f, 1.0f, 0.499f, 0.169f, 0.001f, 0.690f, 0.580f, 0.396f, 1.0f, 0.501f, 0.009f, -0.000f, 0.259f, 0.212f, 0.145f, 1.0f, 0.470f, 0.032f, -0.000f, 0.263f, 0.216f, 0.149f, 1.0f, 0.501f, 0.009f, -0.000f, 0.259f, 0.212f, 0.145f, 1.0f, 0.499f, 0.169f, 0.001f, 0.690f, 0.580f, 0.396f, 1.0f, 0.594f, 0.097f, 0.000f, 0.675f, 0.565f, 0.384f, 1.0f, 0.591f, -0.037f, -0.000f, 0.239f, 0.200f, 0.137f, 1.0f, 0.647f, -0.037f, -0.000f, 0.251f, 0.208f, 0.141f, 1.0f, 0.594f, 0.097f, 0.000f, 0.675f, 0.565f, 0.384f, 1.0f, 0.536f, 0.162f, 0.001f, 0.690f, 0.580f, 0.396f, 1.0f, 0.591f, -0.037f, -0.000f, 0.239f, 0.200f, 0.137f, 1.0f, 0.533f, -0.024f, -0.000f, 0.243f, 0.200f, 0.137f, 1.0f, 0.591f, -0.037f, -0.000f, 0.239f, 0.200f, 0.137f, 1.0f, 0.536f, 0.162f, 0.001f, 0.690f, 0.580f, 0.396f, 1.0f, 0.594f, 0.097f, 0.000f, 0.675f, 0.565f, 0.384f, 1.0f, 0.647f, -0.037f, -0.000f, 0.251f, 0.208f, 0.141f, 1.0f, 0.642f, 0.062f, 0.000f, 0.635f, 0.525f, 0.361f, 1.0f, 0.701f, -0.029f, -0.000f, 0.373f, 0.290f, 0.196f, 1.0f, 0.753f, -0.015f, -0.000f, 0.478f, 0.365f, 0.251f, 1.0f, 0.689f, 0.060f, 0.000f, 0.663f, 0.553f, 0.380f, 1.0f, 0.689f, 0.060f, 0.000f, 0.663f, 0.553f, 0.380f, 1.0f, 0.642f, 0.062f, 0.000f, 0.635f, 0.525f, 0.361f, 1.0f, 0.701f, -0.029f, -0.000f, 0.373f, 0.290f, 0.196f, 1.0f, 0.647f, -0.037f, -0.000f, 0.251f, 0.208f, 0.141f, 1.0f, 0.701f, -0.029f, -0.000f, 0.373f, 0.290f, 0.196f, 1.0f, 0.642f, 0.062f, 0.000f, 0.635f, 0.525f, 0.361f, 1.0f, 0.499f, 0.169f, 0.001f, 0.690f, 0.580f, 0.396f, 1.0f, 0.470f, 0.156f, 0.001f, 0.690f, 0.580f, 0.396f, 1.0f, 0.436f, 0.097f, 0.000f, 0.435f, 0.337f, 0.227f, 1.0f, 0.689f, 0.060f, 0.000f, 0.663f, 0.553f, 0.380f, 1.0f, 0.753f, -0.015f, -0.000f, 0.478f, 0.365f, 0.251f, 1.0f, 0.743f, 0.051f, 0.000f, 0.647f, 0.537f, 0.357f, 1.0f, 0.781f, 0.019f, 0.000f, 0.478f, 0.365f, 0.251f, 1.0f, 0.753f, -0.015f, -0.000f, 0.478f, 0.365f, 0.251f, 1.0f, 0.778f, -0.000f, -0.000f, 0.478f, 0.365f, 0.251f, 1.0f, 0.743f, 0.051f, 0.000f, 0.647f, 0.537f, 0.357f, 1.0f, 0.753f, -0.015f, -0.000f, 0.478f, 0.365f, 0.251f, 1.0f, 0.781f, 0.019f, 0.000f, 0.478f, 0.365f, 0.251f, 1.0f, 0.436f, 0.097f, 0.000f, 0.435f, 0.337f, 0.227f, 1.0f, 0.470f, 0.156f, 0.001f, 0.690f, 0.580f, 0.396f, 1.0f, 0.437f, 0.129f, 0.001f, 0.533f, 0.424f, 0.282f, 1.0f, -0.544f, -0.601f, -0.002f, 0.365f, 0.294f, 0.200f, 1.0f, -0.614f, -0.668f, -0.002f, 0.349f, 0.294f, 0.200f, 1.0f, 1.298f, -0.670f, -0.002f, 0.361f, 0.294f, 0.200f, 1.0f, 1.238f, -0.600f, -0.002f, 0.353f, 0.294f, 0.200f, 1.0f, 1.252f, 0.579f, 0.002f, 0.478f, 0.365f, 0.251f, 1.0f, -0.596f, 0.648f, 0.002f, 0.478f, 0.365f, 0.251f, 1.0f, -0.528f, 0.578f, 0.002f, 0.478f, 0.365f, 0.251f, 1.0f, 1.314f, 0.646f, 0.002f, 0.478f, 0.365f, 0.251f, 1.0f, -0.342f, -0.228f, -0.001f, 0.686f, 0.576f, 0.392f, 1.0f, -0.362f, -0.148f, -0.001f, 0.643f, 0.533f, 0.365f, 1.0f, -0.469f, -0.222f, -0.001f, 0.478f, 0.365f, 0.251f, 1.0f, 1.114f, -0.317f, -0.001f, 0.412f, 0.329f, 0.224f, 1.0f, 0.974f, -0.157f, -0.001f, 0.682f, 0.573f, 0.392f, 1.0f, 0.958f, -0.233f, -0.001f, 0.682f, 0.569f, 0.392f, 1.0f, -0.409f, -0.406f, -0.001f, 0.420f, 0.329f, 0.227f, 1.0f, -0.294f, -0.313f, -0.001f, 0.647f, 0.537f, 0.369f, 1.0f, 0.932f, -0.295f, -0.001f, 0.678f, 0.569f, 0.388f, 1.0f, -0.249f, -0.368f, -0.001f, 0.584f, 0.475f, 0.325f, 1.0f, 1.003f, -0.490f, -0.002f, 0.365f, 0.298f, 0.204f, 1.0f, 0.883f, -0.362f, -0.001f, 0.675f, 0.565f, 0.388f, 1.0f, 0.823f, -0.410f, -0.001f, 0.647f, 0.537f, 0.369f, 1.0f, -0.103f, -0.446f, -0.002f, 0.682f, 0.573f, 0.392f, 1.0f, -0.187f, -0.415f, -0.001f, 0.561f, 0.451f, 0.310f, 1.0f, -0.214f, -0.517f, -0.002f, 0.357f, 0.294f, 0.200f, 1.0f, 0.046f, -0.457f, -0.002f, 0.671f, 0.561f, 0.388f, 1.0f, 0.186f, -0.461f, -0.002f, 0.651f, 0.541f, 0.373f, 1.0f, 1.118f, -0.080f, -0.000f, 0.478f, 0.365f, 0.251f, 1.0f, 0.976f, -0.082f, -0.000f, 0.639f, 0.525f, 0.361f, 1.0f, -0.469f, -0.082f, -0.000f, 0.478f, 0.365f, 0.251f, 1.0f, -0.367f, -0.086f, -0.000f, 0.584f, 0.475f, 0.325f, 1.0f, 0.743f, -0.444f, -0.002f, 0.678f, 0.569f, 0.388f, 1.0f, 0.668f, -0.457f, -0.002f, 0.678f, 0.569f, 0.388f, 1.0f, 0.743f, -0.526f, -0.002f, 0.365f, 0.302f, 0.204f, 1.0f};

        //indicies = new short[]{0, 1, 2, 3, 4, 5, 5, 4, 6, 7, 4, 3, 8, 1, 0, 9, 10, 11, 7, 11, 10, 10, 4, 7, 12, 9, 11, 13, 14, 15, 12, 15, 14, 12, 14, 9, 16, 17, 8, 15, 8, 17, 15, 17, 13, 18, 16, 8, 18, 8, 0, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 70, 72, 73, 74, 75, 76, 74, 77, 75, 70, 76, 71, 71, 76, 75, 73, 72, 74, 72, 77, 74, 78, 79, 80, 81, 82, 83, 78, 80, 84, 85, 78, 84, 81, 83, 86, 87, 85, 84, 88, 89, 90, 91, 92, 93, 94, 91, 93, 95, 94, 93, 96, 97, 82, 96, 82, 81, 98, 80, 79, 98, 79, 99, 86, 89, 88, 86, 88, 81, 87, 84, 93, 87, 93, 92, 100, 101, 102, 95, 93, 102, 95, 102, 101, 90, 100, 102, 90, 102, 88};

    }

}
