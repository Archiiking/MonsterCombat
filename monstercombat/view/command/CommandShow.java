// CommandShow.java
package edu.kit.kastel.monstercombat.view.command;

/**
 * Command to show the current competition state.
 */
public class CommandShow extends ShowCommands {
    @Override
    public boolean execute() {
        showMonsters();
        return true;
    }
}