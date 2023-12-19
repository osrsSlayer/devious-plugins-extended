package net.unethicalite.scripts.SlayCBaller.tasks;

import com.openosrs.client.game.WorldLocation;
import net.runelite.api.*;
import net.runelite.client.chat.ChatColorType;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Bank;
import net.runelite.api.ItemID;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.Reachable;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.utils.MessageUtils;
import javax.swing.SwingUtilities;

public class Banking implements ScriptTask {
    private static final WorldPoint BANK_TILE = new WorldPoint(3096, 3493, 0);

    @Override
    public boolean validate() {
        return shouldBank();
    }

    @Override
    public int execute() {
        MessageUtils.addMessage("Banking validated true", ChatColorType.HIGHLIGHT);

        Player local = Players.getLocal();
        if (!Bank.isOpen()) {
            if (!Movement.isRunEnabled() && Movement.getRunEnergy() >= 30) {
                Movement.toggleRun();
                return 1000;
            }

            if (Movement.isWalking()) {
                return 1000;
            }

            TileObject booth = TileObjects.getFirstAt(BANK_TILE, x -> x.hasAction("Bank", "Collect"));
            if (booth == null || !Reachable.isInteractable(booth)) {
                MessageUtils.addMessage("walking to bank tile", ChatColorType.HIGHLIGHT);
                Movement.walkTo(WorldLocation.EDGEVILLE_BANK.getWorldArea());
                return 1000;
            }

            MessageUtils.addMessage("Opening bank", ChatColorType.HIGHLIGHT);
            booth.interact("Bank");
            Time.sleepUntil(Bank::isOpen, 10000);
            return 3000;
        }

        if (Bank.isOpen()) {
            if (Inventory.contains(ItemID.CANNONBALL)) {
                MessageUtils.addMessage("Depositing cannonballs", ChatColorType.HIGHLIGHT);
                Bank.depositAll(ItemID.CANNONBALL);
                return 600;
            }

            if (!Inventory.isFull() && hasMould()) {
                MessageUtils.addMessage("Withdrawing steel bars", ChatColorType.HIGHLIGHT);
                Bank.withdraw(ItemID.STEEL_BAR, 27, Bank.WithdrawMode.ITEM);
                Time.sleepTick();

            return 100;
            }

            if (!hasMould()) {
                if (Inventory.isFull()) {
                    Bank.depositInventory();
                    return 1000;
                } else if (Bank.contains(ItemID.DOUBLE_AMMO_MOULD)) {
                    MessageUtils.addMessage("Withdrawing Ammo mould", ChatColorType.HIGHLIGHT);
                    Bank.withdraw(ItemID.DOUBLE_AMMO_MOULD, 1, Bank.WithdrawMode.ITEM);
                    return 1000;
                } else if (Bank.contains(ItemID.AMMO_MOULD)) {
                    MessageUtils.addMessage("Withdrawing double Ammo mould", ChatColorType.HIGHLIGHT);
                    Bank.withdraw(ItemID.AMMO_MOULD, 1, Bank.WithdrawMode.ITEM);
                    return 1000;
                } else {
                    MessageUtils.addMessage("No supplies to continue");
                }

            }
        }







        return 1000;
    }
    public boolean shouldBank() {
        return !hasMould() || (!Inventory.contains(ItemID.STEEL_BAR) && Bank.contains(ItemID.STEEL_BAR));
    }

    public boolean hasMould() {
        return (Inventory.contains(ItemID.DOUBLE_AMMO_MOULD)
                || Inventory.contains(ItemID.AMMO_MOULD));
    }
}