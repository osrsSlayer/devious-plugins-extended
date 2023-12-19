package net.unethicalite.scripts.SlayCBaller.tasks;

import net.runelite.api.ItemID;
import net.runelite.client.chat.ChatColorType;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.utils.MessageUtils;




public class Restocking implements ScriptTask {
    @Override
    public boolean blocking() {
        return true;
    }

    @Override
    public boolean validate()
    {
        return Restock();
    }

    public boolean Restock()
    {
        return (!Bank.contains(ItemID.STEEL_BAR)
                && !Inventory.contains(ItemID.STEEL_BAR));
    }

    @Override
    public int execute() {
        MessageUtils.addMessage("Restock validated true", ChatColorType.HIGHLIGHT);


        return 600;
    }

}
