package edu.kit.kastel.monstercombat.model.effect;

import edu.kit.kastel.monstercombat.model.Monster;
import edu.kit.kastel.monstercombat.model.StatusCondition;

/**
 * Represents a status condition effect.
 */
public class EffectStatusCondition extends Effect {
    private final StatusCondition condition;
    private final boolean targetIsUser;

    /**
     * Constructs a new status condition effect.
     *
     * @param condition the status condition to inflict
     * @param targetIsUser whether the target is the user
     * @param hitRate the hit rate
     */
    public EffectStatusCondition(StatusCondition condition, boolean targetIsUser, int hitRate) {
        super(hitRate);
        this.condition = condition;
        this.targetIsUser = targetIsUser;
    }

    @Override
    public boolean execute(Monster user, Monster target, boolean isFirstEffect) {
        Monster actualTarget = targetIsUser ? user : target;

        if (actualTarget.isFainted()) {
            return false;
        }

        // Can't inflict a status condition if monster already has one
        if (actualTarget.getStatusCondition() != StatusCondition.NONE) {
            return true; // Effect "succeeds" but does nothing
        }

        // Inflict the condition
        actualTarget.setStatusCondition(condition);

        // Output message
        System.out.printf("%s %s!\n", actualTarget.getDisplayName(), condition.getInflictedMessage());

        return true;
    }
}