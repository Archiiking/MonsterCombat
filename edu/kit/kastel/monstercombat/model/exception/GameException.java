package edu.kit.kastel.monstercombat.model.exception;

import java.io.Serial;

/**
 * Base exception for all game-related exceptions.
 * @author ursxd
 */
public class GameException extends Exception {
    @Serial
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