package edu.kit.kastel.monstercombat.model;

public enum StatusCondition {
    NONE("NONE", null, null, null),
    WET("WET", " becomes soaking wet!", " is soaking wet!", " woke up!"),
    BURN("BURN", " caught on fire!", " is burning!", "'s burning has faded!"),
    QUICKSAND("QUICKSAND", " gets caught by quicksand!", " is caught in quicksand!", " escaped the quicksand!"),
    SLEEP("SLEEP", " falls asleep!", " is asleep!", " woke up!"),
    FAINTED("FAINTED", null, null, null);

    private final String representation;
    private final String inflictedMessage;
    private final String activeMessage;
    private final String endMessage;

    StatusCondition(String representation, String startMessage, String activeMessage, String endMessage) {
        this.representation = representation;
        this.inflictedMessage = startMessage;
        this.activeMessage = activeMessage;
        this.endMessage = endMessage;
    }

    @Override
    public String toString() {
        return representation;
    }

    public String getInflictedMessage() {
        return inflictedMessage;
    }

    public String getActiveMessage() {
        return activeMessage;
    }

    public String getEndMessage() {
        return endMessage;
    }

    public double getStatusConditionMultiplier(Stat stat) {
        switch (this) {
            case WET:
                if (stat == Stat.DEF) {
                    return 0.75; // 25% reduction in defense
                }
                break;
            case BURN:
                if (stat == Stat.ATK) {
                    return 0.75; // 25% reduction in attack
                }
                break;
            case QUICKSAND:
                if (stat == Stat.SPD) {
                    return 0.75; // 25% reduction in speed
                }
                break;
            default:
                return 1.0;
        }
        return 1.0; // No effect on other stats
    }

    public boolean preventsActions() {
        return this == SLEEP || this == FAINTED;
    }

    public static StatusCondition fromString(String representation) {
        for (StatusCondition condition : values()) {
            if (condition.representation.equals(representation)) {
                return condition;
            }
        }
        return NONE;
    }
}
