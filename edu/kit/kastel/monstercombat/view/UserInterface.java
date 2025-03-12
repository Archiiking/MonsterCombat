package edu.kit.kastel.monstercombat.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import edu.kit.kastel.monstercombat.model.Competition;

/**
 * User interface for the monster battle game.
 */
public class UserInterface {
    private final InputStream inputSource;
    private final PrintStream outputStream;
    private final PrintStream errorStream;
    private final CommandHandler commandHandler;
    private boolean isRunning;

    /**
     * Constructs a new user interface.
     *
     * @param inputSource the input source
     * @param outputStream the output stream
     * @param errorStream the error stream
     */
    public UserInterface(InputStream inputSource, PrintStream outputStream, PrintStream errorStream) {
        this.inputSource = inputSource;
        this.outputStream = outputStream;
        this.errorStream = errorStream;
        this.commandHandler = new CommandHandler(this);
        this.isRunning = false;
    }

    /**
     * Handles user input.
     */
    public void handleUserInput() {
        this.isRunning = true;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputSource));

        try {
            while (isRunning) {
                String line = reader.readLine();

                if (line == null) {
                    break;
                }

                commandHandler.processCommand(line);

                // If in a competition, ensure we keep asking for actions
                if (commandHandler.isInCompetition() && Competition.getInstance().getCurrentMonster() != null) {
                    System.out.println();
                    System.out.printf("What should %s do?\n",
                            Competition.getInstance().getCurrentMonster().getDisplayName());
                }
            }
        } catch (IOException e) {
            errorStream.println("Error reading input: " + e.getMessage());
        }
    }

    /**
     * Stops handling user input.
     */
    public void stop() {
        this.isRunning = false;
    }
}