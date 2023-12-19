package net.unethicalite.plugins.Slaytrade;

import net.runelite.client.config.Button;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("SlayTrade")
public interface SlayTradeConfig extends Config {

    @ConfigItem(
            keyName = "account",
            name = "Account",
            description = "Account to trade",
            position = 0

    )
    default String Account()
    {
        return "null";
    }


    @ConfigItem(
            keyName = "muleAmount",
            name = "Mule Amount",
            description = "Amount of starter gp to mule",
            position = 1
    )
    default int muleAmount()
    {
        return 0;
    }

    @ConfigItem(
            keyName = "Start",
            name = "Start/Stop",
            description = "Start/Stop button",
            position = 2)
    default Button startStopButton()
    {
        return new Button();
    }
}
