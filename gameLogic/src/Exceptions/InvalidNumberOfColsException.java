package Exceptions;

import java.math.BigInteger;

public class InvalidNumberOfColsException extends Throwable {
    public int colValue;
    public InvalidNumberOfColsException(int cols) {
        colValue = cols;
    }
}
