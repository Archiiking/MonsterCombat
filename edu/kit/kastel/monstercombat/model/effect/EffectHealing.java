package edu.kit.kastel.monstercombat.model.effect;

import edu.kit.kastel.monstercombat.model.Monster;

public class EffectHealing extends Effect {

    public enum HealingType {
        BASE,
        RELATIVE,
        ABSOLUTE
    }

    private final HealingType healingType;
    private final int value;

    public EffectHealing(TargetType targetType, HealingType healingType, int value, int hitRate) {
        super(hitRate, targetType);
        this.healingType = healingType;
        this.value = value;
    }

    @Override
    public boolean execute(Monster user, Monster target, boolean isFirstEffect) {
        if (target.isDefeated()) {
            return false;
        }

        int healAmount;
        if (healingType == HealingType.ABSOLUTE) {
            healAmount = value;
        } else if (healingType == HealingType.RELATIVE) {
            // Relative healing is a percentage of max HP
            healAmount = (int) Math.ceil(target.getMaxHp() * value / 100.0);
        } else if (healingType == HealingType.BASE) {
            // Similar to damage but simplified for healing
            healAmount = (int) Math.ceil(value * (1.0 / 3.0));
        } else {
            return false;
        }

        int actualHeal = target.heal(healAmount);
        System.out.printf("%s gains back %d health!\n", target.getDisplayName(), actualHeal);
        return true;
    }
}
