package edu.kit.kastel.monstercombat.view.command;

import java.util.List;

import edu.kit.kastel.monstercombat.model.Competition;
import edu.kit.kastel.monstercombat.model.ConfigurationLoader;
import edu.kit.kastel.monstercombat.model.Monster;

public class CommandCompetition implements Command {
    private final List<String> monsterNames;

    public CommandCompetition(List<String> monsterNames) {
        this.monsterNames = monsterNames;
    }

    @Override
    public boolean execute() {
        Competition competition = Competition.getInstance();

        // Clear the current competition
        competition.clear();

        // Get the monsters from the configuration
        ConfigurationLoader loader = new ConfigurationLoader();
        List<Monster> allMonsters = loader.getMonsters();

        // Add the specified monsters to the competition
        for (String name : monsterNames) {
            // Find the monster with the given name
            Monster found = null;
            for (Monster monster : allMonsters) {
                if (monster.getName().equals(name)) {
                    found = monster;
                    break;
                }
            }

            if (found == null) {
                System.out.printf("Error, monster %s not found.\n", name);
                return false;
            }

            competition.addMonster(found);
        }

        // Start the competition
        System.out.printf("The %d monsters enter the competition!\n", monsterNames.size());

        return true;
    }
}