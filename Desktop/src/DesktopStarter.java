/**
 * Created with IntelliJ IDEA.
 * User: jparker
 * Date: 10/3/13
 * Time: 12:53 PM
 * To change this template use File | Settings | File Templates.
 */
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopStarter {
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Title";
        cfg.useGL20 = true;
        cfg.width = 1480;
        cfg.height = 1480;
        new LwjglApplication(new com.sausageApp.Game.myGame(), cfg);


    }
}
