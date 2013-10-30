package com.sausageApp.Scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.sausageApp.Game.State;
import com.sausageApp.Game.Units;
import com.sausageApp.Players.Link;
import com.sausageApp.Players.Player;
import com.sausageApp.Simulation.Contactable;


import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/26/13
 * Time: 11:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class SensorObject implements Contactable {
    public State state = State.getInstance();
    private Units units = new Units();

    public Body body;
    public String tag;
    public String usage;
    public float fmod;
    public int imod;
    public String istring;
    public boolean enter;
    public boolean exit;
    public float[] loc;
    public float[] verts = new float[]{};
    public HashMap<Player,Integer> player_contacts = new HashMap<Player, Integer>();

    public boolean active = false;

    public SensorObject(SensorData data){
        tag = data.tag;
        usage = data.usage;
        fmod = data.fmod;
        imod = data.imod;
        istring = data.istring;
        enter = data.enter;
        exit = data.exit;
        verts = data.verts;
        loc = data.loc;

        Vector2[] pbies = new Vector2[(int)verts.length/2];
        for (int j = 0; j < (int)verts.length/2; j++) {
            pbies[j] = units.unAspect(units.S2B(units.gl2S(new Vector2(verts[j * 2], verts[j * 2 + 1]))));
        }

        body = state.box.createStaticChain( pbies, true, 1);
        body.setUserData(this);
    }


    public void beginContact(Vector2 n, Contactable thing){
        if (thing instanceof Link){
            Link link = (Link)thing;
            Player p = link.sausage.player;
            if (!player_contacts.containsKey(p) || player_contacts.get(p) <= 0){
                player_contacts.put(p, 1);

                active = true;

                if (enter) state.scenario.enterSensor(this,p);
            } else {
                player_contacts.put(p,player_contacts.get(p)+1);
            }
        }
    }

    public void endContact(Vector2 n, Contactable thing){
        if (thing instanceof Link){
            Link link = (Link)thing;
            Player p = link.sausage.player;
            player_contacts.containsKey(p);

            if ( player_contacts.containsKey(p) ){
                int count = player_contacts.get(p);
                player_contacts.put(p, count - 1);
                if (count-1 <= 0){
                    if (exit) state.scenario.exitSensor(this,p);
                    //Gdx.app.log("END", "PLAYER CONTACT");
                    active = false;
                }

            }
        }
    }

    public void trigger(Player player){
        if (usage.equals("MASK")){
            Gdx.app.log("TRIGGER", "MASK CHANGE:"+imod);
            player.sausage.setCategory(imod);
            player.sausage.setMask(imod);
            //player.sausage.setGroupIndex(imod);
        }
        if (usage.equals("ZOOM")){
            state.scene.view_dist = fmod;
        }
        if (usage.equals("CHANGEZ")){
            player.sausage.z_depth = fmod;
        }
        if (usage.equals("GRAVITY")){

            state.box.world.setGravity( new Vector2(0f,fmod) );
        }
    }

    public void activate(){



    }

}
