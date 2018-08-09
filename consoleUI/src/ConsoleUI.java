import Exceptions.InvalidNumberOfColsException;
import Exceptions.InvalidNumberOfRowsException;
import Exceptions.InvalidTargetException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI implements Serializable {
    private NinaGame gameLogic;
    private NinaGame restartCopyGameLogic;
    private boolean exitGame;

    static private String BigYes = "Y";
    static private String SmallYes = "y";
    static private String BigNo = "N";
    static private String SmallNo = "n";

    static private char participant1Symbol = '$';
    static private char participant2Symbol = '@';

    private long startTime;

    private ConsoleUI() {
        exitGame = false;
    }

    public static void main(String[] args){
        ConsoleUI console = new ConsoleUI();

        MenuOption userSelection;

        while(console.keepPlaying()) {
            userSelection = console.getUserMenuSelection();

            userSelection.makeAction(console);

            if(console.gameLogic != null) {
                if (console.gameLogic.isWinnerFound()) { // A Winner was found
                    System.out.println("Congratulations! " + console.gameLogic.getCurrentParticipantName() + " Won!");
                    console.reloadGame(console);
                } else if (console.gameLogic.isGameOver()) { // No winner was found, there are no more possible turns
                    System.out.println("Seems there are no more possible turns, nobody won!");
                    console.reloadGame(console);
                } else if (!console.keepPlaying()) { // Exit was selected
                    System.out.println("Thank you for playing, goodbye!");
                }
            }
        }

        System.out.println("Thank you for playing\nGoodbye!");
    }

    private void reloadGame(ConsoleUI console) {
        String userInput;
        Scanner inputScanner = new Scanner(System.in);
        System.out.println("Would you like to display the turn summary for the last game? Enter Y/y for yes.");
        boolean showSummary = false;

        userInput = inputScanner.nextLine();
        if(userInput.equals(BigYes) || userInput.equals(SmallYes)){
            showSummary = true;
        }

        if(showSummary){
            showTurnHistory();
        }

        System.out.println("Reloading last game's XML file loaded state for replay.");
        console.restartGame();
    }

    private MenuOption getUserMenuSelection() {
        int userInteger;

        showMenuOptions();

        userInteger = getIntegerInput(9, "Please enter the menu selection");

        return getAppropriateMenuSelection(userInteger);
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
            System.out.println("Can't start the game since no XML file has been loaded yet.");
        } else {
            getBothParticipantData();

            showBoard(gameLogic.getBoard());

            startTime = System.nanoTime();
        }
    }

    private void getBothParticipantData(){
        if(gameLogic == null || !gameLogic.isActive()) {
            Scanner input = new Scanner(System.in);

            System.out.println("Participant1:");
            getParticipantData(input);

            System.out.println("Participant2:");
            getParticipantData(input);
        } else {
            System.out.println("The game is already active, please finish it before starting another one.");
        }

    }

    private void getParticipantData(Scanner inputScanner){
        String userInput;
        boolean participantIsBot = false;
        System.out.println("Is the participant a bot? (please input Y or y for yes, or N or n for no)");
        boolean goodInput = false;

        do {
            userInput = inputScanner.nextLine();
            if (userInput.equals(BigYes) || userInput.equals(SmallYes)) {
                participantIsBot = true;
                goodInput = true;
            } else if (userInput.equals(BigNo) || userInput.equals(SmallNo)) {
                goodInput = true;
            } else {
                System.out.println("Please enter a valid input - Y or y for yes, N or n for no.");
            }
        } while (!goodInput);

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
            if((rows - i) > 9 || rows < 9){
                System.out.print((rows - i) + "|");
            } else if (rows > 9) {
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
        if(gameLogic == null || !gameLogic.isActive()){
            System.out.println("The game isn't active yet, so you can't take a turn!");
        } else {
            System.out.println(gameLogic.getCurrentParticipantName() + "'s turn.");
            if (gameLogic.isCurrentParticipantBot()) {
                System.out.println("Taking "+ gameLogic.getCurrentParticipantName() + "'s turn, beep boop boop:");
                gameLogic.takeBotTurn();
            } else {
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
        if(gameLogic == null || gameLogic.isTurnHistoryEmpty()){
            System.out.println("No turns have been made so far, so there is nothing to show.");
        } else {
            List<Integer> turnHistory = gameLogic.getTurnHistory();

            System.out.println("Turn history up until now is:");
            for(int i = 0 ; i < turnHistory.size(); i++){
                String currentParticipant = i%2 == 0? gameLogic.getParticipant1Name() : gameLogic.getParticipant2Name();
                int column = turnHistory.get(i);
                System.out.println((i+1) + ")" + "Player " + currentParticipant + " Chose column " + column);
            }
        }
        showBoard(gameLogic.getBoard());

    }

    private int getIntegerInput(int valueLimit, String inputPrompt){
        int participantChoice = 0;
        boolean done = false;
        String userInput;

        Scanner inScanner = new Scanner(System.in);

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

    private enum MenuOption implements Serializable{
        LOAD_XML {
            @Override
            public String toString() {
                return "1)Load XML File";
            }

            void makeAction(ConsoleUI console){
                Scanner inScanner = new Scanner(System.in);

                System.out.println("Please enter the full path for an XML file:");
                String filename = inScanner.nextLine();
                console.loadXMLFile(filename, console);
                if(console.gameLogic != null && console.gameLogic.successfulLoad()){
                    console.showGameProperties();
                }
            }
        },
        START {
            @Override
            public String toString() {
                return "2)Start Game";
            }

            void makeAction(ConsoleUI console){
                console.startGame();
            }
        },
        SETTINGS {
            @Override
            public String toString() {
                return "3)Show Game Settings";
            }

            void makeAction(ConsoleUI console){
                console.showGameProperties();
            }
        },
        MAKE_MOVE{
            @Override
            public String toString() {
                return "4)Take Turn";
            }

            void makeAction(ConsoleUI console){
                console.makeMove();
            }
        },
        HISTORY{
            @Override
            public String toString() {
                return "5)Show Turn History";
            }

            void makeAction(ConsoleUI console){
                console.showTurnHistory();
            }
        },
        UNDO {
            @Override
            public String toString() {
                return "6)Undo Last Turn";
            }

            void makeAction(ConsoleUI console){
                console.undoTurn();
            }
        },
        SAVE {
            @Override
            public String toString() {
                return "7)Save Game";
            }

            void makeAction(ConsoleUI console){
                console.saveGame();
            }
        },
        LOAD{
            @Override
            public String toString() {
                return "8)Load Game";
            }

            void makeAction(ConsoleUI console){
                console.loadGame();
            }
        },
        EXIT {
            @Override
            public String toString() {
                return "9)Exit Game";
            }

            void makeAction(ConsoleUI console){
                console.setExitGame();
            }
        };

        abstract void makeAction(ConsoleUI console);
    }

    private void loadGame() {
        String filename;
        System.out.println("Please enter the full path of your .sav save file:");
        Scanner inScanner = new Scanner(System.in);

        filename = inScanner.nextLine();

        File fileToOpen = new File(filename);

        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream(fileToOpen))) {
            ConsoleUI readObject = (ConsoleUI)in.readObject();
            this.gameLogic = readObject.gameLogic;
            this.restartCopyGameLogic = readObject.restartCopyGameLogic;

            showGameProperties();
        } catch(FileNotFoundException e){
            System.out.println("Please make sure you entered a valid filename.");
        }
        catch(IOException e){
            System.out.println("There seems to be an issue with the file, please make sure you enterede the correct filename.");
        }
        catch(ClassNotFoundException e){
            System.out.println("Issue loading the file, try again.");
        }
    }

    private void saveGame() {
        if(gameLogic == null){
            System.out.println("There is no game to save yet.");
        } else {
            String fileName;

            System.out.println("Please enter the name for your .dat save file:");
            Scanner inScanner = new Scanner(System.in);

            fileName = inScanner.nextLine();

            fileName = fileName.concat(".dat");
            try (ObjectOutputStream out =
                         new ObjectOutputStream((
                                 new FileOutputStream(fileName)))) {
                out.writeObject(this);
                out.flush();
            } catch (FileNotFoundException e) {
                System.out.println("Please make sure you entered a valid filename.");
            } catch (IOException e) {
                System.out.println("There seems to be an issue with the file, please make sure you enterede the correct filename.");
            }
        }
    }

    private void setExitGame() {
        exitGame = true;
    }

    private void loadXMLFile(String filename, ConsoleUI console) {
        if(gameLogic == null || gameLogic.isGameOver()) {
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
        } else {
            System.out.println("The game is already active, can't load XML files until it is over!");
        }
    }

    private void undoTurn() {
        if(gameLogic == null){
            System.out.println("A game has yet to be loaded.");
        } else {
            gameLogic.undoTurn();
            showBoard(gameLogic.getBoard());
        }
    }

    private boolean keepPlaying() {
        return !exitGame;
    }

    private void restartGame() {
        gameLogic = restartCopyGameLogic;
    }
}
