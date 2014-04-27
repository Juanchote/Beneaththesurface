package es.juancho.beneath.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import es.juancho.beneath.BeneathMain;
import es.juancho.beneath.controllers.WorldController;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Juanma on 27/04/2014.
 */
public class Enemy {

    private final String TAG = "ENEMY";

    private Texture texture;
    private Sprite sprite;

    private BodyDef bodyDef;
    private Body body;
    private PolygonShape boxShape;
    private short mask = (short) ~CategoryGroup.ENEMY;
    private Vector2 position;

    private ArrayList<MovePattern> movePattern;
    private Iterator<MovePattern> iterator;
    private MovePattern currentPattern;
    private float patternDelta = 0;
    private float patternTime = 0;

    private float max_health = 20;
    private float current_healt = max_health;
    private boolean destroy = false;
    private float MAX_SPEED;

    private String bulletJsonString;
    private String enemyJsonString;
    private boolean attack = false;
    private float attackTime = 0.2f;
    private float attackDelta = attackTime;

    private float unitScale = BeneathMain.getScale();


    private class MovePattern{
        private String direction;
        private float time;

        public MovePattern(String direction, float time) {
            this.direction = direction;
            this.time = time;
        }

        public Vector2 parseMovement() {
            //System.out.println(TAG + "- parsingMovement.." + "type: " + direction + " delta: " + patternDelta);
            Vector2 velocity = new Vector2();
            if (direction.equals("front")) {
                velocity.set(-MAX_SPEED,0);
            }else if(direction.equals("left")) {
                velocity.set(0,-MAX_SPEED);
            }else if(direction.equals("right")) {
                velocity.set(0,MAX_SPEED);
            }

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
        movePattern = new ArrayList<MovePattern>();

        this.position = new Vector2(position);
        enemyJsonString = url;
        Json json = new Json();
        json.addClassTag("enemy", Enemy.class);
        ObjectMap objectMap = json.fromJson(ObjectMap.class, Gdx.files.internal(enemyJsonString));

        for (JsonValue jsonValue : movePatternsObject) {
            String direction = jsonValue.getString("direction");
            float time = jsonValue.getFloat("time");
            movePattern.add(new MovePattern(direction, time));
            System.out.println(TAG + "- Added Pattern =>" + direction + " " + time);
        }

        bulletJsonString = objectMap.get("bullet").toString();
        MAX_SPEED = Float.parseFloat(objectMap.get("speed").toString());
        System.out.println(TAG + "- Parsed JSON + number of Patterns: " + movePattern.size());

        if (!movePattern.isEmpty()) {
            currentPattern = movePattern.get(0);
            patternTime = currentPattern.getTime();
            iterator = movePattern.iterator();
        }

        this.texture = new Texture(objectMap.get("texture").toString());

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
            deathHandler();
        }else{
            WorldController.getInstance().addEnemyForDestroy(this);
        }
    }

    private void movementHandler() {
        if (patternDelta < patternTime) {
            Vector2 velocity = currentPattern.parseMovement();

            body.setLinearVelocity(velocity);
            position.set(body.getPosition().x - (sprite.getWidth() / 2), body.getPosition().y - (sprite.getHeight() / 2));
            sprite.setPosition(position.x, position.y);
        }else{
            if (iterator.hasNext()) {
                currentPattern = iterator.next();
                patternTime = currentPattern.getTime();
                patternDelta = 0;
            }else{
                setForDestroy();
            }
        }
        patternDelta += Gdx.graphics.getDeltaTime();
    }

    public void getDamage(float damage) {
        this.current_healt -= damage;
        System.out.println(TAG + "-Received damage: " + damage);
    }

    private void deathHandler() {
        if (current_healt <= 0) {
            destroy = true;
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
