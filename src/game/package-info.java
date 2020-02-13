package game;

/**
 * A Mastermind game engine.
 *
 * ControlInterface:
 * The main interface to control the game flow. This class provides all functions for a game.
 * This is the only public class in the game package. This design is used to provide a single
 * interface for a frontend (or an AI) which guarantees a correct and save execution.
 *
 * Game:
 * Game-engine class. Controls the game flow.
 *
 * GameField:
 * The actual game field. This includes the guess rows and the result rows.
 * The active row number is stored here also.
 *
 * HTTPUtils:
 * Low level class-util that handles all http work for obtaining results
 * without third-party libs.
 *
 * SecretCode:
 * This class represents the secret code the player has to guess.
 *
 * Settings:
 * Contains all settings of the game.
 */