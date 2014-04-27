package es.juancho.beneath.controllers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import es.juancho.beneath.BeneathMain;
import es.juancho.beneath.classes.Bullet;

import java.util.ArrayList;

/**
 * Class for listening for collisions.
 * @author JuanMa
 *
 */
public class CollisionListener implements ContactListener {

    ArrayList<Bullet> bullets = BeneathMain.getBullets();
    CharacterController characterController = CharacterController.getInstance();

    @Override
    public void beginContact(Contact contact) {
        System.out.println("BEGIN contactA:" + contact.getFixtureA().getFilterData().categoryBits
                + " contactB:" + contact.getFixtureB().getFilterData().categoryBits);
        characterController.getInstance().beginContact(contact);

        for(Bullet bullet : bullets) bullet.beginContact(contact);
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
