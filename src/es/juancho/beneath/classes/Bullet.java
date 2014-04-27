package es.juancho.beneath.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import es.juancho.beneath.controllers.WorldController;

public class Bullet {
    private final String TAG = "BULLET";

    private float dmg;
    private Sprite sprite;
    private Texture texture;
    private Json json;
    private Vector2 position;
    private float MAX_VELOCITY = 200f;

    private Body body;
    private BodyDef bodyDef;
    private PolygonShape boxShape;
    private final short mask = (short) ~CategoryGroup.BULLET;

    private float lifeTime = 5;
    private float deltaTime = 0;
    private boolean setToDestroy = false;

    public Bullet(String url, Vector2 position) {

        this.json = new Json();
        json.addClassTag("bullet", Bullet.class);
        ObjectMap objectMap = json.fromJson(ObjectMap.class, Gdx.files.internal(url));

        dmg = Float.parseFloat(objectMap.get("dmg").toString());

        texture = new Texture(Gdx.files.internal(objectMap.get("texture").toString()));
        sprite = new Sprite(texture);
        sprite.rotate(270);
        sprite.setPosition(position.x, position.y);
        this.position = new Vector2(position);

        System.out.println(TAG + "- parsed Bullet JSON");
        bodyDef = new BodyDef();
        bodyDef.position.set(position.x - (sprite.getWidth() / 2), position.y - (sprite.getHeight() / 2));
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.angle = 0;
        bodyDef.bullet = true;
        bodyDef.fixedRotation = true;

        body = WorldController.getInstance().getWorld().createBody(bodyDef);

        boxShape = new PolygonShape();
        boxShape.setAsBox(sprite.getHeight() / 2, sprite.getWidth() / 2);

        FixtureDef fixtureDef= new FixtureDef();
        fixtureDef.shape = boxShape;
        fixtureDef.friction = 0;
        fixtureDef.density = 1;

        body.setFixedRotation(true);
        fixtureDef.filter.categoryBits = CategoryGroup.BULLET;
        fixtureDef.filter.maskBits = mask;
        body.createFixture(fixtureDef);

        body.setUserData(this);

    }

    public void render(SpriteBatch spriteBatch) {
        if (!setToDestroy) {
            deltaTime += Gdx.graphics.getDeltaTime();
            body.setLinearVelocity(MAX_VELOCITY, 0);
            position.set(body.getPosition().x - (sprite.getWidth() / 2), body.getPosition().y - (sprite.getHeight() / 2));
            sprite.setPosition(position.x, position.y);
            sprite.draw(spriteBatch);

            if (deltaTime >= lifeTime) {
                setToDestroy = true;
            }
        }else{
            WorldController.getInstance().addBulletForDestroy(this);
        }
    }

    public float getDamage() {
        return dmg;
    }

    public void setForDestroy() {

        setToDestroy = true;
    }

    public boolean isDestroyable() {
        return setToDestroy;
    }

    public void dispose() {
        WorldController.getInstance().getWorld().destroyBody(body);
    }

    public void beginContact(Contact contact) {
        if (contact.getFixtureA().getFilterData().categoryBits == CategoryGroup.ENEMY) {
            Bullet bullet = (Bullet) contact.getFixtureB().getBody().getUserData();
            Enemy enemy = (Enemy) contact.getFixtureA().getBody().getUserData();
            enemy.getDamage(bullet.getDamage());
            bullet.setForDestroy();
        }
    }
}