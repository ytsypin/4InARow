package Exceptions;

public class InvalidNumberOfRowsException extends Throwable {
    int rowValue;
    public InvalidNumberOfRowsException(int rows) {
        rowValue = rows;
    }
}
