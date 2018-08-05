import Exceptions.InvalidNumberOfColsException;
import Exceptions.InvalidNumberOfRowsException;
import Exceptions.InvalidTargetException;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI implements Serializable {
    private NinaGame gameLogic;
    private NinaGame restartCopyGameLogic;
    private Scanner inScanner;

    static private String BigYes = "Y";
    static private String SmallYes = "y";
    static private String BigNo = "N";
    static private String SmallNo = "n";

    static private char participant1Symbol = '$';
    static private char participant2Symbol = '@';

    private long startTime;

    public ConsoleUI() {
        inScanner = new Scanner(System.in);
    }

    public static void main(String[] args){
        ConsoleUI console = new ConsoleUI();

        boolean exitGame = false;
        boolean roundOver;

        MenuOption userSelection;

        while(!exitGame) { // TODO: Track the exit game option, rearrange the logic
            userSelection = console.getUserMenuSelection();

            exitGame = userSelection.makeAction(console); // TODO: Keep track whether the board is full and round over

//            boolean playAgain = false;
//            String userInput;
//            boolean goodInput = false;


            // TODO: When round is over, check if play again - separate into methods?
//            while(!goodInput && !exitGame && roundOver) {
//                if (roundOver) {
//                    System.out.println("Would you like to play again?");
//                    userInput = console.inScanner.nextLine();
//                    if (userInput.equals(ConsoleUI.BigYes) || userInput.equals(ConsoleUI.SmallYes)) {
//                        playAgain = true;
//                        goodInput = true;
//                    } else if (userInput.equals(BigNo) || userInput.equals(SmallNo)) {
//                        playAgain = false;
//                        goodInput = true;
//                    } else {
//                        System.out.println("Please enter a valid input - Y or y for yes, N or n for no.");
//                    }
//                }
//            }
        }

        System.out.println("Thank you for playing\nGoodbye!");
    }

    private MenuOption getUserMenuSelection() {
        MenuOption userSelection = null;
        int userInteger;
        boolean goodSelection = false;

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

        if(gameLogic == null){ // TODO: Take care of this, add a check in the future method save notifying the user
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
        if(gameLogic == null){
            System.out.println("Can't start the game yet since no XML file has been loaded yet.");
        } else {
            getBothParticipantData();

            showBoard(gameLogic.getBoard());

            startTime = System.nanoTime();
        }
    }

    private void getBothParticipantData(){
        Scanner input = new Scanner(System.in);

        System.out.println("Participant1:");
        getParticipantData(input);

        System.out.println("Participant2:");
        getParticipantData(input);

    }

    private void getParticipantData(Scanner inputScanner){
        String userInput;
        boolean participantIsBot = false;
        System.out.println("Is the participant a bot? (please input Y or y for yes, or N or n for no)");
        boolean goodInput = false;

        do{
            userInput = inputScanner.nextLine();
            if(userInput.equals(BigYes) || userInput.equals(SmallYes)){
                participantIsBot = true;
                goodInput = true;
            }
            else if(userInput.equals(BigNo) || userInput.equals(SmallNo)){
                goodInput = true;
            } else {
                System.out.println("Please enter a valid input - Y or y for yes, N or n for no.");
            }
        } while(!goodInput);

        System.out.println("Please enter the participant's name:");
        userInput = inputScanner.nextLine();

        gameLogic.addParticipant(userInput, participantIsBot);
    }

    private void showGameProperties(){
        if(gameLogic == null){
            System.out.println("No game has been loaded yet, so there are no settings to show!");
        } else {
            int[][] board = gameLogic.getBoard();

            showBoard(board);

            int N = gameLogic.getN();

            System.out.println("The goal is " + N + " in a row!");

            boolean isActive = gameLogic.isActive();

            if (isActive) {
                System.out.println("The game is active!");
                String currentParticipant = gameLogic.getCurrentParticipantName();
                // get the current participant's symbol
                char currentParticipantSymbol = gameLogic.currentParticipantNumber() == 1 ? participant1Symbol : participant2Symbol;
                int currentParticipantTurns = gameLogic.getCurrentParticipantTurnsPlayed();

                System.out.println("It's " + currentParticipant + "'s turn.");
                System.out.println("His symbol is " + currentParticipantSymbol);
                System.out.println("He has taken " + currentParticipantTurns + " turns");

                String otherParticipant = gameLogic.getOtherParticipantsName();
                // get the other participant's symbol
                char otherParticipantSymbol = currentParticipantSymbol == participant1Symbol ? participant2Symbol : participant1Symbol;
                int otherParticipantTurns = gameLogic.getOtherParticipantTurnsPlayed();

                System.out.println(otherParticipant + " is next.");
                System.out.println("His symbol is " + otherParticipantSymbol);
                System.out.println("He has taken " + otherParticipantTurns + " turns");

                showElapsedTime();

            } else {
                System.out.println("The game is inactive.");
            }

        }
    }

    private void showElapsedTime() {
        long currentTime = System.nanoTime();

        SimpleDateFormat sdf = new SimpleDateFormat("MM:SS");

        System.out.println("The current elapsed time is: " + sdf.format(new Date(currentTime-startTime)));
    }

    private void showBoard(int[][] board){
        int rows = gameLogic.getRows();
        int cols = gameLogic.getCols();

        // each row
        for(int i = 0; i < rows; i++){
            // number of the row
            if((rows - i) > 9){
                System.out.print((rows - i) + "|");
            } else {
                System.out.print((rows - i) + " |");
            }
            for(int j = 0; j < cols; j++){
                char tileSymbol;
                if(board[i][j] == 0){
                    tileSymbol = 0;
                } else {
                    tileSymbol = board[i][j] == 1 ? participant1Symbol : participant2Symbol;
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
        if(rows < 10) {
            System.out.print(" |");
        } else {
            System.out.print("  |");
        }
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
        if(!gameLogic.isActive()){
            System.out.println("The game isn't active yet, so you can't make a move!");
        } else {
            if (gameLogic.isCurrentParticipantBot()) {
                gameLogic.takeBotTurn();
            } else {
                System.out.println(gameLogic.getCurrentParticipantName() + "'s turn.");
                int col = getIntegerInput(gameLogic.getCols(), "Please enter the column of your choice");

                if (gameLogic.moveIsValid(col - 1)) {
                    gameLogic.takeParticipantTurn(col - 1);
                } else {
                    System.out.println("The selected column is full, please enter a column which isn't.");
                }
            }

            showBoard(gameLogic.getBoard());
        }
    }

    private void showTurnHistory(){
        if(gameLogic.isTurnHistoryEmpty()){
            System.out.println("No turns have been made so far, so there is nothing to show.");
        } else {
            List<Integer> turnHistory = gameLogic.getTurnHistory();

            for(int i = 0 ; i < turnHistory.size(); i++){
                String currentParticipant = i%2 == 0? gameLogic.getParticipant1Name() : gameLogic.getParticipant2Name();
                int column = turnHistory.get(i);
                System.out.println((i+1) + ")" + currentParticipant + " Chose column " + column);
            }
        }
    }

    private int getIntegerInput(int valueLimit, String inputPrompt){
        int participantChoice = 0;
        boolean done = false;
        String userInput;

        while(!done) {
            System.out.println(inputPrompt + ":(A number between 1 and " + valueLimit + ")");
            userInput = inScanner.nextLine();

            try {
                participantChoice = Integer.parseInt(userInput);
                if((participantChoice <= 0) || (valueLimit < participantChoice)){
                    System.out.println("Please enter a valid number between 1 and " + valueLimit + ".");
                } else {
                    done = true;
                }
            } catch (NumberFormatException e){
                System.out.println("Please enter a valid number between 1 and " + valueLimit + ".");
            }
        }

        return participantChoice;
    }

    private enum MenuOption {
        LOAD_XML {
            @Override
            public String toString() {
                return "1)Load XML File";
            }

            boolean makeAction(ConsoleUI console){
                System.out.println("Please enter the full path for an XML file:");
                String filename = console.inScanner.nextLine();
                console.loadXMLFile(filename, console);
                return false;
            }
        },
        START {
            @Override
            public String toString() {
                return "2)Start Game";
            }

            boolean makeAction(ConsoleUI console){
                console.startGame();
                return false;
            }
        },
        SETTINGS {
            @Override
            public String toString() {
                return "3)Show Game Settings";
            }

            boolean makeAction(ConsoleUI console){
                console.showGameProperties();
                return false;
            }
        },
        MAKE_MOVE{
            @Override
            public String toString() {
                return "4)Make Move";
            }

            boolean makeAction(ConsoleUI console){
                console.makeMove();

                return console.isGameOver();
            }
        },
        HISTORY{
            @Override
            public String toString() {
                return "5)Show Turn History";
            }

            boolean makeAction(ConsoleUI console){
                console.showTurnHistory();
                return false;
            }
        },
        UNDO {
            @Override
            public String toString() {
                return "6)Undo Last Turn";
            }

            boolean makeAction(ConsoleUI console){
                console.undoTurn();

                return false;
            }
        },
        SAVE {
            @Override
            public String toString() {
                return "7)Save Game";
            }

            boolean makeAction(ConsoleUI console){
                // TODO: console.saveGame();

                return false;
            }
        },
        LOAD{
            @Override
            public String toString() {
                return "8)Load Game";
            }

            boolean makeAction(ConsoleUI console){
                // TODO: console.loadGame();

                return console.isGameOver();
            }
        },
        EXIT {
            @Override
            public String toString() {
                return "9)Exit Game";
            }

            boolean makeAction(ConsoleUI console){
                //console.exitGame();
                return true;
            }
        };

        abstract boolean makeAction(ConsoleUI console);
    }

    private void loadXMLFile(String filename, ConsoleUI console) {
        try {
            console.gameLogic = NinaGame.extractXML(filename);
            console.restartCopyGameLogic = NinaGame.extractXML(filename);
        } catch (InvalidNumberOfRowsException e) {
            System.out.println("Invalid number of rows: " + e.rowValue);
        } catch (InvalidNumberOfColsException e) {
            System.out.println("Invalid number of columns: " + e.colValue);
        } catch (InvalidTargetException e) {
            System.out.println("Invalid target value: " + e.nValue);
        }
    }

    private void undoTurn() {
        gameLogic.undoTurn();
    }

    private boolean isGameOver() {
        return gameLogic.isGameOver();
    }

}
