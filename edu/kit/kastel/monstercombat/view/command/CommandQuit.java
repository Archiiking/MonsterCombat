package edu.kit.kastel.monstercombat.view.command;

public class CommandQuit implements Command {
    @Override
    public boolean execute() {
        System.exit(0);
        return true; // Never reached
    }
}