package org.project.model.crossword.structures;

import java.util.Optional;

/**
 * Class that represent adjustable matrix structure. It's extends DynamicArray class.
 * It uses the same main array and assigns positions for rows and columns based on the array's size,
 * calculating these positions accordingly.
 * @param <T>
 *
 * @author Pawe&#x142; Drzazga
 * @version 1.0
 */
public class DynamicMatrix<T> extends DynamicArray<T> {

    /**
     * Represents the number of rows in the DynamicMatrix.
     */
    private int numberOfRows;

    /**
     * Represents the number of columns in the DynamicMatrix.
     */
    private int numberOfColumns;

    /**
     * Constructs a new instance of the DynamicMatrix class with the default size of 0.
     */
    public DynamicMatrix(){
        super();
        this.numberOfRows = 0;
        this.numberOfColumns = 0;
    }

    /**
     * Constructs a new instance of the DynamicMatrix class with specified row and column number .
     * @param rows A specified number of rows.
     * @param columns A specified number of columns.
     */
    public DynamicMatrix(int rows, int columns){
        super(rows*columns);
        this.numberOfRows = rows;
        this.numberOfColumns = columns;
    }

    /**
     * Constructs a new instance of the DynamicMatrix class initialized with elements from another DynamicMatrix object.
     * @param dynamicMatrix Another DynamicArray object whose elements will be copied.
     */
    public DynamicMatrix(DynamicMatrix<T> dynamicMatrix){
        super(dynamicMatrix);
        this.numberOfRows = dynamicMatrix.getNumberOfRows();
        this.numberOfColumns = dynamicMatrix.getNumberOfColumns();
    }

    /**
     * Retrieves the value at the specified row and column index.
     * @param rowIndex The row index from witch to retrieve the value.
     * @param columnIndex The column index from witch to retrieve the value.
     * @return The value at the specified row and column index.
     */
    public Optional<T> getValue(int rowIndex, int columnIndex) {
        return Optional.ofNullable(data[calculateIndex(rowIndex, columnIndex)]);
    }

    /**
     * Sets the value at the specified index.
     * @param rowIndex The row index at which to set the value.
     * @param columnIndex The column index at which to set the value.
     * @param value The value to be stored at the specified index.
     */
    public void setValue(int rowIndex, int columnIndex, T value) {
        data[calculateIndex(rowIndex, columnIndex)] = value;
    }

    /**
     * Retrieves the value at the specified row and column index.
     * @param rowIndex The row index from which to retrieve the value.
     * @param columnIndex The column index from which to retrieve the value.
     * @return An Optional containing the value at the specified row and column index if present, otherwise empty.
     */
    public Optional<T> getValueIfInBounds(int rowIndex, int columnIndex){
        if(rowIndex < 0 || columnIndex < 0)
            return Optional.empty();
        if(rowIndex >= numberOfRows || columnIndex >= numberOfColumns)
            return Optional.empty();
        return getValue(rowIndex, columnIndex);
    }

    /**
     * Sets the value at the specified index. If the index exceeds the current size of the DynamicMatrix,
     * the DynamicMatrix will resize to accommodate the new value.
     * @param rowIndex The row index at which to set the value.
     * @param columnIndex The column index at which to set the value.
     * @param value The value to be stored at the specified index.
     */
    @SuppressWarnings("unchecked")
    public void setValueOutOfBounds(int rowIndex, int columnIndex, T value){
        if(rowIndex >= 0 && columnIndex >= 0 && rowIndex < numberOfRows && columnIndex < numberOfColumns) {
            setValue(rowIndex, columnIndex, value);
            return;
        }

        int newNumberOfColumns, newNumberOfRows;
        int rowShift = 0;
        int columnShift = 0;

        if(rowIndex < 0 && columnIndex < 0){
            columnShift = Math.abs(columnIndex);
            rowShift = Math.abs(rowIndex);
            if(numberOfColumns == 0 || numberOfRows == 0){
                newNumberOfRows = numberOfRows + rowShift + 1;
                newNumberOfColumns = numberOfColumns + columnShift + 1;
            }else{
                newNumberOfRows = numberOfRows + rowShift;
                newNumberOfColumns = numberOfColumns + columnShift;
            }
        }else if(columnIndex < 0){
            columnShift = Math.abs(columnIndex);
            if(numberOfColumns == 0 ){
                newNumberOfColumns = numberOfColumns + columnShift + 1;
            }else{
                newNumberOfColumns = numberOfColumns + columnShift;
            }
            newNumberOfRows = Math.max(rowIndex + 1, numberOfRows);
        }else if(rowIndex < 0){
            rowShift = Math.abs(rowIndex);
            if(numberOfRows == 0 ){
                newNumberOfRows = numberOfRows + rowShift + 1;
                rowShift++;
            }else{
                newNumberOfRows = numberOfRows + rowShift;
            }
            newNumberOfColumns = Math.max(columnIndex + 1, numberOfColumns);
        }else{
            newNumberOfRows = Math.max(rowIndex + 1, numberOfRows);
            newNumberOfColumns = Math.max(columnIndex + 1, numberOfColumns);
        }
        T[] tempData = (T[]) new Object[newNumberOfRows * newNumberOfColumns];
        for(int i = 0; i < data.length; i++){
            int tempRowIndex = calculateRowIndex(i);
            int tempColumnIndex = calculateColumnIndex(i);
            tempData[calculateIndexWithShift(tempRowIndex + rowShift, tempColumnIndex + columnShift, newNumberOfColumns)]
                    = data[calculateIndex(tempRowIndex, tempColumnIndex)];
        }
        data = tempData;
        numberOfRows = newNumberOfRows;
        numberOfColumns = newNumberOfColumns;
        data[calculateIndex(rowIndex + rowShift, columnIndex + columnShift)] = value;
    }

    /**
     * Retrieves the number of rows in the DynamicMatrix.
     * @return The number of rows.
     */
    public int getNumberOfRows(){
        return numberOfRows;
    }

    /**
     * Retrieves the number of columns in the DynamicMatrix.
     * @return The number of columns.
     */
    public int getNumberOfColumns(){
        return numberOfColumns;
    }

    /**
     * Checks if the specified index is the last index in its row.
     * @param index The index to check.
     * @return True if the index is the last index in its row, otherwise false.
     */
    public boolean isLastIndexInRow(int index) {
        if(numberOfColumns == 0) return false;
        return (index + 1) % numberOfColumns == 0;
    }

    /**
     * Calculates the linear index for the given row and column cords.
     * @param rowIndex The row index.
     * @param columnIndex The column index.
     * @return The calculated linear index.
     */
    public int calculateIndex(int rowIndex, int columnIndex){
        return rowIndex * numberOfColumns + columnIndex;
    }

    /**
     * Calculates the linear index for the given row and column cords, considering a new number of columns.
     * @param rowIndex The row index.
     * @param columnIndex The column index.
     * @param newNumberOfColumns The new number of columns to consider.
     * @return The calculated linear index.
     */
    private int calculateIndexWithShift(int rowIndex, int columnIndex, int newNumberOfColumns){
        return rowIndex * newNumberOfColumns + columnIndex;
    }

    /**
     * Calculates the row index for the given linear index.
     * @param index The linear index.
     * @return The calculated row index.
     */
    public int calculateRowIndex(int index) {
        if(numberOfColumns == 0) return 0;
        return index / numberOfColumns;
    }

    /**
     * Calculates the column index for the given linear index.
     * @param index The linear index.
     * @return The calculated column index.
     */
    public int calculateColumnIndex(int index) {
        if(numberOfColumns == 0) return 0;
        return index % numberOfColumns;
    }

    /**
     * Retrieves the index of the row following the given index, if it exists.
     * @param index The current index.
     * @return An Optional containing the index of the next row if it exists, otherwise empty.
     */
    public Optional<Integer> nextRow(int index) {
        int nextRowIndex = index + numberOfColumns;
        return nextRowIndex < (numberOfRows * numberOfColumns) ? Optional.of(nextRowIndex) : Optional.empty();
    }

    /**
     * Retrieves the index of the column following the given index, if it exists.
     * @param index The current index.
     * @return An Optional containing the index of the next column if it exists, otherwise empty.
     */
    public Optional<Integer> nextColumn(int index) {
        int nextColumnIndex = index + 1;
        return nextColumnIndex % numberOfColumns != 0 ? Optional.of(nextColumnIndex) : Optional.empty();
    }

    /**
     * Retrieves the index of the row preceding the given index, if it exists.
     * @param index The current index.
     * @return An Optional containing the index of the previous row if it exists, otherwise empty.
     */
    public Optional<Integer> prevRow(int index) {
        int prevRowIndex = index - numberOfColumns;
        return prevRowIndex >= 0 ? Optional.of(prevRowIndex) : Optional.empty();
    }

    /**
     * Retrieves the index of the column preceding the given index, if it exists.
     * @param index The current index.
     * @return An Optional containing the index of the previous column if it exists, otherwise empty.
     */
    public Optional<Integer> prevColumn(int index) {
        int prevColumnIndex = index - 1;
        return prevColumnIndex >= 0 && prevColumnIndex % numberOfColumns != numberOfColumns - 1 ? Optional.of(prevColumnIndex) : Optional.empty();
    }
}
