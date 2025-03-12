package edu.kit.kastel.monstercombat.model;

import edu.kit.kastel.monstercombat.model.effect.Effect;

import java.util.ArrayList;
import java.util.List;

public class Action {

    private final String name;
    private final Element element;
    private final List<Effect> effects;

    public Action(String name, Element element) {
        this.name = name;
        this.element = element;
        this.effects = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Element getElement() {
        return element;
    }

    public void addEffect(Effect effect) {
        effects.add(effect);
    }

    public List<Effect> getEffects() {
        return new ArrayList<>(effects);
    }

    public int getFirstEffectHitRate() {
        if (effects.isEmpty()) {
            return 0;
        }
        return effects.get(0).getHitRate();
    }

}
