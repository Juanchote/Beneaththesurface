package es.juancho.beneath.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import es.juancho.beneath.BeneathMain;
import es.juancho.beneath.classes.Bullet;
import es.juancho.beneath.factories.EnemyFactory;

import javax.script.ScriptException;
import java.util.ArrayList;

public class LevelController {

    private final String TAG = "LEVEL";
    private int number = 0;

    private ArrayList<Music> musics;
    private Music currentMusic;
    private String dataJson;
    private String playerData;
    private Texture textureFirst;
    private Texture textureSecond;
    private Sprite sprite;
    private Vector2 startingPosition;

    private EnemyFactory enemyFactory;

    private float textureFirstStartPoint = 0;
    private float textureSecondStartPoint;

    private static float levelMovement = 1f;

    private Camera camera;

    private static LevelController INSTANCE = new LevelController();

    private LevelController() {
        musics = new ArrayList<Music>();

        camera = CameraController.getInstance().getCamera();
        enemyFactory = EnemyFactory.getInstance();

        CameraController.getInstance().restartCamera();
    }

    public void startLevel(String url) {
        System.out.println(TAG + "- starting level..");
        dataJson = url;

        Json json = new Json();
        ObjectMap objectMap = json.fromJson(ObjectMap.class, Gdx.files.internal(url));
        Array<JsonValue> music = (Array) objectMap.get("music");

        number = (int) Float.parseFloat(objectMap.get("number").toString());
        textureFirst = new Texture(Gdx.files.internal(objectMap.get("background").toString()));
        playerData = objectMap.get("player").toString();
        Array<JsonValue> enemyPatterns = (Array) objectMap.get("enemyPatterns");

        textureSecond = textureFirst;

        textureFirstStartPoint = 0;
        textureSecondStartPoint = textureFirstStartPoint + textureFirst.getWidth();

        startingPosition = new Vector2(Gdx.graphics.getWidth() / 6, Gdx.graphics.getHeight() / 2);
        CharacterController.getInstance().createPlayer(startingPosition, playerData);
        CharacterController.getInstance().setLevelMovement(levelMovement);

        for (Bullet bullet : BeneathMain.getBullets()) {
            bullet.setForDestroy();
        }

        for (JsonValue jsonValue : music) {
            musics.add((Music) AssetManagerController.getInstance().get(jsonValue.getString("song"), Music.class));
        }

        currentMusic = musics.get(0);

        if (currentMusic.isPlaying()) {
            currentMusic.stop();
        }

        playTheme();

        for (JsonValue enemyPattern : enemyPatterns) {
            enemyFactory.createPattern(enemyPattern.getString("enemyPattern"), enemyPattern.getInt("time"), enemyPattern.getString("enemy"), new Vector2(enemyPattern.getFloat("x"), enemyPattern.getFloat("y")));
        }
    }

    public void restartLevel() throws ScriptException {
        Timer.instance().clear();
        EnemyFactory.getInstance().restartLevel();
        CameraController.getInstance().restartCamera();
        startLevel(dataJson);
    }

    public void playTheme() {
        Timer timer = Timer.instance();

        if(!currentMusic.isPlaying()) {
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    currentMusic = musics.get(0);
                    currentMusic.play();
                    currentMusic.setOnCompletionListener(new Music.OnCompletionListener() {
                        @Override
                        public void onCompletion(Music music) {
                            currentMusic = musics.get(1);
                            currentMusic.setLooping(true);
                            currentMusic.play();
                        }
                    });
                }
            }, 0);
        }
    }

    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(textureFirst, textureFirstStartPoint, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.draw(textureSecond, textureSecondStartPoint, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (camera.position.x >= (textureFirstStartPoint + (Gdx.graphics.getWidth() / 2))) {
            textureSecondStartPoint = textureFirstStartPoint + Gdx.graphics.getWidth();
        }
        if( camera.position.x >= (textureSecondStartPoint + (Gdx.graphics.getWidth() / 2))) {
            textureFirstStartPoint = textureSecondStartPoint + Gdx.graphics.getWidth();
        }
    }

    public void dispose() {
        for (Music music : musics) {
            music.dispose();
        }
        textureFirst.dispose();
        textureSecond.dispose();
    }

    public static float getLevelMovement() {
        return levelMovement;
    }

    public static LevelController getInstance() {
        return INSTANCE;
    }
}
