package org.project.model.crossword.structures;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Class that represents adjustable array structure.
 * It's main feature is to insert new values even in non-existing index.
 * @param <T>
 *
 * @author Pawe&#x142; Drzazga
 * @version 1.0
 */
public class DynamicArray<T> implements Iterable<T>{

    /**
     * An array to store elements of type T. Main data container.
     */
    protected T[] data;

    /**
     * Constructs a new instance of the DynamicArray class with the default size of 0.
     */
    @SuppressWarnings("unchecked")
    public DynamicArray(){
        data = (T[]) new Object[0];
    }

    /**
     * Constructs a new instance of the DynamicArray class with specified size.
     * @param size A specified size of new DynamicArray object.
     */
    @SuppressWarnings("unchecked")
    public DynamicArray(int size){
        data = (T[]) new Object[size];
    }

    /**
     * Constructs a new instance of the DynamicArray class initialized with elements from another DynamicArray object.
     * @param dynamicArray Another DynamicArray object whose elements will be copied.
     */
    public DynamicArray(DynamicArray<T> dynamicArray){
        data = dynamicArray.data.clone();
    }

    /**
     * Returns an iterator over the elements in this DynamicArray in proper sequence.
     * @return An iterator over the elements in this DynamicArray in proper sequence.
     */
    @Override
    public @NotNull Iterator<T> iterator() {
        return new ArrayIterator();
    }

    /**
     * Indicates whether some other object is "equal to" this DynamicArray.
     * @param o The reference object with which to compare.
     * @return True if this DynamicArray is the same as the object argument, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DynamicArray<?> that = (DynamicArray<?>) o;
        return Objects.deepEquals(data, that.data);
    }

    /**
     * Returns the hash code value for this DynamicArray.
     * @return The hash code value for this DynamicArray.
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    /**
     * Inner class implementing the Iterator interface for iterating over elements in the DynamicArray.
     */
    private class ArrayIterator implements Iterator<T> {
        private int currentIndex = 0;

        /**
         * Returns true if the iteration has more elements.
         * @return True if the iteration has more elements, false otherwise.
         */
        @Override
        public boolean hasNext() {
            return currentIndex < data.length;
        }

        /**
         * Returns the next element in the iteration.
         * @return The next element in the iteration.
         */
        @Override
        public T next() {
            return data[currentIndex++];
        }
    }

    /**
     * Sets the value at the specified index.
     * @param index The index at which to set the value.
     * @param value The value to be stored at the specified index.
     */
    public void setValue(int index, T value){
        data[index] = value;
    }

    /**
     * Sets the value at the specified index. If the index exceeds the current size of the DynamicArray,
     * the DynamicArray will resize to accommodate the new value.
     * @param index The index at which to set the value.
     * @param value The value to be stored at the specified index.
     */
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

    /**
     * Returns a sequential {@link Stream} with the elements of this collection.
     *
     * @return a sequential stream of elements from this collection
     */
    public Stream<T> stream(){
        return Arrays.stream(data).sequential();
    }

    /**
     * Retrieves the value at the specified index.
     * @param index The index from which to retrieve the value.
     * @return The value at the specified index.
     */
    public Optional<T> getValue(int index)
    {
        return Optional.ofNullable(data[index]);
    }

    /**
     * Retrieves the current size of the DynamicArray.
     * @return the number of elements currently stored in the DynamicArray.
     */
    public int getSize(){
        return data.length;
    }

    /**
     * Checks whether the DynamicArray contains the specified object.
     * @param value The object to be checked for presence in the DynamicArray.
     * @return True if the DynamicArray contains the specified object, otherwise false.
     */
    public boolean contains(T value){
        if(value == null) throw new NullPointerException("Cannot invoke \"DynamicArray.contains(Object)\" because \"value\" is null");
        for (T val : data) {
            if (val != null && val.equals(value)) return true;
        }
        return false;
    }
}
