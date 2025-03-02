package edu.kit.kastel.monstercombat.model;

/**
 * Represents the stats that monsters possess.
 */
public enum Stat {
    HP("HP", 0),
    ATK("ATK", 2),
    DEF("DEF", 2),
    SPD("SPD", 2),
    PRC("PRC", 3),
    AGL("AGL", 3);

    private final String representation;
    private final int base;

    Stat(String representation, int base) {
        this.representation = representation;
        this.base = base;
    }

    @Override
    public String toString() {
        return representation;
    }

    /**
     * Gets the base value used for calculating stat changes.
     *
     * @return the base value
     */
    public int getBase() {
        return base;
    }

    /**
     * Calculates the stat factor based on the given change in stages.
     *
     * @param stages the change in stages
     * @return the calculated stat factor
     */
    public double calculateStatFactor(int stages) {
        if (stages >= 0) {
            return (double) (base + stages) / base;
        } else {
            return (double) base / (base - stages);
        }
    }

    /**
     * Creates a Stat from its string representation.
     *
     * @param representation the string representation
     * @return the corresponding Stat or null if no matching stat is found
     */
    public static Stat fromString(String representation) {
        for (Stat stat : values()) {
            if (stat.representation.equals(representation)) {
                return stat;
            }
        }
        return null;
    }
}