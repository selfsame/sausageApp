package com.sausageApp.Scenario;

import com.sausageApp.Players.Player;
import com.sausageApp.Scene.SensorObject;

/**
 * Created with IntelliJ IDEA.
 * User: jplur
 * Date: 10/29/13
 * Time: 6:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Scenario {

    public String title = null;

    public void start();

    public void update(float delta);

    public void finish();

    public void playerInput(Player player, int input);

    public void enterSensor(SensorObject sensor, Player player);
    public void exitSensor(SensorObject sensor, Player player);
}
