package gui;

/**
 * A GUI for the Mastermind engine (package game).
 *
 * MainWindow:
 * The class contains all GUI elements and functions that a user needs to interact
 * with the Mastermind game engine (package game). The Mastermind engine is self-contained,
 * so that any UI (GUI, console, etc. ) only has to use the ControlInterface of the
 * game package. This design makes the GUI and engine nearly independent form each other.
 * Logical changes in the Mastermind engine don't affect the GUI. Of course graphical
 * changes in the GUI don't affect the Mastermind engine neither.
 *
 * how-to:
 * Mastermind User Guide in PDF format with detailed description of game roles and all
 * the elements of the game. Automatically opens if user select "Help" -> "How To Play"
 * at the game control menu panel.
 */