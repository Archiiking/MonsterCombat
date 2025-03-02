package edu.kit.kastel.monstercombat.view.command;

/**
 * Interface for commands that can be executed by the user.
 */
public interface Command {
    /**
     * Executes the command.
     *
     * @return true if the command was executed successfully, false otherwise
     */
    boolean execute();
}