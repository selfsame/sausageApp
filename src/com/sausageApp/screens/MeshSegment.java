package com.sausageApp.screens;

import com.sausageApp.Players.Player;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/6/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class MeshSegment {
    private Player player;
    public MeshSegment(int node_index, ArrayList<Float> verticies, ArrayList<Short> indicies, Player _player){
        player = _player;
        //left, middle, and right pairs
        //each pair has a top and bottom vertex
        // each vertex has x, y, node_index, curve_coord_v, curve_coord_w, segment_pair_index
        float[] x_spread = new float[]{0f,1.0f,.0f};
        float[] y_spread = new float[]{1f,-1f};
        float[] cp1 = new float[]{0f,.5f,1f};
        float[] cp2 = new float[]{0f,0f,1f};
        float[] interpolation_left = new float[]{.5f,0.0f,0f};
        float[] interpolation_right = new float[]{0f,0.0f,.5f};

        int v_count = node_index*10; // the first node only has 5 vertices

        for (int i=0;i<3;i++){
            for (int j=0;j<2;j++){
              verticies.add( x_spread[i] ); //this is actually used to determine if middle;
              verticies.add( y_spread[j] );
              verticies.add( (float)node_index );

              verticies.add( cp1[i] );
              verticies.add( cp2[i] );
              verticies.add( interpolation_left[i] ); // this is the pair_index, which is a weight for interpolation between prev and next.
              verticies.add( interpolation_right[i] );

                if (node_index % 2 == 23 ){
                    verticies.add( .5f );
                    verticies.add( .8f );
                    verticies.add( .8f );
                    verticies.add( 1.0f );
                } else {
                    verticies.add( player.color.r );
                    verticies.add( player.color.g );
                    verticies.add( player.color.b );
                    verticies.add( 1.0f );
                }

            }
        }

         x_spread = new float[]{0f,.0f};
         interpolation_left = new float[]{.5f,0f};
         interpolation_right = new float[]{0f,.5f};
        for (int i=0;i<2;i++){
            for (int j=0;j<2;j++){
                verticies.add( x_spread[i] ); //this is actually used to determine if middle;
                verticies.add( y_spread[j] );
                verticies.add( (float)node_index );

                verticies.add( 0f );
                verticies.add( 0f );
                verticies.add( interpolation_left[i] ); // this is the pair_index, which is a weight for interpolation between prev and next.
                verticies.add( interpolation_right[i] );

                if  (false){ //(node_index == 1 || node_index == 18-2 ){
                    verticies.add( .9f );
                    verticies.add( .2f );
                    verticies.add( .2f );
                    verticies.add( 1.0f );
                } else {
                    verticies.add( player.color.r );
                    verticies.add( player.color.g );
                    verticies.add( player.color.b );
                    verticies.add( 1.0f );
                }
            }
        }



        indicies.add( (short)v_count );
        indicies.add( (short)(v_count + 2) );
        indicies.add( (short)(v_count + 4) );

        indicies.add( (short)(v_count + 1) );
        indicies.add( (short)(v_count + 3) );
        indicies.add( (short)(v_count + 5) );


        indicies.add( (short)(v_count + 6) );
        indicies.add( (short)(v_count + 7) );
        indicies.add( (short)(v_count + 8) );

        indicies.add( (short)(v_count + 7) );
        indicies.add( (short)(v_count + 8) );
        indicies.add( (short)(v_count + 9) );














//        indicies.add( (short)(v_count ) );
//        indicies.add( (short)(v_count + 1) );
//        indicies.add( (short)(v_count + 2) );
//
//        indicies.add( (short)(v_count + 1) );
//        indicies.add( (short)(v_count + 2) );
//        indicies.add( (short)(v_count + 3) );
//
//        indicies.add( (short)(v_count + 2) );
//        indicies.add( (short)(v_count + 3) );
//        indicies.add( (short)(v_count + 4) );
//
//        indicies.add( (short)(v_count + 3) );
//        indicies.add( (short)(v_count + 4) );
//        indicies.add( (short)(v_count + 5) );








    }


    public MeshSegment(int node_index, ArrayList<Float> verticies, ArrayList<Short> indicies, boolean first){
        //left, middle, and right pairs
        //each pair has a top and bottom vertex
        // each vertex has x, y, node_index, curve_coord_v, curve_coord_w, segment_pair_index
        float[] x_spread = new float[]{0f,1.0f,.0f};
        float[] y_spread = new float[]{1f,-1f};
        float[] cp1 = new float[]{0f,.5f,1f};
        float[] cp2 = new float[]{0f,0f,1f};
        float[] interpolation_left = new float[]{.5f,0.0f,0f};
        float[] interpolation_right = new float[]{0f,0.0f,.5f};

        int v_count = 0; // was 6

        for (int i=0;i<3;i++){
            for (int j=0;j<2;j++){
                if (i==0){
                    if (j==0){
                        verticies.add( x_spread[i] ); //this is actually used to determine if middle;
                        verticies.add( .5f );
                        verticies.add( (float)node_index );

                        verticies.add( .5f );
                        verticies.add( 0f );
                        verticies.add( 2.0f); // this is the pair_index, which is a weight for interpolation between prev and next.
                        verticies.add( 0f );

                        verticies.add( .2f );
                        verticies.add( .8f );
                        verticies.add( .2f );
                        verticies.add( 1.0f );
                    }

                }  else {
                    verticies.add( x_spread[i] ); //this is actually used to determine if middle;
                    verticies.add( y_spread[j] );
                    verticies.add( (float)node_index );

                    verticies.add( cp1[i] );
                    verticies.add( cp2[i] );
                    verticies.add( interpolation_left[i] ); // this is the pair_index, which is a weight for interpolation between prev and next.
                    verticies.add( interpolation_right[i] );
                }
            }
        }



        indicies.add( (short)(v_count + 1) );
        indicies.add( (short)(v_count + 0) );
        indicies.add( (short)(v_count + 2) );

        indicies.add( (short)(v_count + 2) );
        indicies.add( (short)(v_count + 3) );
        indicies.add( (short)(v_count + 4) );

        indicies.add( (short)(v_count + 2) );
        indicies.add( (short)(v_count + 5) );
        indicies.add( (short)(v_count + 4) );







    }



}
