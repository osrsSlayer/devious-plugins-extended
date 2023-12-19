package net.unethicalite.scripts.SlayCBaller.tasks;

import net.runelite.client.eventbus.Subscribe;
import net.runelite.api.TileObject;
import net.runelite.api.events.GameTick;
import net.runelite.client.chat.ChatColorType;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.utils.MessageUtils;
import net.runelite.api.ItemID;
import net.unethicalite.api.widgets.Production;
import net.unethicalite.client.Static;

public class Smithing implements ScriptTask {
    public Smithing() {
        Static.getEventBus().register(this);
    }
    @Override
    public boolean validate() {
        return hasMould() && Inventory.contains(ItemID.STEEL_BAR);
    }

    @Override
    public int execute() {
        MessageUtils.addMessage("Smithing validated true", ChatColorType.HIGHLIGHT);

        var local = Players.getLocal();

        if (Movement.isWalking()
                || Players.getLocal().isAnimating()
                || (timeout > 0)) {
            return 1000;
        }


        if (Production.isOpen()) {
            Production.choosePreviousOption();
            Time.sleepUntil(
                    () -> !Inventory.contains(ItemID.STEEL_BAR),
                    () -> Players.getLocal().isAnimating(), 5000);
            return 1200;
        }

        TileObject furnace = TileObjects.getNearest("Furnace");
        if (furnace == null && !local.isMoving() && !Production.isOpen()) {
            Movement.walkTo(3274, 3181, 0);
            return 1000;
        }

        if (!Reachable.isInteractable(furnace)) {
            MessageUtils.addMessage("Walkng to the furnace", ChatColorType.HIGHLIGHT);
            Movement.walkTo(furnace);
            return 1000;
        }

        MessageUtils.addMessage("Interacting with the furnace", ChatColorType.HIGHLIGHT);
        furnace.interact("Smelt");
        Time.sleepTick();
        return 1300;
    }

    public boolean hasMould() {
        return (Inventory.contains(ItemID.DOUBLE_AMMO_MOULD) || Inventory.contains(ItemID.AMMO_MOULD));

    }
    @Subscribe
    public void onGameTick (GameTick event) {
        if (timeout > 0) {
            timeout --;
        }
    }

    int timeout = 0;
}
