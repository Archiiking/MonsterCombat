package edu.kit.kastel.monstercombat.view.command;

import java.util.List;

import edu.kit.kastel.monstercombat.model.Monster;
import edu.kit.kastel.monstercombat.view.CommandHandler;

/**
 * Command to show all monsters.
 */
public class CommandShowMonsters extends ShowCommands {
    private final CommandHandler handler;

    /**
     * Constructs a new show monsters command.
     *
     * @param handler the command handler
     */
    public CommandShowMonsters(CommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public boolean execute() {
        List<Monster> monsters = handler.getMonsters();

        for (Monster monster : monsters) {
            System.out.println(monster);
        }

        return true;
    }
}