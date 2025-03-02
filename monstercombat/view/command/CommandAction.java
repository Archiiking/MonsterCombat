package edu.kit.kastel.monstercombat.view.command;

import edu.kit.kastel.monstercombat.model.Monster;
import edu.kit.kastel.monstercombat.model.Competition;

/**
 * Command to perform an action with a monster.
 */
public class CommandAction implements Command {
    private final String actionName;
    private final String targetName;

    /**
     * Constructs a new action command.
     *
     * @param actionName the name of the action to perform
     * @param targetName the name of the target monster (can be null)
     */
    public CommandAction(String actionName, String targetName) {
        this.actionName = actionName;
        this.targetName = targetName;
    }

    @Override
    public boolean execute() {
        Competition competition = Competition.getInstance();
        Monster currentMonster = competition.getCurrentMonster();

        if (currentMonster == null) {
            System.out.println("Error, no monster is currently active.");
            return false;
        }

        // Get the action
        var action = currentMonster.getAction(actionName);
        if (action == null) {
            System.out.printf("Error, %s does not know the action %s.\n",
                    currentMonster.getDisplayName(), actionName);
            return false;
        }

        // Get the target (if needed)
        Monster target = null;
        if (targetName != null) {
            target = competition.getMonster(targetName);
            if (target == null) {
                System.out.printf("Error, monster %s not found.\n", targetName);
                return false;
            }
        } else {
            // Find any target that is not the current monster and not fainted
            for (Monster monster : competition.getMonsters()) {
                if (!monster.equals(currentMonster) && !monster.isFainted()) {
                    target = monster;
                    break;
                }
            }

            if (target == null) {
                System.out.println("Error, no valid target found.");
                return false;
            }
        }

        // Set the action and target
        competition.setCurrentAction(action);
        competition.setCurrentTarget(target);

        return true;
    }
}