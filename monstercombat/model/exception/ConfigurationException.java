package edu.kit.kastel.monstercombat.model.exception;

/**
 * Exception thrown when there's an issue with the configuration.
 */
public class ConfigurationException extends GameException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new configuration exception with the specified message.
     *
     * @param message the detail message
     */
    public ConfigurationException(String message) {
        super(message);
    }
}