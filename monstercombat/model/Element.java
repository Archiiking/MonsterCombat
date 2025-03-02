package edu.kit.kastel.monstercombat.model;

/**
 * Represents the elements that monsters and actions can have.
 */
public enum Element {
    WATER("WATER"),
    FIRE("FIRE"),
    EARTH("EARTH"),
    NORMAL("NORMAL");

    private final String representation;

    Element(String representation) {
        this.representation = representation;
    }

    @Override
    public String toString() {
        return representation;
    }

    /**
     * Determines if this element is very effective against the target element.
     *
     * @param target the target element
     * @return true if this element is very effective against the target
     */
    public boolean isVeryEffectiveAgainst(Element target) {
        return (this == WATER && target == FIRE) ||
                (this == FIRE && target == EARTH) ||
                (this == EARTH && target == WATER);
    }

    /**
     * Determines if this element is not very effective against the target element.
     *
     * @param target the target element
     * @return true if this element is not very effective against the target
     */
    public boolean isNotVeryEffectiveAgainst(Element target) {
        return target.isVeryEffectiveAgainst(this);
    }

    /**
     * Determines if this element has normal effectiveness against the target element.
     *
     * @param target the target element
     * @return true if this element has normal effectiveness against the target
     */
    public boolean isNormalEffectiveAgainst(Element target) {
        return this == NORMAL || target == NORMAL || this == target;
    }

    /**
     * Creates an Element from its string representation.
     *
     * @param representation the string representation
     * @return the corresponding Element or null if no matching element is found
     */
    public static Element fromString(String representation) {
        for (Element element : values()) {
            if (element.representation.equals(representation)) {
                return element;
            }
        }
        return null;
    }
}