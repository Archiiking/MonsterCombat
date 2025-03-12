package edu.kit.kastel.monstercombat.view.command;

import java.util.List;

import edu.kit.kastel.monstercombat.model.Competition;
import edu.kit.kastel.monstercombat.model.Monster;

public class CommandShow implements Command {
    @Override
    public boolean execute() {
        showMonsters();
        return true;
    }

    private void showMonsters() {
        Competition competition = Competition.getInstance();
        Monster currentMonster = competition.getCurrentMonster();

        for (Monster monster : competition.getMonsters()) {
            String statusText;
            if (monster.isDefeated()) {
                statusText = "FAINTED";
            } else {
                statusText = "(" + monster.getStatusCondition().getActiveMessage() + ")";
            }

            // Mark the current monster with an asterisk
            String marker = (monster.equals(currentMonster)) ? "*" : "";

            // Find the monster's number in the competition
            int monsterNumber = 0;
            List<Monster> monsters = competition.getMonsters();
            for (int i = 0; i < monsters.size(); i++) {
                if (monsters.get(i).equals(monster)) {
                    monsterNumber = i + 1;
                    break;
                }
            }

            System.out.printf("%s %d %s%s %s\n",
                    monster.getHealthBar(), monsterNumber, marker, monster.getDisplayName(), statusText);
        }
    }
}