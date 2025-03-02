package edu.kit.kastel.monstercombat.model;

import java.util.ArrayList;
import java.util.List;

import edu.kit.kastel.monstercombat.model.effect.Effect;

/**
 * Represents an action that a monster can perform.
 */
public class Action {
    private final String name;
    private final Element element;
    private final List<Effect> effects;

    /**
     * Constructs a new action.
     *
     * @param name the name of the action
     * @param element the element of the action
     */
    public Action(String name, Element element) {
        this.name = name;
        this.element = element;
        this.effects = new ArrayList<>();
    }

    /**
     * Gets the name of the action.
     *
     * @return the action's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the element of the action.
     *
     * @return the action's element
     */
    public Element getElement() {
        return element;
    }

    /**
     * Adds an effect to the action.
     *
     * @param effect the effect to add
     */
    public void addEffect(Effect effect) {
        effects.add(effect);
    }

    /**
     * Gets all effects of the action.
     *
     * @return a list of the action's effects
     */
    public List<Effect> getEffects() {
        return new ArrayList<>(effects);
    }

    /**
     * Gets the hit rate of the first effect.
     *
     * @return the hit rate of the first effect or 0 if there are no effects
     */
    public int getFirstEffectHitRate() {
        if (effects.isEmpty()) {
            return 0;
        }
        return effects.get(0).getHitRate();
    }
}