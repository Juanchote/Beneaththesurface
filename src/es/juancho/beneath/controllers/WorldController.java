package es.juancho.beneath.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import es.juancho.beneath.BeneathMain;
import es.juancho.beneath.classes.Bullet;
import es.juancho.beneath.classes.BulletCollection;
import es.juancho.beneath.classes.Enemy;
import es.juancho.beneath.classes.EnemyCollection;
import es.juancho.beneath.factories.EnemyFactory;

import java.util.ArrayList;


/**
 * Controller for World Class
 * @author Juan Manuel Rodulfo Salcedo
 */
public class WorldController {

    private Vector2 gravity = new Vector2(0, 0);
    private World world;
    private boolean doSleep = true;
    private BitmapFont font;
    private static OrthographicCamera camera;
    private BulletCollection<Bullet> bulletDestroyerArray;
    private EnemyCollection<Enemy> enemyDestroyerArray;
    private ArrayList<Bullet> bullets;
    private ArrayList<Enemy> enemies;

    private double accumulator;
    private float step = 1.0f / 60.0f;
    private float speed = 5;

    private int velocityIterations = 8;   //how strongly to correct velocity
    private int positionIterations = 3;   //how strongly to correct position
        
    private static WorldController INSTANCE = new WorldController(); //Singleton Pattern
    
    /**
     * private Contructor so only one instance of WorldController is allowed.
     */
    private WorldController() {
	    world = new World(gravity, doSleep);
	    font = new BitmapFont();
	    bulletDestroyerArray = new BulletCollection<Bullet>();
        enemyDestroyerArray = new EnemyCollection<Enemy>();
    }
    
    /**
     * Returns the instance of the Controller
     * @return the only instance of the WorldController Class.
     */
    public static WorldController getInstance() {
        return INSTANCE;
    }

    /**
     * Disposes the world object.
     */
    public void dispose() {
        world.dispose();
    }


    public void render() {
        bullets = BeneathMain.getBullets();
        enemies = EnemyFactory.getEnemies();

        doPhysicsStep(Gdx.graphics.getDeltaTime());

        if (!bulletDestroyerArray.isEmpty()) {
        	bullets.removeAll(bulletDestroyerArray);
            for(Bullet bullet : bulletDestroyerArray) {
                bullet.dispose();
            }
            bulletDestroyerArray.clear();
        }

        if (!enemyDestroyerArray.isEmpty()) {
            enemies.removeAll(enemyDestroyerArray);
            for(Enemy enemy : enemyDestroyerArray) {
                enemy.dispose();
            }
            enemyDestroyerArray.clear();
        }
        
    }

    private void doPhysicsStep(float deltaTime) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime * speed, 0.25f);
        accumulator += frameTime;
        while (accumulator >= step) {
            world.step(step, velocityIterations, positionIterations);
            accumulator -= step;
        }
    }

    public void addBulletForDestroy(Bullet b) {

        bulletDestroyerArray.add(b);
    }

    /**
     * Returns the World object used for all the Box2d physic.
     * @return the world object
     */
    public World getWorld() {
        if (world == null)
                return null;
        else
                return world;
    }

    public void addEnemyForDestroy(Enemy enemy) {
        enemyDestroyerArray.add(enemy);
    }
    
    /**
     * Changes the current gravity of the world with the new given.
     * @param gravity new gravity for the world.
     */
    public void setGravity(Vector2 gravity) {
        this.gravity = gravity;
        world.setGravity(gravity);
    }
    
    /**
     * Returns the current gravity of the world.
     * @return the current gravity
     */
    public Vector2 getGravity() {
        return gravity;
    }
    

        
}