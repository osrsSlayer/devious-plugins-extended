package net.unethicalite.plugins.SlayWood;

import net.runelite.client.config.Button;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("SlayWood")
public interface SlayWoodConfig extends Config {

    @ConfigItem(
            keyName = "tree",
            name = "Tree type",
            description = "The type of tree to chop",
            position = 0
    )
    default TreeType tree() {
        return TreeType.OAK;
    }

    @ConfigItem(
            keyName = "Destination level",
            name = "Destination level",
            description = "Stop when level is reached",
            position = 1)
    default int destinationLevel() {
        return 99;
    }

    @ConfigItem(
            keyName = "makeFire",
            name = "Make fire",
            description = "Make fire while chopping",
            position = 3
    )
    default boolean makeFire() {
        return true;
    }

    @ConfigItem(
            keyName = "Start",
            name = "Start/Stop",
            description = "Start/Stop button",
            position = 2)
    default Button startStopButton() {
        return new Button();
    }
}
