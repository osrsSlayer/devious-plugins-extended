package net.unethicalite.scripts.RuneDragons.tasks.CombatSubTasks;

public interface CombatSubTask
{
    // validation method to see if this subtask should be executed
    boolean validate();

    // execution method
    int execute();

    // check if this subtask blocks the next one
    boolean blocking();
}