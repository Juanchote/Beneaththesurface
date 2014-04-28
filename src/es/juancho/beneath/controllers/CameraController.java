package es.juancho.beneath.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Controller for Camera class.
 * @author JuanMa
 *
 */
public class CameraController {
    private final String TAG = "Camera";
        
    private OrthographicCamera camera;

    private ShapeRenderer renderer;

    private static CameraController INSTANCE = new CameraController();

    private CameraController() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderer = new ShapeRenderer();
    }

    public void restartCamera() {
        camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2,0);
    }

    public static CameraController getInstance() {

        return INSTANCE;
    }

    public OrthographicCamera getCamera() {

        return camera;
    }

    /**
     * The camera follows the character with a smooth move.
     */
    public void cameraControl() {
        camera.position.set(camera.position.x + LevelController.getLevelMovement(), camera.position.y, camera.position.z);
    }

    public Vector2 calculateAbsolutePosition(Vector2 position) {
        Vector2 centerToLeftBottom = new Vector2(camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2);
        System.out.println(TAG + "- centerToLeftBottom => " + centerToLeftBottom);

        return new Vector2(200 + centerToLeftBottom.x + ( position.x * camera.viewportWidth ),centerToLeftBottom.y + ( position.y * camera.viewportHeight ));
    }

    /**
     * Renders 4 red lines representing the margins for the smooth camera movement.
     */
    public void shapeRender() {

    }

    public Vector2 fromScreenToWorld(Vector2 vector) {
        Vector3 temp = new Vector3(vector.x, vector.y, 0);
        camera.project(temp);

        Vector2 v2 = new Vector2(temp.x, temp.y);
        return v2;
    }
}