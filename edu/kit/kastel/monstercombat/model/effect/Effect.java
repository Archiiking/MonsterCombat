package edu.kit.kastel.monstercombat.model.effect;

import edu.kit.kastel.monstercombat.model.Monster;

import java.util.Queue;

public abstract class Effect {

    public enum TargetType {
        USER,
        TARGET
    }

    private final int hitRate;
    private final TargetType targetType;

    protected Effect(int hitRate, TargetType targetType) {
        this.hitRate = hitRate;
        this.targetType = targetType;
    }

    public int getHitRate() {
        return hitRate;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public Monster getActualTarget(Monster user, Monster selectedTarget) {
        return targetType == TargetType.USER ? user : selectedTarget;
    }

    public abstract boolean execute(Monster user, Monster target, boolean isFirstEffect);

    public void addToQueue(Queue<Effect> queue) {
        queue.add(this);
    }

    public boolean isDamageEffect() {
        return false;
    }

    public String getDamageTypeCode() {
        return "";
    }

    public int getValue() {
        return 0;
    }

/*
    public boolean calculateHit(Competition competition, Monster user, Monster target) {
        // If either monster is defeated, the effect always misses
        if (user.isDefeated() || (target != null && target.isDefeated())) {
            return false;
        }

        // Calculate the effective hit rate
        double effectiveHitRate = hitRate;

        // Apply precision and agility if targeting another monster
        if (target != null && target != user) {
            effectiveHitRate *= user.getEffectivePrecision() / target.getEffectiveAgility();
        } else {
            effectiveHitRate *= user.getEffectivePrecision();
        }

        // Decide if the effect hits
        return competition.decideYesOrNo("attack hit", effectiveHitRate);
    }
 */

}
