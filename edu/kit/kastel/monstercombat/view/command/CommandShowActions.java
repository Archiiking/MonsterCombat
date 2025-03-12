package edu.kit.kastel.monstercombat.view.command;

import edu.kit.kastel.monstercombat.model.Action;
import edu.kit.kastel.monstercombat.model.Competition;
import edu.kit.kastel.monstercombat.model.Monster;
import edu.kit.kastel.monstercombat.model.effect.Effect;

public class CommandShowActions implements Command {
    @Override
    public boolean execute() {
        Monster currentMonster = Competition.getInstance().getCurrentMonster();

        if (currentMonster == null) {
            System.out.println("Error, no monster is currently active.");
            return false;
        }

        showActions(currentMonster);
        return true;
    }

    private void showActions(Monster monster) {
        System.out.printf("ACTIONS OF %s\n", monster.getDisplayName());

        for (Action action : monster.getActions()) {
            String damageInfo = "--";
            int hitRate = action.getFirstEffectHitRate();

            for (Effect effect : action.getEffects()) {
                String effectDamageInfo = effect.getDamageInfo();
                if (!effectDamageInfo.equals("--")) {
                    damageInfo = effectDamageInfo;
                    break;
                }
            }

            System.out.printf("%s: ELEMENT %s, Damage %s, HitRate %d\n",
                    action.getName(), action.getElement(), damageInfo, hitRate);
        }
    }
}