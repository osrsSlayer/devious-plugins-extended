package net.unethicalite.scripts.SlayCBaller;
import net.runelite.client.chat.ChatColorType;
import net.unethicalite.api.plugins.Script;
import net.unethicalite.api.utils.MessageUtils;
import net.unethicalite.scripts.SlayCBaller.tasks.Banking;
import net.unethicalite.scripts.SlayCBaller.tasks.Restocking;
import net.unethicalite.scripts.SlayCBaller.tasks.ScriptTask;
import net.unethicalite.scripts.SlayCBaller.tasks.Smithing;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

// This annotation is required in order for the client to detect it as a plugin/script.
@PluginDescriptor(name = "SlayCBaller", enabledByDefault = false)

@Extension
public class SlayCBaller extends Script {
    private static final ScriptTask[] TASKS = new ScriptTask[]{
            new Restocking(),
            new Banking(),
            new Smithing()
    };

    /**
     * Gets executed whenever a script starts.
     * Can be used to for example initialize script settings, or perform tasks before starting the loop logic.
     *
     * @param args any script arguments passed to the script, separated by spaces.
     */
    @Override
    public void onStart(String... args) {
        MessageUtils.addMessage("Script started", ChatColorType.HIGHLIGHT);
    }

    /**
     * Any logic passed inside this method will be repeatedly executed by an internal loop that calls this method.
     *
     * @return the amount of milliseconds to sleep after each loop iteration.
     */
    @Override
    protected int loop() {
        // Here I use task-based logic. You can also just write the entire script logic
        for (ScriptTask task : TASKS) {
            if (task.validate()) {
                // Perform the task and store the sleep value
                int sleep = task.execute();
                // If this task blocks the next task, return the sleep value and the internal
                // loop will sleep for this amount of time
                if (task.blocking()) {
                    return sleep;
                }
            }
        }

        return 1000;
    }
    @Override
    public void onStop() {
        MessageUtils.addMessage("Script stopped", ChatColorType.HIGHLIGHT);

    }
}
