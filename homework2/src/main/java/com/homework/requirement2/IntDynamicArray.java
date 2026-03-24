package com.homework.requirement2;

import com.homework.requirement4.CapacityStrategy;
import com.homework.requirement4.DoublingStrategy;

import java.util.Arrays;

public class IntDynamicArray implements DynamicArray {
    private int[] data;
    private int size;
    private final CapacityStrategy strategy;

    public IntDynamicArray() {
        this(10, new DoublingStrategy());
    }

    public IntDynamicArray(int initialCapacity) {
        this(initialCapacity, new DoublingStrategy());
    }

    public IntDynamicArray(int initialCapacity, CapacityStrategy strategy) {
        if (initialCapacity < 0) throw new IllegalArgumentException("initialCapacity < 0");
        this.data = new int[Math.max(1, initialCapacity)];
        this.size = 0;
        this.strategy = (strategy == null) ? new DoublingStrategy() : strategy;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(int element) {
        return indexOf(element) != -1;
    }

    @Override
    public boolean add(int e) {
        ensureCapacity(size + 1);
        data[size++] = e;
        return true;
    }

    @Override
    public boolean containsAll(DynamicArray c) {
        if (c == null) throw new IllegalArgumentException("c is null");
        for (int i = 0; i < c.size(); i++) {
            if (!contains(c.get(i))) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(DynamicArray c) {
        if (c == null) throw new IllegalArgumentException("c is null");
        if (c.size() == 0) return false;

        ensureCapacity(size + c.size());
        for (int i = 0; i < c.size(); i++) {
            data[size++] = c.get(i);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, DynamicArray c) {
        if (c == null) throw new IllegalArgumentException("c is null");
        checkIndexForAdd(index);
        if (c.size() == 0) return false;
        ensureCapacity(size + c.size());

        System.arraycopy(data, index, data, index + c.size(), size - index);

        for (int i = 0; i < c.size(); i++) {
            data[index + i] = c.get(i);
        }
        size += c.size();
        return true;
    }

    @Override
    public boolean removeAll(DynamicArray c) {
        if (c == null) throw new IllegalArgumentException("c is null");
        if (c.size() == 0) return false;
        int oldSize = size;
        int write = 0;
        for (int read = 0; read < size; read++) {
            int x = data[read];
            if (!containsInCollection(c, x)) {
                data[write++] = x;
            }
        }
        size = write;
        return size != oldSize;
    }

    @Override
    public boolean retainAll(DynamicArray c) {
        if (c == null) throw new IllegalArgumentException("c is null");

        int oldSize = size;
        int write = 0;
        for (int read = 0; read < size; read++) {
            int x = data[read];
            if (containsInCollection(c, x)) {
                data[write++] = x;
            }
        }
        size = write;
        return size != oldSize;
    }

    @Override
    public void sort() {
        Arrays.sort(data, 0, size);
    }

    @Override
    public void clear() {
        size = 0;
    }

    @Override
    public int get(int index) {
        checkIndex(index);
        return data[index];
    }

    @Override
    public int set(int index, int element) {
        checkIndex(index);
        int prev = data[index];
        data[index] = element;
        return prev;
    }

    @Override
    public void add(int index, int element) {
        checkIndexForAdd(index);
        ensureCapacity(size + 1);
        System.arraycopy(data, index, data, index + 1, size - index);
        data[index] = element;
        size++;
    }

    @Override
    public int remove(int index) {
        checkIndex(index);
        int removed = data[index];
        System.arraycopy(data, index + 1, data, index, size - index - 1);
        size--;
        return removed;
    }

    @Override
    public int indexOf(int element) {
        for (int i = 0; i < size; i++) {
            if (data[i] == element) return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(int element) {
        for (int i = size - 1; i >= 0; i--) {
            if (data[i] == element) return i;
        }
        return -1;
    }

    private boolean containsInCollection(DynamicArray c, int value) {
        for (int i = 0; i < c.size(); i++) {
            if (c.get(i) == value) return true;
        }
        return false;
    }

    private void ensureCapacity(int requiredCapacity) {
        if (requiredCapacity <= data.length) return;
        int newCap = strategy.calculateNewCapacity(data.length, requiredCapacity);
        data = Arrays.copyOf(data, newCap);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
        }
    }

    private void checkIndexForAdd(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(Arrays.copyOf(data, size));
    }
}