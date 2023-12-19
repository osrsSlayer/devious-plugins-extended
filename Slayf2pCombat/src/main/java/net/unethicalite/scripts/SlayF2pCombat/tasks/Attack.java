package net.unethicalite.scripts.SlayF2pCombat.tasks;

import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.unethicalite.api.coords.Area;
import net.unethicalite.api.coords.RectangularArea;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.movement.Reachable;

public class Attack implements ScriptTask {
    Area chickenPen = new RectangularArea(3172, 3301, 3183, 3290);
    Player local = Players.getLocal();

    @Override
    public boolean validate() {
        return chickenPen.contains(local) && !local.isInteracting();
    }

    @Override
    public int execute() {
        NPC Chicken = Combat.getAttackableNPC(x -> x.getName() != null
                && x.getName().equals("Chicken")
                && !x.isInteracting()
                && x.isDead());

        if (chickenPen.contains(local) && !local.isMoving()
                || (Chicken != null)
                || Reachable.isInteractable(Chicken)) {
            Chicken.interact("Attack");

        }
        return -2;
    }

}
