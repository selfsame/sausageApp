package com.sausageApp.Scene;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.sausageApp.Game.State;
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
public class GameObject implements Moveable {
    public State state = State.getInstance();
    public String name = null;
    public ArrayList<String> children = null;
    public String parent = null;
    public boolean texture;
    public boolean wire;

    public Matrix4 local_mat4 = null;
    public float[] static_vertices = null;
    public short[] static_indicies = null;
    public float[] wire_vertices = null;
    public short[] wire_indicies = null;
    public boolean alpha;
    public boolean template;
    public Mesh mesh = null;
    public Mesh wire_mesh = null;



    public Quaternion rotation = new Quaternion();
    public Vector3 position = new Vector3(0f,0f,0f);
    public Vector3 scale = new Vector3(0f,0f,0f);

    public Matrix4 smat = null;
    public Matrix4 rmat = null;
    public Matrix4 tmat = null;

    public GameObject(VertexObject data, LevelMeshCompiler meshCompiler){
        name = data.name;
        children = data.children;
        wire = data.wire;
        template = data.template;
        position = new Vector3(data.position);
        scale = new Vector3(data.scale[0], data.scale[1], data.scale[2] );
        rotation = new Quaternion( data.quaternion[1], data.quaternion[2], (float) (data.quaternion[3]), (float) (data.quaternion[0]));



        UpdateMatrix();


        alpha = data.alpha;
        texture = data.texture;
        static_vertices = new float[data.static_vertices.length];
        System.arraycopy(data.static_vertices,0,static_vertices,0,data.static_vertices.length);
        static_indicies = new short[data.static_indicies.length];
        System.arraycopy(data.static_indicies,0,static_indicies,0,data.static_indicies.length);
        if (wire == true) {
            wire_vertices = new float[data.wire_vertices.length];
            System.arraycopy(data.wire_vertices,0,wire_vertices,0,data.wire_vertices.length);
            wire_indicies = new short[data.wire_indicies.length];
            System.arraycopy(data.wire_indicies,0,wire_indicies,0,data.wire_indicies.length);
            wire_mesh = meshCompiler.CompileMesh(data.wire_vertices, data.wire_indicies);
        }

        if (texture){
            mesh = meshCompiler.CompileTexMesh(data.static_vertices, data.static_indicies);
        } else {
            mesh = meshCompiler.CompileMesh(data.static_vertices, data.static_indicies);
        }
    }

    public void UpdateMatrix(){
        smat = new Matrix4().scl(scale);
        rmat = new Matrix4().rotate(rotation);
        tmat = new Matrix4().translate(position);
        //tmat = new Matrix4().translate(position.cpy().add( getParentPosition()));
        local_mat4 = tmat.cpy().mul(rmat.cpy()).mul(smat.cpy());
        if (parent != null){

            GameObject pobj = state.scene.object_map.get(parent);
            Matrix4 p_mat4 = pobj.local_mat4.cpy();
            local_mat4 = tmat.cpy().mul(rmat.cpy()).mul(smat.cpy());


            //local_mat4 = p_mat4.mul(tmat).mul(smat);
            local_mat4 = p_mat4.mul(local_mat4);

        }

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
        updateChildPosition();

    }

    public void setScale(Vector3 p){
        scale = p;
        UpdateMatrix();
    }

    public void updateChildPosition(){
        for (String cname: children){
            if (state.scene.object_map.containsKey(cname)){
                GameObject child = state.scene.object_map.get(cname);
                child.UpdateMatrix();
                child.updateChildPosition();
            }
        }
    }

    public Vector3 getParentPosition(){
        if (parent != null){
            if (state.scene.object_map.containsKey(parent)){
                return state.scene.object_map.get(parent).position.cpy().add(state.scene.object_map.get(parent).getParentPosition());
            }
        }
        return new Vector3(0f,0f,0f);
    }

    public Vector3 getParentScale(){
        if (parent != null){
            if (state.scene.object_map.containsKey(parent)){
                Vector3 ps = state.scene.object_map.get(parent).getParentScale();
                return state.scene.object_map.get(parent).scale.cpy().scl(ps);   //new Vector3(ps.x,ps.y,ps.z)
            }
        }
        return scale.cpy();//return new Vector3(1f,1f,1f);
    }

    public Quaternion getParentRotation(){
        if (parent != null){
            if (state.scene.object_map.containsKey(parent)){
                Quaternion pr = state.scene.object_map.get(parent).getParentRotation();
                Quaternion r =  rotation.cpy();

                return pr.mul(state.scene.object_map.get(parent).rotation.cpy());
                //return state.scene.object_map.get(parent).rotation.cpy().mul(pr);
            }
        }
        // bad hack beacause the model space rotation is not using the coordinate system of the game
        return rotation.cpy();//.mul(new Quaternion().setEulerAngles(0f,0f,0f));

    }

}
