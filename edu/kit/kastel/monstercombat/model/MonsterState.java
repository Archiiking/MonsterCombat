package edu.kit.kastel.monstercombat.model;


import java.util.EnumMap;
import java.util.Map;

public class MonsterState {
    private final Map<Stat, Integer> baseStats;
    private final Map<Stat, Integer> statChanges;
    private StatusCondition statusCondition;
    private int currentHp;
    private int protectionDuration;
    private boolean isProtectedAgainstDamage;
    private boolean isProtectedAgainstStatChanges;

    public MonsterState(int maxHp, int baseAttack, int baseDefense, int baseSpeed) {
        this.baseStats = new EnumMap<>(Stat.class);
        this.baseStats.put(Stat.HP, maxHp);
        this.baseStats.put(Stat.ATK, baseAttack);
        this.baseStats.put(Stat.DEF, baseDefense);
        this.baseStats.put(Stat.SPD, baseSpeed);
        this.baseStats.put(Stat.PRC, 1);
        this.baseStats.put(Stat.AGL, 1);

        this.statChanges = new EnumMap<>(Stat.class);
        for (Stat stat : Stat.values()) {
            statChanges.put(stat, 0);
        }
        this.statusCondition = StatusCondition.NONE;
        this.currentHp = maxHp;
        this.protectionDuration = 0;
        this.isProtectedAgainstDamage = false;
        this.isProtectedAgainstStatChanges = false;
    }

    public int getBaseStat(Stat stat) {
        return baseStats.getOrDefault(stat, 0);
    }

    public double getEffectiveStat(Stat stat) {
        double value = baseStats.getOrDefault(stat, 0);

        // Apply stat changes
        int statChange = statChanges.getOrDefault(stat, 0);
        value *= stat.calculateStatChangeFactor(statChange) * statusCondition.getStatusConditionMultiplier(stat);

        // Ensure the value doesn't go below 1.0
        return Math.max(1.0, value);
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getMaxHp() {
        return baseStats.get(Stat.HP);
    }

    public StatusCondition getStatusCondition() {
        return statusCondition;
    }

    public void setStatusCondition(StatusCondition condition) {
        this.statusCondition = condition;
    }

    public int getStatChange(Stat stat) {
        return statChanges.getOrDefault(stat, 0);
    }

    public void setStatChange(Stat stat, int statChange) {
        int currentStatChange = statChanges.getOrDefault(stat, 0);
        int newStatChange = Math.max(-5, Math.min(5, currentStatChange + statChange));
        statChanges.put(stat, newStatChange);
    }

    public int takeDamage(int amount) {
        if (isProtectedAgainstDamage) {
            return 0;
        }
        int actualDamage = Math.min(currentHp, amount);
        currentHp -= actualDamage;
        return actualDamage;
    }

    public int heal(int amount) {
        int maxHeal = getMaxHp() - currentHp;
        int actualHeal = Math.min(maxHeal, amount);
        currentHp += actualHeal;
        return actualHeal;
    }

    public boolean isDefeated() {
        return currentHp <= 0 || statusCondition == StatusCondition.FAINTED;
    }

    public boolean isProtectedAgainstDamage() {
        return isProtectedAgainstDamage;
    }

    public boolean isProtectedAgainstStatChanges() {
        return isProtectedAgainstStatChanges;
    }

    public void setProtection(boolean protectHealth, boolean protectStatChanges, int protectionDuration) {
        this.isProtectedAgainstDamage = protectHealth;
        this.isProtectedAgainstStatChanges = protectStatChanges;
        this.protectionDuration = protectionDuration;
    }

    public int getProtectionDuration() {
        return protectionDuration;
    }

    public boolean decreaseProtectionDuration() {
        if (protectionDuration > 0) {
            protectionDuration--;
            if (protectionDuration == 0) {
                boolean hadProtection = isProtectedAgainstDamage || isProtectedAgainstStatChanges;
                isProtectedAgainstDamage = false;
                isProtectedAgainstStatChanges = false;
                return hadProtection;
            }
        }
        return false;
    }
}