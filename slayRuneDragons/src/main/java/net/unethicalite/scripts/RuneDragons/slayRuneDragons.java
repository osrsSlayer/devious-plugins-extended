package net.unethicalite.scripts.RuneDragons;

import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.unethicalite.api.plugins.Script;
import net.unethicalite.api.utils.MessageUtils;
import net.unethicalite.scripts.RuneDragons.tasks.*;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;

// This annotation is required in order for the client to detect it as a plugin/script.
@PluginDescriptor(name = "slayRevs", enabledByDefault = false)
@Extension
public class slayRuneDragons extends Script
{
    // OverlayManager to manage overlays
    @Inject
    private OverlayManager overlayManager;

    private Instant scriptStartTime;

    // Custom SlayRevsOverlay
    @Inject
    private SlayRuneDragonsOverlay slayRevsOverlay;

    /**
     * Gets executed whenever a script starts.
     * Can be used to for example initialize script settings, or perform tasks before starting the loop logic.
     */

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;
    @Inject
    private ItemManager itemManager;

    private ScriptTask[] TASKS;

    @Override
    protected void startUp() throws Exception
    {
        MessageUtils.addMessage("slayPlug  Rune dragons started.");
        // In startUp() method, instantiate TASKS.
        TASKS = new ScriptTask[]
                {
                        new Wilderness(client, clientThread), // Pass client to Wilderness constructor
                        new Combat(client, clientThread, itemManager),
                        new Banking()
                };
        scriptStartTime = Instant.now();
        overlayManager.add(slayRevsOverlay);
    }

    @Override
    public void onStop()
    {
        MessageUtils.addMessage("slayPlugin stopped.");
        // Remove the custom overlay when script stops
        overlayManager.remove(slayRevsOverlay);
    }

    /**
     * Any logic passed inside this method will be repeatedly executed by an internal loop that calls this method.
     *
     * @return the amount of milliseconds to sleep after each loop iteration.
     */
    @Override
    protected int loop()
    {
        // Here I use task-based logic. You can also just write the entire script logic
        for (ScriptTask task : TASKS)
        {
            if (task.validate())
            {
                // Perform the task and store the sleep value
                int sleep = task.execute();
                // If this task blocks the next task, return the sleep value and the internal loop will sleep for this amount of time
                if (task.blocking())
                {
                    return sleep;
                }
            }
        }

        return 1000;
    }

    @Override
    public void onStart(String... strings)
    {

    }

    public Duration getScriptRuntime()
    {
        if (scriptStartTime == null)
        {
            return Duration.ZERO;
        }
        return Duration.between(scriptStartTime, Instant.now());
    }

}