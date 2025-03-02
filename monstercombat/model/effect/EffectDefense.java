package edu.kit.kastel.monstercombat.model.effect;

import edu.kit.kastel.monstercombat.model.Competition;
import edu.kit.kastel.monstercombat.model.Monster;

/**
 * Represents a protection effect.
 */
public class EffectDefense extends Effect {
    public enum ProtectionTarget {
        HEALTH,
        STATS
    }

    private final ProtectionTarget target;
    private final int count;

    /**
     * Constructs a new defense effect.
     *
     * @param target what to protect
     * @param count the number of rounds the protection lasts
     * @param hitRate the hit rate
     */
    public EffectDefense(ProtectionTarget target, int count, int hitRate) {
        super(hitRate);
        this.target = target;
        this.count = count;
    }

    /**
     * Constructs a new defense effect with a random count.
     *
     * @param target what to protect
     * @param minCount the minimum number of rounds
     * @param maxCount the maximum number of rounds
     * @param hitRate the hit rate
     */
    public EffectDefense(ProtectionTarget target, int minCount, int maxCount, int hitRate) {
        super(hitRate);
        this.target = target;
        this.count = Competition.getInstance().getRandomInt(minCount, maxCount);
    }

    @Override
    public boolean execute(Monster user, Monster target, boolean isFirstEffect) {
        if (user.isFainted()) {
            return false;
        }

        // Set protection based on target
        boolean protectHealth = this.target == ProtectionTarget.HEALTH;
        boolean protectStats = this.target == ProtectionTarget.STATS;

        user.setProtection(count, protectHealth, protectStats);

        // Output message
        if (protectHealth) {
            System.out.printf("%s is now protected against damage!\n", user.getDisplayName());
        } else {
            System.out.printf("%s is now protected against status changes!\n", user.getDisplayName());
        }

        return true;
    }
}