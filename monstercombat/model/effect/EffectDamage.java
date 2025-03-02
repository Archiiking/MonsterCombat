package edu.kit.kastel.monstercombat.model.effect;

import edu.kit.kastel.monstercombat.model.Competition;
import edu.kit.kastel.monstercombat.model.Element;
import edu.kit.kastel.monstercombat.model.Monster;
import edu.kit.kastel.monstercombat.model.Stat;

/**
 * Represents a damage effect.
 */
public class EffectDamage extends Effect {

    public enum DamageType {
        BASE,
        RELATIVE,
        ABSOLUTE
    }

    private final DamageType type;
    private final int value;
    private final boolean targetIsUser;
    private final boolean isBurnDamage;

    /**
     * Constructs a new damage effect.
     *
     * @param type the type of damage
     * @param value the damage value
     * @param targetIsUser whether the target is the user
     * @param hitRate the hit rate
     */
    public EffectDamage(DamageType type, int value, boolean targetIsUser, int hitRate) {
        super(hitRate);
        this.type = type;
        this.value = value;
        this.targetIsUser = targetIsUser;
        this.isBurnDamage = false;
    }

    /**
     * Constructs a new burn damage effect.
     *
     * @param target the target monster
     */
    public EffectDamage(Monster target) {
        super(100); // Burn damage always hits
        this.type = DamageType.RELATIVE;
        this.value = 10; // 10% of max HP
        this.targetIsUser = false;
        this.isBurnDamage = true;
    }

    @Override
    public boolean execute(Monster user, Monster target, boolean isFirstEffect) {
        Monster actualTarget = targetIsUser ? user : target;

        if (actualTarget.isFainted()) {
            return false;
        }

        // Calculate damage
        int damage;

        if (type == DamageType.ABSOLUTE) {
            damage = value;
        } else if (type == DamageType.RELATIVE) {
            // Relative damage is a percentage of max HP
            damage = (int) Math.ceil(actualTarget.getMaxHp() * value / 100.0);
        } else { // BASE damage
            damage = calculateBaseDamage(user, actualTarget, value);
        }

        // Apply damage
        int actualDamage = actualTarget.takeDamage(damage);

        // Output message
        if (isBurnDamage) {
            System.out.printf("%s takes %d damage from burning!\n", actualTarget.getDisplayName(), actualDamage);
        } else if (actualTarget.isHealthProtected() && !targetIsUser) {
            System.out.printf("%s is protected and takes no damage!\n", actualTarget.getDisplayName());
        } else {
            System.out.printf("%s takes %d damage!\n", actualTarget.getDisplayName(), actualDamage);
        }

        return true;
    }

    /**
     * Calculates base damage using the damage formula.
     *
     * @param user the user monster
     * @param target the target monster
     * @param baseDamage the base damage value
     * @return the calculated damage
     */
    private int calculateBaseDamage(Monster user, Monster target, int baseDamage) {
        // Element effectiveness factor
        double elementFactor = 1.0;
        Element actionElement = Competition.getInstance().getCurrentAction().getElement();

        if (actionElement.isVeryEffectiveAgainst(target.getElement())) {
            elementFactor = 2.0;
            System.out.println("It is very effective!");
        } else if (actionElement.isNotVeryEffectiveAgainst(target.getElement())) {
            elementFactor = 0.5;
            System.out.println("It is not very effective...");
        }

        // Stat factor (ATK/DEF)
        double statFactor = user.getEffectiveStat(Stat.ATK) / target.getEffectiveStat(Stat.DEF);

        // Critical hit factor
        double criticalFactor = 1.0;
        double critChance = 10 * (user.getEffectiveStat(Stat.SPD) / target.getEffectiveStat(Stat.SPD));
        if (Competition.getInstance().getRandom().nextDouble() * 100 <= critChance) {
            criticalFactor = 2.0;
            System.out.println("Critical hit!");
        }

        // Same element bonus
        double sameElementFactor = (actionElement == user.getElement()) ? 1.5 : 1.0;

        // Random factor between 0.85 and 1.0
        double randomFactor = 0.85 + Competition.getInstance().getRandom().nextDouble() * 0.15;

        // Normalization factor
        double normalizationFactor = 1.0 / 3.0;

        // Calculate total damage
        double totalDamage = baseDamage * elementFactor * statFactor * criticalFactor *
                sameElementFactor * randomFactor * normalizationFactor;

        // Round up to the next integer
        return (int) Math.ceil(totalDamage);
    }

    public DamageType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

}