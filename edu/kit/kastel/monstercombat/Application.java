package edu.kit.kastel.monstercombat;

import edu.kit.kastel.monstercombat.model.Competition;
import edu.kit.kastel.monstercombat.model.exception.ConfigurationException;
import edu.kit.kastel.monstercombat.model.ConfigurationLoader;
import edu.kit.kastel.monstercombat.view.UserInterface;

/**
 * Main application class.
 * @author ursxd
 */
public final class Application {
    private static final String ERROR_MESSAGE_COMMAND_LINE_ARGUMENTS = "Error, invalid command line arguments.";

    /**
     * Private constructor to prevent instantiation.
     */
    private Application() {
        // Utility class
    }

    /**
     * Main entry point for the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.err.println(ERROR_MESSAGE_COMMAND_LINE_ARGUMENTS);
            System.err.println("Usage: java -jar MonsterBattle.jar <config_file> [<seed>|debug]");
            return;
        }

        String configFile = args[0];
        boolean debugMode = false;
        long seed = System.currentTimeMillis();

        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("debug")) {
                debugMode = true;
            } else {
                try {
                    seed = Long.parseLong(args[1]);
                } catch (NumberFormatException e) {
                    System.err.println("Error, invalid seed. Must be a number or 'debug'.");
                    return;
                }
            }
        }

        // Initialize the competition
        Competition.initialize(seed, debugMode);

        // Load the initial configuration
        ConfigurationLoader loader = new ConfigurationLoader();
        try {
            String configContent = loader.loadConfiguration(configFile);
            System.out.println(configContent);
            System.out.printf("Loaded %d actions, %d monsters.\n",
                    loader.getActionCount(), loader.getMonsterCount());
        } catch (ConfigurationException e) {
            System.err.println("Error, " + e.getMessage());
            return;
        }

        // Start the user interface
        UserInterface userInterface = new UserInterface(System.in, System.out, System.err);
        userInterface.handleUserInput();
    }
}