package net.unethicalite.scripts.RuneDragons.tasks.CombatSubTasks;

import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.game.Skills;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.utils.MessageUtils;

public class Escape implements CombatSubTask
{

    private static final int MINIMUM_HEALTH = 40;
    private static final int MINIMUM_PRAYER = 10;


    @Override
    public boolean validate()
    {
        // this is checking for health / prayer every validation check
        // if it was done above where private static final checks are then
        // it would not update each check and only be set once.
        Item food = Inventory.getFirst(ItemID.SHARK);
        Item prayerPotion = Inventory.getFirst(item -> item.getName().contains("Prayer potion"));
        int currentHp = Skills.getBoostedLevel(Skill.HITPOINTS);
        int currentPray = Skills.getBoostedLevel(Skill.PRAYER);
        return ((currentHp <= MINIMUM_HEALTH && food == null) || (currentPray < MINIMUM_PRAYER && prayerPotion == null));
    }

    @Override
    public int execute()
    {
        MessageUtils.addMessage("Escaping task triggered");

        Item ringOfDueling = Inventory.getFirst(item -> item.getName().contains("Ring of dueling"));
        Item wornRingOfDueling = Equipment.getFirst(item -> item.getName().contains("Ring of dueling"));

        if (wornRingOfDueling == null)
        {
            if (ringOfDueling != null)
            {
                ringOfDueling.interact("Wear");
                Time.sleepTicks(2);
            }
        }
        wornRingOfDueling.interact("Ferox Enclave");

        return -4;
    }

    @Override
    public boolean blocking()
    {
        return false;
    }
}
