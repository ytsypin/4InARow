package Exceptions;

public class InvalidTargetException extends Throwable {
    public int nValue;
    public InvalidTargetException(int n) {
        nValue = n;
    }
}
