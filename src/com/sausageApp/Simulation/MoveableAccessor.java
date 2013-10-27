package com.sausageApp.Simulation;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/27/13
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class MoveableAccessor implements TweenAccessor<GameCamera> {

    public static final int POSITION_X = 1;
    public static final int POSITION_Y = 2;
    public static final int POSITION_Z = 3;
    public static final int POSITION_XYZ = 4;

    @Override
    public int getValues(GameCamera target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_X: returnValues[0] = target.position.x; return 1;
            case POSITION_Y: returnValues[0] = target.position.y; return 1;
            case POSITION_Z: returnValues[0] = target.position.z; return 1;
            case POSITION_XYZ:
                returnValues[0] = target.position.x;
                returnValues[1] = target.position.y;
                returnValues[2] = target.position.z;
                return 3;
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(GameCamera target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POSITION_X: target.position.x = (newValues[0]); break;
            case POSITION_Y: target.position.y = (newValues[0]); break;
            case POSITION_Z: target.position.z = (newValues[0]); break;
            case POSITION_XYZ:
                target.position.x = (newValues[0]);
                target.position.y = (newValues[1]);
                target.position.z = (newValues[2]); break;
            default: assert false; break;
        }
    }

}
