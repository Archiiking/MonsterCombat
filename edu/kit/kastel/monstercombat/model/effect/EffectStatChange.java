package edu.kit.kastel.monstercombat.model.effect;

import edu.kit.kastel.monstercombat.model.Monster;
import edu.kit.kastel.monstercombat.model.Stat;

public class EffectStatChange extends Effect {
    private final Stat stat;
    private final int statChanges;

    public EffectStatChange(TargetType targetType, Stat stat, int statChanges, int hitRate) {
        super(hitRate, targetType);
        this.stat = stat;
        this.statChanges = statChanges;
    }

    @Override
    public boolean execute(Monster user, Monster target, boolean isFirstEffect) {
        if (target.isDefeated()) {
            return false;
        }

        // Check if target is protected against stat decreases
        if (target.isProtectedAgainstStatChanges() && target != user && statChanges < 0) {
            System.out.printf("%s is protected and is unaffected!\n", target.getDisplayName());
            return true;
        }

        // Apply stat change
        target.setStatChange(stat, statChanges);

        // Output message
        if (statChanges > 0) {
            System.out.printf("%s's %s rises!\n", target.getDisplayName(), stat);
        } else {
            System.out.printf("%s's %s decreases...\n", target.getDisplayName(), stat);
        }

        return true;
    }
}
