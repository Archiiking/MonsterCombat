package edu.kit.kastel.monstercombat.model.effect;

import edu.kit.kastel.monstercombat.model.Competition;
import edu.kit.kastel.monstercombat.model.Element;
import edu.kit.kastel.monstercombat.model.Monster;
import edu.kit.kastel.monstercombat.model.Stat;

public class EffectDamage extends Effect {

    public enum DamageType {
        BASE,
        RELATIVE,
        ABSOLUTE
    }

    private final DamageType type;
    private final int value;
    private final boolean isFireDamage;

    public EffectDamage(TargetType targetType, DamageType type, int value, int hitRate) {
        super(hitRate, targetType);
        this.type = type;
        this.value = value;
        this.isFireDamage = false;
    }

    public EffectDamage(Monster target) {
        super(100, TargetType.TARGET);
        this.type = DamageType.RELATIVE;
        this.value = 10;
        this.isFireDamage = true;
    }

    @Override
    public boolean execute(Monster user, Monster target, boolean isFirstEffect) {
        if (target.isDefeated() || target == null) {
            return false;
        }

        // Calculate damage
        int damage;

        if (type == DamageType.ABSOLUTE) {
            damage = value;
        } else if (type == DamageType.RELATIVE) {
            damage = (int) Math.ceil(target.getMaxHp() * value / 100.0);
        } else {
            damage = calculateBaseDamage(user, target, value);
        }

        // Apply damage
        int actualDamage = target.takeDamage(damage);

        // Output message
        if (isFireDamage) {
            System.out.printf("%s takes %d damage from burning!\n", target.getDisplayName(), actualDamage);
        } else if (target.isProtectedAgainstDamage() && target != user) {
            System.out.printf("%s is protected and takes no damage!\n", target.getDisplayName());
        } else {
            System.out.printf("%s takes %d damage!\n", target.getDisplayName(), actualDamage);
        }
        return true;
    }

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
        double totalDamage
                = baseDamage * elementFactor * statFactor * criticalFactor * sameElementFactor * randomFactor * normalizationFactor;

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
