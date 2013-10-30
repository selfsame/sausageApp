package com.sausageApp.Scene;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.sausageApp.Scene.LevelMeshCompiler;
import com.sausageApp.Scene.VertexObject;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/27/13
 * Time: 5:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class GameObject {
    public String name = null;
    public boolean texture;

    public Matrix4 local_mat4 = null;
    public float[] static_vertices = null;
    public short[] static_indicies = null;
    public boolean alpha;
    public Mesh mesh = null;



    public Quaternion rotation = new Quaternion();
    public Vector3 position = new Vector3(0f,0f,0f);
    public Vector3 scale = new Vector3(0f,0f,0f);

    private Matrix4 smat = null;
    private Matrix4 rmat = null;
    private Matrix4 tmat = null;

    public GameObject(VertexObject data, LevelMeshCompiler meshCompiler){
        name = data.name;


        position = new Vector3(data.position);
        scale = new Vector3(data.scale[0], data.scale[1], data.scale[2] );
        rotation = new Quaternion( data.quaternion[1], data.quaternion[2], (float) (data.quaternion[3]), (float) (data.quaternion[0]));



        UpdateMatrix();


        alpha = data.alpha;
        texture = data.texture;
        static_vertices = data.static_vertices.clone();
        static_indicies = data.static_indicies.clone();
        if (texture){
            mesh = meshCompiler.CompileTexMesh(static_vertices, static_indicies);
        } else {
            mesh = meshCompiler.CompileMesh(static_vertices, static_indicies);
        }
    }

    public void UpdateMatrix(){
        smat = new Matrix4().scl(scale);
        rmat = new Matrix4().rotate(rotation);
        tmat = new Matrix4().translate(position);
        local_mat4 = tmat.mul(rmat).mul(smat);
    }


    public Quaternion getRotation(){
        return rotation;
    }
    public void setRotation(Quaternion q){
        //rotation = rotation.setEulerAngles(0f,0f, q.z);
        UpdateMatrix();
    }

    public Vector3 getPosition(){
        return position;
    }
    public void setPosition(Vector3 p){
        position = p;
        UpdateMatrix();
    }

    public void setScale(Vector3 p){
        scale = p;
        UpdateMatrix();
    }


}