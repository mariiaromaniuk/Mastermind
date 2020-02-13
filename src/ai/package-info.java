package ai;

/**
 * Solving algorithms for the Mastermind engine (package game).
 *
 * AI (artificial intelligence) can take up to several minutes, especially if you
 * have set a high width, many colors or when you run the game on a slow computer.
 * This is not a bug, just a side effect of the complex algorithm the AI is using.
 * While the AI is guessing, the GUI is locked. Please stand by until the AI broke
 * the code or the maximum number of tries is reached.
 *
 * Bruteforce:
 * A solving algorithm using the Brute-force search technique.
 * See http://en.wikipedia.org/wiki/Brute-force_search
 * Brute-force search algorithm on Wikipedia.
 *
 * Clues:
 * Checks if the guess is valid or makes no sense in context
 * of previous guesses and results.
 *
 * GeneticSolver:
 * A genetic solving algorithm.
 * This implementation is a slightly modified version of the one in the paper:
 * https://lirias.kuleuven.be/bitstream/123456789/164803/1/KBI_0806.pdf
 * Efficient solutions for Mastermind using genetic algorithms.
 * For more information on genetic algorithms see:
 * http://en.wikipedia.org/wiki/Genetic_algorithm
 *
 * RandomGuesses:
 * A "solving algorithm" that makes random guesses.
 *
 * SolvingAlgorithm:
 * This interface describes how a solving algorithm has to be implemented.
 * It has to be able to generate a valid guess. Normally based on previous guesses.
 * And it has to be able to actually make a guess interacting with the
 * ControlInterface.
 */