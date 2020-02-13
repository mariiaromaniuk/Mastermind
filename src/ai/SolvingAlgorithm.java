package ai;

import common.Row;


/*
 * This interface describes how a solving algorithm has to be implemented.
 * It has to be able to generate a valid guess. Normally based on previous guesses.
 * And it has to be able to actually make a guess interacting with the
 * ControlInterface.
 *
 * AI (artificial intelligence) can take up to several minutes, especially if you
 * have set a high width, many colors or when you run the game on a slow computer.
 * This is not a bug, just a side effect of the complex algorithm the AI is using.
 * While the AI is guessing, the GUI is locked. Please stand by until the AI broke
 * the code or the maximum number of tries is reached.
 *
 * see ControlInterface
 */

public interface SolvingAlgorithm {
    /*
     * This function creates a guess and does a full turn in the game
     * return: the state of the turn() function
     */
    int makeGuess();

    /*
     * Generate a guess and return it as a Row
     * return: the row that was guessed
     */
    Row generateGuess();
}