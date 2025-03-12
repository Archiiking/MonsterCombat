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


//ToDo Instanceof ist verboten.
//FixMe Die 3 Methoden ändern. TaskA hat die nicht.

/*
    public String getDamageString() {
        for (Effect effect : effects) {
            if (effect instanceof EffectDamage) {
                EffectDamage damageEffect = (EffectDamage) effect;
                return damageEffect.getDamageString();
            }
        }
        return "--";
    }

    public String getFirstEffectHitRateString() {
        if (effects.isEmpty()) {
            return "0";
        }
        return String.valueOf(effects.get(0).getHitRate());
    }

    @Override
    public String toString() {
        return String.format("%s: ELEMENT %s, Damage %s, HitRate %s",
                name, element, getDamageString(), getFirstEffectHitRateString());
    }

 */

    public int getFirstEffectHitRate() {
        if (effects.isEmpty()) {
            return 0;
        }
        return effects.get(0).getHitRate();
    }

}
