package net.unethicalite.scripts.RuneDragons.tasks.CombatSubTasks;

import net.runelite.api.Item;
import net.runelite.api.Skill;
import net.runelite.client.chat.ChatColorType;
import net.unethicalite.api.game.Skills;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.utils.MessageUtils;

public class consumeSuperCombat implements CombatSubTask
{


    @Override
    public boolean validate()
    {
        // this is checking for health / prayer every validation check
        // if it was done above where private static final checks are then
        // it would not update each check and only be set once.
        int realStrength = Skills.getLevel(Skill.STRENGTH);
        int boostedStrength = Skills.getBoostedLevel(Skill.STRENGTH);
        Item superCombat = Inventory.getFirst(item -> item.getName().contains("Divine super"));

        //if combat skill levels are not boosted and inventory contains super strength.
        return (boostedStrength == realStrength && superCombat != null);
    }

    @Override
    public int execute()
    {

        MessageUtils.addMessage("Consuming Divine super combat potion", ChatColorType.HIGHLIGHT);
        Item superCombat = Inventory.getFirst(item -> item.getName().contains("Divine super"));

        superCombat.interact("Drink");
        return -1;
    }

    @Override
    public boolean blocking()
    {
        return false;
    }
}
