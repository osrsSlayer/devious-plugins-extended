package net.unethicalite.scripts.RuneDragons.tasks;


import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.scripts.RuneDragons.tasks.CombatSubTasks.*;

import java.util.Arrays;
import java.util.List;

public class Combat implements ScriptTask
{

    private Client client;
    private ClientThread clientThread;
    private ItemManager itemManager;
    private List<CombatSubTask> TASKS;

    public Combat(Client client, ClientThread clientThread, ItemManager itemManager)
    {
        this.client = client;
        this.clientThread = clientThread;
        this.itemManager = itemManager;

        this.TASKS = Arrays.asList(
                new Escape(),
                new Eating(),
                new consumeSuperCombat(),
                new Looting(client, clientThread, itemManager),
                new consumeAntiFire(client, clientThread, itemManager)
        );

    }

    private static final int NO_SUBTASK_EXECUTED = -1;

    @Override
    public boolean validate()
    {
        NPC runeDragon = NPCs.getNearest("Rune dragon");

        return Reachable.isInteractable(runeDragon);
    }

    @Override
    public int execute()
    {

            for (CombatSubTask task : TASKS)
            {
                if (task.validate())
                {
                    // subtask is valid, so execute it
                    int sleep = task.execute();
                    // if this subtask blocks the following subtasks, return immediately
                    if (task.blocking())
                    {
                        return sleep;
                    }
                }

        }

        // Check if no subtask was executed
        return NO_SUBTASK_EXECUTED;
    }

    @Override
    public boolean blocking()
    {
        // Whether it blocks the following main tasks
        return true;
    }
}