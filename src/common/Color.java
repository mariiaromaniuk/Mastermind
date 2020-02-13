package common;


/*
 * Color Enumeration.
 * Representing the numbers and colors used in the game engine (bonded together in enum type).
 * The color value is the RGB value representing the color in the default sRGB ColorModel.
 * Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue.
 * This values are equal to the java.awt.Color.getRGB().
 */

public enum Color {

    Yellow("0",-137672),
    LightBlue("1",-8716289),
    LightGreen("2",-8716422),
    LightRed("3",-34182),
    LightOrange("4",-20614),
    LightPurple("5",-5276929),
    Orange("6",-32768),
    Olive("7",-8351640),
    LightPink("8",-871999),
    Pink("9",-837510),
    White("01",-1),
    Black("02",-16777216),
    Null("00",-2696737);

    private final int rgb;
    private final String num;

    /*
     * Std. Constructor
     * rgb = SRGB-code of the color
     * num = number associated with this color
     */
    Color(String num, int rgb) {
        this.num = num;
        this.rgb = rgb;
    }

    // Returning the SRGB-code as an int
    public int getRGB() {
        return rgb;
    }

    // Returning the SRGB-code as a string
    public String getNum() {
        return num;
    }
}