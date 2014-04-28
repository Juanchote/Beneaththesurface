package es.juancho.beneath.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import es.juancho.beneath.BeneathMain;
import es.juancho.beneath.controllers.AssetManagerController;
import es.juancho.beneath.controllers.CameraController;
import es.juancho.beneath.controllers.LevelController;
import es.juancho.beneath.controllers.WorldController;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Juanma on 27/04/2014.
 */
public class Enemy {

    private final String TAG = "ENEMY";

    //graphic vars
    private Texture texture;
    private Sprite sprite;

    //physic vars
    private BodyDef bodyDef;
    private Body body;
    private PolygonShape boxShape;
    private short mask = (short) ~CategoryGroup.ENEMY;
    private Vector2 position;

    //movement vars
    private ArrayList<MovePattern> movePattern;
    private Iterator<MovePattern> iterator;
    private MovePattern currentPattern;
    private float patternDelta = 0;
    private float patternTime = 0;

    //game-related vars
    private float max_health;
    private float current_health = max_health;
    private boolean destroy = false;
    private float MAX_SPEED;
    private int score;
    private String type;

    //attack-related vars
    private String bulletJsonString;
    private String enemyJsonString;
    private boolean attack = false;
    private float attackTime = 0.2f;
    private float attackDelta = attackTime;

    private float unitScale = BeneathMain.getScale();

    /**
     * @class MovePattern
     * @desc Pattern of movements
     */
    private class MovePattern{
        private String direction;
        private float time;

        public MovePattern(String direction, float time) {
            this.direction = direction;
            this.time = time;
        }

        /**
         * Parses the movement types into a speed Vector
         * @return velocity Vector2
         */
        public Vector2 parseMovement() {
            Vector2 velocity = new Vector2();
            if (direction.equals("front")) {
                velocity.set(-MAX_SPEED,0);
            }else if(direction.equals("left")) {
                velocity.set(0,-MAX_SPEED);
            }else if(direction.equals("right")) {
                velocity.set(0,MAX_SPEED);
            }
            velocity.set(velocity.x + 12 * LevelController.getLevelMovement(), velocity.y);
            return velocity;
        }

        public String getDirection() {
            return this.direction;
        }

        public float getTime() {
            return this.time;
        }
    }

    public Enemy(String url, Vector2 position, Array<JsonValue> movePatternsObject) {
        System.out.println(TAG + "- Creating Enemy in position => " + position);
        movePattern = new ArrayList<MovePattern>();

        this.position = new Vector2(position);
        enemyJsonString = url;
        Json json = new Json();
        json.addClassTag("enemy", Enemy.class);
        ObjectMap objectMap = json.fromJson(ObjectMap.class, Gdx.files.internal(enemyJsonString));

        if (movePatternsObject != null) {
            for (JsonValue jsonValue : movePatternsObject) {
                String direction = jsonValue.getString("direction");
                float time = jsonValue.getFloat("time");
                movePattern.add(new MovePattern(direction, time));
                System.out.println(TAG + "- Added Pattern =>" + direction + " " + time);
            }
        }

        bulletJsonString = objectMap.get("bullet").toString();
        MAX_SPEED = Float.parseFloat(objectMap.get("speed").toString());
        score = (int) Float.parseFloat(objectMap.get("score").toString());
        type = objectMap.get("type").toString();
        max_health = Float.parseFloat(objectMap.get("health").toString());
        current_health = max_health;
        System.out.println(TAG + "- Parsed JSON + number of Patterns: " + movePattern.size());

        if (!movePattern.isEmpty()) {
            currentPattern = movePattern.get(0);
            patternTime = currentPattern.getTime();
            iterator = movePattern.iterator();
        }

        this.texture = (Texture) AssetManagerController.getInstance().get(objectMap.get("texture").toString(), Texture.class);

        sprite = new Sprite(texture);

        sprite.setPosition(position.x, position.y);
        sprite.rotate(90);

        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.angle = 0;

        bodyDef.position.set(position.x + ( sprite.getWidth() / 2), position.y + (sprite.getHeight() / 2));
        body = WorldController.getInstance().getWorld().createBody(bodyDef);

        boxShape = new PolygonShape();
        boxShape.setAsBox(sprite.getHeight() / 2, sprite.getWidth() / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boxShape;
        fixtureDef.density = 100;
        fixtureDef.friction = 0f;

        body.setFixedRotation(true);
        fixtureDef.filter.categoryBits = CategoryGroup.ENEMY;
        fixtureDef.filter.maskBits = mask;

        body.setUserData(this);

        body.createFixture(fixtureDef);

    }

    public void render(SpriteBatch spriteBatch) {
        if (!destroy) {
            sprite.draw(spriteBatch);

            movementHandler();
            attackHandler();
            deathHandler();
        }else{
            WorldController.getInstance().addEnemyForDestroy(this);
        }
    }

    private void movementHandler() {
        if (!type.equals("boss")) {
            if (patternDelta < patternTime) {
                Vector2 velocity = currentPattern.parseMovement();

                body.setLinearVelocity(velocity);
                position.set(body.getPosition().x - (sprite.getWidth() / 2), body.getPosition().y - (sprite.getHeight() / 2));
                sprite.setPosition(position.x, position.y);
            } else {
                if (iterator.hasNext()) {
                    currentPattern = iterator.next();
                    patternTime = currentPattern.getTime();
                    patternDelta = 0;
                } else {
                    setForDestroy();
                }
            }
            patternDelta += Gdx.graphics.getDeltaTime();
        }else{
            if (position.x <= ((Gdx.graphics.getWidth() / 2) - sprite.getWidth()) + CameraController.getInstance().getCamera().position.x) {
                Vector2 velocity = new Vector2((12 * LevelController.getLevelMovement()), body.getLinearVelocity().y);
                body.setLinearVelocity(velocity);
                position.set(body.getPosition().x - (sprite.getWidth() / 2), body.getPosition().y - (sprite.getHeight() / 2));
                sprite.setPosition(position.x, position.y);
                System.out.println(TAG + "- boss in position.");
            }
        }
    }

    private void attackHandler() {
        RayCastCallback rayCastCallback = null;
        Vector2 dest = new Vector2(position);
        dest.set(dest.x - 300,dest.y);
        WorldController.getInstance().getWorld().rayCast(rayCastCallback,position,dest);

        if (rayCastCallback != null) {
            System.out.println(TAG + "- raycasthit");
        }
    }

    public void getDamage(float damage) {
        this.current_health -= damage;
        System.out.println(TAG + "-Received damage: " + damage + " still: " + current_health);
    }

    public int getScore() {
        return score;
    }


    private void deathHandler() {
        if (current_health <= 0) {
            destroy = true;
            LevelController.addScore(score);
            System.out.println(TAG + "- killed enemy => score: " + score);
            if (type.equals("boss")) {
                LevelController.getInstance().setLevelFinished(true);
            }
        }
    }

    public void dispose() {
        WorldController.getInstance().getWorld().destroyBody(body);
    }

    public void setForDestroy() {
        destroy = true;
    }

    public boolean isDestroyable() {
        return destroy;
    }

}
