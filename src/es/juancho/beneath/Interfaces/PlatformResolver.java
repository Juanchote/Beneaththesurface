package es.juancho.beneath.Interfaces;

import com.badlogic.gdx.InputAdapter;
import es.juancho.beneath.controllers.CharacterController;

public interface PlatformResolver {
	public String getDefaultLanguage();
    //public FacebookAPI getFacebookAPI();
    public InputAdapter getInputManager();
    public void setCharacterController(CharacterController characterController);
    public void inputHandler();
}
