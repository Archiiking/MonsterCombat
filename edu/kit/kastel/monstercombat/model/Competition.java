package edu.kit.kastel.monstercombat.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public final class Competition {
    private static Competition instance;
    private final BattleManager battleManager;

    private final List<Monster> monsters;
    private final Map<String, Monster> monstersByNumber;

    private Monster currentMonster;
    private Action currentAction;
    private Monster currentTarget;

    private int currentRound;
    private boolean decided;
    private Monster winner;

    private final Random random;
    private final boolean debugMode;
    private Scanner debugScanner;

    private Competition(long seed, boolean debug) {
        this.battleManager = new BattleManager(this);

        this.monsters = new ArrayList<>();
        this.monstersByNumber = new HashMap<>();

        this.currentRound = 0;
        this.decided = false;
        this.winner = null;

        this.random = new Random(seed);
        this.debugMode = debug;
        if (debug) {
            this.debugScanner = new Scanner(System.in);
        }
    }

    public static void initialize(long seed, boolean debug) {
        instance = new Competition(seed, debug);
    }

    public static Competition getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Competition has not been initialized");
        }
        return instance;
    }

    public void addMonster(Monster monster) {
        monsters.add(monster);
        nameDuplicateMonsters(monster);
    }

    private void nameDuplicateMonsters(Monster monster) {
        // Handle duplicate names
        String baseName = monster.getName();
        if (monstersByNumber.containsKey(baseName)) {
            int suffix = 1;
            while (monstersByNumber.containsKey(baseName + "#" + suffix)) {
                suffix++;
            }
            String displayName = baseName + "#" + suffix;
            monster.setDisplayName(displayName);
            monstersByNumber.put(displayName, monster);
        } else {
            monstersByNumber.put(baseName, monster);
        }
    }

    public Monster getMonster(String name) {
        return monstersByNumber.get(name);
    }

    public List<Monster> getMonsters() {
        return new ArrayList<>(monsters);
    }

    public Monster getCurrentMonster() {
        return currentMonster;
    }

    public Action getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(Action action) {
        this.currentAction = action;
    }

    public Monster getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(Monster target) {
        this.currentTarget = target;
    }

    public boolean isDecided() {
        return decided;
    }

    public Monster getWinner() {
        return winner;
    }

    public double getRandomDouble() {
        if (debugMode) {
            System.out.print("Decide random value: a double? ");
            return Double.parseDouble(debugScanner.nextLine());
        }
        return random.nextDouble();
    }

    public double getRandomDouble(double min, double max) {
        if (debugMode) {
            System.out.printf("Decide random value: a double between %.2f and %.2f? ", min, max);
            return Double.parseDouble(debugScanner.nextLine());
        }
        return min + (max - min) * random.nextDouble();
    }

    public int getRandomInt(int min, int max) {
        if (debugMode) {
            System.out.printf("Decide random count: an integer between %d and %d? ", min, max);
            return Integer.parseInt(debugScanner.nextLine());
        }
        return min + random.nextInt(max - min + 1);
    }

    public Random getRandom() {
        return random;
    }

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

    public void run() {
        System.out.printf("The %d monsters enter the competition!\n", monsters.size());

        while (!decided) {
            runRound();
        }
    }

    private void runRound() {
        currentRound++;

        // Phase 0: Check if competition is decided
        checkDecided();
        if (decided) {
            return;
        }

        // Phase I: Choose actions for each monster
        chooseActionsPhase();

        // If competition decided during action choice, exit
        if (decided) {
            return;
        }

        // Phase II: Execute actions
        executeActionsPhase();

        // End of round - check for protection expiry
        endRoundPhase();
    }

    private void chooseActionsPhase() {
        for (Monster monster : monsters) {
            if (monster.isDefeated()) {
                continue;
            }

            currentMonster = monster;
            currentAction = null;
            currentTarget = null;

            // User chooses action through UI
            System.out.println();
            System.out.printf("What should %s do?\n", monster.getDisplayName());

            // Wait for action to be set by UI
            while (currentAction == null && !monster.isDefeated() && !decided) {
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
    }

    private void executeActionsPhase() {
        // Sort monsters by speed
        List<Monster> monstersSortedBySpeed = new ArrayList<>(monsters);
        monstersSortedBySpeed.sort(Comparator.comparingDouble(
                (Monster m) -> m.getEffectiveStat(Stat.SPD)).reversed());

        for (Monster monster : monstersSortedBySpeed) {
            if (monster.isDefeated()) {
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
            battleManager.executeAction(monster, currentAction, currentTarget);

            // Check if competition is decided
            checkDecided();
            if (decided) {
                return;
            }
        }
    }

    private void endRoundPhase() {
        for (Monster monster : monsters) {
            if (monster.decreaseProtectionDuration()) {
                System.out.printf("%s's protection fades away...\n", monster.getDisplayName());
            }
        }
    }

    private void checkDecided() {
        // Count conscious monsters
        int consciousMonsters = 0;
        Monster lastConsciousMonster = null;

        for (Monster monster : monsters) {
            if (!monster.isDefeated()) {
                consciousMonsters++;
                lastConsciousMonster = monster;
            }
        }

        // Competition is decided if 0 or 1 monsters remain
        if (consciousMonsters <= 1) {
            decided = true;

            if (consciousMonsters == 1) {
                winner = lastConsciousMonster;
                System.out.println();
                System.out.printf("%s has no opponents left and wins the competition!\n", winner.getDisplayName());
            } else {
                winner = null;
                System.out.println();
                System.out.println("All monsters have fainted. The competition ends without a winner!");
            }
        }
    }

    public void reset() {
        // Reset all monsters
        for (Monster monster : monsters) {
            monster.heal(monster.getMaxHp()); // Heal to full
            monster.setStatusCondition(StatusCondition.NONE);

            // Reset stat changes
            for (Stat stat : Stat.values()) {
                if (stat != Stat.HP) {
                    monster.setStatChange(stat, -monster.getStatChange(stat));
                }
            }
        }

        // Reset competition state
        currentRound = 0;
        decided = false;
        winner = null;
    }

    public void clear() {
        monsters.clear();
        monstersByNumber.clear();
        currentMonster = null;
        currentAction = null;
        currentTarget = null;
        currentRound = 0;
        decided = false;
        winner = null;
    }
}
/*
    private final List<Monster> monsters;
    private final Map<Integer, Monster> monstersByNumber;
    private int currentRound;
    private int currentPhase;
    private Monster currentMonster;
    private boolean isRunning;
    private Monster winner;
    private final Random random;
    private final boolean debugMode;
    private final Scanner debugScanner;

    private final Queue<Effect> effectQueue;
    private final Map<Monster, List<String>> pendingMessages;

    // Flags for tracking one-time messages
    private boolean isFirstDamageEffect = true;


    public Competition(List<Monster> monsters, boolean debugMode, long seed) {
        this.monsters = new ArrayList<>();
        this.monstersByNumber = new HashMap<>();
        this.effectQueue = new LinkedList<>();
        this.pendingMessages = new HashMap<>();

        this.debugMode = debugMode;
        this.random = new Random(seed);
        this.debugScanner = new Scanner(System.in);

        // Assign competitor numbers and initialize maps
        for (int i = 0; i < monsters.size(); i++) {
            Monster monster = monsters.get(i);
            int number = i + 1;
            monster.setCompetitorNumber(number);
            monstersByNumber.put(number, monster);
        }

        this.currentRound = 1;
        this.currentPhase = 0;
        this.isRunning = true;
    }

    public void start() {
        System.out.println("The " + monsters.size() + " monsters enter the competition!");
        executePhase0(); // Check for initial win conditions
    }

    public boolean isRunning() {
        return isRunning;
    }

    public Monster getCurrentMonster() {
        return currentMonster;
    }
 */