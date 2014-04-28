package es.juancho.beneath;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import es.juancho.beneath.Interfaces.PlatformResolver;
import es.juancho.beneath.classes.Bullet;
import es.juancho.beneath.controllers.*;
import es.juancho.beneath.factories.EnemyFactory;

import java.util.ArrayList;

public class BeneathMain implements ApplicationListener {

    private final String TAG = "MAIN";
    private static PlatformResolver platformResolver;

    private CameraController cameraController;
    private WorldController worldController;
    private World world;
    private CharacterController characterController;
    private CollisionListener contactListener;
    private EnemyFactory enemyFactory;
    private AssetManagerController assetManagerController;
    private LevelController levelController;

    private static ArrayList<Bullet> bullets;

    private BitmapFont bitmapFont;
	private OrthographicCamera camera;
	private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private Box2DDebugRenderer box2DDebugRenderer;
    private boolean debugState = false;

    float w;
    float h;
    private Vector2 startingPosition;
    private static float unitScale = 32f;

    private boolean once = true;
	
	@Override
	public void create() {
        bullets = new ArrayList<Bullet>();

        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();

        System.out.println(TAG + "- setting graphics properties: width:" + w + " height: " + h);

        cameraController = CameraController.getInstance();
        worldController = WorldController.getInstance();
        assetManagerController = AssetManagerController.getInstance();
        enemyFactory = EnemyFactory.getInstance();
        levelController = LevelController.getInstance();

        world = worldController.getWorld();

        while(!assetManagerController.update());

        characterController = CharacterController.getInstance();

        platformResolver.setCharacterController(characterController);
        Gdx.input.setInputProcessor(platformResolver.getInputManager());
        contactListener = new CollisionListener();

        camera = cameraController.getCamera();
        System.out.println(TAG + "- set camera.");

		spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        box2DDebugRenderer = new Box2DDebugRenderer();
        bitmapFont = new BitmapFont();

        world.setContactListener(contactListener);

        levelController.startLevel("data/json/levels/level1.json");
    }

	@Override
	public void dispose() {
		spriteBatch.dispose();
        worldController.dispose();
        assetManagerController.dispose();
        characterController.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        spriteBatch.setProjectionMatrix(camera.combined);

        if (!assetManagerController.update()) {

        }else {
            camera.update();

            if (debugState) {
                box2DDebugRenderer.render(world, camera.combined);
                characterController.shapeRender(shapeRenderer);
                cameraController.shapeRender();
            }

            cameraController.cameraControl();
            platformResolver.inputHandler();
            worldController.render();

            spriteBatch.begin();
            levelController.render(spriteBatch);
            characterController.render(spriteBatch);
            enemyFactory.render(spriteBatch);
            for(Bullet bullet: bullets) {
                bullet.render(spriteBatch);
            }
            spriteBatch.end();
        }
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
        Timer.instance().stop();
    }

	@Override
	public void resume() {
        Timer.instance().start();
	}

    public static float getScale() {
        return unitScale;
    }

    public static void setPlatformResolver(PlatformResolver myPlatformResolver) {
        platformResolver = myPlatformResolver;
    }

    public static ArrayList<Bullet> getBullets() {
        return bullets;
    }
}
