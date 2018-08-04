import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class NinaGame implements Serializable {
    private int N;
    private NinaBoard gameBoard;
    private Participant participant1 = null;
    private Participant participant2 = null;
    private boolean gameSettingsHaveBeenLoaded;
    private boolean isActive = false;
    private List<Turn> turnHistory;
    private Participant currentParticipant = null;
    private boolean gameOver = false;
    private boolean winnerFound = false;
    private static int noMove = -1;

    private long startTime;

    public boolean isActive() {
        return isActive;
    }

    public int getN() {
        return N;
    }

    public NinaGame(int N, int rows, int cols) {
        this.N = N;
        gameBoard = new NinaBoard(rows, cols);
        startTime = System.currentTimeMillis();
        gameSettingsHaveBeenLoaded = true;
        turnHistory = new LinkedList<>();
    }

    public boolean player1AlreadySet() {
        return participant1 == null;
    }

    public void addPlayer(String name, boolean isBot) {
        Participant participant = new Participant(name, isBot);

        if (participant1 == null) {
            participant1 = participant;
        } else {
            participant2 = participant;
        }

        // Once both players data is in - the game is officially active.
        if (participant2 != null) {
            isActive = true;
            currentParticipant = participant1;
        }
    }

    public int[][] getBoard() {
        return gameBoard.getBoardTiles();
    }

    public String getCurrentPlayerName() {
        return currentParticipant.getName();
    }

    public int getCurrentPlayerTurnsPlayed() {
        return currentParticipant.getTurnsPlayed();
    }

    public String getOtherPlayersName() {
        Participant otherParticipant = getOtherPlayer();
        return otherParticipant.getName();
    }

    public int getOtherPlayerTurnsPlayed() {
        Participant otherParticipant = getOtherPlayer();
        return otherParticipant.getTurnsPlayed();
    }

    private Participant getOtherPlayer() {
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

    public String getPlayer1Name() {
        return participant1.getName();
    }

    public String getPlayer2Name() {
        return participant2.getName();
    }

    public int currentPlayerNumber() {
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

    public boolean isCurrentPlayerBot() {
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
        // presume the column given is exact
        int firstCheckedTile = 1;
        int row = getFirstOpenRow(col);
        Turn currTurn = new Turn(row, col);
        int currPlayerSymbol = currentParticipant.equals(participant1) ? 1 : 2;
        gameBoard.applyTurn(currTurn, currPlayerSymbol);
        turnHistory.add(currTurn);

        currentParticipant.addTurnPlayed();

        boolean checkedCells[][] = new boolean[gameBoard.getRows()][gameBoard.getCols()];

        for(int i = 0; i < gameBoard.getRows(); i++){
            for(int j = 0; j < gameBoard.getCols(); j++){
                checkedCells[i][j] = false;
            }
        }
        checkForWinner(row, col, firstCheckedTile, currPlayerSymbol, checkedCells);

        if(!gameOver) {
            changeCurrentPlayer();
        }
    }

    private void changeCurrentPlayer() {
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
        int emptyColumn = noMove;
        boolean done = false;
        int totalColumns = gameBoard.getCols();
        for (int i = 0; (i < totalColumns) && (!done); i++) {
            if (!gameBoard.colIsFull(i)) {
                emptyColumn = i;
                done = true;
            }
        }

        return emptyColumn;
    }

    public void takePlayerTurn(int col) {
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

    private void checkForWinner(int row, int col, int currCount, int currPlayerSymbol, boolean[][] checkedCells) {
        checkedCells[row][col] = true;
        int empty = 0;
        if(currCount == N){
            winnerFound = true;
            gameOver = true;
        } else {
            if(row > empty){
                if(!checkedCells[row-1][col] && gameBoard.getTileSymbol(row-1, col) == currPlayerSymbol){
                    checkForWinner(row-1, col, ++currCount, currPlayerSymbol, checkedCells);
                }
            }

            if(row < gameBoard.getRows()-1){
                if(!checkedCells[row+1][col] && gameBoard.getTileSymbol(row+1, col) == currPlayerSymbol){
                    checkForWinner(row+1, col, ++currCount, currPlayerSymbol, checkedCells);
                }
            }

            if (col > empty) { // col > 0
                if(!checkedCells[row][col-1] && gameBoard.getTileSymbol(row, col-1) == currPlayerSymbol){
                    checkForWinner(row, col-1, ++currCount, currPlayerSymbol, checkedCells);
                }

                if(row > empty){
                    if(!checkedCells[row-1][col-1] && gameBoard.getTileSymbol(row-1, col-1) == currPlayerSymbol){
                        checkForWinner(row-1, col-1, ++currCount, currPlayerSymbol, checkedCells);
                    }
                }

                if(row < gameBoard.getRows()-1){
                    if(!checkedCells[row+1][col-1] && gameBoard.getTileSymbol(row+1, col-1) == currPlayerSymbol){
                        checkForWinner(row+1, col-1, ++currCount, currPlayerSymbol, checkedCells);
                    }
                }
            }

            if(col < gameBoard.getCols()-1){
                if(!checkedCells[row][col+1] && gameBoard.getTileSymbol(row, col+1) == currPlayerSymbol){
                    checkForWinner(row, col+1, ++currCount, currPlayerSymbol, checkedCells);
                }

                if(row > empty){
                    if(!checkedCells[row-1][col+1] && gameBoard.getTileSymbol(row-1, col+1) == currPlayerSymbol){
                        checkForWinner(row-1, col+1, ++currCount, currPlayerSymbol, checkedCells);
                    }
                }

                if(row < gameBoard.getRows()-1){
                    if(!checkedCells[row+1][col+1] && gameBoard.getTileSymbol(row+1, col+1) == currPlayerSymbol){
                        checkForWinner(row+1, col+1, ++currCount, currPlayerSymbol, checkedCells);
                    }
                }

            }
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
}
