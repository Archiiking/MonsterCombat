package edu.kit.kastel.monstercombat.model;

public enum Stat {
    HP("HP", 0),
    ATK("ATK", 2),
    DEF("DEF", 2),
    SPD("SPD", 2),
    PRC("PRC", 3),
    AGL("AGL", 3);

    private final String representation;
    private final int baseFactor;

    Stat(String representation, int baseFactor) {
        this.representation = representation;
        this.baseFactor = baseFactor;
    }

    @Override
    public String toString() {
        return representation;
    }


//ToDo HP doesnt have a baseFactor
//stat change factor (-5 to +5)


    public double calculateStatChangeFactor(int statChange) {
        if (statChange >= 0) {
            return (double) (baseFactor + statChange) / baseFactor;
        } else {
            return (double) baseFactor / (baseFactor - statChange);
        }
    }

    public static Stat fromString(String representation) {
        for (Stat stat : values()) {
            if (stat.representation.equals(representation)) {
                return stat;
            }
        }
        return null;
    }
}