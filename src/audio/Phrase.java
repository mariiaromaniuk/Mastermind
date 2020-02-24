package audio;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;


// Contains all audio phrases in the program along with logic
// to map between Phrases & keyboard keys, numbers, etc.

public enum Phrase {
    // General: Blank or Empty Phrases
    BLANK(" "),
    EMPTY("EMPTY"),

    // General: Numeric Phrases
    ZERO("0"),
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),

    // General: Keyboard Key Phrases
    A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z,
    SHIFT, SLASH, BACKSLASH, ENTER, EQUALS, MINUS, SEMICOLON, COMMA, PERIOD, QUOTE, ESCAPE,
    UP, DOWN, LEFT, RIGHT, CONTROL, BACKSPACE, ALT,
    CAPS_LOCK("CAPS LOCK"), BACK_QUOTE("BACK QUOTE"),
    OPEN_BRACKET("OPEN BRACKET"), CLOSE_BRACKET("CLOSE BRACKET"),

    // General: Congratulations message
    CONGRATS("YOU'VE FINISHED THE GAME! CONGRATULATIONS!"),

    // General: Error Phrases
    UNRECOGNIZED_KEY("An unrecognized key was pressed on the keyboard:"),

    // Loader
    SPACE_FOR_EXIT("Press the SPACE BAR to exit."),
    PLAY_OR_EXIT("Would you like to play or exit? Use the arrow keys to make your selection."),
    EXITING("The game is exiting. Goodbye!"),
    COMPLETE_THE_GUESS("You didn't enter the whole guess yet!"),

    // Instructions Phrases
    INSTRUCTIONS_4("Welcome to Mastermind! The goal is to accurately guess a secret code of four numbers." +
            " To make a guess use the arrow keys to select a space and enter a number from 1-8. Once you have finalized" +
            " your guess, hit the space key to see if you guessed correctly. If you did not guess the right code, the" +
            " small box to the right of your guess will contain 4 pegs of either black or white. A white peg means that" +
            " one of the numbers you have guessed is correct, but it is in the wrong place. A black peg means that" +
            " one of your numbers is the correct number and is in the correct place. If you have guessed correctly," +
            " you win the game!"),

    INSTRUCTIONS_5("Welcome to Mastermind! The goal is to accurately guess a secret code of five numbers." +
            " To make a guess use the arrow keys to select a space and enter a number from 1-8. Once you have finalized" +
            " your guess, hit the space key to see if you guessed correctly. If you did not guess the right code, the" +
            " small box to the right of your guess will contain five pegs of either black or white. A white peg means that" +
            " one of the numbers you have guessed is correct, but it is in the wrong place. A black peg means that" +
            " one of your numbers is the correct number and is in the correct place. If you have guessed correctly," +
            " you win the game!"),

    INSTRUCTIONS_6("Welcome to Mastermind! The goal is to accurately guess a secret code of six numbers." +
            " To make a guess use the arrow keys to select a space and enter a number from 1-8. Once you have finalized" +
            " your guess, hit the space key to see if you guessed correctly. If you did not guess the right code, the" +
            " small box to the right of your guess will contain six pegs of either black or white. A white peg means that" +
            " one of the numbers you have guessed is correct, but it is in the wrong place. A black peg means that" +
            " one of your numbers is the correct number and is in the correct place. If you have guessed correctly," +
            " you win the game!"),

    // Information Phrases
    CURRENT_VALUE("The current number in this box is"),
    PLACED_NUM("You have placed the number"),
    REMOVED_NUM("You have removed the number"),

    PLACED_CODE("You have placed the code:"),
    GUESS_NUMBER("It is guess number:"),
    NUMBER_CORRECT_POSITION("Pegs with the correct position and number:"),
    NUMBER_ONLY("Pegs with the correct number:");

    // The directory for all of the Phrase audio files ("resources/phrases" folder under the root of the project).
    public static final Path PHRASE_FILES_DIRECTORY = Paths.get(
            System.getProperty("user.dir"), "../Mastermind/resources/phrases/"
    );

    // The Phrase's String value, which is the phrase that needs to be fetched via the Google Cloud API.
    private final String phraseValue;

    /*
     * Creates a new Phrase
     * When no phrase value is passed, just set the phrase value to the name of the enumeration member.
     */
    Phrase() {
        this.phraseValue = this.name();
    }

    /*
     * Creates a new Phrase
     * phraseValue - the phrase value to set to the given enumeration member.
     */
    Phrase(String phraseValue) {
        this.phraseValue = phraseValue;
    }

    /*
     * Converts a key code on the keyboard to a Phrase. If no mapping exists, return Phrase.EMPTY.
     *
     * keyCode - the key code on the keyboard to convert.
     * return: a Phrase that represents the passed key code from the keyboard.
     */
    public static Phrase keyCodeToPhrase(int keyCode) {
        @SuppressWarnings("JavacQuirks") final Map<Integer, Phrase> KEY_CODE_TO_PHRASE = ofEntries(
                // Numeric keyboard values
                entry(KeyEvent.VK_0, Phrase.ZERO),
                entry(KeyEvent.VK_1, Phrase.ONE),
                entry(KeyEvent.VK_2, Phrase.TWO),
                entry(KeyEvent.VK_3, Phrase.THREE),
                entry(KeyEvent.VK_4, Phrase.FOUR),
                entry(KeyEvent.VK_5, Phrase.FIVE),
                entry(KeyEvent.VK_6, Phrase.SIX),
                entry(KeyEvent.VK_7, Phrase.SEVEN),
                entry(KeyEvent.VK_8, Phrase.EIGHT),
                entry(KeyEvent.VK_9, Phrase.NINE),

                // Numeric numpad keyboard values
                entry(KeyEvent.VK_NUMPAD0, Phrase.ZERO),
                entry(KeyEvent.VK_NUMPAD1, Phrase.ONE),
                entry(KeyEvent.VK_NUMPAD2, Phrase.TWO),
                entry(KeyEvent.VK_NUMPAD3, Phrase.THREE),
                entry(KeyEvent.VK_NUMPAD4, Phrase.FOUR),
                entry(KeyEvent.VK_NUMPAD5, Phrase.FIVE),
                entry(KeyEvent.VK_NUMPAD6, Phrase.SIX),
                entry(KeyEvent.VK_NUMPAD7, Phrase.SEVEN),
                entry(KeyEvent.VK_NUMPAD8, Phrase.EIGHT),
                entry(KeyEvent.VK_NUMPAD9, Phrase.NINE),

                // Alphabetic keyboard values
                entry(KeyEvent.VK_A, Phrase.A),
                entry(KeyEvent.VK_B, Phrase.B),
                entry(KeyEvent.VK_C, Phrase.C),
                entry(KeyEvent.VK_D, Phrase.D),
                entry(KeyEvent.VK_E, Phrase.E),
                entry(KeyEvent.VK_F, Phrase.F),
                entry(KeyEvent.VK_G, Phrase.G),
                entry(KeyEvent.VK_H, Phrase.H),
                entry(KeyEvent.VK_I, Phrase.I),
                entry(KeyEvent.VK_J, Phrase.J),
                entry(KeyEvent.VK_K, Phrase.K),
                entry(KeyEvent.VK_L, Phrase.L),
                entry(KeyEvent.VK_M, Phrase.M),
                entry(KeyEvent.VK_N, Phrase.N),
                entry(KeyEvent.VK_O, Phrase.O),
                entry(KeyEvent.VK_P, Phrase.P),
                entry(KeyEvent.VK_Q, Phrase.Q),
                entry(KeyEvent.VK_R, Phrase.R),
                entry(KeyEvent.VK_S, Phrase.S),
                entry(KeyEvent.VK_T, Phrase.T),
                entry(KeyEvent.VK_U, Phrase.U),
                entry(KeyEvent.VK_V, Phrase.V),
                entry(KeyEvent.VK_W, Phrase.W),
                entry(KeyEvent.VK_X, Phrase.X),
                entry(KeyEvent.VK_Y, Phrase.Y),
                entry(KeyEvent.VK_Z, Phrase.Z),

                // Arrow key keyboard values
                entry(KeyEvent.VK_UP, Phrase.UP),
                entry(KeyEvent.VK_DOWN, Phrase.DOWN),
                entry(KeyEvent.VK_LEFT, Phrase.LEFT),
                entry(KeyEvent.VK_RIGHT, Phrase.RIGHT),

                // Other keyboard values
                entry(KeyEvent.VK_SHIFT, Phrase.SHIFT),
                entry(KeyEvent.VK_SLASH, Phrase.SLASH),
                entry(KeyEvent.VK_BACK_SLASH, Phrase.BACKSLASH),
                entry(KeyEvent.VK_ENTER, Phrase.ENTER),
                entry(KeyEvent.VK_EQUALS, Phrase.EQUALS),
                entry(KeyEvent.VK_MINUS, Phrase.MINUS),
                entry(KeyEvent.VK_SEMICOLON, Phrase.SEMICOLON),
                entry(KeyEvent.VK_COMMA, Phrase.COMMA),
                entry(KeyEvent.VK_PERIOD, Phrase.PERIOD),
                entry(KeyEvent.VK_QUOTE, Phrase.QUOTE),
                entry(KeyEvent.VK_CAPS_LOCK, Phrase.CAPS_LOCK),
                entry(KeyEvent.VK_CONTROL, Phrase.CONTROL),
                entry(KeyEvent.VK_BACK_SPACE, Phrase.BACKSPACE),
                entry(KeyEvent.VK_OPEN_BRACKET, Phrase.OPEN_BRACKET),
                entry(KeyEvent.VK_CLOSE_BRACKET, Phrase.CLOSE_BRACKET),
                entry(KeyEvent.VK_BACK_QUOTE, Phrase.BACK_QUOTE),
                entry(KeyEvent.VK_ESCAPE, Phrase.ESCAPE),
                entry(KeyEvent.VK_ALT, Phrase.ALT)
        );

        // Return just an empty Phrase to prevent a null ptr exception (calls audio file that contains no sound).
        return Objects.requireNonNullElse(KEY_CODE_TO_PHRASE.get(keyCode), Phrase.BLANK);
    }

    /*
     * Converts a number between 0 (inclusive) & 9 (inclusive) to a numeric Phrase (0 maps to EMPTY).
     *
     * numberToConvert - the number to convert to a Phrase.
     * return: the numeric Phrase that corresponds to the given number that was passed.
     */
    public static Phrase convertIntegerToPhrase(int numberToConvert) {
        final Phrase[] NUM_PHRASE_ARRAY = new Phrase[]{
                Phrase.EMPTY, Phrase.ONE, Phrase.TWO, Phrase.THREE,
                Phrase.FOUR, Phrase.FIVE, Phrase.SIX, Phrase.SEVEN,
                Phrase.EIGHT, Phrase.NINE
        };

        // Verify that passed number is between 0 & 9 (if not, throw an error)
        if (numberToConvert < 0) {
            throw new IllegalArgumentException("The number to convert must be greater than or equal to 0!");
        }
        if (numberToConvert >= NUM_PHRASE_ARRAY.length) {
            throw new IllegalArgumentException("The number to convert must be less than " + NUM_PHRASE_ARRAY.length);
        }
        return NUM_PHRASE_ARRAY[numberToConvert];
    }

    /*
     * Getter for the phraseValue instance variable
     * return: the value of the current Phrase.
     */
    public String getPhraseValue() {
        return this.phraseValue;
    }

    /*
     * Helper method to retrieve a SHA-256 hash of the current phraseValue.
     * return: a SHA-256 hash of the current phraseValue (empty string if exception thrown).
     */
    private String getPhaseHashValue() {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(this.phraseValue.getBytes(StandardCharsets.UTF_8));
            return String.format("%064x", new BigInteger(1, messageDigest.digest()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    /*
     * Used for calling into Google Cloud Text-to-Speech & generating the audio files.
     * return: the file associated with the current Phrase.
     */
    public File getPhraseAudioFile() {
        return new File(
                String.format("%s/%s.wav", PHRASE_FILES_DIRECTORY.toString(), this.getPhaseHashValue())
        );
    }

    /*
     * Used when reading audio files in the game. Uses "resources" so that it works in a JAR file.
     * return: InputStream to the given Phrase.
     */
    public InputStream getPhraseInputStream() {
        // Audio file is a resource, which is under "phrases/<SHA_256_value>.wav"
        return ClassLoader.getSystemClassLoader().getResourceAsStream(
                String.format("phrases/%s.wav", this.getPhaseHashValue())
        );
    }
}
