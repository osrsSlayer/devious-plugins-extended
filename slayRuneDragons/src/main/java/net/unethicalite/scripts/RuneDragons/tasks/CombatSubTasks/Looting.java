package net.unethicalite.scripts.RuneDragons.tasks.CombatSubTasks;

import net.runelite.api.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.utils.MessageUtils;

import java.util.List;

public class Looting implements CombatSubTask
{

    private static final int MINIMUM_HEALTH = 40;
    private static final int minimumPrayer = 20;
    private final Client client;
    private final ClientThread clientThread;
    private final ItemManager itemManager;

    // Add a constructor that takes Client as a parameter
    public Looting(Client client, ClientThread clientThread, ItemManager itemManager)
    {
        this.client = client;
        this.clientThread = clientThread;
        this.itemManager = itemManager;
    }
    private boolean hasValuableItem = false;


    public void checkGroundItems()
    {
        clientThread.invokeLater(() ->
        {
            List<TileItem> groundItems = TileItems.getAll();
            for (TileItem item : groundItems)
            {
                int itemId = item.getId();
                int itemPrice = itemManager.getItemPrice(itemId);
                int itemQuantity = item.getQuantity();
                int itemValue = itemPrice * itemQuantity;

                if (itemValue > 2000)
                {
                    hasValuableItem = true;
                    break;
                }
            }
        });
    }

    @Override
    public boolean validate()
    {
        checkGroundItems();  // Gets whether valuable items are present
        return hasValuableItem;  // Returns whether valuable items are present
    }


    @Override
    public int execute()
    {
        clientThread.invokeLater(() ->
        {
            List<TileItem> groundItems = TileItems.getAll();

            // Sort the items by total value in descending order
            groundItems.sort((item1, item2) ->
            {
                int item1Value = itemManager.getItemPrice(item1.getId());
                int item2Value = itemManager.getItemPrice(item2.getId());
                return Integer.compare(item2Value, item1Value); // This gives you a descending order
            });

            // Iterate through each item, starting with the one of highest value
            for (TileItem item : groundItems)
            {
                int itemId = item.getId();
                int itemValue = itemManager.getItemPrice(itemId);

                MessageUtils.addMessage("Item ID: " + itemId);
                MessageUtils.addMessage("Looting item worth " + itemValue);
                Time.sleepTick();
                Item food = Inventory.getFirst(ItemID.SHARK);

                if (!Movement.isWalking())
                {
                    if (!Inventory.isFull())
                    {
                        item.interact("Take");
                        Time.sleepTicks(3);
                    }
                    else if (Inventory.contains(ItemID.SHARK))
                    {
                        MessageUtils.addMessage("Eating to make room for loot");
                        food.interact("Eat");
                        while (client.getLocalPlayer().isMoving())
                        {
                            Time.sleep(200); // Waits for 50 milliseconds before checking again
                        }
                    }
                }
            }
        });

        return -2;
    }

    @Override
    public boolean blocking()
    {
        return false;
    }
}
