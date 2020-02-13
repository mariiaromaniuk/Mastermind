package game;

import common.*;
import java.io.Serializable;


// Game-engine class. Controls the game flow
class Game implements Serializable {
    /*
     * Object variables
     * All protected so that the control interface
     * can use the getters and setters
     */
    protected boolean gameEnded = false;
    protected GameField gameField;
    protected SecretCode secretCode;
    protected Settings settings;

    /*
     * Constructor with init instructions for new game.
     * This constructor initializes the Game class with a new gameField and
     * and a new, random secretCode.
     *
     * s - settings for the new game.
     */
    public Game(Settings s) {
        settings = s;
        gameField = new GameField(settings.getWidth(), settings.getMaxTries());
        secretCode = new SecretCode(settings.getColQuant(), settings.getWidth(),
                settings.getDoubleCols());
    }

    /*
     * Aggregates the logic for a game turn.
     * return: 1 for win; -1 for lose; 0 for normal turn
     */
    public int turn() {

        Row result = checkActiveRow();
        gameField.incActiveRowNumber();

        if (result.containsCol(Color.Black) == result.width()) {
            Debug.dbgPrint("Code was broken");
            gameEnded = true;
            return 1;
        }
        if (gameField.getActiveRowNumber() >= settings.getMaxTries()) {
            Debug.dbgPrint("Code was not broken!");
            gameEnded = true;
            return -1;
        }
        return 0;
    }

    /*
     * Checks the active game Row
     * return: the result Row
     * Black = correct (the player had guessed a correct number and its correct location)
     * White = exists (the player had guess a correct number)
     */
    private Row checkActiveRow() {
        // Create real copies
        Color [] row = new Color[settings.getWidth()];
        System.arraycopy(gameField.getRow().getColors(), 0, row, 0,
                settings.getWidth());
        Color [] secretRow = new Color[settings.getWidth()];
        System.arraycopy(secretCode.getCode().getColors(), 0, secretRow, 0,
                settings.getWidth());

        Row result = new Row(row.length);
        int index = 0;
        // Check "correct"-colors first
        // for a result like "black, black, white, null"
        for (int i = 0; i < row.length; i++) {
            if (row[i] == secretRow[i]) {
                // Right color at right position (correct)
                result.setColAtPos(index++, Color.Black);
                // Mark color as checked
                secretRow[i] = null;
                row[i] = null;
            }
        }
        // Now check "exists"-colors
        outer: for (int i = 0; i < row.length; i++) {
            if (row[i] != null) {
                for (int j = 0; j < row.length; j++) {
                    if (secretRow[j] != null && row[i] == secretRow[j]) {
                        // Right color but wrong position (exists)
                        result.setColAtPos(index++, Color.White);
                        // Mark color as checked
                        secretRow[j] = null;
                        continue outer;
                    }
                }
            }
        }
        Debug.dbgPrint("checkActiveRow: " + result);
        gameField.setResult(result);
        return result;
    }
}
