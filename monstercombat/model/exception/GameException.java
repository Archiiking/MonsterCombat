package edu.kit.kastel.monstercombat.model.exception;

/**
 * Base exception for all game-related exceptions.
 */
public class GameException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new game exception with the specified message.
     *
     * @param message the detail message
     */
    public GameException(String message) {
        super(message);
    }
}