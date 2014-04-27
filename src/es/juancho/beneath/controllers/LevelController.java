package es.juancho.beneath.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import es.juancho.beneath.factories.EnemyFactory;

import java.util.ArrayList;

public class LevelController {

    private ArrayList<Music> musics;
    private Music currentMusic;

    private static LevelController INSTANCE = new LevelController();

    private LevelController() {
        musics = new ArrayList<Music>();
        CameraController.getInstance().getCamera().position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
    }

    public void startLevel() {
        Vector2 enemyPos = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 4);
        Vector2 enemyPosR = new Vector2(Gdx.graphics.getWidth(), 3 * Gdx.graphics.getHeight() / 4);

        musics.add((Music) AssetManagerController.getInstance().get("data/sounds/music/theme.ogg", Music.class));
        musics.add((Music) AssetManagerController.getInstance().get("data/sounds/music/theme_loop.ogg", Music.class));
        currentMusic = musics.get(0);

        playTheme();

        EnemyFactory.getInstance().createPattern("data/json/enemyFactoryPatterns/basic_front_left.json", "data/json/enemy_basic_ship.json", enemyPos);
        EnemyFactory.getInstance().createPattern("data/json/enemyFactoryPatterns/basic_front_right.json", "data/json/enemy_basic_ship.json", enemyPosR);

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

    public static LevelController getInstance() {
        return INSTANCE;
    }
}
