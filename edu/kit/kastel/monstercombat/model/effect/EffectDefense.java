package edu.kit.kastel.monstercombat.model.effect;

import edu.kit.kastel.monstercombat.model.Competition;
import edu.kit.kastel.monstercombat.model.Monster;

public class EffectDefense extends Effect {

    public enum ProtectionType {
        HEALTH,
        STATS
    }

    private final ProtectionType target;
    private final int duration;

    public EffectDefense(ProtectionType target, int duration, int hitRate) {
        super(hitRate, TargetType.USER);
        this.target = target;
        this.duration = duration;
    }

    public EffectDefense(ProtectionType target, int minDuration, int maxDuration, int hitRate) {
        super(hitRate, TargetType.USER);
        this.target = target;
        this.duration = Competition.getInstance().getRandomInt(minDuration, maxDuration);
    }

    @Override
    public boolean execute(Monster user, Monster target, boolean isFirstEffect) {
        if (user.isDefeated()) {
            return false;
        }

        // Set protection based on target
        boolean protectHealth = this.target == ProtectionType.HEALTH;
        boolean protectStatChanges = this.target == ProtectionType.STATS;

        user.setProtection(protectHealth, protectStatChanges, duration);

        // Output message
        if (protectHealth) {
            System.out.printf("%s is now protected against damage!\n", user.getDisplayName());
        }
        if (protectStatChanges) {
            System.out.printf("%s is now protected against status changes!\n", user.getDisplayName());
        }
        return true;
    }
}
