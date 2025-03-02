package edu.kit.kastel.monstercombat.view.command;

import edu.kit.kastel.monstercombat.model.Competition;
import edu.kit.kastel.monstercombat.model.Monster;

/**
 * Command to show the stats of the current monster.
 */
public class CommandShowStats extends ShowCommands {
    @Override
    public boolean execute() {
        Competition competition = Competition.getInstance();
        Monster currentMonster = competition.getCurrentMonster();

        if (currentMonster == null) {
            System.out.println("Error, no monster is currently active.");
            return false;
        }

        showStats(currentMonster);
        return true;
    }
}