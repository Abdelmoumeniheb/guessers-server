package dev.m2t.guessers.exception;

public class UserAlreadyInvitedException extends RuntimeException {
    public UserAlreadyInvitedException(String message) {
        super(message);
    }
}
