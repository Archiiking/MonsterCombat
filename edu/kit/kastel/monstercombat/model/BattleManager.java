package edu.kit.kastel.monstercombat.model;

import java.util.LinkedList;
import java.util.Queue;

import edu.kit.kastel.monstercombat.model.effect.Effect;
import edu.kit.kastel.monstercombat.model.effect.EffectDamage;

public class BattleManager {
    private final Competition competition;

    public BattleManager(Competition competition) {
        this.competition = competition;
    }

    public void executeAction(Monster user, Action action, Monster target) {
        if (user.getStatusCondition() == StatusCondition.SLEEP) {
            System.out.printf("%s is asleep!\n", user.getDisplayName());
            if (competition.checkProbability("sleep end", 33.33)) {
                System.out.printf("%s woke up!\n", user.getDisplayName());
                user.setStatusCondition(StatusCondition.NONE);
            }
            return;
        }
        System.out.printf("%s uses %s!\n", user.getDisplayName(), action.getName());
        Queue<Effect> effectQueue = createEffectQueue(action);
        executeEffectQueue(user, target, effectQueue);
        processStatusConditions(user);
    }


//ToDo Instanceof ist verboten.


    private Queue<Effect> createEffectQueue(Action action) {
        Queue<Effect> effectQueue = new LinkedList<>();

        for (Effect effect : action.getEffects()) {
            effect.addToQueue(effectQueue);
        }

        return effectQueue;
    }

    private void executeEffectQueue(Monster user, Monster target, Queue<Effect> effectQueue) {
        if (!effectQueue.isEmpty()) {
            Effect firstEffect = effectQueue.poll();
            boolean firstEffectHit = executeEffect(user, target, firstEffect, true);
            if (!firstEffectHit) {
                System.out.println("The action failed...");
                return;
            }
            while (!effectQueue.isEmpty()) {
                Effect effect = effectQueue.poll();
                executeEffect(user, target, effect, false);
            }
        }
    }

    private boolean executeEffect(Monster user, Monster target, Effect effect, boolean isFirstEffect) {
        // Calculate hit chance
        double hitChance = effect.getHitRate();

        // Apply precision/evasion if target is not user
        if (target != null && target != user) {
            hitChance *= user.getEffectiveStat(Stat.PRC) / target.getEffectiveStat(Stat.AGL);
        }

        // Check if effect hits
        boolean hits = competition.checkProbability("effect hit", hitChance);

        if (hits) {
            return effect.execute(user, target, isFirstEffect);
        }

        return false;
    }

    private void processStatusConditions(Monster monster) {
        if (monster.getStatusCondition() != StatusCondition.NONE
                && monster.getStatusCondition() != StatusCondition.SLEEP) {
            System.out.printf("%s is %s!\n", monster.getDisplayName(),
                    monster.getStatusCondition().getActiveMessage());

            // Check if condition ends
            if (competition.checkProbability("status condition end", 33.33)) {
                System.out.printf("%s %s!\n", monster.getDisplayName(),
                        monster.getStatusCondition().getEndMessage());
                monster.setStatusCondition(StatusCondition.NONE);
            } else if (monster.getStatusCondition() == StatusCondition.BURN) {
                // Burn damage
                EffectDamage burnDamage = new EffectDamage(monster);
                burnDamage.execute(monster, null, false);

                // Check if fainted
                if (monster.isDefeated()) {
                    System.out.printf("%s faints!\n", monster.getDisplayName());
                }
            }
        }
    }
}