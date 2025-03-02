package edu.kit.kastel.monstercombat.model.effect;

import edu.kit.kastel.monstercombat.model.Monster;

/**
 * Represents a continue effect which does nothing and continues to the next effect.
 */
public class EffectContinue extends Effect {
    /**
     * Constructs a new continue effect.
     *
     * @param hitRate the hit rate of the effect
     */
    public EffectContinue(int hitRate) {
        super(hitRate);
    }

    @Override
    public boolean execute(Monster user, Monster target, boolean isFirstEffect) {
        return true; // Just continue to the next effect
    }
}