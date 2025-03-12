package edu.kit.kastel.monstercombat.model.effect;

import edu.kit.kastel.monstercombat.model.Monster;

public class EffectContinue extends Effect {

    public EffectContinue(int hitRate) {
        super(hitRate, TargetType.USER);
    }

    @Override
    public boolean execute(Monster user, Monster target, boolean isFirstEffect) {
        return true; // Just continue to the next effect
    }

}
