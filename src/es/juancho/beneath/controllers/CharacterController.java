package es.juancho.beneath.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import es.juancho.beneath.BeneathMain;
import es.juancho.beneath.classes.Bullet;
import es.juancho.beneath.classes.CategoryGroup;

public class CharacterController {

    private final String TAG = "CHARACTER";

    private Texture texture;
    private Sprite sprite;

    private BodyDef bodyDef;
    private Body body;
    private PolygonShape boxShape;
    private Vector2 position;
    private float levelMovement;


    private String bulletJsonString;
    private boolean attack = false;
    private float attackTime = 0.2f;
    private float attackDelta = attackTime;
    private Sound attackSound;

    private WorldController worldController;
    private float unitScale = BeneathMain.getScale();

    private static CharacterController INSTANCE = new CharacterController();

    private CharacterController() {
    }

    public static CharacterController getInstance() {
        return INSTANCE;
    }

    public void createPlayer(Vector2 position, String url) {
        this.position = new Vector2(position);
        Json json = new Json();
        ObjectMap objectMap = json.fromJson(ObjectMap.class, Gdx.files.internal(url));
        bulletJsonString = objectMap.get("bullet").toString();
        texture = new Texture(objectMap.get("texture").toString());

        sprite = new Sprite(texture);
        sprite.rotate(270);

        this.worldController = WorldController.getInstance();

        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.angle = 0;

        bodyDef.position.set(position.x - (sprite.getWidth() / 2), position.y - (sprite.getHeight() / 2));
        body = worldController.getWorld().createBody(bodyDef);

        boxShape = new PolygonShape();
        boxShape.setAsBox(sprite.getHeight() / 2, sprite.getWidth() / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boxShape;
        fixtureDef.density = 0f;
        fixtureDef.friction = 0f;

        body.setFixedRotation(true);
        fixtureDef.filter.categoryBits = CategoryGroup.CHARACTER;

        body.createFixture(fixtureDef);

        attackSound = (Sound) AssetManagerController.getInstance().get("data/sounds/lasers/shot1.ogg",Sound.class);
        System.out.println(TAG + "- set Player bit: " + fixtureDef.filter.categoryBits);
    }

    public void restartPlayer(Vector2 position, String url) {


    }

    public void render(SpriteBatch spriteBatch) {
        sprite.draw(spriteBatch);
        attackHandler();
    }

    public void shapeRender(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);


        shapeRenderer.end();
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {

        return body.getLinearVelocity();
    }

    public void setAttack(boolean bool) {

        attack = bool;
    }

    public void attackHandler() {
        if (attackDelta >= attackTime) {
            attackDelta = attackTime;

            if(attack) {
                attackSound.play();
                System.out.println(TAG + "- Creating Bullet..");
                Bullet bulletRight = new Bullet(bulletJsonString, new Vector2(position.x + sprite.getWidth() + 1, position.y));
                Bullet bulletLeft = new Bullet(bulletJsonString, new Vector2(position.x + sprite.getWidth() + 1, position.y + sprite.getWidth() - 20));
                System.out.println(TAG + "- Bullet created.");
                System.out.println(TAG + "- Adding Bullet to bullets..");
                BeneathMain.getBullets().add(bulletLeft);
                BeneathMain.getBullets().add(bulletRight);
                System.out.println(TAG + "- Added bullet to bullets, now has: " + BeneathMain.getBullets().size());

                attackDelta = 0;
            }
        }

        attackDelta += Gdx.graphics.getDeltaTime();
    }

    public void movePlayer(Vector2 velocity) {
        Vector2 newVel = new Vector2();
        newVel.set(velocity.x + 12 * levelMovement, velocity.y);

        OrthographicCamera camera = CameraController.getInstance().getCamera();
        if (position.x <= (camera.position.x - camera.viewportWidth / 2)) {
            newVel.set(getVelocity().x + 12 * levelMovement, getVelocity().y);
        }else if((position.x + sprite.getWidth()) >= (camera.position.x + camera.viewportWidth / 2)) {
            newVel.set(0, newVel.y );
        }

        body.setLinearVelocity(newVel);
        position.set(body.getPosition().x - (sprite.getWidth() / 2), body.getPosition().y - (sprite.getHeight() / 2));
        sprite.setPosition(position.x, position.y);
    }

    public void setLevelMovement(float levelMovement) {

        this.levelMovement = levelMovement;
    }

    public void beginContact(Contact contact) {

    }

    public void dispose() {
        attackSound.dispose();
        texture.dispose();
    }
}
