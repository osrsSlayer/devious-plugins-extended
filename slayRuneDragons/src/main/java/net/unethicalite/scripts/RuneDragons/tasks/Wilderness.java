package net.unethicalite.scripts.RuneDragons.tasks;

import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.unethicalite.api.game.Game;
import net.unethicalite.api.utils.MessageUtils;
import net.unethicalite.scripts.RuneDragons.tasks.WildernessSubTasks.HopWorlds;
import net.unethicalite.scripts.RuneDragons.tasks.WildernessSubTasks.WildernessSubTask;


public class Wilderness implements ScriptTask
{
	private final Client client;

	private final ClientThread clientThread;
	private final WildernessSubTask[] TASKS;

	public Wilderness(Client client, ClientThread clientThread)
	{
		this.client = client;
		this.clientThread = clientThread;
		this.TASKS = new WildernessSubTask[] {
				new HopWorlds(client, clientThread)
		};
	}

	private static final int NO_SUBTASK_EXECUTED = -1;
	@Override
	public boolean validate()
	{
		return Game.getWildyLevel() > 0;
	}

	@Override
	public int execute()
	{
		MessageUtils.addMessage("Wilderness task triggered.", ChatColorType.HIGHLIGHT);

		// Here I use subtask-based logic within the Wilderness Task.
		for (WildernessSubTask task : TASKS)
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
		return NO_SUBTASK_EXECUTED;
	}

	@Override
	public boolean blocking()
	{
		// Whether it blocks the following main tasks
		return true;
	}

}
