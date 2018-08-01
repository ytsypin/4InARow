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

        MenuOption userSelection = null;

        while(!exitGame) {
            userSelection = getUserMenuSelection();

            exitGame = userSelection.makeAction(console.GameLogic);
        }

        System.out.println("Thank you for playing\nGoodbye!");
    }

    private static MenuOption getUserMenuSelection() {
        MenuOption userSelection = null;
        boolean goodSelection = false;

        while(!goodSelection){
            showMenuOptions();
        }

        return userSelection;
    }

    private static void showMenuOptions() {
        for (MenuOption menuOption : MenuOption.values()) {
            System.out.println(menuOption);
        }
    }

    private void startGame() {
        boolean endGame = false;
        getBothPlayerData();

        showBoard(GameLogic.getBoard());
        while (!endGame) {
            makeMove();

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
        char symbol;

        GameLogic.addPlayer(userInput, playerIsBot);
    }

    private void showGameProperties(){
        int[][] board = GameLogic.getBoard();

        showBoard(board);

        int N = GameLogic.getN();

        System.out.println("The goal is " + N + " in a row!");

        boolean isActive = GameLogic.isActive();

        if(isActive == true){
            System.out.println("The game is active!");
            String currentPlayer = GameLogic.getCurrentPlayerName();
            // get the current player's symbol
            char currentPlayerSymbol = GameLogic.currentPlayerNumber() == 1? player1Symbol : player2Symbol;
            int currentPlayerTurns = GameLogic.getCurrentPlayerTurnsPlayed();

            System.out.println("It's " + currentPlayer + "'s turn.");
            System.out.println("His symbol is " + currentPlayerSymbol);
            System.out.println("He has taken " + currentPlayerTurns + " turns");

            String otherPlayer = GameLogic.getOtherPlayersName();
            // get the other player's symbol
            char otherPlayerSymbol = currentPlayerSymbol == player1Symbol? player2Symbol : player1Symbol;
            int otherPlayerTurns = GameLogic.getOtherPlayerTurnsPlayed();

            System.out.println(otherPlayer + " is next.");
            System.out.println("His symbol is " + otherPlayerSymbol);
            System.out.println("He has taken " + otherPlayerTurns + " turns");

            // get the elapsed time

        } else {
            System.out.println("The game is inactive.");
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
        if(GameLogic.isCurrentPlayerBot()) {
            GameLogic.takeBotTurn();
        } else {
            int col = getPlayerTurn();

            if(GameLogic.moveIsValid(col - 1)) {
                GameLogic.takePlayerTurn(col - 1);
            } else {
                System.out.println("The selected column is full, please enter a column which isn't.");
            }
        }

        showBoard(GameLogic.getBoard());
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

    private int getPlayerTurn(){
        int playerChoice = 0;
        boolean done = false;
        String userInput;

        while(!done) {
            System.out.println("Please enter the column of your choice:(A number between 1 and " + GameLogic.getCols() + ")");
            userInput = inScanner.nextLine();

            try {
                playerChoice = Integer.parseInt(userInput);
                if((playerChoice <= 0) || (GameLogic.getCols() < playerChoice)){
                    System.out.println("Please enter a valid column number between 1 and " + GameLogic.getCols() + ".");
                } else {
                    done = true;
                }
            } catch (NumberFormatException e){
                System.out.println("Please enter a valid column number between 1 and " + GameLogic.getCols() + ".");
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

            boolean makeAction(Game game){

                return false;
            }
        },
        START {
            @Override
            public String toString() {
                return "2)Start Game";
            }

            boolean makeAction(Game game){

                return false;
            }
        },
        SETTINGS {
            @Override
            public String toString() {
                return "3)Show Game Settings";
            }

            boolean makeAction(Game game){

                return false;
            }
        },
        MAKE_MOVE{
            @Override
            public String toString() {
                return "4)Make Move";
            }

            boolean makeAction(Game game){

                return game.isGameOver();
            }
        },
        HISTORY{
            @Override
            public String toString() {
                return "5)Show Turn History";
            }

            boolean makeAction(Game game){

                return false;
            }
        },
        UNDO {
            @Override
            public String toString() {
                return "6)Undo Last Turn";
            }

            boolean makeAction(Game game){


                return false;
            }
        },
        SAVE {
            @Override
            public String toString() {
                return "7)Save Game";
            }

            boolean makeAction(Game game){

                return false;
            }
        },
        LOAD{
            @Override
            public String toString() {
                return "8)Load Game";
            }

            boolean makeAction(Game game){

                return game.isGameOver();
            }
        },
        EXIT {
            @Override
            public String toString() {
                return "9)Exit Game";
            }

            boolean makeAction(Game game){
                return true;
            }
        };

        abstract boolean makeAction(Game game);
    }

}
