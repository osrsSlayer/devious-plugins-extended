package net.unethicalite.scripts.RuneDragons.tasks;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.chat.ChatColorType;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.utils.MessageUtils;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class DeathWalking implements ScriptTask
{
    @Inject
    private Client client;

    private static final int[] LUMBRIDGE_REGIONS = {12849, 12850, 12851, 13094, 13095};

    private boolean isInLumbridge()
    {
        MessageUtils.addMessage("Death walking activated");
        Player local = Players.getLocal();

        int currentRegion = local.getWorldLocation().getRegionID();
        for (int region : LUMBRIDGE_REGIONS)
        {
            if (currentRegion == region)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean validate()
    {
        return isInLumbridge();
    }

    @Override
    public int execute()
    {
        MessageUtils.addMessage("In lumbridge region walking to edge...", ChatColorType.HIGHLIGHT);

        //checking for Amulet of glory
        List<Integer> amuletOfGlory = Arrays.asList(
                ItemID.AMULET_OF_GLORY1, ItemID.AMULET_OF_GLORY2, ItemID.AMULET_OF_GLORY3,
                ItemID.AMULET_OF_GLORY4, ItemID.AMULET_OF_GLORY5, ItemID.AMULET_OF_GLORY6
        );
        //Inventory glory / worn glory
        Item inventoryGlory = Inventory.getFirst(item -> amuletOfGlory.contains(item.getId()));
        Item wornGlory = Equipment.getFirst(item -> amuletOfGlory.contains(item.getId()));
        // setting worldpoints for banking.
        WorldPoint local = Players.getLocal().getWorldLocation();
        TileObject bank = TileObjects.getNearest(local, x -> x.hasAction("Collect"));

        // Wearing glory if in lumby.
        if (wornGlory == null)
        {
            if (inventoryGlory != null)
            {
                inventoryGlory.interact("Wear");
            }

            //walking to bank if not near bank
            if (bank != null)
            {
                if (!Bank.isOpen())
                {
                    bank.interact("Open");
                    return -1;
                }
            }

        }


        return -5;
    }
}
