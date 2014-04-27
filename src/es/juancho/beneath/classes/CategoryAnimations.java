package es.juancho.beneath.classes;

/**
 * Class for helping the Animation structure with maps.
 * @author Juan Manuel Rodulfo Salcedo
 *
 */
public class CategoryAnimations {
	
	private static int index = 0;
	private static final int MAX_INDEX = 9;
	
	public static int IDLE_RIGHT = 0;
	public static int IDLE_LEFT = 1;
	public static int WALK_RIGHT = 2;
	public static int WALK_LEFT = 3;
	public static int JUMP_RIGHT = 4;
	public static int JUMP_LEFT = 5;
	public static int ATTACK_RIGHT = 6;
	public static int ATTACK_LEFT = 7;
	public static int DIE_RIGHT = 8;
	public static int DIE_LEFT = 9;
	
	public static int EOF = -1;
	
	/**
	 * Returns the current Index and points to the next index. Remember to call initIterator before start using this.
	 * @return the current index position.
	 */
	public static int currentIndex() {
		if(index > MAX_INDEX) {
			index = 0;
			return EOF;
		}else{
			return index++;
		}
	}
	
	public static int getMaxIndex() {
		return MAX_INDEX;
	}
	
	/**
	 * Resets the iteration, use it before using for first time currentIndex.
	 */
	public static void initIterator() {
		index = 0;
	}
}
