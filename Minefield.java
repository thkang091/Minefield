
import java.util.Random;


public class Minefield {
    /**
     * Global Section
     */
    public static final String ANSI_YELLOW_BRIGHT = "\u001B[33;1m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE_BRIGHT = "\u001b[34;1m";
    public static final String ANSI_BLUE = "\u001b[34m";
    public static final String ANSI_RED_BRIGHT = "\u001b[31;1m";
    public static final String ANSI_RED = "\u001b[31m";
    public static final String ANSI_GREEN = "\u001b[32m";
    public static final String ANSI_PURPLE = "\u001b[35m";
    public static final String ANSI_CYAN = "\u001b[36m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001b[47m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001b[45m";
    public static final String ANSI_GREY_BACKGROUND = "\u001b[0m";
    private int flags;
    private final Cell[][] fieldArray;

    // running is the boolean to whether the game is running. If it's true, the game is running
    private boolean running = true;


    /**
     * Minefield
     *
     * Build a 2-d Cell array representing your minefield.
     * Constructor
     * @param rows       Number of rows.
     * @param columns    Number of columns.
     * @param flags      Number of flags, should be equal to mines
     */
    public Minefield(int rows, int columns, int flags) {
        // set each cell in the minefield state to false and "-"(initialize).
        fieldArray = new Cell[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                fieldArray[i][j] = new Cell(false, "-");
            }
        }
        // Set the number of flags
        this.flags = flags;
    }

    /**
     * evaluateField
     *
     *
     * @function:
     * Evaluate entire array.
     * When a mine is found check the surrounding adjacent tiles. If another mine is found during this check, increment adjacent cells status by 1.
     *
     */
    public void evaluateField() {
        // determine how many rows and columns the minefield array contains.
        int rows = fieldArray.length;
        int columns = fieldArray[0].length;


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // check whether the current cell contains a mine
                if (fieldArray[i][j].getStatus().equals("M")) {
                    // If mine is found, update the number of adjacent cells(change the number of the adjacent squares)
                    for (int k = i - 1; k <= i + 1; k++) {
                        for (int l = j - 1; l <= j + 1; l++) {
                            if (k >= 0 && k < rows && l >= 0 && l < columns &&
                                    !fieldArray[k][l].getStatus().equals("M")) {
                                // Increment status if the adjacent cells is non-mine
                                if (fieldArray[k][l].getStatus().equals("-")) {
                                    fieldArray[k][l].setStatus("1");
                                } else {
                                    //if the statues is already a number, increase 1
                                    int currentStatus = Integer.parseInt(fieldArray[k][l].getStatus());
                                    fieldArray[k][l].setStatus(Integer.toString(currentStatus + 1));
                                }
                            }
                        }
                    }
                } else if (fieldArray[i][j].getStatus().equals("-")) {
                    // If empty cell, set status to "0"
                    fieldArray[i][j].setStatus("0");
                }
            }
        }
    }
    /**
     * createMines
     *
     * Randomly generate coordinates for possible mine locations.
     * If the coordinate has not already been generated and is not equal to the starting cell set the cell to be a mine.
     * utilize rand.nextInt()
     *
     * @param x       Start x, avoid placing on this square.
     * @param y        Start y, avoid placing on this square.
     * @param mines      Number of mines to place.
     */
    public void createMines(int x, int y, int mines) {
        // To generate random coordinates, create a Random object.
        Random random = new Random();

        // determine and get how many rows and columns the minefield array contains.
        int rows = fieldArray.length;
        int cols = fieldArray[0].length;
        //keep creating mines until the wanted number is produced.
        while (mines != 0) {
            int X = random.nextInt(rows);
            int Y = random.nextInt(cols);

            // check if the created coordinates are not equal to the start cell
            if (X != x || Y != y) {
                Cell target = fieldArray[X][Y];

                // Verify that the cell is not already revealed and a mine.
                if (!target.getStatus().equals("M") && !target.getRevealed()) {
                    target.setStatus("M");
                    // decrease the number of remaining mines to put
                    mines--;
                }
            }
        }
    }


    /**
     * guess
     *
     * Check if the guessed cell is inbounds (if not done in the Main class).
     * Either place a flag on the designated cell if the flag boolean is true or clear it.
     * If the cell has a 0 call the revealZeroes() method or if the cell has a mine end the game.
     * At the end reveal the cell to the user.
     *
     *
     * @param x       The x value the user entered.
     * @param y       The y value the user entered.
     * @param flag    A boolean value that allows the user to place a flag on the corresponding square.
     * @return boolean Return false if guess did not hit mine or if flag was placed, true if mine found.
     */
    public boolean guess(int x, int y, boolean flag) {
        // get the number of rows and columns the minefield array contains.
        int rows = fieldArray.length;
        int cols = fieldArray[0].length;

        Cell target = fieldArray[x][y];
        // check if the coordinates for x and y are inside the minefield array's boundaries.
        if (x < 0 || x >= rows || y < 0 || y >= cols) {
            return false;
        }

        // Check if the cell has already been revealed
        if (target.getRevealed()) {
            // if the cell has already been revealed, it will return false
            return false;
        }

        // check if flag is used and there are still flags left to put in the minefield.
        if (flag && flags > 0) {
            // place a flag
            target.setStatus("F");
            target.setRevealed(true);
            // decrease the number of flags to use
            flags--;
        }else if (!flag) {
            // Check if the cell contains a mine
            if (target.getStatus().equals("M")) {
                // if mine is in the cell, it will end the game.
                running = false;
                return true;
            } else if (target.getStatus().equals("0")) {
                // if the cell has 0, call the revealZeroes method
                revealZeroes(x, y);
            } else {
                // if the cell has a value that is greater than 0, just reveal the cell
                target.setRevealed(true);
            }
        } else {
            // if there is no more flags left, it will end the game.
            running = false;
        }

        return true;
    }

    /**
     * gameOver
     *
     * Ways a game of Minesweeper ends:
     * 1. player guesses a cell with a mine: game over -> player loses
     * 2. player has revealed the last cell without revealing any mines -> player wins
     *
     * @return boolean Return false if game is not over and squares have yet to be revealed, otheriwse return true.
     */
    public boolean gameOver() {
        // get the number of rows and columns the minefield array contains.
        int rows = fieldArray.length;
        int cols = fieldArray[0].length;
        // check if the game is running
        if (running) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (!fieldArray[i][j].getRevealed() && !fieldArray[i][j].getStatus().equals("M")) {
                        // if the game is not over, return false
                        return false;
                    }
                }
            }
            return true;
        } else {
            // if the game is over, return true
            return true;
        }
    }


    /**
     * Reveal the cells that contain zeroes that surround the inputted cell.
     * Continue revealing 0-cells in every direction until no more 0-cells are found in any direction.
     * Utilize a STACK to accomplish this.
     *
     * This method should follow the psuedocode given in the lab writeup.
     * Why might a stack be useful here rather than a queue?
     *
     * @param x      The x value the user entered.
     * @param y      The y value the user entered.
     */
    public void revealZeroes(int x, int y) {
        // create a new Stack to keep track of cells that need to be revealed
        Stack1Gen<int[]> stack = new Stack1Gen<>();
        stack.push(new int[]{x, y});

        int rows = fieldArray.length;
        int cols = fieldArray[0].length;

        // reveal the 0 cells until the stack is empty
        while (!stack.isEmpty()) {
            int[] elements = stack.pop();
            int currentX = elements[0];
            int currentY = elements[1];
            fieldArray[currentX][currentY].setRevealed(true);

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int newX = currentX + i;
                    int newY = currentY + j;

                    // Check that the neighboring cell has a "0" value, is within bounds, and is not revealed.
                    if (newX >= 0 && newX < rows && newY >= 0 &&
                            newY < cols && !fieldArray[newX][newY].getRevealed() &&
                            fieldArray[newX][newY].getStatus().equals("0")) {
                        stack.push(new int[]{newX, newY});
                    }
                }
            }

            // Check and reveal adjacent cells that are not mine or 0.
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int newX = currentX + i;
                    int newY = currentY + j;

                    if (newX >= 0 && newX < rows &&
                            newY >= 0 && newY < cols &&
                            !fieldArray[newX][newY].getStatus().equals("M") &&
                            !fieldArray[newX][newY].getStatus().equals("0")) {
                        fieldArray[newX][newY].setRevealed(true);
                    }
                }
            }
        }
    }


    /**
     * revealStartingArea
     *
     * On the starting move only reveal the neighboring cells of the inital cell and continue revealing the surrounding concealed cells until a mine is found.
     * Utilize a QUEUE to accomplish this.
     *
     * This method should follow the psuedocode given in the lab writeup.
     * Why might a queue be useful for this function?
     *
     * @param x     The x value the user entered.
     * @param y     The y value the user entered.
     */
    public void revealStartingArea(int x, int y) {
        // Create a queue to store cells that have to reveal
        Q1Gen<int[]> queue = new Q1Gen<>();
        queue.add(new int[]{x, y});

        // do the loop while the queue is not empty, reveal cells
        while (queue != null) {
            int[] current = queue.remove();
            int currentX = current[0];
            int currentY = current[1];

            // If the current cell is 0, reveal all adjacent 0
            if (fieldArray[currentX][currentY].getStatus().equals("0")) {
                revealZeroes(currentX, currentY);
            }

            // If the current cell is not a mine, reveal the cell
            if (!fieldArray[currentX][currentY].getStatus().equals("M")) {
                fieldArray[currentX][currentY].setRevealed(true);
            }

            // Quit revealing cells if the current cell is a mine.
            if (fieldArray[currentX][currentY].getStatus().equals("M")) {
                break;
            }

            // check that every adjacent cell to the current cell and if they are valid and hasn't been revealed, add it to the queue.
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int newX = currentX + i;
                    int newY = currentY + j;
                    int rows = fieldArray.length;
                    int cols = fieldArray[0].length;
                    if (newX >= 0 && newX < rows && newY >= 0 && newY < cols
                            && !fieldArray[newX][newY].getRevealed()) {
                        queue.add(new int[]{newX, newY});
                    }
                }
            }
        }
    }


    /**
     * For both printing methods utilize the ANSI colour codes provided!
     *
     *
     *
     *
     *
     * debug
     *
     * @function This method should print the entire minefield, regardless if the user has guessed a square.
     * *This method should print out when debug mode has been selected.
     */
    public void debug() { // print the entire minefield
        // the column number
        System.out.print("\n ");
        for (int z = 0; z < fieldArray.length; z++) {
            System.out.print(z + " ");
        }
        System.out.println();

        for (int i = 0; i < fieldArray.length; i++) {
            // the row number
            System.out.print(i + " ");

            for (int j = 0; j < fieldArray[i].length; j++) {
                Cell cell = fieldArray[i][j];
                String status = cell.getStatus();

                // Choose colors according to the status
                switch (status) {
                    case "0":
                        System.out.print(ANSI_YELLOW + status + ANSI_GREY_BACKGROUND + " ");
                        break;
                    case "1":
                        System.out.print(ANSI_BLUE_BRIGHT + status + ANSI_GREY_BACKGROUND + " ");
                        break;
                    case "2":
                        System.out.print(ANSI_GREEN + status + ANSI_GREY_BACKGROUND + " ");
                        break;
                    case "3":
                        System.out.print(ANSI_RED_BRIGHT + status + ANSI_GREY_BACKGROUND + " ");
                        break;
                    case "M":
                        System.out.print(ANSI_RED + status + ANSI_GREY_BACKGROUND + " ");
                        break;
                    default:
                        System.out.print(ANSI_YELLOW_BRIGHT + status + ANSI_GREY_BACKGROUND + " ");
                }
            }

            System.out.println();

        }
        // Print column numbers for revealed status
        System.out.print("\n ");
        for (int z = 0; z < fieldArray.length; z++) {
            System.out.print(z + " ");
        }
        System.out.println();

        for (int i = 0; i < fieldArray.length; i++) {
            // Print row number for revealed status
            System.out.print(i + " ");

            for (int j = 0; j < fieldArray[i].length; j++) {
                Cell cell = fieldArray[i][j];

                // show the status if the cell is revealed and not a mine
                if (cell.getRevealed() && !cell.getStatus().equals("M")) {
                    String status = cell.getStatus();
                    // Choose colors according to the status
                    switch (status) {
                        case "0":
                            System.out.print(ANSI_YELLOW + status + ANSI_GREY_BACKGROUND + " ");
                            break;
                        case "1":
                            System.out.print(ANSI_BLUE_BRIGHT + status + ANSI_GREY_BACKGROUND + " ");
                            break;
                        case "2":
                            System.out.print(ANSI_GREEN + status + ANSI_GREY_BACKGROUND + " ");
                            break;
                        case "3":
                            System.out.print(ANSI_RED_BRIGHT + status + ANSI_GREY_BACKGROUND + " ");
                            break;
                        default:
                            System.out.print(ANSI_YELLOW_BRIGHT + status + ANSI_GREY_BACKGROUND + " ");
                    }
                } else {
                    // If not revealed or a mine, print "-"
                    System.out.print(ANSI_CYAN + "-" + ANSI_GREY_BACKGROUND + " ");
                }
            }

            System.out.println();
        }
    }

    /**
     * toString
     *
     * @return String The string that is returned only has the squares that has been revealed to the user or that the user has guessed.
     */
    @Override
    public String toString() {
        String result = "\n ";

        // the column number
        for (int i = 0; i < fieldArray.length; i++) {
            result += " " + i;
        }
        result += "\n";

        for (int i = 0; i < fieldArray.length; i++) {
            result += i + " ";

            for (int j = 0; j < fieldArray[i].length; j++) {
                Cell cell = fieldArray[i][j];

                if (cell.getRevealed()) {
                    String status = cell.getStatus();

                    // Choose colors according to the status
                    switch (status) {
                        case "0":
                            result += ANSI_YELLOW + status + ANSI_GREY_BACKGROUND + " ";
                            break;
                        case "1":
                            result += ANSI_BLUE_BRIGHT + status + ANSI_GREY_BACKGROUND + " ";
                            break;
                        case "M":
                            result += ANSI_RED + status + ANSI_GREY_BACKGROUND + " ";
                            break;
                        default:
                            result += ANSI_YELLOW_BRIGHT + status + ANSI_GREY_BACKGROUND + " ";
                    }
                } else {
                    // If not revealed, add "-"
                    result += ANSI_CYAN + "-" + ANSI_GREY_BACKGROUND + " ";
                }
            }
            result += "\n";
        }
        return result;
    }
}


