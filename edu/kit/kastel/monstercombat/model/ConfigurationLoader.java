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
import edu.kit.kastel.monstercombat.model.effect.EffectRepeat;
import edu.kit.kastel.monstercombat.model.exception.ConfigurationException;

public class ConfigurationLoader {
    private final Map<String, Action> actions;
    private final List<Monster> monsters;
    private final EffectParser effectParser;

    /**
     * Constructs a new configuration loader.
     */
    public ConfigurationLoader() {
        this.actions = new HashMap<>();
        this.monsters = new ArrayList<>();
        this.effectParser = new EffectParser();
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

        // Parse actions and monsters
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
                index = parseRepeatEffect(lines, index, action, repeatedEffects);
                repeatedEffects = new ArrayList<>();
            } else {
                // Parse single effect
                Effect effect = effectParser.parseEffect(line);

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
     * Parses a repeat effect.
     *
     * @param lines the lines of the configuration file
     * @param index the current index
     * @param action the current action
     * @param repeatedEffects the current repeated effects
     * @return the next index to parse from
     * @throws ConfigurationException if the repeat effect configuration is invalid
     */
    private int parseRepeatEffect(List<String> lines, int index, Action action, List<Effect> repeatedEffects)
            throws ConfigurationException {
        String line = lines.get(index);
        String[] parts = line.split(" ");

        if (parts.length < 2) {
            throw new ConfigurationException("Invalid repeat format: " + line);
        }

        if (repeatedEffects == null) {
            repeatedEffects = new ArrayList<>();
        }

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

        return index + 1;
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