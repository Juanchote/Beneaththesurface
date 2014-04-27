package es.juancho.beneath.classes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class EnemyCollection<Enemy> implements Collection<Enemy>{

    private ArrayList<Enemy> enemies = new ArrayList<Enemy>();

    @Override
    public int size() {
        return enemies.size();
    }

    @Override
    public boolean isEmpty() {
        return enemies.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return enemies.contains(o);
    }

    @Override
    public Iterator<Enemy> iterator() {
        return enemies.iterator();
    }

    @Override
    public Object[] toArray() {
        return enemies.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return enemies.toArray(a);
    }

    @Override
    public boolean add(Enemy enemy) {
        return enemies.add(enemy);
    }

    @Override
    public boolean remove(Object o) {
        return enemies.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return enemies.containsAll(c);
    }



    @Override
    public boolean addAll(Collection<? extends Enemy> c) {
        return enemies.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return enemies.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return enemies.retainAll(c);
    }

    @Override
    public void clear() {
        enemies.clear();
    }

}

