package game;

import common.Row;
import java.io.Serializable;


/*
 * The actual game field.
 * This includes the guess rows and the result rows.
 * The active row number is stored here also.
 *
 * The Java Serializable interface (java.io.Serializable is a marker
 * interface class must implement if it is to be serialized and de-serialized.
 * Java object serialization (writing) is done with the ObjectOutputStream
 * and deserialization (reading) is done with the ObjectInputStream.
 */

class GameField implements Serializable {
    // Object variables
    private int activeRowNumber = 0;
    private Row[] gameField;
    private Row[] resultField;

    /*
     * Constructor which init the game field with width and max tries
     * numColors - number of colors per Row
     * maxTries - maximum number of tries
     */
    public GameField(int numColors, int maxTries) {
        gameField = new Row[maxTries];
        for (int i = 0; i < gameField.length; i++) {
            gameField[i] = new Row(numColors);
        }
        resultField = new Row[maxTries];
    }

    /*
     * Setter - saves the result in the active Row of the result field
     * result - result to set
     */
    public void setResult(Row result) {
        resultField[activeRowNumber] = result;
    }

    /*
     * Getter for the active Row number
     * return: the active Row number
     */
    public int getActiveRowNumber() {
        return activeRowNumber;
    }

    /*
     * Getter for active (last) result Row
     * return: the last result
     * If there is no last result, null will be returned
     */
    public Row getResult() {
        if (activeRowNumber > 0) {
            return resultField[activeRowNumber - 1];
        }
        return null;
    }

    /*
     * Getter for a result Row
     * rowNumber - he number of the Row
     * return: the specified Row
     */
    public Row getResult(int rowNumber) {
        return resultField[rowNumber];
    }

    /*
     * Increments the active row number by one
     */
    public void incActiveRowNumber() {
        activeRowNumber++;
    }

    /*
     * Getter for the active Row
     * return: the active Row
     */
    public Row getRow() {
        return gameField[activeRowNumber];
    }

    /*
     * Getter for a game Row
     * rowNumber - the number of the game Row
     * return: the Row with the given number from the game field
     */
    public Row getRow(int rowNumber) {
        return gameField[rowNumber];
    }

    /*
     * Setter for a game Row
     * rowNumber - the number of the Row in the game field
     * row - the Row to set
     */
    public void setRow(int rowNumber, Row row) {
        gameField[rowNumber] = row;
    }

    /*
     * Setter for the active Row
     * The startup value for active Row number is 0
     * row - the Row to set as active row
     */
    public void setRow(Row row) {
        gameField[activeRowNumber] = row;
    }

    /*
     * Getter for the whole game field
     * return: the whole game field
     */
    public Row[] getField() {
        return gameField;
    }

    /*
     * Setter for the whole game field
     * field - the game field to set
     */
    public void setField(Row[] field) {
        gameField = field;
    }
}
