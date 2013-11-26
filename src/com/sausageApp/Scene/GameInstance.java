package com.sausageApp.Scene;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.sausageApp.Scene.LevelMeshCompiler;
import com.sausageApp.Scene.VertexObject;
import com.sausageApp.Simulation.Moveable;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/27/13
 * Time: 5:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class GameInstance implements Moveable {
    public String name = null;
    public ArrayList<String> children = null;
    public String parent = null;



    public Matrix4 local_mat4 = null;
    public String group;


    public Quaternion rotation = new Quaternion();
    public Vector3 position = new Vector3(0f,0f,0f);
    public Vector3 scale = new Vector3(0f,0f,0f);

    public Matrix4 smat = null;
    public Matrix4 rmat = null;
    public Matrix4 tmat = null;

    public GameInstance(InstanceData data){
        name = data.name;
        children = data.children;
        group = data.group;
        position = new Vector3(data.position);
        scale = new Vector3(data.scale[0], data.scale[1], data.scale[2] );
        rotation = new Quaternion( data.quaternion[1], data.quaternion[2], (float) (data.quaternion[3]), (float) (data.quaternion[0]));
        UpdateMatrix();
    }

    public GameInstance(String _name, String _group, Vector3 _position, Vector3 _scale, Quaternion _rotation){
        name = _name;
        group = _group;
        position = new Vector3(_position);
        scale = new Vector3(_scale );
        rotation = new Quaternion( _rotation );
        UpdateMatrix();
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
