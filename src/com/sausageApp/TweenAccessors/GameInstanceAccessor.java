package com.sausageApp.TweenAccessors;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.math.Vector3;
import com.sausageApp.Scene.GameInstance;


/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/27/13
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class GameInstanceAccessor implements TweenAccessor<GameInstance> {

    public static final int POSITION_X = 1;
    public static final int POSITION_Y = 2;
    public static final int POSITION_Z = 3;
    public static final int POSITION_XYZ = 4;

    public static final int SCALE_X = 11;
    public static final int SCALE_Y = 12;
    public static final int SCALE_Z = 13;
    public static final int SCALE_XYZ = 14;

    @Override
    public int getValues(GameInstance target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_X: returnValues[0] = target.getPosition().x; return 1;
            case POSITION_Y: returnValues[0] = target.getPosition().y; return 1;
            case POSITION_Z: returnValues[0] = target.getPosition().z; return 1;
            case POSITION_XYZ:
                Vector3 p = target.getPosition();
                returnValues[0] = p.x;
                returnValues[1] = p.y;
                returnValues[2] = p.z;
                return 3;
            case SCALE_X: returnValues[0] = target.scale.x; return 1;
            case SCALE_Y: returnValues[0] = target.scale.y; return 1;
            case SCALE_Z: returnValues[0] = target.scale.z; return 1;
            case SCALE_XYZ:
                returnValues[0] = target.scale.x;
                returnValues[1] = target.scale.y;
                returnValues[2] = target.scale.z;
                return 3;
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(GameInstance target, int tweenType, float[] newValues) {
        Vector3 p = target.getPosition();
        Vector3 s = target.scale;
        switch (tweenType) {
            case POSITION_X:

                target.setPosition(new Vector3(newValues[0],p.y,p.z)); break;
            case POSITION_Y:

                target.setPosition(new Vector3(p.x,newValues[0],p.z)); break;
            case POSITION_Z:

                target.setPosition(new Vector3(p.x,p.y,newValues[0])); break;
            case POSITION_XYZ:
                target.setPosition(new Vector3(newValues[0],newValues[1],newValues[2])); break;

            case SCALE_X:

                target.setScale(new Vector3(newValues[0],s.y,s.z)); break;
            case SCALE_Y:

                target.setScale(new Vector3(s.x,newValues[0],s.z)); break;
            case SCALE_Z:

                target.setScale(new Vector3(s.x,s.y,newValues[0])); break;
            case SCALE_XYZ:
                target.setScale(new Vector3(newValues[0],newValues[1],newValues[2])); break;
            default: assert false; break;
        }
    }

}
