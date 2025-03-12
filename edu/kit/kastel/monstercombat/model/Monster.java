package edu.kit.kastel.monstercombat.model;

import java.util.ArrayList;
import java.util.List;

public class Monster {

    private final String name;
    private final Element element;
    private final List<Action> actions;
    private final MonsterState state;
    private String displayName;
    /*
    private int competitorNumber;
    private Action selectedAction;
    private Monster actionTarget;
     */

    public Monster(String name, Element element, int maxHp, int baseAttack, int baseDefense, int baseSpeed) {
        this.name = name;
        this.element = element;
        this.actions = new ArrayList<>();
        this.state = new MonsterState(maxHp, baseAttack, baseDefense, baseSpeed);
        this.displayName = name;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Element getElement() {
        return element;
    }

    public int getBaseStat(Stat stat) {
        return state.getBaseStat(stat);
    }

    public double getEffectiveStat(Stat stat) {
        return state.getEffectiveStat(stat);
    }

    public int getCurrentHp() {
        return state.getCurrentHp();
    }

    public int getMaxHp() {
        return state.getMaxHp();
    }

    public StatusCondition getStatusCondition() {
        return state.getStatusCondition();
    }

    public void setStatusCondition(StatusCondition condition) {
        state.setStatusCondition(condition);
    }

    public int getStatChange(Stat stat) {
        return state.getStatChange(stat);
    }

    public void setStatChange(Stat stat, int statChange) {
        state.setStatChange(stat, statChange);
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public List<Action> getActions() {
        return new ArrayList<>(actions);
    }

    public Action getAction(String actionName) {
        for (Action action : actions) {
            if (action.getName().equals(actionName)) {
                return action;
            }
        }
        return null;
    }

    public int takeDamage(int amount) {
        return state.takeDamage(amount);
    }

    public int heal(int amount) {
        return state.heal(amount);
    }

    public boolean isDefeated() {
        return state.isDefeated();
    }

    public void setProtection(boolean protectHealth, boolean protectStatChanges, int protectionDuration) {
        state.setProtection(protectHealth, protectStatChanges, protectionDuration);
    }

    public boolean isProtectedAgainstDamage() {
        return state.isProtectedAgainstDamage();
    }

    public boolean isProtectedAgainstStatChanges() {
        return state.isProtectedAgainstStatChanges();
    }

    public int getProtectionDuration() {
        return state.getProtectionDuration();
    }

    public boolean decreaseProtectionDuration() {
        return state.decreaseProtectionDuration();
    }

    public String getHealthBar() {
        int filledBars = (int) Math.round(20.0 * getCurrentHp() / getMaxHp());
        int emptyBars = 20 - filledBars;

        return "[" + "X".repeat(Math.max(0, filledBars)) + "_".repeat(Math.max(0, emptyBars)) + "]";
    }

    @Override
    public String toString() {
        return String.format("%s: Element %s, HP %d, ATK %d, DEF %d, SPD %d",
                name, element, getMaxHp(), getBaseStat(Stat.ATK),
                getBaseStat(Stat.DEF), getBaseStat(Stat.SPD));
    }

}