package edu.kit.kastel.monstercombat.model.effect;

import java.util.List;

import edu.kit.kastel.monstercombat.model.Competition;
import edu.kit.kastel.monstercombat.model.Monster;

/**
 * Represents a repeat effect that repeats other effects.
 */
public class EffectRepeat extends Effect {
    private final int count;
    private final List<Effect> repeatedEffects;

    /**
     * Constructs a new repeat effect.
     *
     * @param count the number of repetitions
     * @param repeatedEffects the effects to repeat
     */
    public EffectRepeat(int count, List<Effect> repeatedEffects) {
        super(100); // Repeat effect always "hits"
        this.count = count;
        this.repeatedEffects = repeatedEffects;
    }

    /**
     * Constructs a new repeat effect with a random count.
     *
     * @param minCount the minimum number of repetitions
     * @param maxCount the maximum number of repetitions
     * @param repeatedEffects the effects to repeat
     */
    public EffectRepeat(int minCount, int maxCount, List<Effect> repeatedEffects) {
        super(100); // Repeat effect always "hits"
        this.count = Competition.getInstance().getRandomInt(minCount, maxCount);
        this.repeatedEffects = repeatedEffects;
    }

    @Override
    public boolean execute(Monster user, Monster target, boolean isFirstEffect) {
        // The actual execution is handled by the Competition class
        // This method just returns true to signal that the repeat effect "hit"
        return true;
    }

    /**
     * Gets the number of repetitions.
     *
     * @return the repetition count
     */
    public int getCount() {
        return count;
    }

    /**
     * Gets the repeated effects.
     *
     * @return the effects to repeat
     */
    public List<Effect> getRepeatedEffects() {
        return repeatedEffects;
    }
}