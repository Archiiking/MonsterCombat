package edu.kit.kastel.monstercombat.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.kit.kastel.monstercombat.model.effect.Effect;
import edu.kit.kastel.monstercombat.model.effect.EffectContinue;
import edu.kit.kastel.monstercombat.model.effect.EffectDamage;
import edu.kit.kastel.monstercombat.model.effect.EffectDefense;
import edu.kit.kastel.monstercombat.model.effect.EffectHealing;
import edu.kit.kastel.monstercombat.model.effect.EffectRepeat;
import edu.kit.kastel.monstercombat.model.effect.EffectStatChange;
import edu.kit.kastel.monstercombat.model.effect.EffectStatusCondition;
import edu.kit.kastel.monstercombat.model.exception.ConfigurationException;

/**
 * Loads configuration from a file.
 */
public class ConfigurationLoader {
    private final Map<String, Action> actions;
    private final List<Monster> monsters;

    /**
     * Constructs a new configuration loader.
     */
    public ConfigurationLoader() {
        this.actions = new HashMap<>();
        this.monsters = new ArrayList<>();
    }

    /**
     * Loads a configuration from a file.
     *
     * @param filePath the path to the configuration file
     * @return the loaded configuration as a string
     * @throws ConfigurationException if the configuration is invalid
     */
    public String loadConfiguration(String filePath) throws ConfigurationException {
        try {
            Path path = Paths.get(filePath);
            StringBuilder configContent = new StringBuilder();
            List<String> lines = Files.readAllLines(path);

            // Clear previous data
            actions.clear();
            monsters.clear();

            // First pass to collect all configurations for output
            for (String line : lines) {
                configContent.append(line).append("\n");
            }

            // Parse the configuration
            parseConfiguration(lines);

            return configContent.toString();
        } catch (IOException e) {
            throw new ConfigurationException("Error reading configuration file: " + e.getMessage());
        }
    }

    /**
     * Parses the configuration from the provided lines.
     *
     * @param lines the lines of the configuration file
     * @throws ConfigurationException if the configuration is invalid
     */
    private void parseConfiguration(List<String> lines) throws ConfigurationException {
        List<String> filteredLines = new ArrayList<>();

        // Remove empty lines for easier parsing
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                filteredLines.add(line.trim());
            }
        }

        int index = 0;

        // Parse actions
        while (index < filteredLines.size()) {
            String line = filteredLines.get(index);

            if (line.startsWith("action ")) {
                index = parseAction(filteredLines, index);
            } else if (line.startsWith("monster ")) {
                index = parseMonster(filteredLines, index);
            } else {
                throw new ConfigurationException("Invalid configuration line: " + line);
            }
        }
    }

    /**
     * Parses an action from the configuration.
     *
     * @param lines the lines of the configuration file
     * @param startIndex the index to start parsing from
     * @return the next index to parse from
     * @throws ConfigurationException if the action configuration is invalid
     */
    private int parseAction(List<String> lines, int startIndex) throws ConfigurationException {
        // Parse action header
        String headerLine = lines.get(startIndex);
        String[] headerParts = headerLine.split(" ", 3);

        if (headerParts.length < 3) {
            throw new ConfigurationException("Invalid action header: " + headerLine);
        }

        String actionName = headerParts[1];
        Element element = Element.fromString(headerParts[2]);

        if (element == null) {
            throw new ConfigurationException("Invalid element: " + headerParts[2]);
        }

        Action action = new Action(actionName, element);
        List<Effect> repeatedEffects = null;

        int index = startIndex + 1;

        // Parse effects
        while (index < lines.size()) {
            String line = lines.get(index);

            if (line.equals("end action")) {
                // Add action to map
                actions.put(actionName, action);
                return index + 1;
            } else if (line.equals("end repeat")) {
                if (repeatedEffects == null) {
                    throw new ConfigurationException("Unexpected 'end repeat' without matching 'repeat'");
                }

                // Repeats are handled in the repeat parsing
                repeatedEffects = null;
                index++;
            } else if (line.startsWith("repeat ")) {
                String[] parts = line.split(" ");

                if (parts.length < 2) {
                    throw new ConfigurationException("Invalid repeat format: " + line);
                }

                repeatedEffects = new ArrayList<>();

                if (parts[1].equals("random")) {
                    if (parts.length < 4) {
                        throw new ConfigurationException("Invalid random repeat format: " + line);
                    }

                    int min = Integer.parseInt(parts[2]);
                    int max = Integer.parseInt(parts[3]);

                    EffectRepeat repeatEffect = new EffectRepeat(min, max, repeatedEffects);
                    action.addEffect(repeatEffect);
                } else {
                    int count = Integer.parseInt(parts[1]);

                    EffectRepeat repeatEffect = new EffectRepeat(count, repeatedEffects);
                    action.addEffect(repeatEffect);
                }

                index++;
            } else {
                // Parse single effect
                Effect effect = parseEffect(line);

                if (repeatedEffects != null) {
                    repeatedEffects.add(effect);
                } else {
                    action.addEffect(effect);
                }

                index++;
            }
        }

        throw new ConfigurationException("Unexpected end of file while parsing action: " + actionName);
    }

    /**
     * Parses an effect from a configuration line.
     *
     * @param line the configuration line
     * @return the parsed effect
     * @throws ConfigurationException if the effect configuration is invalid
     */
    private Effect parseEffect(String line) throws ConfigurationException {
        String[] parts = line.split(" ");

        if (parts.length < 2) {
            throw new ConfigurationException("Invalid effect format: " + line);
        }

        String effectType = parts[0];

        return switch (effectType) {
            case "damage" -> parseDamageEffect(parts);
            case "inflictStatusCondition" -> parseStatusConditionEffect(parts);
            case "inflictStatChange" -> parseStatChangeEffect(parts);
            case "protectStat" -> parseDefenseEffect(parts);
            case "heal" -> parseHealingEffect(parts);
            case "continue" -> parseContinueEffect(parts);
            default -> throw new ConfigurationException("Unknown effect type: " + effectType);
        };
    }

    /**
     * Parses a damage effect.
     *
     * @param parts the split effect line
     * @return the parsed damage effect
     * @throws ConfigurationException if the damage effect configuration is invalid
     */
    private Effect parseDamageEffect(String[] parts) throws ConfigurationException {
        if (parts.length < 4) {
            throw new ConfigurationException("Invalid damage format: " + String.join(" ", parts));
        }

        boolean targetIsUser = parts[1].equals("user");
        EffectDamage.DamageType type;
        int value;

        switch (parts[2]) {
            case "base" -> {
                type = EffectDamage.DamageType.BASE;
                value = Integer.parseInt(parts[3]);
            }
            case "rel" -> {
                type = EffectDamage.DamageType.RELATIVE;
                value = Integer.parseInt(parts[3]);
            }
            case "abs" -> {
                type = EffectDamage.DamageType.ABSOLUTE;
                value = Integer.parseInt(parts[3]);
            }
            default -> throw new ConfigurationException("Invalid damage type: " + parts[2]);
        }

        int hitRate = Integer.parseInt(parts[4]);

        return new EffectDamage(type, value, targetIsUser, hitRate);
    }

    /**
     * Parses a status condition effect.
     *
     * @param parts the split effect line
     * @return the parsed status condition effect
     * @throws ConfigurationException if the status condition effect configuration is invalid
     */
    private Effect parseStatusConditionEffect(String[] parts) throws ConfigurationException {
        if (parts.length < 4) {
            throw new ConfigurationException("Invalid status condition format: " + String.join(" ", parts));
        }

        boolean targetIsUser = parts[1].equals("user");
        StatusCondition condition = StatusCondition.fromString(parts[2]);

        if (condition == StatusCondition.NONE) {
            throw new ConfigurationException("Invalid status condition: " + parts[2]);
        }

        int hitRate = Integer.parseInt(parts[3]);

        return new EffectStatusCondition(condition, targetIsUser, hitRate);
    }

    /**
     * Parses a stat change effect.
     *
     * @param parts the split effect line
     * @return the parsed stat change effect
     * @throws ConfigurationException if the stat change effect configuration is invalid
     */
    private Effect parseStatChangeEffect(String[] parts) throws ConfigurationException {
        if (parts.length < 5) {
            throw new ConfigurationException("Invalid stat change format: " + String.join(" ", parts));
        }

        boolean targetIsUser = parts[1].equals("user");
        Stat stat = Stat.fromString(parts[2]);

        if (stat == null) {
            throw new ConfigurationException("Invalid stat: " + parts[2]);
        }

        int stages = Integer.parseInt(parts[3]);
        int hitRate = Integer.parseInt(parts[4]);

        return new EffectStatChange(stat, stages, targetIsUser, hitRate);
    }

    /**
     * Parses a defense effect.
     *
     * @param parts the split effect line
     * @return the parsed defense effect
     * @throws ConfigurationException if the defense effect configuration is invalid
     */
    private Effect parseDefenseEffect(String[] parts) throws ConfigurationException {
        if (parts.length < 4) {
            throw new ConfigurationException("Invalid defense format: " + String.join(" ", parts));
        }

        EffectDefense.ProtectionTarget target;

        if (parts[1].equals("health")) {
            target = EffectDefense.ProtectionTarget.HEALTH;
        } else if (parts[1].equals("stats")) {
            target = EffectDefense.ProtectionTarget.STATS;
        } else {
            throw new ConfigurationException("Invalid protection target: " + parts[1]);
        }

        int hitRate = Integer.parseInt(parts[3]);

        if (parts[2].equals("random")) {
            if (parts.length < 6) {
                throw new ConfigurationException("Invalid random defense format: " + String.join(" ", parts));
            }

            int min = Integer.parseInt(parts[3]);
            int max = Integer.parseInt(parts[4]);
            hitRate = Integer.parseInt(parts[5]);

            return new EffectDefense(target, min, max, hitRate);
        } else {
            int count = Integer.parseInt(parts[2]);

            return new EffectDefense(target, count, hitRate);
        }
    }

    /**
     * Parses a healing effect.
     *
     * @param parts the split effect line
     * @return the parsed healing effect
     * @throws ConfigurationException if the healing effect configuration is invalid
     */
    private Effect parseHealingEffect(String[] parts) throws ConfigurationException {
        if (parts.length < 4) {
            throw new ConfigurationException("Invalid healing format: " + String.join(" ", parts));
        }

        boolean targetIsUser = parts[1].equals("user");
        EffectHealing.HealingType type;
        int value;

        switch (parts[2]) {
            case "base" -> {
                type = EffectHealing.HealingType.BASE;
                value = Integer.parseInt(parts[3]);
            }
            case "rel" -> {
                type = EffectHealing.HealingType.RELATIVE;
                value = Integer.parseInt(parts[3]);
            }
            case "abs" -> {
                type = EffectHealing.HealingType.ABSOLUTE;
                value = Integer.parseInt(parts[3]);
            }
            default -> throw new ConfigurationException("Invalid healing type: " + parts[2]);
        }

        int hitRate = Integer.parseInt(parts[4]);

        return new EffectHealing(type, value, targetIsUser, hitRate);
    }

    /**
     * Parses a continue effect.
     *
     * @param parts the split effect line
     * @return the parsed continue effect
     * @throws ConfigurationException if the continue effect configuration is invalid
     */
    private Effect parseContinueEffect(String[] parts) throws ConfigurationException {
        if (parts.length < 2) {
            throw new ConfigurationException("Invalid continue format: " + String.join(" ", parts));
        }

        int hitRate = Integer.parseInt(parts[1]);

        return new EffectContinue(hitRate);
    }

    /**
     * Parses a monster from the configuration.
     *
     * @param lines the lines of the configuration file
     * @param startIndex the index to start parsing from
     * @return the next index to parse from
     * @throws ConfigurationException if the monster configuration is invalid
     */
    private int parseMonster(List<String> lines, int startIndex) throws ConfigurationException {
        String line = lines.get(startIndex);
        String[] parts = line.split(" ");

        if (parts.length < 8) {
            throw new ConfigurationException("Invalid monster format: " + line);
        }

        String monsterName = parts[1];
        Element element = Element.fromString(parts[2]);

        if (element == null) {
            throw new ConfigurationException("Invalid element: " + parts[2]);
        }

        int maxHp = Integer.parseInt(parts[3]);
        int attack = Integer.parseInt(parts[4]);
        int defense = Integer.parseInt(parts[5]);
        int speed = Integer.parseInt(parts[6]);

        Monster monster = new Monster(monsterName, element, maxHp, attack, defense, speed);

        // Add actions to monster
        for (int i = 7; i < parts.length; i++) {
            String actionName = parts[i];
            Action action = actions.get(actionName);

            if (action == null) {
                throw new ConfigurationException("Unknown action: " + actionName);
            }

            monster.addAction(action);
        }

        monsters.add(monster);

        return startIndex + 1;
    }

    /**
     * Gets all monsters from the configuration.
     *
     * @return the monsters
     */
    public List<Monster> getMonsters() {
        return new ArrayList<>(monsters);
    }

    /**
     * Gets the number of actions in the configuration.
     *
     * @return the number of actions
     */
    public int getActionCount() {
        return actions.size();
    }

    /**
     * Gets the number of monsters in the configuration.
     *
     * @return the number of monsters
     */
    public int getMonsterCount() {
        return monsters.size();
    }
}