package common;


// Used for debugging, i.e. with a function for debug-output
public class Debug {

    // This value enables (true) or disables (false) debugging. False by default
    private static boolean debug = false;

    // This value enables (true) or disables (false) error messages. True by default
    private static boolean error = true;

    // Print the given String if debugging is enabled
    // s - the String to be printed.
    public static void dbgPrint(String s) {
        if (debug) {
            System.out.println("Debug: " + s);
        }
    }

    // Print the given String if error is enabled
    // s - the String to be printed.
    public static void errorPrint(String s) {
        if (error) {
            System.out.println("Error: " + s);
        }
    }

    // Set the debugging on or off.
    // value - true to switch debugging on, false to switch debugging off
    public static void setDebug(boolean value) {
        debug = value;
    }
}
