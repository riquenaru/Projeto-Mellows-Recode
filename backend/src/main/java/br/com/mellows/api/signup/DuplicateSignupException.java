package br.com.mellows.api.signup;

public class DuplicateSignupException extends RuntimeException {
    public DuplicateSignupException() { super("Este e-mail já está cadastrado."); }
}
