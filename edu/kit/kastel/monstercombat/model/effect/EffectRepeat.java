package edu.kit.kastel.monstercombat.model.effect;

import edu.kit.kastel.monstercombat.model.Competition;
import edu.kit.kastel.monstercombat.model.Monster;

import java.util.List;
import java.util.Queue;

public class EffectRepeat extends Effect {
    private final int count;
    private final List<Effect> repeatedEffects;

    public EffectRepeat(int count, List<Effect> repeatedEffects) {
        super(100, TargetType.USER);
        this.count = count;
        this.repeatedEffects = repeatedEffects;
    }

    public EffectRepeat(int minCount, int maxCount, List<Effect> repeatedEffects) {
        super(100, TargetType.USER);
        this.count = Competition.getInstance().getRandomInt(minCount, maxCount);
        this.repeatedEffects = repeatedEffects;
    }

    @Override
    public boolean execute(Monster user, Monster target, boolean isFirstEffect) {
        return true;
    }

    @Override
    public void addToQueue(Queue<Effect> queue) {
        int repeatCount = getCount();

        for (int i = 0; i < repeatCount; i++) {
            queue.addAll(getRepeatedEffects());
        }
    }

    public int getCount() {
        return count;
    }

    public List<Effect> getRepeatedEffects() {
        return repeatedEffects;
    }
}
