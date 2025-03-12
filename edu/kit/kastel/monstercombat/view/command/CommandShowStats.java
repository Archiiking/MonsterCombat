package edu.kit.kastel.monstercombat.view.command;

import edu.kit.kastel.monstercombat.model.Competition;
import edu.kit.kastel.monstercombat.model.Monster;
import edu.kit.kastel.monstercombat.model.Stat;

/**
 * Command to show the stats of the current monster.
 */
public class CommandShowStats implements Command {
    @Override
    public boolean execute() {
        Monster currentMonster = Competition.getInstance().getCurrentMonster();

        if (currentMonster == null) {
            System.out.println("Error, no monster is currently active.");
            return false;
        }

        showStats(currentMonster);
        return true;
    }

    private void showStats(Monster monster) {
        System.out.printf("STATS OF %s\n", monster.getDisplayName());

        int hp = monster.getCurrentHp();
        int maxHp = monster.getMaxHp();

        // Basic stats
        StringBuilder stats = new StringBuilder();
        stats.append(String.format("HP %d/%d, ", hp, maxHp));

        // Other stats with changes
        appendStats(stats, monster);

        // Output stats
        System.out.println(stats);
    }

    private void appendStats(StringBuilder stats, Monster monster) {
        for (Stat stat : new Stat[] { Stat.ATK, Stat.DEF, Stat.SPD, Stat.PRC, Stat.AGL }) {
            int change = monster.getStatChange(stat);
            if (change != 0) {
                stats.append(String.format("%s %d(%+d), ", stat, monster.getBaseStat(stat), change));
            } else {
                stats.append(String.format("%s %d, ", stat, monster.getBaseStat(stat)));
            }
        }
    }
}