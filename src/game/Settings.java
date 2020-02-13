package game;

import java.io.Serializable;


/*
 * Contains all settings of the game
 */
class Settings implements Serializable {
    /*
     * Object variables representing the settings
     * Init with standard values

     * Maximum number of Tries
     * Default value is 10 (as per requirements)
    */
    private int maxTries = 10;

    /*
     * Number of digits in the secret code
     * Default value is 4 (as per requirements)
     */
    private int width = 4;

    /*
     * Quantity of colors to choose from
     * Default colQuant is 0-7 (as per requirements)
     */
    private int colQuant = 8;

    /*
     * Allowance of duplicate colors in the code
     * True by default
     */
    private boolean doubleCols = true;

    /*
     * Game mode
     * True = AI: Codebreaker, Human: Codemaker
     * False = AI: Codemaker, Human: Codebreaker
     * False by default
     */
    private boolean aiMode = false;

    /*
     * Getter for game mode
     * True = AI: Codebreaker, Human: Codemaker
     * False = AI: Codemaker, Human: Codebreaker
     */
    public boolean getAiMode() {
        return aiMode;
    }

    /*
     * Setter for game mode
     * True = AI: Codebreaker, Human: Codemaker
     * False = AI: Codemaker, Human: Codebreaker
     */
    public void setAiMode(boolean status) {
        aiMode = status;
    }

    /*
     * Getter for max. attempts (guesses)
     * return: max. number of tries
     */
    public int getMaxTries() {
        return maxTries;
    }

    /*
     * Setter for max. attempts (guesses)
     * tries - the max. number  of tries
     */
    public void setMaxTries(int tries) {
        maxTries = tries;
    }

    /*
     * Getter for the game width
     * return: the width of a Row
     */
    public int getWidth() {
        return width;
    }

    /*
     * Setter for the game width
     * width: the width for a Row
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /*
     * Getter for the quantity of colors used in the game
     * return: the quantity of colors
     */
    public int getColQuant() {
        return colQuant;
    }

    /*
     * Setter for the quantity of colors used in the game
     * colQuant - the quantity of colors
     */
    public void setColQuant(int colQuant) {
        this.colQuant = colQuant;
    }

    /*
     * Getter for duplicate colors allowance
     * return: true if duplicate colors are allowed, false otherwise
     */
    public boolean getDoubleCols() {
        return doubleCols;
    }

    /*
     * Setter for duplicate colors allowance
     * doubleCols - true for the allowance of duplicate colors, false otherwise
     */
    public void setDoubleCols(boolean doubleCols) {
        this.doubleCols = doubleCols;
    }
}
