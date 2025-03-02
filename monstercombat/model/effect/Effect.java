package edu.kit.kastel.monstercombat.model.effect;

import edu.kit.kastel.monstercombat.model.Monster;

/**
 * Base class for all effects that actions can have.
 */
public abstract class Effect {
    private final int hitRate;

    /**
     * Constructs a new effect with the specified hit rate.
     *
     * @param hitRate the hit rate of the effect (0-100)
     */
    protected Effect(int hitRate) {
        this.hitRate = hitRate;
    }

    /**
     * Gets the hit rate of the effect.
     *
     * @return the hit rate (0-100)
     */
    public int getHitRate() {
        return hitRate;
    }

    /**
     * Executes the effect.
     *
     * @param user   the monster using the effect
     * @param target the target monster of the effect
     * @param isFirstEffect whether this is the first effect of an action
     * @return true if the effect hit, false otherwise
     */
    public abstract boolean execute(Monster user, Monster target, boolean isFirstEffect);
}