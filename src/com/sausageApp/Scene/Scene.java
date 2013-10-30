package com.sausageApp.Scene;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.sausageApp.Game.State;
import com.sausageApp.Game.Units;
import com.sausageApp.TweenAccessors.GameObjectAccessor;
import com.sausageApp.TweenAccessors.MoveableAccessor;


import java.util.ArrayList;
import java.util.HashMap;

/**
Uses json to set up the scene and render it.
 */
public class Scene {

    public State state = State.getInstance();
    private Units units = new Units();

    public final TweenManager tweenManager = new TweenManager();

    private SceneData scene_data;
    private Json json = new Json();

    private ShaderProgram level_shader;

    private Texture tex;
    private ShaderProgram tex_shader;
    private ArrayList<GameObject> texture_meshes = new ArrayList<GameObject>();
    private ArrayList<GameObject> texture_meshes_alpha = new ArrayList<GameObject>();

    private ShaderProgram wire_shader;
    private Mesh wire_mesh;

    private ArrayList<GameObject> vertex_meshes = new ArrayList<GameObject>();
    private ArrayList<GameObject> vertex_meshes_alpha = new ArrayList<GameObject>();





    public HashMap<String, GameObject> object_map = new HashMap<String, GameObject>();
    public ArrayList<DynamicObject> dynamics = new ArrayList<DynamicObject>();
    public ArrayList<SensorObject> sensors = new ArrayList<SensorObject>();
    public ArrayList<Locus> spawn_points = new ArrayList<Locus>();

    public Vector2 gravity;
    public GameCamera camera;
    public float default_view_dist = 3f;
    public float view_dist = 3f;
    //public Scenario scenario;

    public Scene(String filename){
        Tween.registerAccessor(GameCamera.class, new MoveableAccessor());
        Tween.registerAccessor(GameObject.class, new GameObjectAccessor());
        scene_data = json.fromJson(SceneData.class, Gdx.files.internal( filename ));
        gravity = new Vector2(.0f, scene_data.gravity);
        state.newBox(gravity);
        tex = new Texture(Gdx.files.internal("house_tex.png"));
        camera = new GameCamera(46.596f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 0f, 40f);


        LevelMeshCompiler level_geo = new LevelMeshCompiler();

        for (VertexObject obj: scene_data.vertex_objects) {
            GameObject game_obj = new GameObject(obj, level_geo);
            object_map.put(game_obj.name, game_obj);
            if (game_obj.alpha == false){
                vertex_meshes.add(game_obj);
            } else {
                vertex_meshes_alpha.add(game_obj);
            }
        }

        level_shader = level_geo.MakeShader();


        for (VertexObject obj: scene_data.texture_objects) {
            GameObject game_obj = new GameObject(obj, level_geo);
            object_map.put(game_obj.name, game_obj);
            if (game_obj.alpha == false){
                texture_meshes.add(game_obj);
            } else {
                texture_meshes_alpha.add(game_obj);
            }
        }

        tex_shader = level_geo.MakeTexShader();

        wire_mesh = level_geo.CompileMesh(scene_data.wire_vertices.clone(), scene_data.wire_indicies.clone());
        wire_shader = level_geo.MakeWireShader();

        for (Locus locus: scene_data.locii) {
            if (locus.usage.equals("PLAYER_SPAWN")){
                spawn_points.add(locus);
            }

        }

        // WARNING need to make these Body containing objects safe

        for (Collider group: scene_data.collide_groups) {
            float[] verts = group.verts.clone();
            Vector2[] pbies = new Vector2[(int)verts.length/2];
            for (int j = 0; j < (int)verts.length/2; j++) {
                pbies[j] = units.S2B(units.unAspect(units.gl2S(new Vector2(verts[j * 2], verts[j * 2 + 1]))));
            }
            state.box.createStaticChain(pbies, false, group.mask);
        }

        for (SensorData group: scene_data.sensor_groups) {
            sensors.add(new SensorObject(group));
        }

        for (DynamicData data: scene_data.dynamic_objects) {
            DynamicObject d = new DynamicObject(this, data);
            dynamics.add(d);
        }

        Tween.to(object_map.get("Lamp"), GameObjectAccessor.POSITION_Y, .3f).targetRelative(.1f).repeatYoyo(100, 0f).start(tweenManager);
        Tween.to(object_map.get("Lamp"), GameObjectAccessor.SCALE_XYZ, .1f).delay(.3f).target(1f,1f,.5f).repeatYoyo(100, .2f).start(tweenManager);
        Tween.to(object_map.get("Cloud"), GameObjectAccessor.POSITION_X, 40f).targetRelative(-50f).repeatYoyo(10, .2f).start(tweenManager);
        Tween.to(object_map.get("Cloud.001"), GameObjectAccessor.POSITION_X, 40f).targetRelative(-50f).repeatYoyo(10, .2f).start(tweenManager);
        Tween.to(object_map.get("Cloud.002"), GameObjectAccessor.POSITION_X, 50f).targetRelative(-30f).repeatYoyo(10, .2f).start(tweenManager);
        Tween.to(object_map.get("Cloud.003"), GameObjectAccessor.POSITION_X, 30f).targetRelative(-10f).repeatYoyo(10, .2f).start(tweenManager);
        Tween.to(object_map.get("Cloud.004"), GameObjectAccessor.POSITION_X, 30f).targetRelative(10f).repeatYoyo(10, .2f).start(tweenManager);
    }

    public void render(float delta){
        tweenManager.update(Gdx.graphics.getDeltaTime());

        float[] b = scene_data.background;
        Gdx.gl.glClearColor(b[0], b[1], b[2], 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        camera.update();


        Gdx.gl.glDisable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        tex_shader.begin();
        Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        tex.bind();
        Gdx.gl.glDepthRangef(0f, 1f);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST) ;
        tex_shader.setUniformMatrix("u_viewProj", camera.combined);
        for (GameObject obj: texture_meshes) {
            tex_shader.setUniformMatrix("u_obj_mat4", obj.local_mat4);
            obj.mesh.render(tex_shader, GL20.GL_TRIANGLES);
        }
        tex_shader.end();




        Gdx.gl.glDisable(GL20.GL_BLEND);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST) ;

        level_shader.begin();
        Gdx.gl.glDepthRangef(0f, 1f);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);
        level_shader.setUniformMatrix("u_viewProj", camera.combined);

        for (GameObject obj: vertex_meshes) {
            level_shader.setUniformMatrix("u_obj_mat4", obj.local_mat4);
            obj.mesh.render(level_shader, GL20.GL_TRIANGLES);
        }
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        for (GameObject obj: vertex_meshes_alpha) {
            level_shader.setUniformMatrix("u_obj_mat4", obj.local_mat4);
            obj.mesh.render(level_shader, GL20.GL_TRIANGLES);
        }
        level_shader.end();

        tex_shader.begin();
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        for (GameObject obj: texture_meshes_alpha) {
            tex_shader.setUniformMatrix("u_obj_mat4", obj.local_mat4);
            obj.mesh.render(tex_shader, GL20.GL_TRIANGLES);
        }
        tex_shader.end();


        wire_shader.begin();
        Gdx.gl20.glLineWidth(1f);
        Gdx.gl.glPolygonOffset(1f, 2f);
        Gdx.gl.glDepthRangef(0f, 100f);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST) ;
        Gdx.gl20.glDisable(GL20.GL_BLEND);
        wire_shader.setUniformMatrix("u_viewProj", camera.combined);
        wire_mesh.render(wire_shader, GL20.GL_LINES);
        Gdx.gl20.glDisable(GL20.GL_BLEND);
        wire_shader.end();







        state.ui.render(delta);


    }



}