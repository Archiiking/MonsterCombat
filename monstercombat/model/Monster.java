package edu.kit.kastel.monstercombat.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a monster in the game.
 */
public class Monster {
    private final String name;
    private final Element element;
    private final Map<Stat, Integer> baseStats;
    private final List<Action> actions;

    private int currentHp;
    private StatusCondition statusCondition;
    private final Map<Stat, Integer> statChanges;
    private int protectionCount;
    private boolean protectHealth;
    private boolean protectStats;
    private String displayName;

    /**
     * Constructs a new monster.
     *
     * @param name the name of the monster
     * @param element the element of the monster
     * @param maxHp the maximum health points
     * @param attack the base attack value
     * @param defense the base defense value
     * @param speed the base speed value
     */
    public Monster(String name, Element element, int maxHp, int attack, int defense, int speed) {
        this.name = name;
        this.element = element;
        this.baseStats = new EnumMap<>(Stat.class);
        this.baseStats.put(Stat.HP, maxHp);
        this.baseStats.put(Stat.ATK, attack);
        this.baseStats.put(Stat.DEF, defense);
        this.baseStats.put(Stat.SPD, speed);
        this.baseStats.put(Stat.PRC, 1); // Default values for precision and agility
        this.baseStats.put(Stat.AGL, 1);

        this.currentHp = maxHp;
        this.actions = new ArrayList<>();
        this.statusCondition = StatusCondition.NONE;
        this.statChanges = new EnumMap<>(Stat.class);
        for (Stat stat : Stat.values()) {
            statChanges.put(stat, 0);
        }
        this.protectionCount = 0;
        this.protectHealth = false;
        this.protectStats = false;
        this.displayName = name;
    }

    /**
     * Gets the name of the monster.
     *
     * @return the monster's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the display name of the monster (including potential #n suffix).
     *
     * @return the monster's display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name of the monster.
     *
     * @param displayName the new display name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the element of the monster.
     *
     * @return the monster's element
     */
    public Element getElement() {
        return element;
    }

    /**
     * Gets the base stat value for the specified stat.
     *
     * @param stat the stat to get
     * @return the base stat value
     */
    public int getBaseStat(Stat stat) {
        return baseStats.getOrDefault(stat, 0);
    }

    /**
     * Gets the effective stat value for the specified stat, considering
     * stat changes and status conditions.
     *
     * @param stat the stat to get
     * @return the effective stat value
     */
    public double getEffectiveStat(Stat stat) {
        double value = baseStats.getOrDefault(stat, 0);

        // Apply stat changes
        int stages = statChanges.getOrDefault(stat, 0);
        value *= stat.calculateStatFactor(stages);

        // Apply status condition effects
        if (statusCondition == StatusCondition.WET && stat == Stat.DEF) {
            value *= 0.75; // 25% decrease
        } else if (statusCondition == StatusCondition.BURN && stat == Stat.ATK) {
            value *= 0.75; // 25% decrease
        } else if (statusCondition == StatusCondition.QUICKSAND && stat == Stat.SPD) {
            value *= 0.75; // 25% decrease
        }

        // Ensure the value doesn't go below 1.0
        return Math.max(1.0, value);
    }

    /**
     * Gets the current HP of the monster.
     *
     * @return the current HP
     */
    public int getCurrentHp() {
        return currentHp;
    }

    /**
     * Gets the maximum HP of the monster.
     *
     * @return the maximum HP
     */
    public int getMaxHp() {
        return baseStats.get(Stat.HP);
    }

    /**
     * Gets the current status condition of the monster.
     *
     * @return the status condition
     */
    public StatusCondition getStatusCondition() {
        return statusCondition;
    }

    /**
     * Sets the status condition of the monster.
     *
     * @param condition the new status condition
     */
    public void setStatusCondition(StatusCondition condition) {
        this.statusCondition = condition;
    }

    /**
     * Gets the stat change in stages for the specified stat.
     *
     * @param stat the stat to get the change for
     * @return the stat change in stages
     */
    public int getStatChange(Stat stat) {
        return statChanges.getOrDefault(stat, 0);
    }

    /**
     * Changes the stat by the specified number of stages.
     *
     * @param stat the stat to change
     * @param stages the number of stages to change
     */
    public void changeStatBy(Stat stat, int stages) {
        int currentStages = statChanges.getOrDefault(stat, 0);
        int newStages = Math.max(-5, Math.min(5, currentStages + stages));
        statChanges.put(stat, newStages);
    }

    /**
     * Adds an action to the monster.
     *
     * @param action the action to add
     */
    public void addAction(Action action) {
        actions.add(action);
    }

    /**
     * Gets all actions of the monster.
     *
     * @return a list of the monster's actions
     */
    public List<Action> getActions() {
        return new ArrayList<>(actions);
    }

    /**
     * Gets an action by its name.
     *
     * @param actionName the name of the action
     * @return the action or null if not found
     */
    public Action getAction(String actionName) {
        for (Action action : actions) {
            if (action.getName().equals(actionName)) {
                return action;
            }
        }
        return null;
    }

    /**
     * Takes damage, reducing the monster's HP.
     *
     * @param amount the amount of damage
     * @return the actual amount of damage taken
     */
    public int takeDamage(int amount) {
        if (protectHealth) {
            return 0;
        }
        int actualDamage = Math.min(currentHp, amount);
        currentHp -= actualDamage;
        return actualDamage;
    }

    /**
     * Heals the monster, increasing its HP.
     *
     * @param amount the amount to heal
     * @return the actual amount healed
     */
    public int heal(int amount) {
        int maxHeal = getMaxHp() - currentHp;
        int actualHeal = Math.min(maxHeal, amount);
        currentHp += actualHeal;
        return actualHeal;
    }

    /**
     * Checks if the monster is fainted (HP = 0).
     *
     * @return true if the monster is fainted
     */
    public boolean isFainted() {
        return currentHp <= 0;
    }

    /**
     * Sets protection for the monster.
     *
     * @param rounds the number of rounds the protection lasts
     * @param protectHealth whether to protect health
     * @param protectStats whether to protect stats
     */
    public void setProtection(int rounds, boolean protectHealth, boolean protectStats) {
        this.protectionCount = rounds;
        this.protectHealth = protectHealth;
        this.protectStats = protectStats;
    }

    /**
     * Checks if the monster is protected against health changes.
     *
     * @return true if protected against health changes
     */
    public boolean isHealthProtected() {
        return protectHealth;
    }

    /**
     * Checks if the monster is protected against stat decreases.
     *
     * @return true if protected against stat decreases
     */
    public boolean isStatsProtected() {
        return protectStats;
    }

    /**
     * Decreases the protection counter at the end of a round.
     *
     * @return true if protection ended this round
     */
    public boolean decreaseProtection() {
        if (protectionCount > 0) {
            protectionCount--;
            if (protectionCount == 0) {
                boolean hadProtection = protectHealth || protectStats;
                protectHealth = false;
                protectStats = false;
                return hadProtection;
            }
        }
        return false;
    }

    /**
     * Creates a string representation of the health bar.
     *
     * @return the health bar string
     */
    public String getHealthBar() {
        int filledBars = (int) Math.round(20.0 * currentHp / getMaxHp());
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