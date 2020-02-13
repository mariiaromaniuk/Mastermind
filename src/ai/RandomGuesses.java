package ai;

import common.*;
import game.ControlInterface;


// A "solving algorithm" that makes random guesses
public class RandomGuesses implements SolvingAlgorithm{

    // The ControlInterface to work with
    private ControlInterface ci;

    // Number of digits in the combination
    private int width;

    // Quantity of colors to choose from
    private int colQuant;

    // Allowance of same colors
    private boolean duplicates;

    // The colors that are "allowed" in the game
    Color[] allowedCols;

    // All colors available in the game
    Color[] allCols;

    /*
     * Initialize the AI with settings from the Mastermind engine
     * ci - a control interface the AI will use to
     * interact with a game
     */
    public RandomGuesses(ControlInterface ci){
        this.ci = ci;
        width = ci.getSettingWidth();
        colQuant = ci.getSettingColQuant();
        duplicates = ci.getSettingDoubleCols();

        // Prepare values to fit numQuant rule
        allowedCols = new Color[colQuant];
        allCols = Color.values();
        System.arraycopy(allCols, 0, allowedCols, 0, colQuant);
    }

    /*
     * Do a full guess on the Mastermind engine
     * This includes to generate a guess, pass it to the engine and do a full game turn
     *
     * return:
     * -1 = Game ended and code was not broken
     *  1 = Game ended an code was broken
     *  0 = Just a normal turn or the game already ended
     *  see ControlInterface.turn()
     */
    public int makeGuess(){
        Row tmp = generateGuess();
        ci.writeToGameField(tmp.getColors());
        return ci.turn();
    }

    /*
     * Generating a totally random guess ignoring previous results
     * return: generated guess
     */
    public Row generateGuess(){
        Row guess = new Row(width);

        int i = 0;
        while (i < width) {
            Color now = allowedCols[(int) (Math.random() * colQuant)];
            if (guess.containsCol(now) > 0) {
                if (duplicates == true) {
                    guess.setColAtPos(i++, now);
                }
            } else {
                guess.setColAtPos(i++, now);
            }
        }
        return guess;
    }
}
