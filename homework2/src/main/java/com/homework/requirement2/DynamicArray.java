package com.homework.requirement2;

public interface DynamicArray {
    int size();
    boolean isEmpty();
    boolean contains(int element);
    boolean add(int e);

    boolean containsAll(DynamicArray c);
    boolean addAll(DynamicArray c);
    boolean addAll(int index, DynamicArray c);

    boolean removeAll(DynamicArray c);
    boolean retainAll(DynamicArray c);

    default void sort() { }

    void clear();

    int get(int index);
    int set(int index, int element);
    void add(int index, int element);

    int remove(int index);

    int indexOf(int element);
    int lastIndexOf(int element);
}