package edu.kit.kastel.monstercombat.view.command;

import edu.kit.kastel.monstercombat.model.Monster;
import edu.kit.kastel.monstercombat.model.Competition;

public class CommandPass implements Command {
    @Override
    public boolean execute() {
        Competition competition = Competition.getInstance();
        Monster currentMonster = competition.getCurrentMonster();

        if (currentMonster == null) {
            System.out.println("Error, no monster is currently active.");
            return false;
        }

        // Set null action to indicate passing
        competition.setCurrentAction(null);

        return true;
    }
}