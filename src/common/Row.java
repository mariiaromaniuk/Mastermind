package common;

import java.io.Serializable;


// This class represents a single Row with colors
public class Row implements Serializable {
    // Object variables
    private Color[] colors;

    // Constructor for a Row with colors
    // colors - an array of colors to initialize the Row
    public Row(Color[] colors) {
        this.colors = colors;
    }

    // Constructor for a Row with a given width
    // width - the width of the Row
    public Row(int width) {
        colors = new Color[width];
        for (int i = 0; i < width; i++) {
            colors[i] = Color.Null;
        }
    }

    // Getter for a Row
    // return: an array of colors enum type
    public Color[] getColors() {
        return colors;
    }

    // Setter for a Row
    // colors - an array of colors enum type
    public void setColors(Color[] colors) {
        this.colors = colors;
    }

    /*
     * Get a color at a specific position in the Row
     * pos - the position from where to get the color
     * return: the color from the position. If the position was
     * out of the Row bounds "null" will be returned.
     */
    public Color getColAtPos(int pos) {
        if (pos < colors.length && pos >= 0) {
            return colors[pos];
        }
        return null;
    }

    /*
     * Set a color at a specific position in the Row
     * pos - the position where to set the color
     * color - the color to set on the position
     * return: true if the position was in the Row bounds
     * and everything went well, false otherwise
     */
    public boolean setColAtPos(int pos, Color color) {
        if (pos < this.colors.length && pos >= 0) {
            this.colors[pos] = color;
            return true;
        }
        return false;
    }

    /*
     * Getter for the Row width
     * return: the width of a Row
     */
    public int width() {
        return colors.length;
    }

    /*
     * Checks how often a Row contains a specific color
     * color - the color to look for
     * return: the quantity of matches
     */
    public int containsCol(Color color) {
        int ret = 0;
        for (int i = 0; i < this.colors.length; i++) {
            if (this.colors[i] == color) {
                ret++;
            }
        }
        return ret;
    }

    /*
     * Checks if the same color is inside the Row more than once
     * return: true if a color is found more than once, false if not
     */
    public boolean containsDoubleCols(){
        for(int i = 0; i < colors.length; i++){
            for(int j = i+1; j < colors.length; j++){
                if(colors[i] == colors[j]){
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * Line-up all colors separated with ","
     * return: a String with all colors lined-up, separated by ","
     */
    @Override
    public String toString() {
        if (colors != null) {
            String ret = "";
            for (int i = 0; i < colors.length-1; i++) {
                ret += colors[i] + ", ";
            }
            return ret + colors[colors.length-1];
        }
        return "no colors";
    }

    /*
     * Check if the specified object equals this Row
     * A Row is equal if it contains the same colors at the same positions
     * o - the object to compare with
     * return: true if given object is equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        Row row;
        if (o instanceof Row) {
            row = (Row)o;
        } else {
            return false;
        }
        for (int i = 0; i < row.width(); i++) {
            if (row.getColAtPos(i) != this.getColAtPos(i)) {
                return false;
            }
        }
        return true;
    }
}

