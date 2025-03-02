package edu.kit.kastel.monstercombat.model.exception;

import java.io.Serial;

/**
 * Exception thrown when there's an issue with a game command.
 */
public class CommandException extends GameException {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new command exception with the specified message.
     *
     * @param message the detail message
     */
    public CommandException(String message) {
        super(message);
    }
}