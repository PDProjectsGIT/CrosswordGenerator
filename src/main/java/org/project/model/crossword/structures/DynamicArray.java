package org.project.model.crossword.structures;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public class DynamicArray<T> implements Iterable<T>{

    protected T[] data;

    @SuppressWarnings("unchecked")
    public DynamicArray(){
        data = (T[]) new Object[0];
    }

    @SuppressWarnings("unchecked")
    public DynamicArray(int size){
        data = (T[]) new Object[size];
    }

    public DynamicArray(DynamicArray<T> dynamicArray){
        data = dynamicArray.data.clone();
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return new ArrayIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DynamicArray<?> that = (DynamicArray<?>) o;
        return Objects.deepEquals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    private class ArrayIterator implements Iterator<T> {
        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < data.length;
        }

        @Override
        public T next() {
            return data[currentIndex++];
        }
    }

    public void setValue(int index, T value){
        data[index] = value;
    }

    @SuppressWarnings("unchecked")
    final public void setValueOutOfBounds(int index, T value){
        if(index < data.length && index >= 0){
            setValue(index, value);
        }
        int size = getSize();
        int newSize;
        int shift = 0;
        if(index < 0){
            shift = Math.abs(index);
            if(size == 0){
                newSize = size + shift + 1;
            }else{
                newSize = size + shift;
            }

        }else{
            newSize = Math.max(index + 1, size);
        }
        T[] tempData = (T[]) new Object[newSize];
        if (size >= 0) System.arraycopy(data, 0, tempData, shift, size);
        data = tempData;
        data[index + shift] = value;
    }

    public Optional<T> getValue(int index)
    {
        return Optional.ofNullable(data[index]);
    }

    public int getSize(){
        return data.length;
    }

    public boolean contains(T value){
        if(value == null) throw new NullPointerException("Cannot invoke \"DynamicArray.contains(Object)\" because \"value\" is null");
        for (T val : data) {
            if (val != null && val.equals(value)) return true;
        }
        return false;
    }
}
