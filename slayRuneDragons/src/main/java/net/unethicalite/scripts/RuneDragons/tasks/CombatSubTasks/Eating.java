package net.unethicalite.scripts.RuneDragons.tasks.CombatSubTasks;

import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.client.chat.ChatColorType;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.game.Skills;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.utils.MessageUtils;

public class Eating implements CombatSubTask
{

    private static final int MINIMUM_HEALTH = 40;
    private static final int MINIMUM_PRAYER = 20;


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

        return (((currentHp <= MINIMUM_HEALTH) && food != null) || ((currentPray < MINIMUM_PRAYER) && prayerPotion != null));
    }

    @Override
    public int execute()
    {

        //TODO CHECK IF ANTIFIRE IS ACTIVE AND IF NOT CONSUME ANTIFIRE AND SUPER COMBAT
        MessageUtils.addMessage("Eating task triggered");
        Item food = Inventory.getFirst(ItemID.SHARK);
        Item prayerPotion = Inventory.getFirst(item -> item.getName().contains("Prayer potion"));
        int pray = Skills.getBoostedLevel(Skill.PRAYER);
        int currentHp = Skills.getBoostedLevel(Skill.HITPOINTS);

        //Combo eating food and prayer potion if prayer is low while also needing to eat.

        if (pray <= MINIMUM_PRAYER && prayerPotion != null)
        {
            if (currentHp < ( MINIMUM_HEALTH + 10 ) && food != null)
            {
                food.interact("Eat");
                Time.sleep(50, 250);
                prayerPotion.interact("Drink");
                MessageUtils.addMessage("Combo eating " + food.getName() + prayerPotion.getName(), ChatColorType.HIGHLIGHT);

                return -2;

            }

            prayerPotion.interact("Drink");
            MessageUtils.addMessage("Drinking a dose of " + prayerPotion.getName(), ChatColorType.HIGHLIGHT);

            return -2;
        }


        if (food != null && currentHp < MINIMUM_HEALTH)
        {
            food.interact("Eat");
            MessageUtils.addMessage("Eating " + food.getName(), ChatColorType.HIGHLIGHT);

            return -2;
        }

        return -1;
    }

    @Override
    public boolean blocking()
    {
        return false;
    }
}
