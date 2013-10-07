package com.sausageApp.screens;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/6/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class MeshSegment {

    public MeshSegment(int node_index, ArrayList<Float> verticies){
        //left, middle, and right pairs
        //each pair has a top and bottom vertex
        // each vertex has x, y, node_index, segment_pair_index, curve_coord_v, curve_coord_w
        float[] x_spread = new float[]{-1f,0f,1f};
        float[] y_spread = new float[]{-1f,1f};
        float[] cp1 = new float[]{0f,.5f,1f};
        float[] cp2 = new float[]{0f,0f,1f};

        for (int i=0;i<3;i++){
            for (int j=0;j<2;j++){
              verticies.add( .025f*x_spread[i] );
              verticies.add( .05f*y_spread[j] );
              verticies.add( (float)node_index );
              verticies.add( .5f*i ); // this is the pair_index, which is a weight for interpolation between prev and next.
              verticies.add( cp1[i] );
              verticies.add( cp2[i] );
            }
        }


    }
}
