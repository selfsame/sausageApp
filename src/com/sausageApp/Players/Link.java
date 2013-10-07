package com.sausageApp.Players;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/5/13
 * Time: 12:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class Link {

    public Body body;

    public Link next = null;
    public Link prev = null;
    public Sausage sausage;

    public Link(Body _body, Sausage _sausage){
        body = _body;
        sausage = _sausage;
    }

    public Boolean HasNext(){
        if (next != null) return true;
        return false;
    }

    public Boolean HasPrev(){
        if (prev != null) return true;
        return false;
    }

    public Vec2 VBetween(Body b1,Body b2){
        Vec2 v1 = b1.getPosition();
        Vec2 v2 = b2.getPosition();
        return v1.sub(v2);
    }

    public void DebugLinearImpulse(Vec2 v){
        Vec2 b = sausage.player.scenario.SFlip(sausage.player.scenario.B2S(  body.getWorldCenter() ));

        sausage.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        sausage.shapeRenderer.setColor(0f, 1f, 0f, 1f);
        sausage.shapeRenderer.line(b.x, b.y, b.x + v.x, b.y - v.y);
        sausage.shapeRenderer.end();
    }

    public void applyLinearImpulse(Vec2 v){
        body.applyLinearImpulse(v, body.getWorldCenter());
    }

    public void applyLinearImpulse(Vec2 v, int propigate, boolean direction, boolean align, float degredation){
        body.applyLinearImpulse(v, body.getWorldCenter());
        //Gdx.app.log("?", "v:"+this+"("+v+") p:"+propigate+", d:"+direction+", a:"+align+", deg:"+degredation);
        if (sausage.player.debug_draw_sausage_force){
            DebugLinearImpulse(v);
        }
        // make sure we have propigation to carry out.
        if (propigate <= 0) return;

        Link target = null;

        if (direction == false){   //forwards
            if (HasNext() == false) return;
            target = next;
        }
        if (direction == true){  //backwards
            if (HasPrev() == false) return;
            target = prev;
        }

        if (align){
           Vec2 av = VBetween(body, target.body);
           v = av.mul((float)(v.length() / av.length()));

        }

        target.applyLinearImpulse(v.mul(degredation), propigate - 1, direction, align, degredation);

    }
}
