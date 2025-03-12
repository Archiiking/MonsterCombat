package edu.kit.kastel.monstercombat.model;

public enum Element {
    NORMAL("NORMAL"),
    WATER("WATER"),
    FIRE("FIRE"),
    EARTH("EARTH");

    private final String representation;

    Element(String representation) {
        this.representation = representation;
    }

    @Override
    public String toString() {
        return representation;
    }

    public boolean isVeryEffectiveAgainst(final Element target) {
        return (this == WATER && target == FIRE)
                || (this == FIRE && target == EARTH)
                || (this == EARTH && target == WATER);
    }

    public boolean isNotVeryEffectiveAgainst(final Element target) {
        return target.isVeryEffectiveAgainst(this);
    }

    public boolean isNormalEffectiveAgainst(final Element target) {
        return this == NORMAL || target == NORMAL || this == target;
    }

    public double getEffectivenessMultiplier(final Element target) {
        if (this.isVeryEffectiveAgainst(target)) {
            return 2.0;
        } else if (this.isNotVeryEffectiveAgainst(target)) {
            return 0.5;
        } else {
            return 1.0;
        }
    }

    public static Element fromString(String representation) {
        for (Element element : values()) {
            if (element.representation.equals(representation)) {
                return element;
            }
        }
        return null;
    }
}