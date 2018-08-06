import Exceptions.InvalidNumberOfColsException;
import Exceptions.InvalidNumberOfRowsException;
import Exceptions.InvalidTargetException;
import resources.generated.GameDescriptor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
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

    public boolean participant1AlreadySet() {
        return participant1 == null;
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
        List<Integer> resultingTurnHistory = new LinkedList<>();

        for (Turn turn : turnHistory) {
            ((LinkedList<Integer>) resultingTurnHistory).addLast(turn.getCol());
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
        int currCount = 0;

        checkForWinningRow(row, col, currCount, currParticipantSymbol, checkedCells);
        if(!winnerFound){
            checkForWinningCol(row, col, currCount, currParticipantSymbol, checkedCells);
        }
        if(!winnerFound){
            checkForWinningAcrossLeft(row,col,currCount,currParticipantSymbol,checkedCells);
        }
        if(!winnerFound){
            checkForWinningAcrossRight(row,col,currCount,currParticipantSymbol,checkedCells);
        }

        if(!winnerFound) {
            changeCurrentParticipant();
            gameOver = (getPossibleColumn() == noMove);
        }
    }

    // direction: /
    private void checkForWinningAcrossRight(int row, int col, int currCount, int currParticipantSymbol, boolean[][] checkedCells) {
        checkedCells[row][col] = true;
        int empty = 0;
        if(currCount == N && !gameOver) {
            winnerFound = true;
            gameOver = true;
        } else if (!gameOver) {
            if (col > empty) { // col > 0
                if(row < gameBoard.getRows()-1){
                    if(!checkedCells[row+1][col-1] && gameBoard.getTileSymbol(row+1, col-1) == currParticipantSymbol){
                        checkForWinningAcrossRight(row+1, col-1, currCount+1, currParticipantSymbol, checkedCells);
                    }
                }
            }

            if(col < gameBoard.getCols()-1) {
                if (row > empty) {
                    if (!checkedCells[row - 1][col + 1] && gameBoard.getTileSymbol(row - 1, col + 1) == currParticipantSymbol) {
                        checkForWinningAcrossRight(row - 1, col + 1, currCount+1, currParticipantSymbol, checkedCells);
                    }
                }
            }
        }
    }

    // direction: \
    private void checkForWinningAcrossLeft(int row, int col, int currCount, int currParticipantSymbol, boolean[][] checkedCells) {
        checkedCells[row][col] = true;
        int empty = 0;
        if(currCount == N && !gameOver) {
            winnerFound = true;
            gameOver = true;
        } else if (!gameOver) {
            if (col > empty) { // col > 0
                if (row > empty) {
                    if (!checkedCells[row - 1][col - 1] && gameBoard.getTileSymbol(row - 1, col - 1) == currParticipantSymbol) {
                        checkForWinningAcrossLeft(row - 1, col - 1, currCount+1, currParticipantSymbol, checkedCells);
                    }
                }

                if(col < gameBoard.getCols()-1) {
                    if(row < gameBoard.getRows()-1){
                        if(!checkedCells[row+1][col+1] && gameBoard.getTileSymbol(row+1, col+1) == currParticipantSymbol){
                            checkForWinningAcrossLeft(row+1, col+1, currCount+1, currParticipantSymbol, checkedCells);
                        }
                    }
                }
            }
        }
    }

    private void checkForWinningCol(int row, int col, int currCount, int currParticipantSymbol, boolean[][] checkedCells) {
        checkedCells[row][col] = true;
        int empty = 0;
        if(currCount == N && !gameOver) {
            winnerFound = true;
            gameOver = true;
        } else if (!gameOver) {
            if (col > empty) {
                if (!checkedCells[row][col - 1] && gameBoard.getTileSymbol(row, col - 1) == currParticipantSymbol) {
                    checkForWinningCol(row, col - 1, currCount+1, currParticipantSymbol, checkedCells);
                }
            }

            if (col < gameBoard.getCols() - 1) {
                if(!checkedCells[row][col+1] && gameBoard.getTileSymbol(row, col+1) == currParticipantSymbol){
                    checkForWinningCol(row, col+1, currCount+1, currParticipantSymbol, checkedCells);
                }
            }
        }
    }

    private void checkForWinningRow(int row, int col, int currCount, int currParticipantSymbol, boolean[][] checkedCells) {
        checkedCells[row][col] = true;
        int empty = 0;
        if(currCount == N && !gameOver) {
            winnerFound = true;
            gameOver = true;
        } else if(!gameOver) {
            if(row < gameBoard.getRows()-1){
                if(!checkedCells[row+1][col] && gameBoard.getTileSymbol(row+1, col) == currParticipantSymbol){
                    checkForWinningRow(row+1, col, currCount+1, currParticipantSymbol, checkedCells);
                }
            }
            if(row > empty){
                if(!checkedCells[row-1][col] && gameBoard.getTileSymbol(row-1, col) == currParticipantSymbol){
                    checkForWinningRow(row-1, col, currCount+1, currParticipantSymbol, checkedCells);
                }
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
        List<Integer> possibleMoves = new LinkedList<>();
        int numOfCols = gameBoard.getCols();

        for(int i = 0; i < numOfCols; i++){
            if(moveIsValid(i)){
                ((LinkedList<Integer>) possibleMoves).addLast(i);
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
        if ((getFirstOpenRow(col) < 0) || (gameBoard.getRows() < getFirstOpenRow(col))) {
            return false;
        } else {
            return true;
        }
    }

    public void undoTurn() {
        if (isTurnHistoryEmpty()) {
            System.out.println("No moves have been made, so there is no move to undo yet!");
        } else {
            Turn lastTurn = turnHistory.get(turnHistory.size());

            turnHistory.remove(lastTurn);

            gameBoard.nullifyCell(lastTurn.row, lastTurn.col);
        }
    }

    public static NinaGame extractXML(String fileName) throws InvalidNumberOfRowsException, InvalidNumberOfColsException, InvalidTargetException {
        NinaGame retGame = null;
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
            e.printStackTrace();
        }

        return retGame;
    }

    public boolean successfulLoad() {
        return loadSuccessful;
    }
}
