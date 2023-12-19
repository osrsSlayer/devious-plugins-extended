package net.unethicalite.scripts.RuneDragons.tasks.CombatSubTasks;

import net.runelite.api.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;


public class EnablePrayer implements CombatSubTask
{

    private static final int MINIMUM_HEALTH = 40;
    private static final int minimumPrayer = 20;
    private final Client client;
    private final ClientThread clientThread;
    private final ItemManager itemManager;

    // Add a constructor that takes Client as a parameter
    public EnablePrayer(Client client, ClientThread clientThread, ItemManager itemManager)
    {
        this.client = client;
        this.clientThread = clientThread;
        this.itemManager = itemManager;
    }
    private boolean hasValuableItem = false;



    @Override
    public boolean validate()
    {
        int quickPrayer = client.getVarbitValue(Varbits.QUICK_PRAYER);

        return false;
    }


    @Override
    public int execute()
    {

        return -2;
    }

    @Override
    public boolean blocking()
    {
        return false;
    }
}
