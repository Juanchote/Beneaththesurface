package es.juancho.beneath.classes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class BulletCollection<Bullet> implements Collection<Bullet>{

	private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	
	@Override
	public int size() {
		return bullets.size();
	}

	@Override
	public boolean isEmpty() {
		return bullets.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return bullets.contains(o);
	}

	@Override
	public Iterator<Bullet> iterator() {
		return bullets.iterator();
	}

	@Override
	public Object[] toArray() {
		return bullets.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return bullets.toArray(a);
	}

	@Override
	public boolean add(Bullet e) {
		return bullets.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return bullets.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return bullets.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Bullet> c) {
		return bullets.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return bullets.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return bullets.retainAll(c);
	}

	@Override
	public void clear() {
		bullets.clear();
	}

}
