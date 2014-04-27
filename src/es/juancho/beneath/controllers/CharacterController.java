package es.juancho.beneath.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import es.juancho.beneath.BeneathMain;
import es.juancho.beneath.classes.Bullet;
import es.juancho.beneath.classes.CategoryGroup;

/**
 * Created by Juanma on 26/04/2014.
 */
public class CharacterController {

    private final String TAG = "CHARACTER";

    private Texture texture;
    private Sprite sprite;

    private BodyDef bodyDef;
    private Body body;
    private PolygonShape boxShape;
    private Vector2 position;

    private String bulletJsonString = "data/json/player_bullet_basic.json";
    private boolean attack = false;
    private float attackTime = 0.2f;
    private float attackDelta= attackTime;

    private WorldController worldController;
    private float unitScale = BeneathMain.getScale();

    private static CharacterController INSTANCE;

    private CharacterController(Vector2 position, Texture texture, WorldController worldController) {
        this.position = new Vector2(position);
        this.texture = texture;
        sprite = new Sprite(texture);
        //sprite.rotate90(true);
        sprite.rotate(270);

        this.worldController = worldController;

        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.angle = 0;

        bodyDef.position.set(position.x - (sprite.getWidth() / 2), position.y - (sprite.getHeight() / 2));
        body = worldController.getWorld().createBody(bodyDef);

        boxShape = new PolygonShape();
        boxShape.setAsBox(sprite.getHeight() / 2, sprite.getWidth() / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boxShape;
        fixtureDef.density = 1;
        fixtureDef.friction = 0f;

        body.setFixedRotation(true);
        fixtureDef.filter.categoryBits = CategoryGroup.CHARACTER;

        body.createFixture(fixtureDef);
        System.out.println(TAG + "- set Player bit: " + fixtureDef.filter.categoryBits);
    }

    public static CharacterController getInstance(Vector2 position,Texture texture, WorldController worldController) {
        if (INSTANCE == null) {
            INSTANCE = new CharacterController(position, texture, worldController);
        }

        return INSTANCE;
    }

    public static CharacterController getInstance() {
        return INSTANCE;
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
        body.setLinearVelocity(velocity);
        position.set(body.getPosition().x - (sprite.getWidth() / 2), body.getPosition().y - (sprite.getHeight() / 2));
        sprite.setPosition(position.x, position.y);
    }

    public void beginContact(Contact contact) {

    }
}
