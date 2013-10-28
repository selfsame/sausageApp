package com.sausageApp.Simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.sausageApp.Players.Link;
import com.sausageApp.Players.Player;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/26/13
 * Time: 11:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class SensorObject implements Contactable{
    public Scenario scenario;
    public Body body;
    public String tag;
    public String usage;
    public float fmod;
    public int imod;
    public float[] loc;
    public float[] verts = new float[]{};
    public HashMap<Player,Integer> player_contacts = new HashMap<Player, Integer>();

    public boolean active = false;

    public SensorObject(Scenario scen, SensorData data){
        scenario = scen;
        tag = data.tag;
        usage = data.usage;
        fmod = data.fmod;
        imod = data.imod;
        verts = data.verts;
        loc = data.loc;

        Vec2[] pbies = new Vec2[(int)verts.length/2];
        for (int j = 0; j < (int)verts.length/2; j++) {
            pbies[j] = scenario.S2B(scenario.gl2S(new Vec2(verts[j * 2], verts[j * 2 + 1])));
        }

        body = scenario.createStaticChain( pbies, true, 1);
        body.setUserData(this);
    }


    public void beginContact(Vec2 n, Contactable thing){
        if (thing instanceof Link){
            Link link = (Link)thing;
            Player p = link.sausage.player;
            if (!player_contacts.containsKey(p) || player_contacts.get(p) <= 0){
                player_contacts.put(p, 1);
                Gdx.app.log("BEGIN", "PLAYER CONTACT");
                if (usage.equals("PORTAL")){
                    active = true;
                }
                trigger(p);
            } else {
                player_contacts.put(p,player_contacts.get(p)+1);
            }
        }
    }

    public void endContact(Vec2 n, Contactable thing){
        if (thing instanceof Link){
            Link link = (Link)thing;
            Player p = link.sausage.player;
            player_contacts.containsKey(p);

            if ( player_contacts.containsKey(p) ){
                int count = player_contacts.get(p);
                player_contacts.put(p, count - 1);
                if (count-1 <= 0){
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
            scenario.view_dist = fmod;
        }
        if (usage.equals("CHANGEZ")){
            player.sausage.z_depth = fmod;
        }
        if (usage.equals("GRAVITY")){

            player.scenario.world.setGravity( new Vec2(0f,fmod) );
        }
    }

    public void activate(){
        if (usage.equals("PORTAL")){
            scenario.game_screen.loadScenario("scenarios/"+tag);
        }


    }

}
