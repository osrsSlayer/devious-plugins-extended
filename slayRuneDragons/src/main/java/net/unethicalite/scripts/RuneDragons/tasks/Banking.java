package net.unethicalite.scripts.RuneDragons.tasks;

import net.runelite.api.ItemID;
import net.runelite.client.chat.ChatColorType;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.utils.MessageUtils;

public class Banking implements ScriptTask
{
    @Override
    public boolean validate()
    {
        return !Inventory.isFull() && Inventory.contains(ItemID.COINS_995);
    }

    @Override
    public int execute()
    {
        MessageUtils.addMessage("banking task triggered.", ChatColorType.HIGHLIGHT);

        if (!Movement.isRunEnabled())
        {
            Movement.toggleRun();
            return 1000;
        }

        if (Movement.isWalking())
        {
            return 1000;
        }
        return -3;
    }
}
