package assignment3;

public interface Tree<K extends Comparable<K>, T> {
    
    public T insert(K key, T obj);
    public T remove(K key);
    public boolean has(K key);
    
}
