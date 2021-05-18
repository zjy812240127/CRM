package com.zjut.exception;

public class LoginException extends MyUserException {
    public LoginException(String message) {
        super(message);
    }

    public LoginException() {
        super();
    }
}
