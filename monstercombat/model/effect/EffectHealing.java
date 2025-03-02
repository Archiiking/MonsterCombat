package edu.kit.kastel.monstercombat.model.effect;

import edu.kit.kastel.monstercombat.model.Monster;

/**
 * Represents a healing effect.
 */
public class EffectHealing extends Effect {
    public enum HealingType {
        BASE,
        RELATIVE,
        ABSOLUTE
    }

    private final HealingType type;
    private final int value;
    private final boolean targetIsUser;

    /**
     * Constructs a new healing effect.
     *
     * @param type the type of healing
     * @param value the healing value
     * @param targetIsUser whether the target is the user
     * @param hitRate the hit rate
     */
    public EffectHealing(HealingType type, int value, boolean targetIsUser, int hitRate) {
        super(hitRate);
        this.type = type;
        this.value = value;
        this.targetIsUser = targetIsUser;
    }

    @Override
    public boolean execute(Monster user, Monster target, boolean isFirstEffect) {
        Monster actualTarget = targetIsUser ? user : target;

        if (actualTarget.isFainted()) {
            return false;
        }

        // Calculate healing amount
        int healAmount;

        if (type == HealingType.ABSOLUTE) {
            healAmount = value;
        } else if (type == HealingType.RELATIVE) {
            // Relative healing is a percentage of max HP
            healAmount = (int) Math.ceil(actualTarget.getMaxHp() * value / 100.0);
        } else { // BASE healing
            // Similar to damage but simplified for healing
            double baseHealing = value;
            healAmount = (int) Math.ceil(baseHealing);
        }

        // Apply healing
        int actualHeal = actualTarget.heal(healAmount);

        // Output message
        System.out.printf("%s gains back %d health!\n", actualTarget.getDisplayName(), actualHeal);

        return true;
    }
}