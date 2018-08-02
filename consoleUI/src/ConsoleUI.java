import java.awt.*;
import java.io.Serializable;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI implements Serializable {
    private Game GameLogic;
    private Scanner inScanner;

    static private String BigYes = "Y";
    static private String SmallYes = "y";
    static private String BigNo = "N";
    static private String SmallNo = "n";

    static private char player1Symbol = '$';
    static private char player2Symbol = '@';

    public ConsoleUI() {
        inScanner = new Scanner(System.in);
    }

    public static void main(String[] args){
        ConsoleUI console = new ConsoleUI();
        console.GameLogic = new Game(5, 7, 10);

        boolean exitGame = false;
        boolean roundOver = false;

        MenuOption userSelection = null;

        while(!exitGame) {
            userSelection = console.getUserMenuSelection();

            roundOver = userSelection.makeAction(console);

            boolean playAgain = false;
            String userInput = null;
            boolean goodInput = false;

            while(!goodInput && !exitGame && roundOver) {
                if (roundOver) {
                    System.out.println("Would you like to play again?");
                    userInput = console.inScanner.nextLine();
                    if (userInput.equals(ConsoleUI.BigYes) || userInput.equals(ConsoleUI.SmallYes)) {
                        playAgain = true;
                        goodInput = true;
                    } else if (userInput.equals(BigNo) || userInput.equals(SmallNo)) {
                        playAgain = false;
                        goodInput = true;
                    } else {
                        System.out.println("Please enter a valid input - Y or y for yes, N or n for no.");
                    }
                }
            }
        }

        System.out.println("Thank you for playing\nGoodbye!");
    }

    private MenuOption getUserMenuSelection() {
        MenuOption userSelection = null;
        int userInteger = 0;
        boolean goodSelection = false;

        String userInput = "";
        while(!goodSelection){
            showMenuOptions();

            userInteger = getIntegerInput(9, "Please enter the menu selection");

            userSelection = getAppropriateMenuSelection(userInteger);

            goodSelection = checkSelectionValidity(userSelection);

        }

        return userSelection;
    }

    private boolean checkSelectionValidity(MenuOption userSelection) {
        boolean validSelection = true;

        if(GameLogic == null){ // TODO: Take care of this, add a check in the future method save notifying the user
            if(userSelection == MenuOption.SAVE){
                validSelection = false;
            }
        }

        return validSelection;
    }

    private MenuOption getAppropriateMenuSelection(int userInteger) {
        MenuOption appropriateSelection = null;

        switch (userInteger){
            case 1:
                appropriateSelection = MenuOption.LOAD_XML;
                break;
            case 2:
                appropriateSelection = MenuOption.START;
                break;
            case 3:
                appropriateSelection = MenuOption.SETTINGS;
                break;
            case 4:
                appropriateSelection = MenuOption.MAKE_MOVE;
                break;
            case 5:
                appropriateSelection = MenuOption.HISTORY;
                break;
            case 6:
                appropriateSelection = MenuOption.UNDO;
                break;
            case 7:
                appropriateSelection = MenuOption.SAVE;
                break;
            case 8:
                appropriateSelection = MenuOption.LOAD;
                break;
            case 9:
                appropriateSelection = MenuOption.EXIT;
                break;
        }

        return appropriateSelection;
    }

    private static void showMenuOptions() {
        for (MenuOption menuOption : MenuOption.values()) {
            System.out.println(menuOption);
        }
    }

    private void startGame() {
        if(GameLogic == null){
            System.out.println("Can't start the game yet since no XML file has been loaded yet.");
        } else {
            getBothPlayerData();

            showBoard(GameLogic.getBoard());
        }
    }

    private void getBothPlayerData(){
        Scanner input = new Scanner(System.in);

        System.out.println("Player1:");
        getPlayerData(input);

        System.out.println("Player2:");
        getPlayerData(input);

    }

    private void getPlayerData(Scanner inputScanner){
        String userInput;
        boolean playerIsBot = false;
        System.out.println("Is the player a bot? (please input Y or y for yes, or N or n for no)");
        boolean goodInput = false;

        do{
            userInput = inputScanner.nextLine();
            if(userInput.equals(BigYes) || userInput.equals(SmallYes)){
                playerIsBot = true;
                goodInput = true;
            }
            else if(userInput.equals(BigNo) || userInput.equals(SmallNo)){
                playerIsBot = false;
                goodInput = true;
            } else {
                System.out.println("Please enter a valid input - Y or y for yes, N or n for no.");
            }
        } while(goodInput == false);

        System.out.println("Please enter the player's name:");
        userInput = inputScanner.nextLine();

        GameLogic.addPlayer(userInput, playerIsBot);
    }

    private void showGameProperties(){
        if(GameLogic == null){
            System.out.println("No game has been loaded yet, so there are no settings to show!");
        } else {
            int[][] board = GameLogic.getBoard();

            showBoard(board);

            int N = GameLogic.getN();

            System.out.println("The goal is " + N + " in a row!");

            boolean isActive = GameLogic.isActive();

            if (isActive == true) {
                System.out.println("The game is active!");
                String currentPlayer = GameLogic.getCurrentPlayerName();
                // get the current player's symbol
                char currentPlayerSymbol = GameLogic.currentPlayerNumber() == 1 ? player1Symbol : player2Symbol;
                int currentPlayerTurns = GameLogic.getCurrentPlayerTurnsPlayed();

                System.out.println("It's " + currentPlayer + "'s turn.");
                System.out.println("His symbol is " + currentPlayerSymbol);
                System.out.println("He has taken " + currentPlayerTurns + " turns");

                String otherPlayer = GameLogic.getOtherPlayersName();
                // get the other player's symbol
                char otherPlayerSymbol = currentPlayerSymbol == player1Symbol ? player2Symbol : player1Symbol;
                int otherPlayerTurns = GameLogic.getOtherPlayerTurnsPlayed();

                System.out.println(otherPlayer + " is next.");
                System.out.println("His symbol is " + otherPlayerSymbol);
                System.out.println("He has taken " + otherPlayerTurns + " turns");

                // get the elapsed time

            } else {
                System.out.println("The game is inactive.");
            }

        }
    }

    private void showBoard(int[][] board){
        int rows = GameLogic.getRows();
        int cols = GameLogic.getCols();

        // each row
        for(int i = 0; i < rows; i++){
            // number of the row
            System.out.print((rows - i) + "|");
            for(int j = 0; j < cols; j++){
                char tileSymbol;
                if(board[i][j] == 0){
                    tileSymbol = 0;
                } else {
                    tileSymbol = board[i][j] == 1 ? player1Symbol : player2Symbol;
                }

                if(tileSymbol == 0){
                    tileSymbol = ' ';
                }

                if(cols > 8){
                    System.out.print(tileSymbol + " |");
                } else {
                    System.out.print(tileSymbol + "|");
                }
            }
            System.out.println();

            // separator between each row
            if(i < rows-1) {
                printSeparatorLine(cols);
            }

        }
        // separator between bottom row and the rest
        printSeparatorLine(cols);

        // bottom row
        System.out.print(" |");
        for(int i = 0; i < cols; i++){
            if(cols > 8){
                if(i > 8){
                    System.out.print((i + 1) + "|");
                } else {
                    System.out.print((i + 1) + " |");
                }
            } else {
                System.out.print((i + 1) + "|");
            }
        }
        System.out.println();
    }

    private void printSeparatorLine(int cols){
        char separator = '-';

        System.out.print(separator);
        for(int i =0; i < cols; i++){
            if(cols > 8){
                System.out.print(separator);
                System.out.print(separator);
                System.out.print(separator);

            } else {
                System.out.print(separator);
                System.out.print(separator);
            }
        }

        if(cols > 8){
            System.out.print(separator);
        }
        System.out.println();
    }

    private void makeMove(){
        if(GameLogic.isActive() == false){
            System.out.println("The game isn't active yet, so you can't make a move!");
        } else {
            if (GameLogic.isCurrentPlayerBot()) {
                GameLogic.takeBotTurn();
            } else {
                int col = getIntegerInput(GameLogic.getCols(), "Please enter the column of your choice");

                if (GameLogic.moveIsValid(col - 1)) {
                    GameLogic.takePlayerTurn(col - 1);
                } else {
                    System.out.println("The selected column is full, please enter a column which isn't.");
                }
            }

            showBoard(GameLogic.getBoard());
        }
    }

    private void showTurnHistory(){
        if(GameLogic.isTurnHistoryEmpty()){
            System.out.println("No turns have been made so far, so there is nothing to show.");
        } else {
            List<Integer> turnHistory = GameLogic.getTurnHistory();

            for(int i = 0 ; i < turnHistory.size(); i++){
                String currentPlayer = i%2 == 0? GameLogic.getPlayer1Name() : GameLogic.getPlayer2Name();
                int column = turnHistory.get(i);
                System.out.println((i+1) + ")" + currentPlayer + " Chose column " + column);
            }
        }
    }

    private int getIntegerInput(int valueLimit, String inputPrompt){
        int playerChoice = 0;
        boolean done = false;
        String userInput;

        while(!done) {
            System.out.println(inputPrompt + ":(A number between 1 and " + valueLimit + ")");
            userInput = inScanner.nextLine();

            try {
                playerChoice = Integer.parseInt(userInput);
                if((playerChoice <= 0) || (valueLimit < playerChoice)){
                    System.out.println("Please enter a valid number between 1 and " + valueLimit + ".");
                } else {
                    done = true;
                }
            } catch (NumberFormatException e){
                System.out.println("Please enter a valid number between 1 and " + valueLimit + ".");
            }
        }

        return playerChoice;
    }

    private enum MenuOption {
        LOAD_XML {
            @Override
            public String toString() {
                return "1)Load XML File";
            }

            boolean makeAction(ConsoleUI conosle){
                // console.loadXMLFile()
                return false;
            }
        },
        START {
            @Override
            public String toString() {
                return "2)Start Game";
            }

            boolean makeAction(ConsoleUI conosle){
                conosle.startGame();
                return false;
            }
        },
        SETTINGS {
            @Override
            public String toString() {
                return "3)Show Game Settings";
            }

            boolean makeAction(ConsoleUI conosle){
                conosle.showGameProperties();
                return false;
            }
        },
        MAKE_MOVE{
            @Override
            public String toString() {
                return "4)Make Move";
            }

            boolean makeAction(ConsoleUI conosle){
                conosle.makeMove();

                return conosle.isGameOver();
            }
        },
        HISTORY{
            @Override
            public String toString() {
                return "5)Show Turn History";
            }

            boolean makeAction(ConsoleUI conosle){
                conosle.showTurnHistory();
                return false;
            }
        },
        UNDO {
            @Override
            public String toString() {
                return "6)Undo Last Turn";
            }

            boolean makeAction(ConsoleUI conosle){
                conosle.undoTurn();

                return false;
            }
        },
        SAVE {
            @Override
            public String toString() {
                return "7)Save Game";
            }

            boolean makeAction(ConsoleUI conosle){
                //conosle.saveGame();

                return false;
            }
        },
        LOAD{
            @Override
            public String toString() {
                return "8)Load Game";
            }

            boolean makeAction(ConsoleUI conosle){
                //conosle.loadGame();

                return conosle.isGameOver();
            }
        },
        EXIT {
            @Override
            public String toString() {
                return "9)Exit Game";
            }

            boolean makeAction(ConsoleUI conosle){
                //conosle.exitGame();
                return true;
            }
        };

        abstract boolean makeAction(ConsoleUI conosle);
    }

    private void undoTurn() {
        GameLogic.undoTurn();
    }

    private boolean isGameOver() {
        return GameLogic.isGameOver();
    }

}
