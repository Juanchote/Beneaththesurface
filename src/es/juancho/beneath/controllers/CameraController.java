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
        
        private OrthographicCamera camera;

        private ShapeRenderer renderer;

        private static CameraController INSTANCE = new CameraController();
        
        private CameraController() {
            camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            renderer = new ShapeRenderer();
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