package net.unethicalite.scripts.RuneDragons.tasks.CombatSubTasks;

import net.runelite.api.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.utils.MessageUtils;

import java.util.List;

import static net.runelite.api.Varbits.ANTIFIRE;

public class consumeAntiFire implements CombatSubTask
{

    private static final int MINIMUM_HEALTH = 40;
    private static final int minimumPrayer = 20;
    private final Client client;
    private final ClientThread clientThread;
    private final ItemManager itemManager;

    // Add a constructor that takes Client as a parameter
    public consumeAntiFire(Client client, ClientThread clientThread, ItemManager itemManager)
    {
        this.client = client;
        this.clientThread = clientThread;
        this.itemManager = itemManager;
    }

    private boolean shouldAntFire = false;


    public void checkAntiFire()
    {
        clientThread.invokeLater(() ->
        {
            List<TileItem> groundItems = TileItems.getAll();
            int antiFireVarbit = client.getVarbitValue(ANTIFIRE);
            System.out.println(antiFireVarbit);
            if (client.getVarbitValue(ANTIFIRE) == 0)
            {

                shouldAntFire = true;

            }
        });
    }

    @Override
    public boolean validate()
    {
        checkAntiFire();  // Gets whether valuable items are present
        return shouldAntFire;  // Returns whether valuable items are present
    }


    @Override
    public int execute()
    {
        Item antifire = Inventory.getFirst(item -> item.getName().contains("Extended antifire"));

        clientThread.invokeLater(() ->
        {
            List<TileItem> groundItems = TileItems.getAll();
            if (client.getVarbitValue(ANTIFIRE) == 0)
            {

                shouldAntFire = true;

            }
            shouldAntFire = false;
        });

        clientThread.invokeLater(() ->
        {
            if (shouldAntFire && antifire != null)
            {
                int antiFireVarbit = client.getVarbitValue(ANTIFIRE);
                MessageUtils.addMessage(String.valueOf(antiFireVarbit));
                try
                {
                    Thread.sleep(3 * 600);  // Replace Time.sleepTicks(3) with Thread.sleep(3 * 600)
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();  // Handle the interrupted exception
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
