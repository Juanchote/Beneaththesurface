package es.juancho.beneath.controllers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class AssetManagerController {
    AssetManager assetManager;

    private static AssetManagerController INSTANCE = new AssetManagerController();

    private AssetManagerController() {
        assetManager = new AssetManager();

        loadAssets();
    }

    public void loadAssets() {
        assetManager.load("data/sounds/music/theme.ogg", Music.class);
        assetManager.load("data/sounds/music/theme_loop.ogg", Music.class);
        assetManager.load("data/sprites/ships/player_basic_ship.png", Texture.class);
        assetManager.load("data/sounds/lasers/shot1.ogg", Sound.class);
    }

    public static AssetManagerController getInstance() {
        return INSTANCE;
    }

    public Object get(String url,Class type) {
        return assetManager.get(url,type);
    }

    public boolean update() {
        return assetManager.update();
    }

    public void dispose() {
        assetManager.dispose();
    }
}
