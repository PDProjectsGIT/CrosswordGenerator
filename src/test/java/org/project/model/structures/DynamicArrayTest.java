package org.project.model.structures;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.project.model.crossword.structures.DynamicArray;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DynamicArrayTest {

    @ParameterizedTest
    @MethodSource("getDataForSetValueOutOfBoundsTest")
    public void setValueOutOfBoundsTest(int[] values){
        DynamicArray<Integer> dynamicArray = new DynamicArray<>();
        for(int i = 0; i < values.length; i++){
            dynamicArray.setValueOutOfBounds(i, values[i]);
        }
        assertEquals(values.length, dynamicArray.getSize());
    }

    private static Stream<int[]> getDataForSetValueOutOfBoundsTest() {
        return Stream.of(
                new int[]{1,2,3,4,5,6,7,8,9,10},
                new int[]{-1},
                new int[]{}
        );
    }

    @ParameterizedTest
    @CsvSource({
            "0, 1",
            "1, 2",
            "-1, 2",
            "-4, 5"
    })
    public void setValueOutOfBoundsTest(int index, int expectedSize){
        DynamicArray<Integer> dynamicArray = new DynamicArray<>();
        dynamicArray.setValueOutOfBounds(index, 0);
        assertEquals(dynamicArray.getSize(), expectedSize);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 1, false",
            "-1, 0, true",
            "10, 11, false",
            "0, 0, true"
    })
    public void setValueInBoundsTest(int index, int size, boolean expectedException){
        DynamicArray<Integer> dynamicArray = new DynamicArray<>(size);
        if(expectedException){
            assertThrows(IndexOutOfBoundsException.class, () -> {
                dynamicArray.setValue(index, 1);
            });
        }else{
            assertDoesNotThrow(() -> {
                dynamicArray.setValue(index, 1);
            });
        }

    }

    @ParameterizedTest
    @CsvSource({
            "0, 0, true",
            "0, -1, false",
            ",5,false",
            "0,,false"

    })
    public void containsTest(Integer value, Integer valueToCheck, boolean excepted){
        DynamicArray<Integer> dynamicArray = new DynamicArray<>();
        dynamicArray.setValueOutOfBounds(5, value);
        if(valueToCheck == null){
            assertThrows(NullPointerException.class, ()->{
                dynamicArray.contains(valueToCheck);
            });
        }else{
            dynamicArray.setValueOutOfBounds(5, value);
            assertEquals(excepted, dynamicArray.contains(valueToCheck));
        }

    }

    @ParameterizedTest
    @CsvSource({
            "1, 1, 0, 0, true, ",
            "1, 1, 0, -1, false", // second should be bigger
            "0,,0,0, false",
            ",,2,2, true",
            ",,-2,-2, true",
            ",,-2,-1, false",

    })
    public void equalsTest(Integer valueA, Integer valueB, int indexA, int indexB, boolean excepted){
        DynamicArray<Integer> dynamicArrayA = new DynamicArray<>();
        dynamicArrayA.setValueOutOfBounds(indexA, valueA);
        DynamicArray<Integer> dynamicArrayB = new DynamicArray<>();
        dynamicArrayB.setValueOutOfBounds(indexB, valueB);
        assertEquals(excepted, dynamicArrayA.equals(dynamicArrayB));
    }

}