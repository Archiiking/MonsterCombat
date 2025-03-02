package edu.kit.kastel.monstercombat.model.effect;

import edu.kit.kastel.monstercombat.model.Monster;
import edu.kit.kastel.monstercombat.model.Stat;

/**
 * Represents a stat change effect.
 */
public class EffectStatChange extends Effect {
    private final Stat stat;
    private final int stages;
    private final boolean targetIsUser;

    /**
     * Constructs a new stat change effect.
     *
     * @param stat the stat to change
     * @param stages the number of stages to change
     * @param targetIsUser whether the target is the user
     * @param hitRate the hit rate
     */
    public EffectStatChange(Stat stat, int stages, boolean targetIsUser, int hitRate) {
        super(hitRate);
        this.stat = stat;
        this.stages = stages;
        this.targetIsUser = targetIsUser;
    }

    @Override
    public boolean execute(Monster user, Monster target, boolean isFirstEffect) {
        Monster actualTarget = targetIsUser ? user : target;

        if (actualTarget.isFainted()) {
            return false;
        }

        // Check if target is protected against stat decreases
        if (!targetIsUser && stages < 0 && actualTarget.isStatsProtected()) {
            System.out.printf("%s is protected and is unaffected!\n", actualTarget.getDisplayName());
            return true;
        }

        // Apply stat change
        actualTarget.changeStatBy(stat, stages);

        // Output message
        if (stages > 0) {
            System.out.printf("%s's %s rises!\n", actualTarget.getDisplayName(), stat);
        } else {
            System.out.printf("%s's %s decreases...\n", actualTarget.getDisplayName(), stat);
        }

        return true;
    }
}