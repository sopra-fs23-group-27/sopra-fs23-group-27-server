package ch.uzh.ifi.hase.soprafs23.exceptions;

public class SopraServiceException extends Exception {
    public SopraServiceException(String errorMessage) {
        super(errorMessage);
    }
}