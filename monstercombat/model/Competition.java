package edu.kit.kastel.monstercombat.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

import edu.kit.kastel.monstercombat.model.effect.Effect;
import edu.kit.kastel.monstercombat.model.effect.EffectDamage;
import edu.kit.kastel.monstercombat.model.effect.EffectRepeat;

/**
 * Represents a competition between monsters.
 */
public class Competition {
    private static final int MONSTER_DISPLAY_WIDTH = 20;
    private static Competition instance;

    private final List<Monster> monsters;
    private final Map<String, Monster> monstersByName;
    private final Random random;
    private Monster currentMonster;
    private Action currentAction;
    private Monster currentTarget;
    private int currentRound;
    private boolean decided;
    private Monster winner;
    private final boolean debugMode;
    private Scanner debugScanner;

    /**
     * Constructs a new competition.
     *
     * @param seed the seed for the random number generator
     * @param debug whether to run in debug mode
     */
    private Competition(long seed, boolean debug) {
        this.monsters = new ArrayList<>();
        this.monstersByName = new HashMap<>();
        this.random = new Random(seed);
        this.currentRound = 0;
        this.decided = false;
        this.winner = null;
        this.debugMode = debug;
        if (debug) {
            this.debugScanner = new Scanner(System.in);
        }
    }

    /**
     * Initializes the singleton instance of the competition.
     *
     * @param seed the seed for the random number generator
     * @param debug whether to run in debug mode
     */
    public static void initialize(long seed, boolean debug) {
        instance = new Competition(seed, debug);
    }

    /**
     * Gets the singleton instance of the competition.
     *
     * @return the competition instance
     * @throws IllegalStateException if the competition has not been initialized
     */
    public static Competition getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Competition has not been initialized");
        }
        return instance;
    }

    /**
     * Adds a monster to the competition.
     *
     * @param monster the monster to add
     */
    public void addMonster(Monster monster) {
        monsters.add(monster);

        // Handle duplicate names
        String baseName = monster.getName();
        if (monstersByName.containsKey(baseName)) {
            int suffix = 1;
            while (monstersByName.containsKey(baseName + "#" + suffix)) {
                suffix++;
            }
            String displayName = baseName + "#" + suffix;
            monster.setDisplayName(displayName);
            monstersByName.put(displayName, monster);
        } else {
            monstersByName.put(baseName, monster);
        }
    }

    /**
     * Gets a monster by its name.
     *
     * @param name the name of the monster
     * @return the monster or null if not found
     */
    public Monster getMonster(String name) {
        return monstersByName.get(name);
    }

    /**
     * Gets all monsters in the competition.
     *
     * @return a list of all monsters
     */
    public List<Monster> getMonsters() {
        return new ArrayList<>(monsters);
    }

    /**
     * Gets the current monster that is taking its turn.
     *
     * @return the current monster
     */
    public Monster getCurrentMonster() {
        return currentMonster;
    }

    /**
     * Gets the current action being performed.
     *
     * @return the current action
     */
    public Action getCurrentAction() {
        return currentAction;
    }

    /**
     * Sets the current action being performed.
     *
     * @param action the action to set
     */
    public void setCurrentAction(Action action) {
        this.currentAction = action;
    }

    /**
     * Gets the current target of the action.
     *
     * @return the current target
     */
    public Monster getCurrentTarget() {
        return currentTarget;
    }

    /**
     * Sets the current target of the action.
     *
     * @param target the target to set
     */
    public void setCurrentTarget(Monster target) {
        this.currentTarget = target;
    }

    /**
     * Gets whether the competition has been decided.
     *
     * @return true if the competition has been decided
     */
    public boolean isDecided() {
        return decided;
    }

    /**
     * Gets the winning monster.
     *
     * @return the winning monster or null if there is no winner
     */
    public Monster getWinner() {
        return winner;
    }

    /**
     * Gets a random double value.
     *
     * @return a random double between 0.0 and 1.0
     */
    public double getRandomDouble() {
        if (debugMode) {
            System.out.print("Decide random value: a double? ");
            return Double.parseDouble(debugScanner.nextLine());
        }
        return random.nextDouble();
    }

    /**
     * Gets a random double value in the specified range.
     *
     * @param min the minimum value (inclusive)
     * @param max the maximum value (exclusive)
     * @return a random double between min and max
     */
    public double getRandomDouble(double min, double max) {
        if (debugMode) {
            System.out.printf("Decide random value: a double between %.2f and %.2f? ", min, max);
            return Double.parseDouble(debugScanner.nextLine());
        }
        return min + (max - min) * random.nextDouble();
    }

    /**
     * Gets a random integer value in the specified range.
     *
     * @param min the minimum value (inclusive)
     * @param max the maximum value (inclusive)
     * @return a random integer between min and max
     */
    public int getRandomInt(int min, int max) {
        if (debugMode) {
            System.out.printf("Decide random count: an integer between %d and %d? ", min, max);
            return Integer.parseInt(debugScanner.nextLine());
        }
        return min + random.nextInt(max - min + 1);
    }

    /**
     * Gets the random number generator.
     *
     * @return the random number generator
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Checks if a random value is less than or equal to the specified probability.
     *
     * @param context the context description for debug mode
     * @param probability the probability percentage (0-100)
     * @return true if the random value is less than or equal to the probability
     */
    public boolean checkProbability(String context, double probability) {
        if (debugMode) {
            System.out.printf("Decide %s: yes or no? (y/n) ", context);
            String input = debugScanner.nextLine().trim().toLowerCase();
            while (!input.equals("y") && !input.equals("n")) {
                System.out.println("Error, enter y or n.");
                System.out.printf("Decide %s: yes or no? (y/n) ", context);
                input = debugScanner.nextLine().trim().toLowerCase();
            }
            return input.equals("y");
        }
        return random.nextDouble() * 100 <= probability;
    }

    /**
     * Runs the competition.
     */
    public void run() {
        System.out.printf("The %d monsters enter the competition!\n", monsters.size());

        while (!decided) {
            runRound();
        }
    }

    /**
     * Runs a single round of the competition.
     */
    private void runRound() {
        currentRound++;

        // Phase 0: Check if competition is decided
        checkDecided();
        if (decided) {
            return;
        }

        // Phase I: Choose actions for each monster
        for (Monster monster : monsters) {
            if (monster.isFainted()) {
                continue;
            }

            currentMonster = monster;
            currentAction = null;
            currentTarget = null;

            // User chooses action through UI
            System.out.println();
            System.out.printf("What should %s do?\n", monster.getDisplayName());

            // Wait for action to be set by UI
            while (currentAction == null && !monster.isFainted() && !decided) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // If competition was decided while waiting, exit
            if (decided) {
                return;
            }
        }

        // Phase II: Execute actions
        // Sort monsters by speed
        List<Monster> sortedMonsters = new ArrayList<>(monsters);
        sortedMonsters.sort(Comparator.comparingDouble((Monster m) -> m.getEffectiveStat(Stat.SPD)).reversed());

        for (Monster monster : sortedMonsters) {
            if (monster.isFainted()) {
                continue;
            }

            currentMonster = monster;

            System.out.println();
            System.out.printf("It's %s's turn.\n", monster.getDisplayName());

            // Skip if monster has no action (should never happen)
            if (currentAction == null) {
                System.out.printf("%s passes!\n", monster.getDisplayName());
                continue;
            }

            // Execute action
            executeAction(monster, currentAction, currentTarget);

            // Check if competition is decided
            checkDecided();
            if (decided) {
                return;
            }
        }

        // End of round - check for protection expiry
        for (Monster monster : monsters) {
            if (monster.decreaseProtection()) {
                System.out.printf("%s's protection fades away...\n", monster.getDisplayName());
            }
        }
    }

    /**
     * Executes an action by a monster on a target.
     *
     * @param user the monster using the action
     * @param action the action to execute
     * @param target the target monster
     */
    private void executeAction(Monster user, Action action, Monster target) {
        // Skip if monster is asleep
        if (user.getStatusCondition() == StatusCondition.SLEEP) {
            System.out.printf("%s is asleep!\n", user.getDisplayName());

            // Check if sleep ends
            if (checkProbability("sleep end", 33.33)) {
                System.out.printf("%s woke up!\n", user.getDisplayName());
                user.setStatusCondition(StatusCondition.NONE);
            }

            return;
        }

        // Announce action
        System.out.printf("%s uses %s!\n", user.getDisplayName(), action.getName());

        // Create queue of effects to execute
        Queue<Effect> effectQueue = new LinkedList<>();
        for (Effect effect : action.getEffects()) {
            if (effect instanceof EffectRepeat repeatEffect) {
                int repeatCount = repeatEffect.getCount();

                for (int i = 0; i < repeatCount; i++) {
                    effectQueue.addAll(repeatEffect.getRepeatedEffects());
                }
            } else {
                effectQueue.add(effect);
            }
        }

        // Execute effects
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

        // Handle status condition effects at end of turn
        if (user.getStatusCondition() != StatusCondition.NONE && user.getStatusCondition() != StatusCondition.SLEEP) {
            System.out.printf("%s is %s!\n", user.getDisplayName(), user.getStatusCondition().getActiveMessage());

            // Check if condition ends
            if (checkProbability("status condition end", 33.33)) {
                System.out.printf("%s %s!\n", user.getDisplayName(), user.getStatusCondition().getEndMessage());
                user.setStatusCondition(StatusCondition.NONE);
            } else if (user.getStatusCondition() == StatusCondition.BURN) {
                // Burn damage
                EffectDamage burnDamage = new EffectDamage(user);
                burnDamage.execute(user, null, false);

                // Check if fainted
                if (user.isFainted()) {
                    System.out.printf("%s faints!\n", user.getDisplayName());
                }
            }
        }
    }

    /**
     * Executes a single effect.
     *
     * @param user the monster using the effect
     * @param target the target monster
     * @param effect the effect to execute
     * @param isFirstEffect whether this is the first effect of the action
     * @return true if the effect hit, false otherwise
     */
    private boolean executeEffect(Monster user, Monster target, Effect effect, boolean isFirstEffect) {
        // Calculate hit chance
        double hitChance = effect.getHitRate();

        // Apply precision/evasion if target is not user
        if (target != null && target != user) {
            hitChance *= user.getEffectiveStat(Stat.PRC) / target.getEffectiveStat(Stat.AGL);
        }

        // Check if effect hits
        boolean hits = checkProbability("effect hit", hitChance);

        if (hits) {
            return effect.execute(user, target, isFirstEffect);
        }

        return false;
    }

    /**
     * Checks if the competition has been decided.
     */
    private void checkDecided() {
        // Count conscious monsters
        int conscious = 0;
        Monster lastConscious = null;

        for (Monster monster : monsters) {
            if (!monster.isFainted()) {
                conscious++;
                lastConscious = monster;
            }
        }

        // Competition is decided if 0 or 1 monsters remain
        if (conscious <= 1) {
            decided = true;

            if (conscious == 1) {
                winner = lastConscious;
                System.out.println();
                System.out.printf("%s has no opponents left and wins the competition!\n", winner.getDisplayName());
            } else {
                winner = null;
                System.out.println();
                System.out.println("All monsters have fainted. The competition ends without a winner!");
            }
        }
    }

    /**
     * Resets the competition with the current monsters.
     */
    public void reset() {
        // Reset all monsters
        for (Monster monster : monsters) {
            monster.heal(monster.getMaxHp()); // Heal to full
            monster.setStatusCondition(StatusCondition.NONE);

            // Reset stat changes
            for (Stat stat : Stat.values()) {
                if (stat != Stat.HP) {
                    monster.changeStatBy(stat, -monster.getStatChange(stat));
                }
            }
        }

        // Reset competition state
        currentRound = 0;
        decided = false;
        winner = null;
    }

    /**
     * Clears all monsters from the competition.
     */
    public void clear() {
        monsters.clear();
        monstersByName.clear();
        currentMonster = null;
        currentAction = null;
        currentTarget = null;
        currentRound = 0;
        decided = false;
        winner = null;
    }
}