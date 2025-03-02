package edu.kit.kastel.monstercombat.model;

/**
 * Represents the status conditions that can affect monsters.
 */
public enum StatusCondition {
    WET("WET", "soaking wet", "dried up"),
    BURN("BURN", "burning", "burning has faded"),
    QUICKSAND("QUICKSAND", "caught in quicksand", "escaped the quicksand"),
    SLEEP("SLEEP", "asleep", "woke up"),
    NONE("NONE", "OK", "");

    private final String representation;
    private final String activeMessage;
    private final String endMessage;

    StatusCondition(String representation, String activeMessage, String endMessage) {
        this.representation = representation;
        this.activeMessage = activeMessage;
        this.endMessage = endMessage;
    }

    @Override
    public String toString() {
        return representation;
    }

    /**
     * Gets the message describing the active condition.
     *
     * @return the active condition message
     */
    public String getActiveMessage() {
        return activeMessage;
    }

    /**
     * Gets the message for when the condition ends.
     *
     * @return the end condition message
     */
    public String getEndMessage() {
        return endMessage;
    }

    /**
     * Gets the phrase for when the condition is inflicted.
     *
     * @return the phrase for inflicting the condition
     */
    public String getInflictedMessage() {
        return switch (this) {
            case WET -> "becomes soaking wet";
            case BURN -> "caught on fire";
            case QUICKSAND -> "gets caught by quicksand";
            case SLEEP -> "falls asleep";
            default -> "";
        };
    }

    /**
     * Creates a StatusCondition from its string representation.
     *
     * @param representation the string representation
     * @return the corresponding StatusCondition or NONE if no matching condition is found
     */
    public static StatusCondition fromString(String representation) {
        for (StatusCondition condition : values()) {
            if (condition.representation.equals(representation)) {
                return condition;
            }
        }
        return NONE;
    }
}