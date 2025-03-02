package edu.kit.kastel.monstercombat.view.command;

import java.util.List;

import edu.kit.kastel.monstercombat.model.Action;
import edu.kit.kastel.monstercombat.model.Competition;
import edu.kit.kastel.monstercombat.model.Monster;
import edu.kit.kastel.monstercombat.model.Stat;
import edu.kit.kastel.monstercombat.model.effect.Effect;
import edu.kit.kastel.monstercombat.model.effect.EffectDamage;

/**
 * Base class for show commands.
 */
public abstract class ShowCommands implements Command {
    /**
     * Shows all monsters in the competition.
     */
    protected void showMonsters() {
        Competition competition = Competition.getInstance();
        Monster currentMonster = competition.getCurrentMonster();

        for (Monster monster : competition.getMonsters()) {
            String statusText;
            if (monster.isFainted()) {
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

    /**
     * Shows all actions of the specified monster.
     *
     * @param monster the monster to show actions for
     */
    protected void showActions(Monster monster) {
        System.out.printf("ACTIONS OF %s\n", monster.getDisplayName());

        for (Action action : monster.getActions()) {
            String damageInfo = "--";
            int hitRate = action.getFirstEffectHitRate();

            // Find the first damage effect
            for (Effect effect : action.getEffects()) {
                if (effect instanceof EffectDamage damageEffect) {
                    EffectDamage.DamageType type = damageEffect.getType();
                    int value = damageEffect.getValue();

                    if (type == EffectDamage.DamageType.BASE) {
                        damageInfo = "b" + value;
                    } else if (type == EffectDamage.DamageType.RELATIVE) {
                        damageInfo = "r" + value;
                    } else { // ABSOLUTE
                        damageInfo = "a" + value;
                    }
                    break;
                }
            }

            System.out.printf("%s: ELEMENT %s, Damage %s, HitRate %d\n",
                    action.getName(), action.getElement(), damageInfo, hitRate);
        }
    }

    /**
     * Shows the stats of the specified monster.
     *
     * @param monster the monster to show stats for
     */
    protected void showStats(Monster monster) {
        System.out.printf("STATS OF %s\n", monster.getDisplayName());

        int hp = monster.getCurrentHp();
        int maxHp = monster.getMaxHp();

        // Basic stats
        System.out.printf("HP %d/%d, ", hp, maxHp);

        // Other stats with changes
        for (Stat stat : new Stat[] { Stat.ATK, Stat.DEF, Stat.SPD, Stat.PRC, Stat.AGL }) {
            int change = monster.getStatChange(stat);
            if (change != 0) {
                System.out.printf("%s %d(%+d), ", stat, monster.getBaseStat(stat), change);
            } else {
                System.out.printf("%s %d, ", stat, monster.getBaseStat(stat));
            }
        }

        // Remove trailing comma and space
        System.out.println();
    }
}