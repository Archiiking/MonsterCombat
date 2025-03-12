package edu.kit.kastel.monstercombat.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.kit.kastel.monstercombat.model.exception.CommandException;
import edu.kit.kastel.monstercombat.model.Competition;
import edu.kit.kastel.monstercombat.model.Monster;
import edu.kit.kastel.monstercombat.view.command.Command;
import edu.kit.kastel.monstercombat.view.command.CommandAction;
import edu.kit.kastel.monstercombat.view.command.CommandCompetition;
import edu.kit.kastel.monstercombat.view.command.CommandLoad;
import edu.kit.kastel.monstercombat.view.command.CommandPass;
import edu.kit.kastel.monstercombat.view.command.CommandQuit;
import edu.kit.kastel.monstercombat.view.command.CommandShow;
import edu.kit.kastel.monstercombat.view.command.CommandShowActions;
import edu.kit.kastel.monstercombat.view.command.CommandShowMonsters;
import edu.kit.kastel.monstercombat.view.command.CommandShowStats;

/**
 * Handler for user commands.
 */
public class CommandHandler {
    private final UserInterface userInterface;
    private List<Monster> monsters;
    private boolean inCompetition;

    /**
     * Constructs a new command handler.
     *
     * @param userInterface the user interface
     */
    public CommandHandler(UserInterface userInterface) {
        this.userInterface = userInterface;
        this.monsters = new ArrayList<>();
        this.inCompetition = false;
    }

    /**
     * Processes a command.
     *
     * @param input the command input string
     * @return true if the command was executed successfully, false otherwise
     */
    public boolean processCommand(String input) {
        if (input == null || input.trim().isEmpty()) {
            System.out.println("Error, empty command.");
            return false;
        }

        String[] parts = input.trim().split("\\s+");
        String command = parts[0].toLowerCase();

        // Special handling for debug mode responses
        if (isDebugResponse(command, parts)) {
            return true;
        }

        try {
            return executeCommand(command, Arrays.copyOfRange(parts, 1, parts.length));
        } catch (Exception e) {
            System.out.println("Error, " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the input is a response to a debug mode prompt.
     *
     * @param command the command
     * @param parts the command parts
     * @return true if the input is a debug response
     */
    private boolean isDebugResponse(String command, String[] parts) {
        // Check if we're in debug mode
        Competition comp = Competition.getInstance();
        if (comp == null) {
            return false;
        }

        // Check for the two main debug response types
        return command.equals("y") || command.equals("n")
                || (command.matches("\\d+(\\.\\d+)?") && parts.length == 1);
    }

    /**
     * Executes a command.
     *
     * @param command the command
     * @param args the command arguments
     * @return true if the command was executed successfully, false otherwise
     * @throws CommandException if there's an issue with the command
     */
    private boolean executeCommand(String command, String[] args) throws CommandException {
        Command cmd;
        switch (command) {
            case "quit":
                checkArgCount(args, 0);
                cmd = new CommandQuit();
                break;
            case "load":
                checkArgCount(args, 1);
                cmd = new CommandLoad(args[0], this);
                break;
            case "competition":
                checkMinArgCount(args, 2);
                cmd = new CommandCompetition(Arrays.asList(args));
                inCompetition = true;
                break;
            case "show":
                if (args.length == 0) {
                    if (!inCompetition) {
                        throw new CommandException("Not in a competition.");
                    }
                    cmd = new CommandShow();
                } else {
                    cmd = switch (args[0].toLowerCase()) {
                        case "monsters" -> new CommandShowMonsters(this);
                        case "actions" -> {
                            if (!inCompetition) {
                                throw new CommandException("Not in a competition.");
                            }
                            yield new CommandShowActions();
                        }
                        case "stats" -> {
                            if (!inCompetition) {
                                throw new CommandException("Not in a competition.");
                            }
                            yield new CommandShowStats();
                        }
                        default -> throw new CommandException("Unknown show command: " + args[0]);
                    };
                }
                break;
            case "action":
                if (!inCompetition) {
                    throw new CommandException("Not in a competition.");
                }
                checkMinArgCount(args, 1);

                if (args.length >= 2) {
                    cmd = new CommandAction(args[0], args[1]);
                } else {
                    cmd = new CommandAction(args[0], null);
                }
                break;
            case "pass":
                if (!inCompetition) {
                    throw new CommandException("Not in a competition.");
                }
                checkArgCount(args, 0);
                cmd = new CommandPass();
                break;
            default:
                throw new CommandException("Unknown command: " + command);
        }
        return cmd.execute();
    }

    /**
     * Checks if the argument count is exactly as expected.
     *
     * @param args the arguments
     * @param expected the expected count
     * @throws CommandException if the count doesn't match
     */
    private void checkArgCount(String[] args, int expected) throws CommandException {
        if (args.length != expected) {
            throw new CommandException("Expected " + expected + " arguments, got " + args.length);
        }
    }

    /**
     * Checks if the argument count is at least the minimum expected.
     *
     * @param args the arguments
     * @param min the minimum expected count
     * @throws CommandException if the count is less than expected
     */
    private void checkMinArgCount(String[] args, int min) throws CommandException {
        if (args.length < min) {
            throw new CommandException("Expected at least " + min + " arguments, got " + args.length);
        }
    }

    /**
     * Sets the monsters.
     *
     * @param monsters the monsters to set
     */
    public void setMonsters(List<Monster> monsters) {
        this.monsters = new ArrayList<>(monsters);
        this.inCompetition = false;
    }

    /**
     * Gets the monsters.
     *
     * @return the monsters
     */
    public List<Monster> getMonsters() {
        return new ArrayList<>(monsters);
    }

    /**
     * Checks if currently in a competition.
     *
     * @return true if in a competition
     */
    public boolean isInCompetition() {
        return inCompetition;
    }
}