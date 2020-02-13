package game;

import common.Color;
import common.Debug;
import common.Row;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;


/*
 * This class represents the secret code the player has to guess.
 */
class SecretCode implements Serializable {
    /*
     * Integer generator part of random.org API.
     * Provides methods for random integer numbers generating.
     * Generate count numbers between min and max inclusively.
     * Similar behavior to Arrays.shuffle()
     *
     * return: list of numbers
     * throws: IOException
     */


    // Parameters for query.
    // num = 4 and max = 7 assigned by default in Settings
    int min = 0;
    int col = 1;
    int base = 10;
    String format = "plain";
    String rnd = "new";

    public int[] generate(int num, int max) throws IOException {

     String query = "https://www.random.org/integers/?num=" + num
                + "&min=" + min
                + "&max=" + max
                + "&col=" + col
                + "&base=" + base
                + "&format=" + format
                + "&rnd=" + rnd;

        ArrayList<String> strings = HTTPUtils.get(query);
        int[] numbers = new int[num];

        for (int i = 0; i < strings.size(); i++) {
            numbers[i] = Integer.parseInt(strings.get(i));
        }
        return numbers;
    }


    // Translates randomly generated numbers into Color enums
    private Color translateToColor (int num) {
        Color ret = null;
        switch (num) {
            case 0:
                ret = Color.Yellow;
                break;
            case 1:
                ret = Color.LightBlue;
                break;
            case 2:
                ret = Color.LightGreen;
                break;
            case 3:
                ret = Color.LightRed;
                break;
            case 4:
                ret = Color.LightOrange;
                break;
            case 5:
                ret = Color.LightPurple;
                break;
            case 6:
                ret = Color.Orange;
                break;
            case 7:
                ret = Color.Olive;
                break;
            case 8:
                ret = Color.LightPink;
                break;
            case 9:
                ret = Color.Pink;
                break;
        }
        return ret;
    }


    // Object variables
    private Row secretCode;

    /*
     * Initializes a new secret code with the given parameters
     *
     * colQuant - number of different colors to choose from
     * width - width (number of pins) of the code
     * duplicates - allowance of same digits
     */
    public SecretCode(int colQuant, int width, boolean duplicates) {
        generateCode(colQuant, width, duplicates);
    }

    /*
     * Creates a secret code
     *
     * colQuant - number of different colors
     * width - width (number of pins) of the code
     * duplicates - allowance of same colors
     */
    private void generateCode(int colQuant, int width, boolean duplicates) {

        try {
            // Generate random numbers combination and save it in the array of integers
            // range is 8 (numbers from 0 to 7 inclusive)
            int[] code = generate(width, colQuant-1);
            secretCode = new Row(width);

            // Prepare values to fit colorQuant-rule
            Color[] values = new Color[colQuant];
            Color[] all = Color.values();
            System.arraycopy(all, 0, values, 0, colQuant);

            // Convert generated code of random numbers into colors
            int i = 0;
            while (i < width) {
                Color now = translateToColor(code[i]);
                if (secretCode.containsCol(now) > 0) {
                    if (duplicates == true) {
                        secretCode.setColAtPos(i++, now);
                    }
                } else {
                secretCode.setColAtPos(i++, now);
                }
            }
            Debug.dbgPrint("Secret code is: " + secretCode);

        } catch (IOException ex) {
            ex.getMessage();
        }

    }

    /*
     * Getter for the secret code
     * return: the secret code
     */
    public Row getCode() {
        return secretCode;
    }

    /*
     * Setter for the secret code
     * code - the Row to set as secret code
     * return: true if the code was set successfully, false otherwise
     */
    public boolean setCode(Row code) {
        if (secretCode.width() == code.width()) {
            secretCode = code;
            return true;
        }
        return false;
    }
}
