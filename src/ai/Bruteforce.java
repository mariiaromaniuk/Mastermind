package ai;

import common.*;
import game.ControlInterface;


/*
 * A solving algorithm using the Brute-force search technique
 * See http://en.wikipedia.org/wiki/Brute-force_search
 * Brute-force search algorithm on Wikipedia.
 */

public class Bruteforce implements SolvingAlgorithm {

    // The ControlInterface to work with
    private ControlInterface ci;

    // Holding the guess
    Row guess;

    // Width of the row
    private int width;

    // Quantity of colors to choose from
    private int colQuant;

    // Allowance of duplicates colors
    private boolean doubleCols;

    // Arrays of colors holding all colors (allCols)
    // and the colors used in the running game (allowedCols)
    Color[] allowedCols;
    Color[] allCols;

    // Initialize the AI with settings from the Mastermind engine
    public Bruteforce(ControlInterface ci){
        width = ci.getSettingWidth();
        colQuant = ci.getSettingColQuant();
        doubleCols = ci.getSettingDoubleCols();
        guess = new Row(width);
        // Prepare values to fit colorQuant-rule
        allowedCols = new Color[colQuant];
        allCols = Color.values();
        System.arraycopy(allCols, 0, allowedCols, 0, colQuant);
    }

    /*
     * Do a full guess on the Mastermind engine.
     * This includes to generate a guess, pass it to the engine and do a full game turn.
     *
     * return:
     * -1 = Game ended and code was not broken.
     *  1 = Game ended an code was broken.
     *  0 = Just a normal turn or game already ended.
     *  see ControlInterface#turn()
     */

    public int makeGuess(){
        Row tmp = generateGuess();
        Debug.dbgPrint("Code generation.");
        ci.writeToGameField(tmp.getColors());
        return ci.turn();
    }

    /*
     * Generate a guess by "incrementing" the previous guess.
     * If no previous guess available generate "lowest" possible guess.
     * Every color is given a value what makes it possible to generate
     * a "lowest" guess and to "increment" a guess.
     * Returns the generated guess
     */
    public Row generateGuess(){
        // First try
        int col = 0;
        if(ci.getActiveRowNumber() == 0){
            Debug.dbgPrint("First try.");
            // Fill guess with the first color(s)
            for(int i = 0; i < width; i++){
                guess.setColAtPos(i, allowedCols[col]);
                col = doubleCols ? 0 : col++;
            }
            return guess;
        }
        Debug.dbgPrint("Not first try.");
        // Later try
        boolean carry = true;
        Color[] tmp = guess.getColors();
        int index;
        for (int i = tmp.length-1; i >= 0; i--){
            if(!carry){
                break;
            }
            index = findColorIndex(allowedCols,tmp[i]);
            if (++index < allowedCols.length){
                tmp[i] = allowedCols[index];
                carry = false;
            }
            else {
                 tmp[i] = allowedCols[0];
                 carry = true;
            }
        }
        if(!doubleCols && guess.containsDoubleCols()){
            return generateGuess();
        }
        return guess;
    }

    /*
     * Search for a color in an array and return its index
     * ca - the array within we want to do a search
     * c - the color to be found
     * returns: index of the color, "-1" if not found
     */
    private int findColorIndex(Color[] ca, Color c){
        int i = 0;
        while(i < ca.length){
            if(ca[i] == c){
                return i;
            }
            i++;
        }
        return -1;
    }
}
