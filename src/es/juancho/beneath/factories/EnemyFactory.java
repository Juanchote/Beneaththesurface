package es.juancho.beneath.factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import es.juancho.beneath.classes.Enemy;
import es.juancho.beneath.controllers.CameraController;

import java.util.ArrayList;

/**
 * Created by Juanma on 26/04/2014.
 */
public class EnemyFactory {

    private final String TAG = "ENEMY FACTORY";
    private static ArrayList<Enemy> enemies;

    private static EnemyFactory INSTANCE = new EnemyFactory();

    private EnemyFactory() {
        enemies = new ArrayList<Enemy>();
    }

    public static EnemyFactory getInstance() {
        return INSTANCE;
    }

    public void createPattern(String patternJson,final int time, final String enemyJson, final Vector2 position) {
        System.out.println(TAG + "- Entering pattern creation..");
        Json json = new Json();
        final ObjectMap objectMap = json.fromJson(ObjectMap.class, Gdx.files.internal(patternJson));
        float count = Float.parseFloat(objectMap.get("count").toString());
        float interval = Float.parseFloat(objectMap.get("interval").toString());
        final Array<JsonValue> movePatternsObject = (Array) objectMap.get("movePatterns");
        System.out.println(TAG + "- parsed Json => count: " + count-- + " interval: " + interval + " position: " + position);

        Timer timer = Timer.instance();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                createEnemy(enemyJson, position, movePatternsObject);
            }
        },time, interval,(int) count);
    }

    public void createEnemy(String url, Vector2 position, Array<JsonValue> movePatternsObject) {
        Enemy enemy = new Enemy(url, CameraController.getInstance().calculateAbsolutePosition(position), movePatternsObject);
        enemies.add(enemy);
        System.out.println(TAG + "- enemy created. total => " + enemies.size());
    }

    public void destroyEnemy(Enemy enemy) {
        enemy.setForDestroy();
    }

    public void render(SpriteBatch spriteBatch) {
        for (Enemy enemy : enemies) {
            enemy.render(spriteBatch);
        }
    }

    public static ArrayList<Enemy> getEnemies() {

        return enemies;
    }

    public static int getEnemyCount() {

        return enemies.size();
    }

    public void restartLevel() {
        Timer.instance().clear();

        for (Enemy enemy : enemies) {
            enemy.setForDestroy();
        }
        enemies.clear();
    }
}
