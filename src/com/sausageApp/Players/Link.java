package com.sausageApp.Players;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.sausageApp.Simulation.AnonContact;
import com.sausageApp.Simulation.Contactable;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/5/13
 * Time: 12:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class Link implements Contactable {

    public Body body;

    public Link next = null;
    public Link prev = null;
    public Sausage sausage;
    public boolean reversing = false;
    public float potential = 1f;
    public float curvature = 0f;
    public float curve_potential = 0f;

    boolean has_contact;

    public Link(Body _body, Sausage _sausage){
        body = _body;
        sausage = _sausage;
    }

    public void Update(){
        if (has_contact == false && potential > .4f){
            potential *= .98f;
            reversing = false;
        }
        if (HasNext() && HasPrev()){
            curvature = (float)Math.abs(Math.PI - (float)Math.abs(PrevAngle() - NextAngle()));
            curvature = curvature / (float)Math.PI;
        }
    }

    public Boolean HasNext(){
        if (next != null) return true;
        return false;
    }

    public Boolean HasPrev(){
        if (prev != null) return true;
        return false;
    }

    public float NextAngle(){
        if (HasNext()){
            Vec2 heading = body.getPosition().sub(next.body.getPosition());
            return (float)Math.atan2(heading.y, heading.x);
        } else {
            return 0f;
        }
    }

    public float PrevAngle(){
        if (HasPrev()){
            Vec2 heading = body.getPosition().sub( prev.body.getPosition() );
            return (float)Math.atan2(heading.y, heading.x);
        } else {
            return 0f;
        }
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
        float v_rot = (float)Math.atan2(v.y, v.x);
        Link target = null;
        if (direction == false){   //forwards
            if (HasNext() == false) return;
            target = next;
            float rot = NextAngle();
            if (v.length() > .01f && Math.abs(v_rot - rot) >= Math.PI/2.2f){
                align = false;
                reversing = true;
            }

        }
        if (direction == true){  //backwards
            if (HasPrev() == false) return;
            target = prev;
            float rot = PrevAngle();
            if (v.length() > .1f && Math.abs(v_rot - rot) >= Math.PI/2.2f){
                align = false;
                reversing = true;
            }
        }



        Vector2 curve_mod = new Vector2(v.x*.8f*curve_potential , v.y*.8f*curve_potential  );

        Vector2 mod = new Vector2( potential*(.5f*v.x),  potential*(.5f*v.y));
        Vector2 result = mod.add(curve_mod);
        body.applyLinearImpulse( new Vec2(v.x, v.y), body.getWorldCenter());



        //Gdx.app.log("?", "v:"+this+"("+v+") p:"+propigate+", d:"+direction+", a:"+align+", deg:"+degredation);
        if (sausage.player.debug_draw_sausage_force){
            DebugLinearImpulse(v);
        }
        // make sure we have propigation to carry out.
        if (propigate <= 0) return;








        if (align){
           Vec2 av = VBetween(body, target.body);
           av = av.mul((float)(v.length() / av.length()));
           v = v.add(av).mul(.5f);

        }

        target.applyLinearImpulse(v.mul(degredation), propigate - 1, direction, align, degredation);

    }





    public void beginContact(Vec2 n, Contactable thing){
       if (thing instanceof Link){
           Link link = (Link)thing;
           if (sausage == link.sausage) if (next == link || prev == link || link == this) return;
           potential = 1f;
           has_contact = true;
       }
        if (thing instanceof AnonContact) {
            potential = 1f;

        }
    }
    public void endContact(Vec2 n, Contactable thing){
        if (thing instanceof Link ){
            Link link = (Link)thing;
            if (sausage == link.sausage) if (next == link || prev == link || link == this) return;
            has_contact = false;
        }
        if (thing instanceof AnonContact) has_contact = false;
    }


}
