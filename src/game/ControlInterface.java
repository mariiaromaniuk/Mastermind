package game;

import common.Color;
import common.Debug;
import common.Row;

import java.io.*;


/*
 * The main interface to control the game flow.
 * This class provides all functions for a game.
 * This is the only public class in the game package.
 * This design is used to provide a single interface for a frontend
 * (or an AI) which guarantees a correct and save execution.
 */
public class ControlInterface {
    // Class variables
    // Always create a game first
    private Game game = new Game(new Settings());

    // The file extension for saving
    public final String FILE_EXTENSION = "mm";

    // Identifier for loaded games
    private boolean loaded = false;

    // Getter for the active Row number
    // return: the active Row number
    public int getActiveRowNumber() {
        return game.gameField.getActiveRowNumber();
    }

    /*
     * Do a full game turn.
     * This includes to check the guess, increment the active Row number,
     * set the result pegs and return the game state.
     *
     * return:
     * -1 = Game ended and code was not broken
     *  1 = Game ended an code was broken
     *  0 = Just a normal turn or game already ended
     */
    public int turn() {
        if (getGameEnded() == false) {
            return game.turn();
        }
        return 0;
    }

    /*
     * Set a Row of colors to the active game Row
     * colors - the colors to set as active game Row
     */
    public void writeToGameField(Color[] colors) {
        game.gameField.setRow(new Row(colors));
    }

    /*
     * Get the newest results
     * return: the last game result
     * If there is no result null will be returned
     */
    public Row getLastResultRow() {
        if (game.gameField.getActiveRowNumber() > 0) {
            return game.gameField.getResult();
        }
        return null;
    }

    /*
     * Get a specific result Row
     * row - the Row number
     * return: the specified result Row
     * If the row does not exist, null will be returned
     */
    public Row getResultRow(int row) {
        if (row >= 0 && row < game.settings.getMaxTries()) {
            return game.gameField.getResult(row);
        }
        return null;
    }

    /*
     * Get a specific game Row
     * row - the Row number
     * return: the specified game Row
     * If the row does not exist, null will be returned
     */
    public Row getGameFieldRow(int row) {
        if (row >= 0 && row < game.settings.getMaxTries()) {
            return game.gameField.getRow(row);
        }
        return null;
    }

    /*
     * Get the secret code
     * return: the secret code
     */
    public Row getSecretCode() {
        return game.secretCode.getCode();
    }

    /*
     * Set the secret code
     * colors - the secret code to set
     */
    public void setSecretCode(Color[] colors) {
        game.secretCode.setCode(new Row(colors));
        common.Debug.dbgPrint("Secret code is set to: " +
                game.secretCode.getCode());
    }

    /*
     * Getter for the color quantity
     * The quantity of different colors to choose from
     * return: the quantity of colors in the game
     */
    public int getSettingColQuant() {
        return game.settings.getColQuant();
    }

    /*
     * Setter for the colors quantity
     * If there are too less colors to fill a row
     * with different colors (if double colors are not allowed)
     * nothing will be set.
     * quant - quantity of colors in the game. (Between 1 and 10).
     */
    public void setSettingColQuant(int quant) {
        if(quant > 0 && quant <= 10 && (game.settings.getWidth() <= quant ||
                game.settings.getDoubleCols())) {
            game.settings.setColQuant(quant);
        }
    }

    /*
     * Getter for the game width
     * This equals the length of a guess/result Row
     * return - the width/length of an guess/result Row
     */
    public int getSettingWidth() {
        return game.settings.getWidth();
    }

    /*
     * Setter for the game width
     * This equals the length of a guess/result Row
     * If the width is too big to fill a Row
     * with different colors (if double colors are not allowed)
     * nothing will be set.
     * width - the width of a guess Row. (Between 1 and 8)
     */
    public void setSettingWidth(int width) {
        if(width > 0 && width <= 8 && (game.settings.getColQuant() >= width ||
                game.settings.getDoubleCols())) {
            game.settings.setWidth(width);
        }
    }

    /*
     * Setter for max. number of tries (guesses)
     * max - max. number of tries / guesses
     * Between 1 and Integer.MAX_VALUE
     */
    public void setSettingMaxTries(int max) {
        if (max > 0 && max <= Integer.MAX_VALUE) {
            game.settings.setMaxTries(max);
        }
    }

    /*
     * Getter for max. number of tries (guesses)
     * return: the max. number of tries
     */
    public int getSettingMaxTries() {
        return game.settings.getMaxTries();
    }

    /*
     * Setter for duplicate colors allowance
     * Allowance to place same colors in one Row
     * allow - allow duplicate colors
     */
    public void setSettingDoubleCols(boolean allow) {
        game.settings.setDoubleCols(allow);
    }

    /*
     * Getter for duplicate colors allowance
     * return: true if duplicate colors are allowed, false otherwise
     */
    public boolean getSettingDoubleCols() {
        return game.settings.getDoubleCols();
    }

    /*
     * Getter for AI mode
     * true for AI = Codebreaker, human = Codemaker
     * false for AI = Codemaker, human = Codebreaker
     */
    public boolean getSettingAiMode() {
        return game.settings.getAiMode();
    }

    public boolean getSettingDoublePlayerMode() {
        return game.settings.getDoublePlayerMode();
    }

    public void setSettingDoublePlayerMode(boolean status) {
        game.settings.setDoublePlayerMode(status);
    }

    /*
     * Setter for AI mode
     * true for AI = Codebreaker, human = Codemaker
     * false for AI = Codemaker, human = Codebreaker
     */
    public void setSettingAiMode(boolean status) {
        game.settings.setAiMode(status);
    }

    // Start a new game
    public void newGame() {
        Debug.dbgPrint("New game started");
        game = new Game(game.settings);
    }

    /*
     * Getter for loaded game state
     * return: true if the game is a loaded game, false otherwise
     */
    public boolean getLoaded() {
        return loaded;
    }

    /*
     * Getter for game ended state
     * return: true if the game has ended, false otherwise
     */
    public boolean getGameEnded(){
        return game.gameEnded;
    }

    /*
     * Save the game with settings as "save_game" file
     *
     * throws FileNotFoundException
     * throws SecurityException
     * throws IOException
     */
    public void save()
            throws FileNotFoundException, SecurityException, IOException {
        save("save_game." + FILE_EXTENSION);
    }

    /*
     * Save the game with settings to a specific path/filename.
     *
     * fileName - path and filename.
     * throws FileNotFoundException
     * throws SecurityException
     * throws IOException
     */
    public void save(String fileName)
            throws FileNotFoundException, SecurityException, IOException{
        // Create output streams
        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        // Write object
        oos.writeObject(game);
        oos.close();
    }

    /*
     * Load a game from "save_game" file
     *
     * throws FileNotFoundException
     * throws SecurityException
     * throws IOException
     * throws ClassNotFoundException
     */
    public void load() throws FileNotFoundException, SecurityException,
            IOException, ClassNotFoundException {
        load("save_game." + FILE_EXTENSION);
    }

    /*
     * Load a game from a specific path/filename
     *
     * fileName - path and filename
     * throws FileNotFoundException
     * throws SecurityException
     * throws IOException
     * throws ClassNotFoundException
     */
    public void load(String fileName)throws FileNotFoundException,
            SecurityException, IOException, ClassNotFoundException{
        // Create input streams
        FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);

        // Read object
        Game mygame = (Game) ois.readObject();
        ois.close();
        game = mygame;
        loaded = true;
    }
}
