package edu.kit.kastel.monstercombat.view.command;

import edu.kit.kastel.monstercombat.view.UserInterface;

public class CommandQuit implements Command {
    private final UserInterface userInterface;

    public CommandQuit(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    @Override
    public boolean execute() {
        userInterface.stop();
        return true;
    }
}