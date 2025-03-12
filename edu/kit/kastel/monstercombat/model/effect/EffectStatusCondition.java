package edu.kit.kastel.monstercombat.model.effect;

import edu.kit.kastel.monstercombat.model.Monster;
import edu.kit.kastel.monstercombat.model.StatusCondition;

public class EffectStatusCondition extends Effect {
    private final StatusCondition condition;

    public EffectStatusCondition(TargetType targetType, StatusCondition statusCondition, int hitRate) {
        super(hitRate, targetType);
        this.condition = statusCondition;
    }

    @Override
    public boolean execute(Monster user, Monster target, boolean isFirstEffect) {
        if (target.isDefeated()) {
            return false;
        }

        // Can't inflict a status condition if monster already has one
        if (target.getStatusCondition() != StatusCondition.NONE) {
            return true; // Effect "succeeds" but does nothing
        }

        // Inflict the condition
        target.setStatusCondition(condition);

        // Output message
        System.out.printf("%s%s\n", target.getDisplayName(), condition.getInflictedMessage());

        return true;
    }
}
