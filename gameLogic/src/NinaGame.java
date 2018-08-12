import Exceptions.InvalidNumberOfColsException;
import Exceptions.InvalidNumberOfRowsException;
import Exceptions.InvalidTargetException;
import Exceptions.WrongFileException;
import resources.generated.GameDescriptor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class NinaGame implements Serializable {
    private int N;
    private NinaBoard gameBoard;
    private Participant participant1 = null;
    private Participant participant2 = null;
    private boolean isActive = false;
    private List<Turn> turnHistory;
    private Participant currentParticipant = null;
    private boolean gameOver = false;
    private boolean winnerFound = false;
    private static int noMove = -1;
    private boolean loadSuccessful = false;


    public boolean isActive() {
        return isActive;
    }

    public int getN() {
        return N;
    }

    public NinaGame(int N, int rows, int cols) {
        this.N = N;
        gameBoard = new NinaBoard(rows, cols);
        turnHistory = new LinkedList<>();
    }

    public boolean isWinnerFound() {
        return winnerFound;
    }

    public void addParticipant(String name, boolean isBot) {
        Participant participant = new Participant(name, isBot);

        if (participant1 == null) {
            participant1 = participant;
        } else {
            participant2 = participant;
        }

        // Once both participants data is in - the game is officially active.
        if (participant2 != null) {
            isActive = true;
            currentParticipant = participant1;
        }
    }

    public int[][] getBoard() {
        return gameBoard.getBoardTiles();
    }

    public String getCurrentParticipantName() {
        return currentParticipant.getName();
    }

    public int getCurrentParticipantTurnsPlayed() {
        return currentParticipant.getTurnsPlayed();
    }

    public String getOtherParticipantsName() {
        Participant otherParticipant = getOtherParticipant();
        return otherParticipant.getName();
    }

    public int getOtherParticipantTurnsPlayed() {
        Participant otherParticipant = getOtherParticipant();
        return otherParticipant.getTurnsPlayed();
    }

    private Participant getOtherParticipant() {
        if (currentParticipant == participant1) {
            return participant2;
        } else {
            return participant1;
        }
    }

    public int getRows() {
        return gameBoard.getRows();
    }

    public int getCols() {
        return gameBoard.getCols();
    }

    public String getParticipant1Name() {
        return participant1.getName();
    }

    public String getParticipant2Name() {
        return participant2.getName();
    }

    public int currentParticipantNumber() {
        return currentParticipant == participant1 ? 1 : 2;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isTurnHistoryEmpty() {
        return turnHistory.isEmpty();
    }

    public List<Integer> getTurnHistory() {
        LinkedList<Integer> resultingTurnHistory = new LinkedList<>();

        for (Turn turn : turnHistory) {
            resultingTurnHistory.addLast(turn.getCol());
        }

        return resultingTurnHistory;
    }

    public boolean isCurrentParticipantBot() {
        return currentParticipant.isBot();
    }

    public void takeBotTurn() {
        int column = getPossibleColumn();

        if (column == noMove) {
            gameOver = true;
        } else {
            implementTurn(column);
        }
    }

    private void implementTurn(int col) {
        int row = getFirstOpenRow(col);
        Turn currTurn = new Turn(row, col);
        int currParticipantSymbol = currentParticipant.equals(participant1) ? 1 : 2;
        gameBoard.applyTurn(currTurn, currParticipantSymbol);
        turnHistory.add(currTurn);

        currentParticipant.addTurnPlayed();

        boolean checkedCells[][] = new boolean[gameBoard.getRows()][gameBoard.getCols()];

        for(int i = 0; i < gameBoard.getRows(); i++){
            for(int j = 0; j < gameBoard.getCols(); j++){
                checkedCells[i][j] = false;
            }
        }

        checkForWinner(row, col, currParticipantSymbol, checkedCells);

        if(!winnerFound) {
            changeCurrentParticipant();
            gameOver = (getPossibleColumn() == noMove);
        }
    }

    // direction: /
    private void checkForWinningAcrossRight(int row, int col, int currParticipantSymbol, boolean[][] checkedCells) {
        int currStreak = 1;
        boolean keepLooking = true;
        int lastRow = gameBoard.getRows()-1;
        int lastCol = gameBoard.getCols()-1;

        // Check the first way starting from the starting cell
        int currRow = row;
        int currCol = col;
        while((currRow < lastRow) && (currCol > 0) && (!winnerFound) && keepLooking){
            currRow++;
            currCol--;
            checkedCells[currRow][currCol] = true;
            if(gameBoard.getTileSymbol(currRow,currCol) == currParticipantSymbol){
                currStreak++;
            } else {
                keepLooking = false;
            }

            if(currStreak == N){
                winnerFound = true;
            }
        }

        // Get back to the starting cell, check the other way
        keepLooking = true;
        currRow = row;
        currCol = col;
        while((currRow > 0) && (currCol < lastCol) && !winnerFound && keepLooking){
            currRow--;
            currCol++;
            checkedCells[currRow][currCol] = true;
            if(gameBoard.getTileSymbol(currRow,currCol) == currParticipantSymbol){
                currStreak++;
            } else {
                keepLooking = false;
            }

            if(currStreak == N){
                winnerFound = true;
            }
        }
    }

    // direction: \
    private void checkForWinningAcrossLeft(int row, int col, int currParticipantSymbol, boolean[][] checkedCells) {
        int currStreak = 1;
        boolean keepLooking = true;
        int lastRow = gameBoard.getRows()-1;
        int lastCol = gameBoard.getCols()-1;

        // Check the first way starting from the starting cell
        int currRow = row;
        int currCol = col;
        while((currRow < lastRow) && (currCol < lastCol) && (!winnerFound) && keepLooking){
            currRow++;
            currCol++;
            checkedCells[currRow][currCol] = true;
            if(gameBoard.getTileSymbol(currRow,currCol) == currParticipantSymbol){
                currStreak++;
            } else {
                keepLooking = false;
            }

            if(currStreak == N){
                winnerFound = true;
            }
        }

        // Get back to the starting cell, check the other way
        keepLooking = true;
        currRow = row;
        currCol = col;
        while((currRow > 0) && (currCol > 0) && !winnerFound && keepLooking){
            currRow--;
            currCol--;
            checkedCells[currRow][currCol] = true;
            if(gameBoard.getTileSymbol(currRow,currCol) == currParticipantSymbol){
                currStreak++;
            } else {
                keepLooking = false;
            }

            if(currStreak == N){
                winnerFound = true;
            }
        }
    }

    private void checkForWinningCol(int row, int col, int currParticipantSymbol, boolean[][] checkedCells) {
        int currStreak = 1;
        boolean keepLooking = true;
        int lastCol = gameBoard.getCols()-1;

        // Check the first way starting from the starting cell
        int currRow = row;
        int currCol = col;
        while((currCol < lastCol) && (!winnerFound) && keepLooking){
            currCol++;
            checkedCells[currRow][currCol] = true;
            if(gameBoard.getTileSymbol(currRow,currCol) == currParticipantSymbol){
                currStreak++;
            } else {
                keepLooking = false;
            }

            if(currStreak == N){
                winnerFound = true;
            }
        }

        // Get back to the starting cell, check the other way
        keepLooking = true;
        currRow = row;
        currCol = col;
        while((currCol > 0) && !winnerFound && keepLooking){
            currCol--;
            checkedCells[currRow][currCol] = true;
            if(gameBoard.getTileSymbol(currRow,currCol) == currParticipantSymbol){
                currStreak++;
            } else {
                keepLooking = false;
            }

            if(currStreak == N){
                winnerFound = true;
            }
        }
    }

    private void checkForWinningRow(int row, int col, int currParticipantSymbol, boolean[][] checkedCells) {
        int currStreak = 1;
        int lastRow = gameBoard.getRows()-1;
        boolean keepLooking = true;

        // Check the first way starting from the starting cell
        int currRow = row;
        int currCol = col;
        while((currRow < lastRow) && (!winnerFound) && keepLooking){
            currRow++;
            checkedCells[currRow][currCol] = true;
            if(gameBoard.getTileSymbol(currRow,currCol) == currParticipantSymbol){
                currStreak++;
            } else {
                keepLooking = false;
            }

            if(currStreak == N){
                winnerFound = true;
            }
        }

        // Get back to the starting cell, check the other way
        keepLooking = true;
        currRow = row;
        currCol = col;
        while((currRow > 0) && !winnerFound && keepLooking){
            currRow--;
            checkedCells[currRow][currCol] = true;
            if(gameBoard.getTileSymbol(currRow,currCol) == currParticipantSymbol){
                currStreak++;
            } else {
                keepLooking = false;
            }

            if(currStreak == N){
                winnerFound = true;
            }
        }
    }

    private void changeCurrentParticipant() {
        if (currentParticipant.equals(participant1)) {
            currentParticipant = participant2;
        } else {
            currentParticipant = participant1;
        }
    }

    private int getFirstOpenRow(int column) {
        return gameBoard.getFirstOpenRow(column);
    }

    private int getPossibleColumn() {
        List<Integer> possibleMoves = getPossibleMoves();

        if (possibleMoves.size() == 0) {
            return noMove;
        } else {
            Random random = new Random();
            return possibleMoves.get(random.nextInt(possibleMoves.size()));
        }
    }

    private List<Integer> getPossibleMoves() {
        LinkedList<Integer> possibleMoves = new LinkedList<>();
        int numOfCols = gameBoard.getCols();

        for(int i = 0; i < numOfCols; i++){
            if(moveIsValid(i)){
                possibleMoves.addLast(i);
            }
        }

        return possibleMoves;
    }

    public void takeParticipantTurn(int col) {
        // implement the turn
        // presume the column is valid, and there is a possible move.
        implementTurn(col);
    }

    public boolean moveIsValid(int col) {
        return !((getFirstOpenRow(col) < 0) || (gameBoard.getRows() < getFirstOpenRow(col)));
    }

    public void undoTurn() {
        Turn lastTurn = turnHistory.get(turnHistory.size()-1);

        turnHistory.remove(lastTurn);

        gameBoard.nullifyCell(lastTurn.row, lastTurn.col);
        changeCurrentParticipant();
    }

    public static NinaGame extractXML(String fileName) throws WrongFileException, InvalidNumberOfRowsException, InvalidNumberOfColsException, InvalidTargetException {
        NinaGame retGame;
        try {
            File file = new File(fileName);

            JAXBContext jaxbContext = JAXBContext.newInstance(GameDescriptor.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            GameDescriptor gameDescriptor = (GameDescriptor) jaxbUnmarshaller.unmarshal(file);

            int rows = gameDescriptor.getGame().getBoard().getRows();

            int cols = gameDescriptor.getGame().getBoard().getColumns().intValue();

            int N = gameDescriptor.getGame().getTarget().intValue();

            if(rows < 5 || 50 < rows){
                throw new InvalidNumberOfRowsException(rows);
            }

            if(cols < 6 || 30 < cols){
                throw new InvalidNumberOfColsException(cols);
            }

            if(N >= Math.min(rows, cols) || N < 2){
                throw new InvalidTargetException(N);
            }

            retGame = new NinaGame(N, rows, cols);
            retGame.loadSuccessful = true;
        } catch(JAXBException e){
            throw new WrongFileException();
        }

        return retGame;
    }

    public void popOut(int col){
        if(gameBoard.colIsEmpty(col)){
            // to-do: throw new ColumnIsEmptyException();
        } else {
            gameBoard.popOut(col);

            int numOfRows = gameBoard.getRows();

            boolean[][] checkedCells = new boolean[gameBoard.getRows()][gameBoard.getCols()];

            int currParticipantSymbol;

            for(int i = numOfRows-1; i > 0 && !winnerFound; i--){
                nullifyCheckedCells(checkedCells);
                currParticipantSymbol = gameBoard.getTileSymbol(i, col);
                checkForWinningAcrossLeft(i, col, currParticipantSymbol, checkedCells);
                if(winnerFound){
                    currentParticipant = currParticipantSymbol == 1? participant1 : participant2;
                }
            }
        }
    }

    private void checkForWinner(int row, int col, int currParticipantSymbol, boolean[][] checkedCells){
        checkForWinningRow(row, col, currParticipantSymbol, checkedCells);
        if(!winnerFound){
            checkForWinningCol(row, col, currParticipantSymbol, checkedCells);
        }
        if(!winnerFound){
            checkForWinningAcrossLeft(row,col,currParticipantSymbol,checkedCells);
        }
        if(!winnerFound){
            checkForWinningAcrossRight(row,col,currParticipantSymbol,checkedCells);
        }
    }

    private void nullifyCheckedCells(boolean[][] checkedCells) {
        for(int i = 0; i < gameBoard.getRows(); i++){
            for(int j = 0; j < gameBoard.getCols(); j++){
                checkedCells[i][j] = false;
            }
        }
    }

    public boolean successfulLoad() {
        return loadSuccessful;
    }
}
