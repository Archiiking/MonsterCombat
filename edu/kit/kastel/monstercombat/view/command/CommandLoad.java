package edu.kit.kastel.monstercombat.view.command;

import edu.kit.kastel.monstercombat.model.ConfigurationLoader;
import edu.kit.kastel.monstercombat.model.exception.ConfigurationException;
import edu.kit.kastel.monstercombat.view.CommandHandler;

public class CommandLoad implements Command {
    private final String filePath;
    private final CommandHandler handler;

    public CommandLoad(String filePath, CommandHandler handler) {
        this.filePath = filePath;
        this.handler = handler;
    }

    @Override
    public boolean execute() {
        try {
            // Load the configuration
            ConfigurationLoader loader = new ConfigurationLoader();
            String configContent = loader.loadConfiguration(filePath);

            // Set the loaded monsters in the handler
            handler.setMonsters(loader.getMonsters());

            // Output the configuration
            System.out.println(configContent);
            System.out.printf("Loaded %d actions, %d monsters.\n",
                    loader.getActionCount(), loader.getMonsterCount());

            return true;
        } catch (ConfigurationException e) {
            System.out.println("Error, " + e.getMessage());
            return false;
        }
    }
}