package ai;

import common.Color;
import common.Debug;
import common.Row;
import game.ControlInterface;

import java.util.ArrayList;


/*
 * A genetic solving algorithm.
 * This implementation is a slightly modified version of the one in the paper:
 * https://lirias.kuleuven.be/bitstream/123456789/164803/1/KBI_0806.pdf
 * Efficient solutions for Mastermind using genetic algorithms
 * For more information on genetic algorithms see:
 * http://en.wikipedia.org/wiki/Genetic_algorithm
 */

public class GeneticSolver implements SolvingAlgorithm {

    // Size of the population within a generation
    private final int POPULATION_SIZE = 2000;

    /*
     * Number of generations. If no feasible code was found after all generations,
     * a new guess with new generations and populations will be made
     */
    private final int GENERATION_SIZE = 500;

    /*
     * Max. amount of feasible codes (good guesses)
     * If the max. of feasible codes are found, the solver stops and
     * take a turn with one randomly chosen feasible code.
     * see #addToFeasibleCodes()
     */

    private final int FEASIBLE_CODES_MAX = 1;
    private ControlInterface ci;
    private int width;
    private int colQuant;
    private boolean doubleCols;
    private Row[] population = new Row[POPULATION_SIZE];
    private int[] fitness = new int[POPULATION_SIZE];
    private int[] blacks;
    private int[] whites;
    private ArrayList<Row> feasibleCodes = new ArrayList<Row>();
    private int parentPos = 0;

    // Initialize the AI with settings from the Mastermind engine
    // ci - a control interface the AI will use to interact with a game
    public GeneticSolver(ControlInterface ci) {
        this.ci = ci;
        width = ci.getSettingWidth();
        colQuant = ci.getSettingColQuant();
        doubleCols = ci.getSettingDoubleCols();
        population = new Row[POPULATION_SIZE];
        fitness = new int[POPULATION_SIZE];
        blacks = new int[ci.getSettingMaxTries()];
        whites = new int[ci.getSettingMaxTries()];
        initResults();
    }

    // Initialize the arrays "blacks" and "whites" with values from the GameField.
    // The arrays "blacks" and "whites" are needed to accelerate the processing.
    public void initResults() {
        for (int i = 0; i < ci.getActiveRowNumber(); i++) {
            blacks[i] = ci.getResultRow(i)
                    .containsCol(Color.Black);
            whites[i] = ci.getResultRow(i)
                    .containsCol(Color.White);
        }
    }

    /*
     * Do a full guess on the Mastermind engine
     * This includes to generate a guess, pass it to the engine and do a full game turn
     *
     * return:
     * -1 = Game ended and code was not broken
     *  1 = Game ended an code was broken
     *  0 = Just a normal turn or the game already ended
     *  see ControlInterface.turn()
     */
    public int makeGuess() {
        Row guess = generateGuess();
        ci.writeToGameField(guess.getColors());
        int[] result = compare(guess, ci.getSecretCode());
        blacks[ci.getActiveRowNumber()] = result[0];
        whites[ci.getActiveRowNumber()] = result[1];
        return ci.turn();
    }

    /*
     * Create new generations until enough eligible codes are found
     * return: an eligible guess
     *
     * see initPopulation()
     * see calcFitness()
     * see sortFeasibleByFitness(int[], common.Row[])
     * see evolvePopulation()
     * see addToFeasibleCodes()
     */
    public Row generateGuess() {
        Row guess = new Row(width);
        boolean doCalc;
        // first guess
        if (ci.getActiveRowNumber() == 0) {
            return generateRndGuess();
        }
        do {
            int genNumber = 0;
            doCalc = true;
            initPopulation();
            calcFitness();
            sortFeasibleByFitness(fitness, population);

            while (doCalc == true && genNumber <= GENERATION_SIZE
                    && feasibleCodes.size() <= FEASIBLE_CODES_MAX) {
                parentPos = 0;
                evolvePopulation();
                calcFitness();
                sortFeasibleByFitness(fitness, population);
                doCalc = addToFeasibleCodes();
                genNumber++;
            }
            if (feasibleCodes.isEmpty() == true) {
                Debug.dbgPrint("AI: No feasible code found. "
                        + "Retry with new population");
            }
        } while (feasibleCodes.isEmpty() == true);
        // choose guess
        Debug.dbgPrint("AI: There are " + feasibleCodes.size() +
                " feasible code(s)");
        guess = feasibleCodes.get((int) (Math.random() * feasibleCodes.size()));
        Debug.dbgPrint("AI: guess is " + guess);
        return guess;
    }

    /*
     * Evolve the population using crossover, mutation, permutation and inversion
     * 0.5 probability for both xOver1 and xOver2
     * after the crossover
     * 0.03 chance for mutation
     * 0.03 chance for permutation
     * 0.02 chance for inversion
     *
     * http://en.wikipedia.org/wiki/Genetic_algorithm#Reproduction
     *
     * see xOver1(common.Row[], int, int)
     * see xOver2(common.Row[], int, int)
     * see mutation(common.Row[], int)
     * see permutation(common.Row[], int)
     * see inversion(common.Row[], int)
     */
    private void evolvePopulation() {
        Row[] newPopulation = new Row[POPULATION_SIZE];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            newPopulation[i] = new Row(width);
        }
        for (int i = 0; i < POPULATION_SIZE; i += 2) {
            if ((int) (Math.random() * 2) == 0) {
                xOver1(newPopulation, i, i + 1);
            } else {
                xOver2(newPopulation, i, i + 1);
            }
        }

        for (int i = 0; i < POPULATION_SIZE; i++) {
            if ((int) (Math.random() * 100) < 3) {
                mutation(newPopulation, i);
            } else if ((int) (Math.random() * 100) < 3) {
                permutation(newPopulation, i);
            } else if ((int) (Math.random() * 100) < 2) {
                inversion(newPopulation, i);
            }
        }

        doubleToRnd(newPopulation);
        population = newPopulation;
    }

    /*
     * A code c is eligible or feasible if it results in the same values for
     * Xk and Yk for all guesses k that have been played up till that stage,
     * if c was the secret code.
     * X is the number of exact matches. Y is the number of guesses which are
     * the right color but in the wrong position.
     *
     * return: false if feasibleCodes is full, true otherwise
     */
    private boolean addToFeasibleCodes() {
        outer:
        for (int i = 0; i < POPULATION_SIZE; i++) {
            for (int j = 0; j < ci.getActiveRowNumber(); j++) {
                int[] result = compare(population[i],
                        ci.getGameFieldRow(j));

                if (result[0] != blacks[j] || result[1] != whites[j]) {
                    continue outer;
                }
            }

            if (feasibleCodes.size() < FEASIBLE_CODES_MAX) {
                if (feasibleCodes.contains(population[i]) == false) {
                    feasibleCodes.add(population[i]);
                    if (feasibleCodes.size() < FEASIBLE_CODES_MAX) {
                        return false;
                    }
                }
            } else {
                // E is full
                return false;
            }
        }
        return true;
    }

    // Replaces double elements in newPopulation
    // newPopulation - the population array that will be manipulated
    private void doubleToRnd(Row[] newPopulation) {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            if (lookForSame(newPopulation, i) == true) {
                newPopulation[i] = generateRndGuess();
            }
        }
    }

    /*
     * Look for Rows that are equal to the Row at popPos in newPopulation
     * newPopulation - the population array that will be searched
     * popPos - the position of the Row within the population array that will be compared
     *
     * return: true if equal Row found. false if no equal Row found
     */
    private boolean lookForSame(Row[] newPopulation, int popPos) {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            if (population[popPos].equals(newPopulation[popPos]) == true) {
                return true;
            }
        }
        return false;
    }

    /*
     * Mutation. Replaces the digit of one randomly chosen position by a random other digit
     *
     * newPopulation - the population array that will be manipulated
     * popPos - the position of the Row within the population array that will be changed
     */
    private void mutation(Row[] newPopulation, int popPos) {
        newPopulation[popPos].setColAtPos((int) (Math.random() * width),
                Color.values()[(int) (Math.random() * colQuant)]);
    }

    /*
     * Permutation. The colors of two random positions are switched
     *
     * newPopulation - the population array that will be manipulated.
     * popPos - the position of the Row within the population array that will be changed
     */
    private void permutation(Row[] newPopulation, int popPos) {
        int pos1 = (int) (Math.random() * width);
        int pos2 = (int) (Math.random() * width);
        Color tmp = newPopulation[popPos].getColAtPos(pos1);
        newPopulation[popPos].setColAtPos(pos1,
                newPopulation[popPos].getColAtPos(pos2));
        newPopulation[popPos].setColAtPos(pos2, tmp);
    }

    /*
     * Inversion. Two positions are randomly picked, and the sequence of colors
     * between these positions is inverted.
     *
     * newPopulation - the population array that will be manipulated
     * popPos - the position of the Row within the population array that will be changed
     */
    private void inversion(Row[] newPopulation, int popPos) {
        int pos1 = (int) (Math.random() * width);
        int pos2 = (int) (Math.random() * width);

        if (pos2 < pos1) {
            int tmp = pos2;
            pos2 = pos1;
            pos1 = tmp;
        }

        for (int i = 0; i < (pos2 - pos1)/2; i++) {
            Color tmp = newPopulation[popPos].getColAtPos(pos1 + i);
            newPopulation[popPos].setColAtPos(pos1 + i,
                    newPopulation[popPos].getColAtPos(pos2 - i));
            newPopulation[popPos].setColAtPos(pos2 - i, tmp);
        }
    }

    /*
     * One-point crossover. A single crossover point on both parents' organism
     * strings is selected. All data beyond that point in either organism string
     * is swapped between the two parent organisms. The resulting organisms are
     * the children.
     *
     * http://en.wikipedia.org/wiki/Crossover_%28genetic_algorithm%29#One-point_crossover
     *
     * newPopulation - the population array that will be manipulated
     * child1Pos - the position of a Row within the population array that will be changed
     * child2Pos - he position of a Row within the population array that will be changed
     */
    private void xOver1(Row[] newPopulation, int child1Pos, int child2Pos) {
        int mother = getParentPos();
        int father = getParentPos();
        int sep = ((int) (Math.random() * width)) + 1;

        for (int i = 0; i < width; i++) {
            if (i <= sep) {
                newPopulation[child1Pos].setColAtPos(i,
                        population[mother].getColAtPos(i));
                newPopulation[child2Pos].setColAtPos(i,
                        population[father].getColAtPos(i));
            } else {
                newPopulation[child1Pos].setColAtPos(i,
                        population[father].getColAtPos(i));
                newPopulation[child2Pos].setColAtPos(i,
                        population[mother].getColAtPos(i));
            }
        }
    }

    /*
     * Two-point crossover. Two points are selected on the parent organism
     * strings. Everything between the two points is swapped between the parent
     * organisms, rendering two child organisms.
     *
     * http://en.wikipedia.org/wiki/Crossover_%28genetic_algorithm%29#Two-point_crossover
     *
     * newPopulation - the population array that will be manipulated
     * child1Pos - the position of a Row within the population array that will be changed
     * child2Pos - the position of a Row within the population array that will be changed
     */
    private void xOver2(Row[] newPopulation, int child1Pos, int child2Pos) {
        int mother = getParentPos();
        int father = getParentPos();
        int sep1;
        int sep2;

        sep1 = ((int) (Math.random() * width)) + 1;
        sep2 = ((int) (Math.random() * width)) + 1;

        if (sep1 > sep2) {
            int temp = sep1;
            sep1 = sep2;
            sep2 = temp;
        }

        for (int i = 0; i < width; i++) {
            if (i <= sep1 || i > sep2) {
                newPopulation[child1Pos].setColAtPos(i,
                        population[mother].getColAtPos(i));
                newPopulation[child2Pos].setColAtPos(i,
                        population[father].getColAtPos(i));
            } else {
                newPopulation[child1Pos].setColAtPos(i,
                        population[father].getColAtPos(i));
                newPopulation[child2Pos].setColAtPos(i,
                        population[mother].getColAtPos(i));
            }
        }

    }

    /*
     * Getter for a good parent position in the population.
     * In this case the one of the best fifth of the population is used.
     * It is important that only parents with a good fitness value are used to
     * generate the next generation.
     *
     * return: one position in the first fifth of the population-array successively increasing
     */
    private int getParentPos() {
        parentPos += (int) (Math.random() * 7);
        if (parentPos < POPULATION_SIZE / 5) {
            return parentPos;
        } else {
            parentPos = 0;
        }
        return parentPos;
    }

    /*
     * Calculates the fitness of every Row in population.
     * A fitness-value and the corresponding element of the population array
     * both have the same index in their respective arrays.
     *
     * It should resemble the function as described in the paper:
     * https://lirias.kuleuven.be/bitstream/123456789/164803/1/KBI_0806.pdf
     * Efficient solutions for Mastermind using genetic algorithms on page 6.
     */
    private void calcFitness() {
        int xtmp;
        int ytmp;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            xtmp = 0;
            ytmp = 0;
            for (int j = 0; j < ci.getActiveRowNumber(); j++) {
                int[] result = compare(
                        population[i], ci.getGameFieldRow(j));
                xtmp += Math.abs(result[0] - blacks[j]);//+width * j;
                ytmp += Math.abs(result[1] - whites[j]);//+width * j;
            }
            fitness[i] = (xtmp + ytmp);
        }
    }

    /*
     * Compares two Rows.
     * result[0] is incremented if an equal value(color) is at an equal position
     * result[1] is incremented if an equal value is an different positions
     * result[0] are the black pegs
     * result[1] are the white pegs
     * http://en.wikipedia.org/wiki/Mastermind_%28board_game%29#Gameplay_and_rules
     *
     * a - Row Guesscode.
     * b - Row Secretcode.
     * return:
     * [0] is the number of black pegs,
     * [1] is the number of white pegs
     */
    private int[] compare(Row a, Row b) {
        int[] result = {0, 0};
        Color[] code = new Color[width];
        Color[] secret = new Color[width];
        // Create real copy
        System.arraycopy(a.getColors(), 0, code, 0, width);
        System.arraycopy(b.getColors(), 0, secret, 0, width);

        for (int i = 0; i < width; i++) {
            if (code[i] == secret[i]) {
                result[0]++;
                secret[i] = null;
                code[i] = null;
            }
        }

        outer:
        for (int i = 0; i < width; i++) {
            if (code[i] != null) {
                for (int j = 0; j < width; j++) {
                    if (code[i] == secret[j]) {
                        result[1]++;
                        secret[j] = null;
                        continue outer;
                    }
                }
            }
        }
        return result;
    }

    // Initializes the Population with random Rows
    // feasibleCodes gets purged
    private void initPopulation() {
        // init population with random guesses.
        int i = 0;
        feasibleCodes.clear();
        while (i < POPULATION_SIZE) {
            population[i] = generateRndGuess();
            i++;
        }
    }

    /*
     * Generates a Row with random colors
     * Generates a Row with the current width setting. The available colors are
     * determined by the colorQuant setting. If the doubleColors setting
     * is false, only different colors are set.
     *
     * Warning: Using this function can take up to
     * several minutes, especially if you have set a high width,
     * many colors or when you run the game on a slow computer.
     * This is not a bug, just a side effect of the complex
     * algorithm the AI is using.
     *
     * return: a Row with random colors
     */
    private Row generateRndGuess() {
        Row guess = new Row(width);
        // Prepare values to fit range-rule
        Color[] values = new Color[colQuant];
        Color[] all = Color.values();
        System.arraycopy(all, 0, values, 0, colQuant);
        // Do the actual code generation
        int i = 0;
        while (i < width) {
            Color now = values[(int) (Math.random() * colQuant)];
            if (guess.containsCol(now) > 0) {
                if (doubleCols == true) {
                    guess.setColAtPos(i++, now);
                }
            } else {
                guess.setColAtPos(i++, now);
            }
        }
        return guess;
    }

    /*
     * This is a Quicksort that sorts the fitness and pop arrays
     * by the criteria in the fitness-array
     *
     * fitness - an int array
     * pop - an array of Rows
     * see sort(int[], common.Row[], int, int)
     * see divide(int[], common.Row[], int, int, int)
     * see swap(common.Row[], int, int)
     * see swap(int[], int, int)
     */
    private void sortFeasibleByFitness(int[] fitness, Row[] pop) {
        sort(fitness, pop, 0, fitness.length - 1);
    }

    /*
     * Helper function for recursive sorting
     *
     * fitness - an int array
     * pop - an array of Rows
     * low - the lower limit
     * up - the upper limit
     */
    private void sort(int[] fitness, Row[] pop, int low, int up) {
        int p = (low + up) / 2;
        if (up > low) {
            // Divide
            int pn = divide(fitness, pop, low, up, p);
            // and sort
            sort(fitness, pop, low, pn - 1);
            sort(fitness, pop, pn + 1, up);
        }
    }

    /*
     * Helper function for partitioning
     *
     * fitness - an int array
     * pop - an array of Rows
     * low - the lower limit
     * up - the upper limit
     * pivot - the position of the Pivot element
     * return the new Position of the Pivot element
     */
    private int divide(int[] fitness, Row[] pop, int low, int up, int pivot) {
        int pn = low;
        int pv = fitness[pivot];
        swap(fitness, pivot, up);
        swap(pop, pivot, up);
        for (int i = low; i < up; i++) {
            if (fitness[i] <= pv) {
                swap(fitness, pn, i);
                swap(pop, pn++, i);
            }
            swap(fitness, up, pn);
            swap(pop, up, pn);
        }
        return pn;
    }

    /*
     * Helper function to swap two elements of an int array
     *
     * fitness - an int array
     * a - position of the first element
     * b - position of the second element
     */
    private void swap(int[] fitness, int a, int b) {
        int tmp = fitness[a];
        fitness[a] = fitness[b];
        fitness[b] = tmp;
    }

    /*
     * Helper function to swap two elements of an array of Rows
     *
     * pop - an array of Rows
     * a - position of the first element
     * b - position of the second element
     */
    private void swap(Row[] pop, int a, int b) {
        Row tmp = pop[a];
        pop[a] = pop[b];
        pop[b] = tmp;
    }
}

