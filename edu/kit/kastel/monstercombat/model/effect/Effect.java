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

    public int getValue() {
        return 0;
    }

    public String getDamageInfo() {
        return "--";
    }
}
