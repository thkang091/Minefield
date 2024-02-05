
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        // Initialize the Variables
        int boundaries = 0;
        int flags = 0;
        boolean boolFlag = false;
        Scanner scanner = new Scanner(System.in);
        String choose;
        // creat input do choose difficulty selection input
        while (true) {
            System.out.println("Choose the difficulty of your minesweeper game: (easy, medium, hard)");
            choose = scanner.nextLine();

            if (choose.equals("easy") || choose.equals("medium") || choose.equals("hard")) {
                break;
            } else {
                System.out.println("try again!");
            }
        }
        switch (choose) {
            case "easy":
                boundaries = 5;
                flags = 5;
                break;
            case "medium":
                boundaries = 9;
                flags = 12;
                break;
            case "hard":
                boundaries = 20;
                flags = 40;
                break;
        }
        scanner = new Scanner(System.in);
        System.out.println("Choose debug mode (yes/no)");
        String debug = scanner.nextLine();


        // Initialize the minefield with the preset values
        Minefield field = new Minefield(boundaries, boundaries, flags);
        // create input for start coordinates
        System.out.println("Choose starting coordinates (X Y): ");
        int x = scanner.nextInt();
        int y = scanner.nextInt();



        while (x >= boundaries || y >= boundaries || x < 0 || y < 0){
            System.out.println("Invalid choice (it is not in the field boundary)");
            System.out.println("Choose starting coordinates (X Y): ");
            x = scanner.nextInt();
            y = scanner.nextInt();

        }

        // using the previously set values to create mines
        field.createMines(x, y, flags);

        field.evaluateField();
        // check input
        if (debug.equals("yes")) {
            field.debug();

        } else {
            field.revealStartingArea(x, y);
            System.out.println(field);

        }
        while (!field.gameOver()) {
            System.out.println("Enter a coordinate and if you wish to place a flag (Remaining: " + flags + "): [x] [y] [do you want place a flag? (yes/no)]");
            x = scanner.nextInt();
            y = scanner.nextInt();
            String flag = scanner.nextLine();
            while (x >= boundaries || y >= boundaries || x < 0 || y < 0){
                System.out.println("Invalid choice (it is not in the field boundary)");
                System.out.println("Enter a coordinate and if you wish to place a flag (Remaining: " + flags + "): [x] [y] [do you want place a flag? (yes/no)]");
                x = scanner.nextInt();
                y = scanner.nextInt();
                flag = scanner.nextLine();

            }
            flag = flag.trim(); // because there is white space
            boolFlag = flag.equals("yes");
            field.guess(x, y, boolFlag);

            if (debug.equals("yes")) {
                field.debug();

            } else if (debug.equals("no")){
                System.out.println(field);
            }


        }
        // if all of the remain cell is mines, you will win
        if (field.gameOver() && !field.fieldArray[x][y].getStatus().equals("M")) {
            System.out.println("You Win!");
        }
        // After the game is over, the character prints a board with everything that is shown on it.
        else if (field.gameOver()) {
                System.out.println("You Lose");
                field.debug();
        }
    }
}
